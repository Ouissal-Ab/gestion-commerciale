package org.example;

import org.example.dto.VenteParCategorie;
import org.example.entities.*;
import org.example.service.CommandeService;
import org.example.service.CrudService;
import org.example.service.ProduitService;
import org.example.service.RapportService;
import org.example.util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class App {

    public static void main(String[] args) {
        try {
            CrudService<Categorie, Long> catService = new CrudService<>(Categorie.class);
            ProduitService produitService = new ProduitService();
            CommandeService commandeService = new CommandeService();
            CrudService<User, Long> userService = new CrudService<>(User.class);

            if (catService.getAll().isEmpty()) {
                System.out.println("\n=== Catégories ===");
                Categorie electronique = new Categorie("Électronique");
                Categorie informatique = new Categorie("Informatique");
                Categorie papeterie = new Categorie("Papeterie");
                catService.create(electronique);
                catService.create(informatique);
                catService.create(papeterie);
                System.out.println("Catégorie créée avec l'ID : " + electronique.getId());

                System.out.println("\n=== Produits (ManyToOne) ===");
                Produit casque = new Produit("Casque Bluetooth", new BigDecimal("79.90"), electronique);
                Produit clavier = new Produit("Clavier mécanique", new BigDecimal("49.99"), informatique);
                Produit souris = new Produit("Souris sans fil", new BigDecimal("25.00"), informatique);
                Produit stylo = new Produit("Stylo", new BigDecimal("1.20"), papeterie);
                for (Produit p : List.of(casque, clavier, souris, stylo)) {
                    produitService.create(p);
                }

                System.out.println("\n=== Héritage JOINED ===");
                userService.create(new Client("Amina Benali"));
                userService.create(new Fournisseur("TechDistrib", "Casablanca"));

                System.out.println("\n=== Commandes (N:N, clé composée) ===");
                LocalDate moisDernier = YearMonth.now().minusMonths(1).atDay(15);

                Commande c1 = new Commande();
                c1.setDateCommande(moisDernier);
                c1.addProduit(clavier, 3);
                c1.addProduit(casque, 2);
                commandeService.create(c1);

                Commande c2 = new Commande();
                c2.setDateCommande(moisDernier.plusDays(5));
                c2.addProduit(souris, 4);
                c2.addProduit(stylo, 50);
                commandeService.create(c2);

                Commande c3 = new Commande(); // ce mois-ci : hors rapport
                c3.setDateCommande(LocalDate.now());
                c3.addProduit(casque, 10);
                commandeService.create(c3);

                System.out.println("Commandes créées : " + c1.getId() + ", " + c2.getId() + ", " + c3.getId());
            }

            System.out.println("\n=== Named Queries & Criteria ===");
            System.out.println("Recherche 'cla' : " + produitService.searchByLibelle("cla"));
            System.out.println("Prix 20-60 : " + produitService.search(null, new BigDecimal("20"), new BigDecimal("60")));

            System.out.println("\n=== Pagination ===");
            System.out.println("Page 1 : " + produitService.findPage(1, 2));
            System.out.println("Page 2 : " + produitService.findPage(2, 2));
            System.out.println("Utilisateurs : " + userService.getAll());

            System.out.println("\n=== JOIN FETCH ===");
            Produit p = produitService.getByIdWithCategorie(produitService.findPage(1, 1).get(0).getId());
            System.out.println(p + " -> " + p.getCategorie().getNom());

            System.out.println("\n=== Rapport du mois précédent ===");
            List<VenteParCategorie> rapport = new RapportService().rapportMoisPrecedent();
            if (rapport.isEmpty()) {
                System.out.println("Aucune vente le mois précédent.");
            } else {
                rapport.forEach(System.out::println);
            }
        } finally {
            HibernateUtil.shutdown();
        }
    }
}