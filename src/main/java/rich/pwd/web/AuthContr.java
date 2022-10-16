package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rich.pwd.bean.dto.payload.request.LoginRequest;
import rich.pwd.bean.dto.payload.request.SignupRequest;
import rich.pwd.bean.dto.payload.request.TokenRefreshRequest;
import rich.pwd.bean.dto.payload.response.JwtResponse;
import rich.pwd.bean.dto.payload.response.MessageResponse;
import rich.pwd.bean.dto.payload.response.TokenRefreshResponse;
import rich.pwd.bean.po.RefreshToken;
import rich.pwd.bean.po.Role;
import rich.pwd.bean.po.RoleEnum;
import rich.pwd.bean.po.User;
import rich.pwd.config.jwt.JwtUtils;
import rich.pwd.config.jwt.RefreshTokenService;
import rich.pwd.config.jwt.UserDetailsImpl;
import rich.pwd.ex.TokenRefreshException;
import rich.pwd.repo.RoleDao;
import rich.pwd.repo.UserDao;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "*", maxAge = 3600)
public class AuthContr {

  private static final String ANONYMOUS_USER = "anonymousUser";

  private final AuthenticationManager authenticationManager;
  private final UserDao userDao;
  private final RoleDao roleDao;
  private final PasswordEncoder encoder;
  private final JwtUtils jwtUtils;
  private final RefreshTokenService refreshTokenService;

  @Autowired
  public AuthContr(AuthenticationManager authenticationManager,
                   UserDao userDao, RoleDao roleDao,
                   PasswordEncoder encoder,
                   JwtUtils jwtUtils,
                   RefreshTokenService refreshTokenService) {
    this.authenticationManager = authenticationManager;
    this.userDao = userDao;
    this.roleDao = roleDao;
    this.encoder = encoder;
    this.jwtUtils = jwtUtils;
    this.refreshTokenService = refreshTokenService;
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    /*
      AuthenticationManager 包含裝載 UserDetailsService & PasswordEncoder 的 DaoAuthenticationProvider
      用於驗證含登入資訊 (LoginRequest) 的 UsernamePasswordAuthenticationToken

      回傳包含授權資訊的 Authentication 物件
    */

    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    // 刪除舊 token
    Optional<RefreshToken> user = refreshTokenService.findByUserId(userDetails.getId());
    if (user.isPresent()) {
      refreshTokenService.deleteByUser(user.get().getUser());
    }
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    return ResponseEntity.ok(new JwtResponse(
            jwt,
            refreshToken.getToken(),
            userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),
            roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
    if (userDao.existsByUsername(signupRequest.getUsername())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Username is already taken!"));
    }
    if (userDao.existsByEmail(signupRequest.getEmail())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Email is already in use"));
    }

    User user = new User(signupRequest.getUsername(),
            signupRequest.getEmail(),
            encoder.encode(signupRequest.getPassword()));
    Set<String> strRoles = signupRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (null == strRoles) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Role is not found in request."));
    } else {
      List<String> strRoleList = new ArrayList<>(strRoles);
      for (int i = 0; i < strRoleList.size(); i++) {
        Role role;
        if ("admin".equals(strRoleList.get(i))) {
          role = roleDao.findByName(RoleEnum.ROLE_ADMIN)
                  .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
          roles.add(role);
        } else if ("mod".equals(strRoleList.get(i))) {
          role = roleDao.findByName(RoleEnum.ROLE_MODERATOR)
                  .orElseThrow(() -> new RuntimeException("Error: Mod Role is not found."));
          roles.add(role);
        } else if ("user".equals(strRoleList.get(i))) {
          role = roleDao.findByName(RoleEnum.ROLE_USER)
                  .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
          roles.add(role);
        } else {
          return ResponseEntity
                  .badRequest()
                  .body(new MessageResponse("Error: Not Accepted Role: " + strRoleList.get(i)));
        }
      }
    }
    user.setRoles(roles);
    userDao.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
            .map(token -> refreshTokenService.verifyExpiration(token))
            .map(token -> token.getUser())
            .map(user -> {
              String token = jwtUtils.generateTokenFromUsername(user.getUsername());
              return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
            }).orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (ANONYMOUS_USER.equals(authentication.getPrincipal().toString())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Not Accepted Request"));
    }
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Optional<RefreshToken> user = refreshTokenService.findByUserId(userDetails.getId());
    if (!user.isPresent()) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Already signout"));
    } else {
      refreshTokenService.deleteByUser(user.get().getUser());
      return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
  }
}
