package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Auto;
import it.uniroma3.siw.repository.AutoRepository;
import jakarta.transaction.Transactional;

@Service
public class AutoService {
    @Autowired
    private AutoRepository autoRepository;

    public Auto findById(Long id) {
        return autoRepository.findById(id).orElse(null);
    }

    public Iterable<Auto> findAll() {
        return autoRepository.findAll();
    }

    @Transactional
    public void saveAuto(Auto auto) {
        autoRepository.save(auto);
    }

    public Optional<Auto> getAuto(Long id) {
        return autoRepository.findById(id);
    }

    public Iterable<Auto> getAllAuto() {
        return autoRepository.findAll();
    }

    public void deleteAuto(Long id) {
        autoRepository.deleteById(id);
    }

    public void save(Auto auto) {
        autoRepository.save(auto);
    }

    public Auto findByName(String nome) {
        Optional<Auto> result = autoRepository.findByNome(nome);
        return result.orElse(null);
    }

    public void deleteAuto(Auto auto) {
        autoRepository.delete(auto);
    }
}
