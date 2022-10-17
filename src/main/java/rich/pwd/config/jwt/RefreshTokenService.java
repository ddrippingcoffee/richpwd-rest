package rich.pwd.config.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rich.pwd.bean.po.RefreshToken;
import rich.pwd.bean.po.User;
import rich.pwd.ex.TokenRefreshException;
import rich.pwd.repo.RefreshTokenDao;
import rich.pwd.repo.UserDao;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  @Value("${richpwd.app.jwtRefreshExpirationSec}")
  private Long refreshTokenDurationSec;

  private final RefreshTokenDao refreshTokenDao;
  private final UserDao userDao;

  @Autowired
  public RefreshTokenService(RefreshTokenDao refreshTokenDao, UserDao userDao) {
    this.refreshTokenDao = refreshTokenDao;
    this.userDao = userDao;
  }

  public Optional<RefreshToken> findByUserId(Long userId) {
    return refreshTokenDao.findByUser(userDao.findById(userId).get());
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenDao.findByToken(token);
  }

  public RefreshToken createRefreshToken(Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(userDao.findById(userId).get());
    refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationSec));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenDao.save(refreshToken);
    return refreshToken;
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (0 > token.getExpiryDate().compareTo(Instant.now())) {
      refreshTokenDao.delete(token);
      throw new TokenRefreshException(
              token.getToken(),
              "Refresh token was expired. Please make a new signin request");
    }
    return token;
  }

  @Transactional
  public int deleteByUser(User user) {
    return refreshTokenDao.deleteByUser(user);
  }
}