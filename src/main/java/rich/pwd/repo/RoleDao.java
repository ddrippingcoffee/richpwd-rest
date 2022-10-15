package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.Role;
import rich.pwd.bean.po.RoleEnum;

import java.util.Optional;

@Repository
public interface RoleDao extends JpaRepository<Role, Long> {

  Optional<Role> findByName(RoleEnum name);
}
