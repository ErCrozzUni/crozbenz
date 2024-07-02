package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Concessionario;
import it.uniroma3.siw.repository.ConcessionarioRepository;
import jakarta.transaction.Transactional;

@Service
public class ConcessionarioService {
    @Autowired
    private ConcessionarioRepository concessionarioRepository;

    public Concessionario findById(Long id) {
        return concessionarioRepository.findById(id).orElse(null);
    }

    public Iterable<Concessionario> findAll() {
        return concessionarioRepository.findAll();
    }

    @Transactional
    public void saveConcessionario(Concessionario concessionario) {
        concessionarioRepository.save(concessionario);
    }
    public Optional<Concessionario> getConcessionario(Long id) {
        return concessionarioRepository.findById(id);
    }

    public Iterable<Concessionario> getAllConcessionari() {
        return concessionarioRepository.findAll();
    }

    public void deleteConcessionario(Long id) {
        concessionarioRepository.deleteById(id);
    }

    public void save(Concessionario concessionario) {
        concessionarioRepository.save(concessionario);
    }

    public Concessionario findByName(String nome) {
        Optional<Concessionario> result = concessionarioRepository.findByNome(nome);
        return result.orElse(null);
    }

    public void deleteConcessionario(Concessionario concessionario) {
        concessionarioRepository.delete(concessionario);
    }
}
