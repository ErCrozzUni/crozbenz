package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Concessionario;

public interface ConcessionarioRepository extends CrudRepository<Concessionario, Long> {
    Optional<Concessionario> findByNome(String nome);
}
