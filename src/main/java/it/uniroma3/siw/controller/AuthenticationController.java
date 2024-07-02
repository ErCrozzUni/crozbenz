package it.uniroma3.siw.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Concessionario;
import it.uniroma3.siw.model.Credenziali;
import it.uniroma3.siw.service.ConcessionarioService;
import it.uniroma3.siw.service.CredenzialiService;

@Controller
public class AuthenticationController {

	@Autowired
	private CredenzialiService credenzialiService;

	@Autowired
	private ConcessionarioService concessionarioService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/////////////////////////////////////ACCESSO////////////////////////////////////////////////////////////////////////
	@GetMapping(value = "/login")
	public String showLoginForm(Model model) {
		return "login";
	}

	@GetMapping(value = "/success")
	public String success(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			return "index.html";
		} else {
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credenziali credenziali = credenzialiService.getCredenziali(userDetails.getUsername());
			if (credenziali.getRuolo().equals(Credenziali.ADMIN_ROLE)) {
				return "admin/indexAdmin.html";
			}
			if (credenziali.getRuolo().equals(Credenziali.CONCES_ROLE)) {
				return "concessionario/indexConcessionario.html";
			}
		}
		return "index";
	}

	/////////////////////////////////////REGISTRAZIONE////////////////////////////////////////////////////////////////////////
	@GetMapping(value = "/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("concessionario", new Concessionario());
		model.addAttribute("credenziali", new Credenziali());
		return "register";
	}

	@PostMapping(value = { "/register" })
	public String registerUser(@RequestParam("nome") String nome,
			@RequestParam("amministratore") String amministratore,
			@RequestParam("email") String email,
			@RequestParam("dataFondazione") String dataFondazione,
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("immagine") MultipartFile immagine,
			@RequestParam("descrizione") String descrizione,
			Model model) {
		// Crea e salva il concessionario
		Concessionario concessionario = new Concessionario();
		// Gestisci l'immagine
		if (!immagine.isEmpty()) {
			try {
				String nuovoNomeImmagine = "src/main/resources/static/uploads/concessionari/" + immagine.getOriginalFilename();
				File nuovoFileImmagineTemp = new File(System.getProperty("java.io.tmpdir") + "/" + nuovoNomeImmagine);

				// Assicurati che la directory esista
				File directoryTemp = nuovoFileImmagineTemp.getParentFile();
				if (!directoryTemp.exists()) {
					directoryTemp.mkdirs();
				}

				immagine.transferTo(nuovoFileImmagineTemp);

				// Copia l'immagine nella directory static
				File nuovoFileImmagine = new File("src/main/resources/static/uploads/concessionari/" + immagine.getOriginalFilename());
				File directory = nuovoFileImmagine.getParentFile();
				if (!directory.exists()) {
					directory.mkdirs();
				}
				Files.copy(nuovoFileImmagineTemp.toPath(), nuovoFileImmagine.toPath(), StandardCopyOption.REPLACE_EXISTING);

				concessionario.setImmagine(nuovoNomeImmagine);
			} catch (IOException e) {
				e.printStackTrace();
				return "redirect:/register";
			}
		}

		concessionario.setNome(nome);
		concessionario.setAmministratore(amministratore);
		concessionario.setEmail(email);
		concessionario.setDataFondazione(LocalDate.parse(dataFondazione));
		concessionario.setImmagine("uploads/concessionari/" + immagine.getOriginalFilename());
		concessionario.setDescrizione(descrizione);

		Credenziali credenziali = new Credenziali();
		credenziali.setUsername(username);
		credenziali.setPassword(passwordEncoder.encode(password));
		credenziali.setRuolo(Credenziali.CONCES_ROLE);
		credenziali.setConcessionario(concessionario);
		concessionario.setCredenziali(credenziali);

		// Salva le credenziali
		credenzialiService.save(credenziali);

		// Salva il concessionario
		concessionarioService.saveConcessionario(concessionario);

		model.addAttribute("concessionario", concessionario);
		return "login";
	}

	///////////////////METODO PER LA GESTIONE DELLA NAVBAR QUANDO PERMO SUL LOGO/////////////////////////////////////////////
	@GetMapping(value = "/home")
	public String home(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication instanceof AnonymousAuthenticationToken) {
	        return "index";
	    } else {
	        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        Credenziali credenziali = credenzialiService.getCredenziali(userDetails.getUsername());
	        if (credenziali.getRuolo().equals("ADMIN")) {
	            return "admin/indexAdmin";
	        }
	        if (credenziali.getRuolo().equals("CONCES")) {
	            return "concessionario/indexConcessionario";
	        }
	    }
	    return "index";
	}


}
