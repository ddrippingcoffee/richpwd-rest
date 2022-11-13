package rich.pwd.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.dto.proj.ComInfoProj;
import rich.pwd.bean.po.ComInfo;

import java.util.List;

@Repository
public interface ComInfoDao extends JpaRepository<ComInfo, Long> {

  ComInfo findComInfoBySymb(String symb);

  List<ComInfoProj> findDistinctBy();

  Slice<ComInfo> findAllByComNmContaining(String comNm, Pageable pageable);

  Page<ComInfo> findAllByComMainContaining(String comMain, Pageable pageable);

  Page<ComInfo> findAllByComCotedContaining(String comCoted, Pageable pageable);

  Page<ComInfo> findAllByComCepContaining(String comCep, Pageable pageable);

  Page<ComInfo> findAllByComIndusContaining(String indus, Pageable pageable);

  @Deprecated
  Slice<ComInfo> getAllByComIndusContaining(String indus, Pageable pageable);

  @Modifying
  int deleteComInfoBySymb(String symb);
}
