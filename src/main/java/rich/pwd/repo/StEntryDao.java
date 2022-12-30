package rich.pwd.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.dto.proj.StEntryCountProj;
import rich.pwd.bean.po.StEntry;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StEntryDao extends JpaRepository<StEntry, Long> {

  @Query(value = "SELECT st_entry.symb, " +
          "     com_info.com_nm comNm, " +
          "     COUNT(*) entrySum, " +
          "     COUNT(CASE WHEN st_entry.del_dtm IS NULL THEN 1 END) actSum, " +
          "     COUNT(CASE WHEN st_entry.del_dtm IS NOT NULL THEN 1 END) oldSum " +
          "     FROM st_entry INNER JOIN com_info ON st_entry.symb = com_info.symb " +
          "     WHERE st_entry.user_id = ?1 GROUP BY st_entry.symb ORDER BY actSum DESC, st_entry.symb ",
          countQuery = "SELECT COUNT(_sum.symbSum) FROM (SELECT COUNT(*) symbSum FROM st_entry WHERE user_id = ?1 GROUP BY symb) _sum ",
          nativeQuery = true)
  Page<StEntryCountProj> findTotalEntry(Long userId, Pageable pageable);

  @Query(value = "SELECT st_entry.symb, " +
          "     com_info.com_nm comNm, " +
          "     COUNT(*) entrySum, " +
          "     COUNT(CASE WHEN st_entry.del_dtm IS NULL THEN 1 END) actSum, " +
          "     COUNT(CASE WHEN st_entry.del_dtm IS NOT NULL THEN 1 END) oldSum " +
          "     FROM st_entry INNER JOIN com_info ON st_entry.symb = com_info.symb " +
          "     WHERE st_entry.user_id = ?1 AND st_entry.symb LIKE CONCAT('%',?2,'%')" +
          "     GROUP BY st_entry.symb ORDER BY actSum DESC, st_entry.symb ",
          countQuery =
                  "SELECT COUNT(_sum.symbSum) " +
                          " FROM (SELECT COUNT(*) symbSum FROM st_entry WHERE user_id = ?1 AND symb LIKE CONCAT('%',?2,'%') GROUP BY symb) _sum ",
          nativeQuery = true)
  Slice<StEntryCountProj> findTotalEntryByFuzzySymb(Long userId, String symb, Pageable pageable);

  @Query(value = "SELECT st_entry.symb, " +
          "     com_info.com_nm comNm, " +
          "     COUNT(*) entrySum, " +
          "     COUNT(CASE WHEN st_entry.del_dtm IS NULL THEN 1 END) actSum, " +
          "     COUNT(CASE WHEN st_entry.del_dtm IS NOT NULL THEN 1 END) oldSum " +
          "     FROM st_entry INNER JOIN com_info ON st_entry.symb = com_info.symb " +
          "     WHERE st_entry.user_id = ?1 AND st_entry.symb IN ?2 " +
          "     GROUP BY st_entry.symb ORDER BY actSum DESC, st_entry.symb ",
          countQuery =
                  "SELECT COUNT(_sum.symbSum) " +
                          " FROM (SELECT COUNT(*) symbSum FROM st_entry WHERE user_id = ?1 AND symb IN ?2 GROUP BY symb) _sum ",
          nativeQuery = true)
  Slice<StEntryCountProj> findTotalEntryBySymbList(Long userId, List<String> symbList, Pageable pageable);

  Page<StEntry> findAllByUserIdAndSymbOrderByC8tDtmDesc(Long userId, String symb, Pageable pageable);

  Page<StEntry> findAllByUserIdAndDelDtmIsNull(Long userId, Pageable pageable);

  Page<StEntry> findAllByUserIdAndDelDtmIsNotNull(Long userId, Pageable pageable);

  Slice<StEntry> findAllByUserIdAndSymbIn(Long userId, List<String> symbList, Pageable pageable);

  @Modifying
  @Query("update StEntry entry set entry.delDtm = :delDtm  where entry.userId = :userId and entry.symb = :symb  and entry.c8tDtm = :c8tDtm")
  int updateDeleteTimeByUserIdAndSymbAndC8tDtm(@Param("userId") Long userId,
                                               @Param("symb") String symb,
                                               @Param("c8tDtm") LocalDateTime c8tDtm,
                                               @Param("delDtm") LocalDateTime delDtm);
}
