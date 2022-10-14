package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.StEntry;

import java.util.List;

@Repository
public interface StEntryDao extends JpaRepository<StEntry, Long> {

  List<StEntry> findAllByDelDtmIsNullOrderByC8tDtmDesc();
  List<StEntry> findAllByDelDtmIsNotNullOrderByDelDtmDesc();
}
