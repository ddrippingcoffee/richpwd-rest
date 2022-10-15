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
import rich.pwd.bean.dto.payload.response.JwtResponse;
import rich.pwd.bean.dto.payload.response.MessageResponse;
import rich.pwd.bean.po.Role;
import rich.pwd.bean.po.RoleEnum;
import rich.pwd.bean.po.User;
import rich.pwd.config.jwt.JwtUtils;
import rich.pwd.config.jwt.UserDetailsImpl;
import rich.pwd.repo.RoleDao;
import rich.pwd.repo.UserDao;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthContr {

  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  UserDao userDao;
  @Autowired
  RoleDao roleDao;
  @Autowired
  PasswordEncoder encoder;
  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    return ResponseEntity.ok(
            new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles)
    );
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
      Role userRole = roleDao.findByName(RoleEnum.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleDao.findByName(RoleEnum.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
            roles.add(adminRole);
            break;
          case "mod":
            Role modRole = roleDao.findByName(RoleEnum.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Mod Role is not found."));
            roles.add(modRole);
            break;
          default:
            Role userRole = roleDao.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userDao.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
