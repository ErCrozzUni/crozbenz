package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Auto;

public interface AutoRepository extends CrudRepository<Auto, Long> {
    Optional<Auto> findByNome(String nome);
}
