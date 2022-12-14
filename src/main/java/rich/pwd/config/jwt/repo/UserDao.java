package rich.pwd.config.jwt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.config.jwt.bean.po.User;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
