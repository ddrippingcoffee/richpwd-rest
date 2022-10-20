package rich.pwd.config.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class JwtUtils {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${richpwd.app.jwtSecret}")
  private String jwtSecret;

  @Value("${richpwd.app.jwtExpirationSec}")
  private int jwtExpirationSec;

  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    return generateTokenFromUsername(userPrincipal.getUsername());
  }

  public String generateTokenFromUsername(String username) {

    /*
        Jwt 無法手動使 token 失效
        https://stackoverflow.com/questions/37959945/how-to-destroy-jwt-tokens-on-logout
     */

    ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneId.systemDefault());
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(zdt.toInstant()))
            .setExpiration(Date.from(zdt.plusSeconds(jwtExpirationSec).toInstant()))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public JwtTokenEnum validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
      return JwtTokenEnum.SIGNATURE_EX;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
      return JwtTokenEnum.MALFORMED_EX;
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
      return JwtTokenEnum.EXPIRED_EX;
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
      return JwtTokenEnum.UNSUPPORTED_EX;
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
      return JwtTokenEnum.ILLEGAL_ARGUMENT_EX;
    }
    return JwtTokenEnum.VALID;
  }
}
