package rich.pwd.config.jwt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import rich.pwd.config.jwt.bean.po.RefreshToken;
import rich.pwd.config.jwt.bean.po.User;

import java.util.Optional;

@Repository
public interface RefreshTokenDao extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByUserId(Long userId);

  @Modifying
  int deleteByUser(User user);

  @Modifying
  void deleteAll();
}
