package org.example.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_commande")
    private LocalDate dateCommande;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandeProduit> lignes = new ArrayList<>();

    public Commande() {}

    public void addProduit(Produit p, int quantite) {
        lignes.add(new CommandeProduit(this, p, quantite));
    }

    public Long getId() { return id; }
    public LocalDate getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDate dateCommande) { this.dateCommande = dateCommande; }
    public List<CommandeProduit> getLignes() { return lignes; }

    @Override
    public String toString() {
        return "Commande{id=" + id + ", date=" + dateCommande + "}";
    }
}