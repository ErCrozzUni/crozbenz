package it.uniroma3.siw.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Auto;
import it.uniroma3.siw.model.Concessionario;
import it.uniroma3.siw.model.Credenziali;
import it.uniroma3.siw.service.AutoService;
import it.uniroma3.siw.service.ConcessionarioService;
import it.uniroma3.siw.service.CredenzialiService;
import jakarta.transaction.Transactional;

@Controller
public class AutoController {

	@Autowired
	private AutoService autoService;

	@Autowired
	private ConcessionarioService concessionarioService;

	@Autowired
	private CredenzialiService credenzialiService;

	//****************************VIEW ALL***********************************************************
	@GetMapping("/auto")
	public String showAuto(Model model) {
		model.addAttribute("auto", autoService.findAll());
		return "auto.html"; // restituisce il nome della vista
	}
	
	//****************************VIEW ONE***********************************************************
	@GetMapping("/dettagliAuto/{id}")
	public String getAuto(@PathVariable("id") Long id, Model model) {
	    Auto auto = this.autoService.findById(id); // Assicurati di avere un servizio AutoService per gestire le auto
	    model.addAttribute("auto", auto);
	    return "dettagliAuto"; // restituisce il nome della vista Thymeleaf
	}


	//****************************REMOVE AUTO FROM ADMIN***********************************************************
	@Transactional
	@GetMapping("admin/auto/delete/{id}")
	public String deleteAutoAdmin(@PathVariable("id") Long id) {
	    Auto auto = autoService.findById(id);
	    if (auto != null) {
	        // Rimuovi il riferimento al concessionario
	        Concessionario concessionario = auto.getConcessionario();
	        if (concessionario != null) {
	            concessionario.getAuto().remove(auto);
	            auto.setConcessionario(null);
	            concessionarioService.saveConcessionario(concessionario); // Salva le modifiche del concessionario
	        }

	        // Cancella l'immagine associata
	        String immaginePath = "src/main/resources/static/" + auto.getImmagine();
	        File immagineFile = new File(immaginePath);
	        if (immagineFile.exists()) {
	            immagineFile.delete();
	        }

	        // Cancella specifiche e optional
	        auto.getSpecifiche().clear();
	        auto.getOptional().clear();

	        autoService.deleteAuto(auto);
	    }
	    return "redirect:/admin/managementAuto"; // Reindirizza alla lista delle auto
	}
	//****************************REMOVE AUTO FROM CONCESSIONARIO***********************************************************
	@GetMapping("concessionario/auto/delete/{id}")
	public String deleteAutoConc(@PathVariable("id") Long id) {
	    Auto auto = autoService.findById(id);
	    if (auto != null) {
	        // Rimuovi il riferimento al concessionario
	        Concessionario concessionario = auto.getConcessionario();
	        if (concessionario != null) {
	            concessionario.getAuto().remove(auto);
	            auto.setConcessionario(null);
	            concessionarioService.saveConcessionario(concessionario); // Salva le modifiche del concessionario
	        }

	        // Cancella l'immagine associata
	        String immaginePath = "src/main/resources/static/" + auto.getImmagine();
	        File immagineFile = new File(immaginePath);
	        if (immagineFile.exists()) {
	            immagineFile.delete();
	        }

	        // Cancella specifiche e optional
	        auto.getSpecifiche().clear();
	        auto.getOptional().clear();

	        autoService.deleteAuto(auto);
	    }
	    return "redirect:/admin/managementAuto"; // Reindirizza alla lista delle auto
	}


