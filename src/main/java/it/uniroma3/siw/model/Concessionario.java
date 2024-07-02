package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Concessionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String amministratore;
    private LocalDate dataFondazione;
    private String email;
    private String immagine;
    private String descrizione; // Nuovo campo descrizione

    @OneToMany(mappedBy = "concessionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auto> auto = new ArrayList<>();
    
	@OneToOne
	private Credenziali credenziali;

    // Costruttori
    public Concessionario() {}

    public Concessionario(String nome, String amministratore, LocalDate dataFondazione, String immagine, String descrizione) {
        this.nome = nome;
        this.amministratore = amministratore;
        this.dataFondazione = dataFondazione;
        this.immagine = immagine;
        this.descrizione = descrizione;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAmministratore() {
        return amministratore;
    }

    public void setAmministratore(String amministratore) {
        this.amministratore = amministratore;
    }

    public LocalDate getDataFondazione() {
        return dataFondazione;
    }

    public void setDataFondazione(LocalDate localDate) {
        this.dataFondazione = localDate;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<Auto> getAuto() {
        return auto;
    }

    public void setAuto(List<Auto> auto) {
        this.auto = auto;
    }

    // Metodi di utilità
    public void addAuto(Auto auto) {
        this.auto.add(auto);
        auto.setConcessionario(this);
    }

    public void removeAuto(Auto auto) {
        this.auto.remove(auto);
        auto.setConcessionario(null);
    }

	public void setCredenziali(Credenziali credenziali2) {
		this.credenziali=credenziali2;
		
	}
	
	public void setEmail(String email2) {
		this.email=email2;
		
	}

	public String getEmail() {
		return email;
	}

	public Credenziali getCredenziali() {
		return credenziali;
	}


}
