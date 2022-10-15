package rich.pwd.config.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rich.pwd.bean.po.User;
import rich.pwd.repo.UserDao;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  /*
    UserDetailsService
    透過 username 載入 user 的介面
    回傳 Spring Security 處理 authentication 及 validation 的 UserDetails

    UserDetails
    包含 username password authorities
    建立 Authentication object
  */

  @Autowired
  UserDao userDao;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userDao.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    return UserDetailsImpl.build(user);
  }
}
