package it.uniroma3.siw.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String immagine;
    private String descrizione;

    @ManyToOne
    @JoinColumn(name = "concessionario_id")
    private Concessionario concessionario;

    @ElementCollection
    private List<String> specifiche;

    @ElementCollection
    private List<String> optional;

    // Costruttori
    public Auto() {
        this.specifiche = new ArrayList<>();
        this.optional = new ArrayList<>();
    }

    public Auto(String nome, String immagine, String descrizione, Concessionario concessionario) {
        this.nome = nome;
        this.immagine = immagine;
        this.descrizione = descrizione;
        this.concessionario = concessionario;
        this.specifiche = new ArrayList<>();
        this.optional = new ArrayList<>();
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

    public Concessionario getConcessionario() {
        return concessionario;
    }

    public void setConcessionario(Concessionario concessionario) {
        this.concessionario = concessionario;
    }

    public List<String> getSpecifiche() {
        return specifiche;
    }

    public void setSpecifiche(List<String> specifiche) {
        this.specifiche = specifiche;
    }

    public List<String> getOptional() {
        return optional;
    }

    public void setOptional(List<String> optional) {
        this.optional = optional;
    }

    // Metodi per aggiungere e rimuovere elementi dalle liste
    public void aggiungiSpecifica(String specifica) {
        this.specifiche.add(specifica);
    }

    public void rimuoviSpecifica(String specifica) {
        this.specifiche.remove(specifica);
    }

    public void aggiungiOptional(String optional) {
        this.optional.add(optional);
    }

    public void rimuoviOptional(String optional) {
        this.optional.remove(optional);
    }
}
