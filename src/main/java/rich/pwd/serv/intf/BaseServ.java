package rich.pwd.serv.intf;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface BaseServ<T, ID> {

  /**
   * 無使用
   */
  Class<T> getDomainClass();

  // Create
  // Create
  // Create

  <E extends T> E save(E entity);

  <S extends T> List<S> saveAll(Iterable<S> entities);

  void flush();

  <S extends T> S saveAndFlush(S entity);

  // Read
  // Read
  // Read

  boolean existsById(ID id);

  <S extends T> boolean exists(Example<S> example);

  T getReferenceById(ID id);

  Optional<T> findById(ID id);

  <S extends T> Optional<S> findOne(Example<S> example);

  List<T> findAll();

  List<T> findAll(Sort sort);

  Page<T> findAll(Pageable pageable);

  List<T> findAllById(Iterable<ID> ids);

  <S extends T> List<S> findAll(Example<S> example);

  <S extends T> List<S> findAll(Example<S> example, Sort sort);

  <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

  long count();

  <S extends T> long count(Example<S> example);

  // Delete
  // Delete
  // Delete

  void delete(T entity);

  void deleteById(ID id);

  void deleteAll();

  void deleteAll(Iterable<? extends T> entities);

  void deleteAllInBatch(Iterable<T> entities);
}
