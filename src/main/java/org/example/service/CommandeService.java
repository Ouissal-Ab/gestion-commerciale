package org.example.service;

import org.example.dao.CommandeDAO;
import org.example.entities.Commande;

public class CommandeService extends CrudService<Commande, Long> {

    public CommandeService() {
        super(new CommandeDAO());
    }

    @Override
    public void create(Commande commande) {
        requireNonNull(commande);
        if (commande.getLignes().isEmpty()) {
            throw new IllegalArgumentException("Une commande doit contenir au moins un produit");
        }
        super.create(commande);
    }
}