package it.uniroma3.siw.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Concessionario;
import it.uniroma3.siw.model.Credenziali;
import it.uniroma3.siw.service.ConcessionarioService;
import it.uniroma3.siw.service.CredenzialiService;

@Controller
public class ConcessionarioController {

	@Autowired
	private ConcessionarioService concessionarioService;

	@Autowired
	private CredenzialiService credenzialiService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/*private static String UPLOAD_DIR = "src/main/resources/static/uploads/";*/

	//****************************VIEW ALL***********************************************************
	@GetMapping("/concessionari")
	public String showConcessionari(Model model) {
		model.addAttribute("concessionari", concessionarioService.findAll());
		return "concessionari"; //restituisce il nome della vista
	}
	
	//****************************VIEW CONCESSIONARIO ID ***********************************************************
	
	@GetMapping("/dettagliConc/{id}")
	public String getConcessionario(@PathVariable("id") Long id, Model model) {
	    Concessionario concessionario = this.concessionarioService.findById(id);
	    model.addAttribute("concessionario", concessionario);
	    model.addAttribute("auto", concessionario.getAuto()); // Aggiungi la lista delle auto al modello
	    return "dettagliConc"; // restituisce il nome della vista Thymeleaf
	}



	//****************************EDIT CONCESSIONARIO*************************************
	@GetMapping("/editConcessionario")
	public String showEditForm(@RequestParam("id") Long id, Model model) {
	    Concessionario concessionario = concessionarioService.findById(id);
	    if (concessionario == null) {
	        return "redirect:/admin/managementConcessionari"; // Redirect if the concessionario is not found
	    }
	    model.addAttribute("concessionario", concessionario);
	    return "/editConcessionario";
	}

	@PostMapping("/admin/updateConcessionario")
	public String updateConcessionario(@RequestParam("id") Long id,
	                                   @RequestParam("nome") String nome,
	                                   @RequestParam("amministratore") String amministratore,
	                                   @RequestParam("immagine") MultipartFile immagine) {
	    Concessionario existingConcessionario = concessionarioService.findById(id);
	    if (existingConcessionario != null) {
	        existingConcessionario.setNome(nome);
	        existingConcessionario.setAmministratore(amministratore);

	        // Gestisci l'immagine
	        if (!immagine.isEmpty()) {
	            // Cancella la vecchia immagine
	            String vecchiaImmagine = existingConcessionario.getImmagine();
	            if (vecchiaImmagine != null && !vecchiaImmagine.isEmpty()) {
	                File fileVecchiaImmagine = new File("src/main/resources/static/" + vecchiaImmagine);
	                if (fileVecchiaImmagine.exists()) {
	                    fileVecchiaImmagine.delete();
	                }
	            }
	            // Salva la nuova immagine nella directory temporanea
	            try {
	                String nuovoNomeImmagine = "/uploads/concessionari/" + immagine.getOriginalFilename();
	                File nuovoFileImmagineTemp = new File(System.getProperty("java.io.tmpdir") + "/" + nuovoNomeImmagine);

	                // Assicurati che la directory esista
	                File directoryTemp = nuovoFileImmagineTemp.getParentFile();
	                if (!directoryTemp.exists()) {
	                    directoryTemp.mkdirs();
	                }

	                immagine.transferTo(nuovoFileImmagineTemp);

	                // Copia l'immagine nella directory static
	                File nuovoFileImmagine = new File("src/main/resources/static" + nuovoNomeImmagine);
	                File directory = nuovoFileImmagine.getParentFile();
	                if (!directory.exists()) {
	                    directory.mkdirs();
	                }
	                Files.copy(nuovoFileImmagineTemp.toPath(), nuovoFileImmagine.toPath(), StandardCopyOption.REPLACE_EXISTING);

	                existingConcessionario.setImmagine(nuovoNomeImmagine);
	            } catch (IOException e) {
	                e.printStackTrace();
	                return "redirect:/admin/managementConcessionari";
	            }
	        }

	        concessionarioService.save(existingConcessionario);
	    }
	    return "redirect:/admin/managementConcessionari";
	}


