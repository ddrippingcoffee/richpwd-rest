package rich.pwd.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import rich.pwd.bean.po.RefreshToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthTokenFilter extends OncePerRequestFilter {

  /*
    OncePerRequestFilter
    提供 doFilterInternal() 用於
    解析和驗證 JWT
    加載用戶詳細信息（使用 UserDetailsService ）
    檢查授權（使用 UsernamePasswordAuthenticationToken ）
  */

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
  private static final List<String> AUTH_PATH_URL_LIST = List.of("/auth/signin", "/auth/signout");

  @Autowired
  private JwtUtils jwtUtils;
  @Autowired
  private UserDetailsServiceImpl userDetailsService;
  @Autowired
  private RefreshTokenService refreshTokenService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    try {

      /*
         JWT 無法手動使 token 到期
       */

      String jwt = parseJwt(request);
      // 是否包含 Access Token
      if (null != jwt) {
        JwtTokenEnum validateRslt = jwtUtils.validateJwtToken(jwt);
        // 檢驗 Access Token 是否合法
        if (JwtTokenEnum.VALID == validateRslt) {
          String username = jwtUtils.getUsernameFromJwtToken(jwt);
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

          // 非登入及登出檢查 Access Token 是否能取得 User
          if (!AUTH_PATH_URL_LIST.contains(request.getServletPath())) {
            UserDetailsImpl userDetailImpl = (UserDetailsImpl) authentication.getPrincipal();
            Optional<RefreshToken> user = refreshTokenService.findByUserId(userDetailImpl.getId());
            if (user.isEmpty()) {
              logger.error("使用者已登出但 Access Token 未到期, 請求 URL");
              setInvalidMsgResponse(request, response,
                      "No access token found, Blocked by filter, Url: " + request.getServletPath());
              return;
            }
          }
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          if (JwtTokenEnum.SIGNATURE_EX == validateRslt) {
            setInvalidMsgResponse(request, response, "SIGNATURE_EX");
          } else if (JwtTokenEnum.MALFORMED_EX == validateRslt) {
            setInvalidMsgResponse(request, response, "MALFORMED_EX");
          } else if (JwtTokenEnum.EXPIRED_EX == validateRslt) {
            setInvalidMsgResponse(request, response, "EXPIRED_EX");
          } else if (JwtTokenEnum.UNSUPPORTED_EX == validateRslt) {
            setInvalidMsgResponse(request, response, "UNSUPPORTED_EX");
          } else if (JwtTokenEnum.ILLEGAL_ARGUMENT_EX == validateRslt) {
            setInvalidMsgResponse(request, response, "ILLEGAL_ARGUMENT_EX");
          }
          return;
        }
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e.getMessage());
    }
    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7, headerAuth.length());
    }
    return null;
  }

  private void setInvalidMsgResponse(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String errMsg) throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    final Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_FORBIDDEN);
    body.put("error", "Forbidden");
    body.put("message", errMsg);
    body.put("path", request.getServletPath());
    body.put("timestamp", LocalDateTime.now().toString());
    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), body);
  }
}
