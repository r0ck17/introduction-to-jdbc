package by.javaguru.dao;

import java.util.List;
import java.util.Optional;

public interface Dao <K, E> {
    E save(E ticket);
    boolean update(E id);
    boolean delete(K id);
    Optional<E> findById(K id);
    List<E> findAll();
}
