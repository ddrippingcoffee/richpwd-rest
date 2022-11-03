package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.StFileDb;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StFileDbDao extends JpaRepository<StFileDb, Long> {

  List<StFileDb> findAllByUserIdAndSymbAndC8tDtm(Long userId, String symb, LocalDateTime c8tDtm);
}
