package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.FileDb;

@Repository
public interface FileDbDao extends JpaRepository<FileDb, String> {
}
