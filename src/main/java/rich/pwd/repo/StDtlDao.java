package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.StDtl;

@Repository
public interface StDtlDao extends JpaRepository<StDtl, Long> {
}