	//****************************MANAGEMENT ADMIN***********************************************************
	@GetMapping(value = "/admin/managementConcessionari")
	public String managementConcessionari(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			Credenziali credenziali = credenzialiService.getCredenziali(userDetails.getUsername());

			if (credenziali.getRuolo().equals(Credenziali.ADMIN_ROLE)) {
				model.addAttribute("concessionari", concessionarioService.findAll());
				return "admin/managmentConcessionari.html";
			}
		}
		return "redirect:/";
	}

	//****************************ADD CONCESSIONARIO FROM ADMIN***********************************************************
	@GetMapping("/admin/formNewConcessionario")
	public String showFormNewConcessionario(Model model) {
		model.addAttribute("concessionario", new Concessionario());
		return "admin/fromNewConcessionario";
	}

	@PostMapping("/admin/saveConcessionario")
	public String saveConcessionario(@RequestParam("nome") String nome,
			@RequestParam("amministratore") String amministratore,
			@RequestParam("dataFondazione") LocalDate dataFondazione,
			@RequestParam("descrizione") String descrizione,
			@RequestParam("immagine") MultipartFile immagine,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
		Concessionario concessionario = new Concessionario();
		concessionario.setNome(nome);
		concessionario.setAmministratore(amministratore);
		concessionario.setDataFondazione(dataFondazione);
		concessionario.setDescrizione(descrizione);

		// Gestisci l'immagine
		if (!immagine.isEmpty()) {
			try {
				String nuovoNomeImmagine = "/uploads/concessionari/" + immagine.getOriginalFilename();
				File nuovoFileImmagineTemp = new File(System.getProperty("java.io.tmpdir") + "/" + nuovoNomeImmagine);

				// Assicurati che la directory esista
				File directoryTemp = nuovoFileImmagineTemp.getParentFile();
				if (!directoryTemp.exists()) {
					directoryTemp.mkdirs();
				}

				immagine.transferTo(nuovoFileImmagineTemp);

				// Copia l'immagine nella directory static
				File nuovoFileImmagine = new File("src/main/resources/static" + nuovoNomeImmagine);
				File directory = nuovoFileImmagine.getParentFile();
				if (!directory.exists()) {
					directory.mkdirs();
				}
				Files.copy(nuovoFileImmagineTemp.toPath(), nuovoFileImmagine.toPath(), StandardCopyOption.REPLACE_EXISTING);

				concessionario.setImmagine(nuovoNomeImmagine);
			} catch (IOException e) {
				e.printStackTrace();
				return "redirect:/admin/formNewConcessionario";
			}
		}

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

		return "redirect:/admin/managementConcessionari"; // Reindirizza alla lista dei concessionari
	}


	//****************************DELETE CONCESSIONARIO FROM ADMIN***********************************************************
	@GetMapping("/admin/concessionario/delete/{id}")
	public String deleteConcessionario(@PathVariable("id") Long id) {
	    Concessionario concessionario = concessionarioService.findById(id);
	    if (concessionario != null) {
	        // Elimina le credenziali associate
	        Credenziali credenziali = concessionario.getCredenziali();
	        if (credenziali != null) {
	            // Disassocia le credenziali dal concessionario
	            concessionario.setCredenziali(null);
	            concessionarioService.save(concessionario);  // Salva le modifiche per disassociare

	            credenziali.setConcessionario(null);
	            credenzialiService.save(credenziali);  // Salva le modifiche per disassociare
	            
	            // Ora possiamo eliminare le credenziali
	            credenzialiService.delete(credenziali);
	        }

	        // Cancella l'immagine associata
	        String vecchiaImmagine = concessionario.getImmagine();
	        if (vecchiaImmagine != null && !vecchiaImmagine.isEmpty()) {
	            File immagineFile = new File("src/main/resources/static" + vecchiaImmagine);
	            if (immagineFile.exists()) {
	                immagineFile.delete();
	            }
	        }

	        // Elimina il concessionario
	        concessionarioService.deleteConcessionario(concessionario);
	    }
	    return "redirect:/admin/managementConcessionari"; // Reindirizza alla lista dei concessionari
	}

}
