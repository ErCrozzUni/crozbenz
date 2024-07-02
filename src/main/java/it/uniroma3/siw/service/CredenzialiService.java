package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credenziali;
import it.uniroma3.siw.repository.CredenzialiRepository;

@Service
public class CredenzialiService {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected CredenzialiRepository credenzialiRepository;

	@Transactional
	public Credenziali getCredentiziali(Long id) {
		Optional<Credenziali> result = this.credenzialiRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional
	public Credenziali getCredenziali(String username) {
		Optional<Credenziali> result = this.credenzialiRepository.findByUsername(username);
		return result.orElse(null);
	}
	
	@Transactional
	public void save(Credenziali credenziali) {
		credenzialiRepository.save(credenziali);
	}
	
	public void delete(Credenziali credenziali) {
        credenzialiRepository.delete(credenziali);
    }

	/*	@Transactional
	public Credenziali saveCredenziali(Credenziali credentials) {
		// Non sovrascrivere il ruolo qui
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}*/

}