	//****************************MANAGEMENT ADMIN***********************************************************
	@GetMapping(value = "/admin/managementAuto")
	public String managementAuto(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			Credenziali credenziali = credenzialiService.getCredenziali(userDetails.getUsername());

			if (credenziali.getRuolo().equals(Credenziali.ADMIN_ROLE)) {
				model.addAttribute("auto", autoService.findAll());
				return "/admin/managementAuto";
			}
		}
		return "redirect:/";
	}

	//****************************ADD AUTO***********************************************************
	@GetMapping("/formNewAuto")
	public String showFormNewAuto(@RequestParam(value = "concessionarioId", required = false) Long concessionarioId, Model model, @RequestHeader(value = "referer", required = false) String referer) {
		if (concessionarioId != null) {
			Concessionario concessionario = concessionarioService.findById(concessionarioId);
			model.addAttribute("concessionario", concessionario);
		} else {
			Iterable<Concessionario> concessionari = concessionarioService.findAll();
			model.addAttribute("concessionari", concessionari);
		}
		model.addAttribute("auto", new Auto());
		model.addAttribute("referer", referer);
		return "/formNewAuto";
	}



	@PostMapping("/formNewAuto")
	public String addAuto(@RequestParam("nome") String nome,
			@RequestParam("immagine") MultipartFile immagine,
			@RequestParam("descrizione") String descrizione,
			@RequestParam("concessionarioId") Long concessionarioId,
			@RequestParam Map<String, String> allParams,
			@RequestParam("referer") String referer) {
		Concessionario concessionario = concessionarioService.findById(concessionarioId);
		if (concessionario == null) {
			return "redirect:/admin/managementConcessionari";
		}

		Auto auto = new Auto();
		auto.setNome(nome);
		auto.setDescrizione(descrizione);
		auto.setConcessionario(concessionario);

		// Gestisci l'immagine
		if (!immagine.isEmpty()) {
			try {
				String nuovoNomeImmagine = "images/auto/" + immagine.getOriginalFilename();
				File nuovoFileImmagineTemp = new File(System.getProperty("java.io.tmpdir") + "/" + nuovoNomeImmagine);

				// Assicurati che la directory esista
				File directoryTemp = nuovoFileImmagineTemp.getParentFile();
				if (!directoryTemp.exists()) {
					directoryTemp.mkdirs();
				}

				immagine.transferTo(nuovoFileImmagineTemp);

				// Copia l'immagine nella directory static
				File nuovoFileImmagine = new File("src/main/resources/static/" + nuovoNomeImmagine);
				File directory = nuovoFileImmagine.getParentFile();
				if (!directory.exists()) {
					directory.mkdirs();
				}
				Files.copy(nuovoFileImmagineTemp.toPath(), nuovoFileImmagine.toPath(), StandardCopyOption.REPLACE_EXISTING);

				auto.setImmagine(nuovoNomeImmagine);
			} catch (IOException e) {
				e.printStackTrace();
				return "redirect:/admin/managementConcessionari";
			}
		}

		List<String> specifiche = new ArrayList<>();
		List<String> optional = new ArrayList<>();
		int i = 0;
		while (allParams.containsKey("specifiche[" + i + "]")) {
			specifiche.add(allParams.get("specifiche[" + i + "]"));
			i++;
		}
		i = 0;
		while (allParams.containsKey("optional[" + i + "]")) {
			optional.add(allParams.get("optional[" + i + "]"));
			i++;
		}
		auto.setSpecifiche(specifiche);
		auto.setOptional(optional);

		autoService.save(auto);

		// Reindirizza alla pagina precedente
		return "redirect:" + referer;
	}


	//****************************EDIT AUTO***********************************************************
	@GetMapping("/editAuto")
	public String showEditAutoForm(@RequestParam("id") Long autoId, Model model, @RequestHeader(value = "referer", required = false) String referer) {
		Auto auto = autoService.findById(autoId);
		if (auto == null) {
			return "redirect:/admin/managementAuto";
		}
		List<Concessionario> concessionariDisponibili = (List<Concessionario>) concessionarioService.findAll();
		model.addAttribute("auto", auto);
		model.addAttribute("concessionari", concessionariDisponibili);
		model.addAttribute("referer", referer);
		return "editAuto";
	}

	@PostMapping("/editAuto")
	public String editAuto(@RequestParam("id") Long id,
			@RequestParam("nome") String nome,
			@RequestParam("descrizione") String descrizione,
			@RequestParam("immagine") MultipartFile immagine,
			@RequestParam Map<String, String> allParams,
			@RequestParam("referer") String referer) {
		Auto auto = autoService.findById(id);
		if (auto == null) {
			return "redirect:/auto";
		}

		auto.setNome(nome);
		auto.setDescrizione(descrizione);

		// Gestisci l'immagine
		if (!immagine.isEmpty()) {
			// Cancella la vecchia immagine
			String vecchiaImmagine = auto.getImmagine();
			if (vecchiaImmagine != null && !vecchiaImmagine.isEmpty()) {
				File fileVecchiaImmagine = new File("src/main/resources/static/" + vecchiaImmagine);
				if (fileVecchiaImmagine.exists()) {
					fileVecchiaImmagine.delete();
				}
			}
			// Salva la nuova immagine nella directory temporanea
			try {
				String nuovoNomeImmagine = "images/auto/" + immagine.getOriginalFilename();
				File nuovoFileImmagineTemp = new File(System.getProperty("java.io.tmpdir") + "/" + nuovoNomeImmagine);

				// Assicurati che la directory esista
				File directoryTemp = nuovoFileImmagineTemp.getParentFile();
				if (!directoryTemp.exists()) {
					directoryTemp.mkdirs();
				}

				immagine.transferTo(nuovoFileImmagineTemp);

				// Copia l'immagine nella directory static
				File nuovoFileImmagine = new File("src/main/resources/static/" + nuovoNomeImmagine);
				File directory = nuovoFileImmagine.getParentFile();
				if (!directory.exists()) {
					directory.mkdirs();
				}
				Files.copy(nuovoFileImmagineTemp.toPath(), nuovoFileImmagine.toPath(), StandardCopyOption.REPLACE_EXISTING);

				auto.setImmagine(nuovoNomeImmagine);
			} catch (IOException e) {
				e.printStackTrace();
				return "redirect:/auto";
			}
		}

		List<String> specifiche = new ArrayList<>();
		List<String> optional = new ArrayList<>();
		int i = 0;
		while (allParams.containsKey("specifiche[" + i + "]")) {
			specifiche.add(allParams.get("specifiche[" + i + "]"));
			i++;
		}
		i = 0;
		while (allParams.containsKey("optional[" + i + "]")) {
			optional.add(allParams.get("optional[" + i + "]"));
			i++;
		}

		// Aggiorna le collezioni mantenendo quelle originali
		auto.getSpecifiche().clear();
		auto.getSpecifiche().addAll(specifiche);
		auto.getOptional().clear();
		auto.getOptional().addAll(optional);

		autoService.save(auto);

		// Reindirizza esclusivamente alla pagina precedente
		return "redirect:" + referer;
	}


	@GetMapping("/concessionario/mieAuto")
	public String showLeMieAuto(Model model) {
		model.addAttribute("auto", autoService.findAll());
		return "concessionario/managementMyAuto";
	}
}
