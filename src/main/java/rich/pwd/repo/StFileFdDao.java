package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.StFileFd;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StFileFdDao extends JpaRepository<StFileFd, Long> {

  List<StFileFd> findAllBySymbAndC8tDtm(String symb, LocalDateTime c8tDtm);
}
