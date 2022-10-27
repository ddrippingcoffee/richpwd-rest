package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.StFileDb;

@Repository
public interface StFileDbDao extends JpaRepository<StFileDb, Long> {
}
