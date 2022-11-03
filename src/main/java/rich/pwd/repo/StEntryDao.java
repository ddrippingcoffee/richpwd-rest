package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.StEntry;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StEntryDao extends JpaRepository<StEntry, Long> {

  List<StEntry> findAllByUserIdAndDelDtmIsNullOrderByC8tDtmDesc(Long userId);

  @Modifying
  @Query("update StEntry entry set entry.delDtm = :delDtm  where entry.userId = :userId and entry.symb = :symb  and entry.c8tDtm = :c8tDtm")
  int updateDeleteTimeByUserIdAndSymbAndC8tDtm(@Param("userId") Long userId,
                                               @Param("symb") String symb,
                                               @Param("c8tDtm") LocalDateTime c8tDtm,
                                               @Param("delDtm") LocalDateTime delDtm);
}
