package org.example;



import org.example.dto.VenteParCategorie;
import org.example.entities.*;
import org.example.service.CrudService;
import org.example.service.ProduitService;
import org.example.service.RapportService;
import org.junit.Test;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.junit.Assert.*;

public class RapportTest {

    @Test
    public void testChiffreAffairesParCategorie() {
        CrudService<Categorie, Long> catService = new CrudService<>(Categorie.class);
        ProduitService prodService = new ProduitService();
        CrudService<Commande, Long> commandeService = new CrudService<>(Commande.class);

        Categorie info = new Categorie("Rapport-Info");
        Categorie pap = new Categorie("Rapport-Pap");
        catService.create(info);
        catService.create(pap);

        Produit clavier = new Produit("Clavier", new BigDecimal("50.00"), info);
        Produit souris = new Produit("Souris", new BigDecimal("20.00"), info);
        Produit stylo = new Produit("Stylo", new BigDecimal("1.50"), pap);
        prodService.create(clavier);
        prodService.create(souris);
        prodService.create(stylo);

        YearMonth mois = YearMonth.of(2020, 1);

        Commande c1 = new Commande();
        c1.setDateCommande(mois.atDay(10));
        c1.addProduit(clavier, 2);   // 100
        c1.addProduit(stylo, 10);    // 15
        commandeService.create(c1);

        Commande c2 = new Commande();
        c2.setDateCommande(mois.atEndOfMonth());
        c2.addProduit(souris, 3);    // 60
        commandeService.create(c2);

        Commande horsPeriode = new Commande();
        horsPeriode.setDateCommande(mois.plusMonths(1).atDay(1));
        horsPeriode.addProduit(clavier, 100);
        commandeService.create(horsPeriode);

        List<VenteParCategorie> rapport = new RapportService().rapport(mois);

        assertEquals(2, rapport.size());
        assertEquals("Rapport-Info", rapport.get(0).getCategorie());
        assertEquals(0, new BigDecimal("160").compareTo(rapport.get(0).getChiffreAffaires()));
        assertEquals("Rapport-Pap", rapport.get(1).getCategorie());
        assertEquals(0, new BigDecimal("15").compareTo(rapport.get(1).getChiffreAffaires()));
    }

    @Test
    public void testHeritageJoined() {
        CrudService<User, Long> userService = new CrudService<>(User.class);
        userService.create(new Client("Client-heritage"));
        userService.create(new Fournisseur("Fournisseur-heritage", "Rabat"));

        List<User> users = userService.getAll();
        assertTrue(users.stream().anyMatch(u -> u instanceof Client));
        assertTrue(users.stream().anyMatch(u -> u instanceof Fournisseur));
    }
}