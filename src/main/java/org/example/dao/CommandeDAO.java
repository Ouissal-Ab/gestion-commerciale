package org.example.dao;

import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;

public class CommandeDAO extends GenericDAO<Commande, Long> {

    public CommandeDAO() {
        super(Commande.class);
    }

    @Override
    public void create(Commande commande) {
        executeInTransaction(em -> {
            // Remplace chaque produit détaché par une référence gérée par CET EntityManager
            for (CommandeProduit ligne : commande.getLignes()) {
                Long produitId = ligne.getProduit().getId();
                ligne.setProduit(em.getReference(Produit.class, produitId));
            }
            em.persist(commande);
        });
    }
}