package rich.pwd.config.jwt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.config.jwt.bean.po.Role;
import rich.pwd.config.jwt.bean.po.RoleEnum;

import java.util.Optional;

@Repository
public interface RoleDao extends JpaRepository<Role, Long> {

  Optional<Role> findByName(RoleEnum name);
}
