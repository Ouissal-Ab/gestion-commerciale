package org.example.dao;

import org.example.dto.VenteParCategorie;
import org.example.entities.Categorie;
import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;
import org.example.util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


public class RapportDAO {

    public List<VenteParCategorie> chiffreAffairesMoisPrecedent() {
        return chiffreAffairesParCategorie(YearMonth.now().minusMonths(1));
    }

    public List<VenteParCategorie> chiffreAffairesParCategorie(YearMonth mois) {
        LocalDate debut = mois.atDay(1);
        LocalDate fin = mois.atEndOfMonth();

        EntityManager em = HibernateUtil.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<VenteParCategorie> cq = cb.createQuery(VenteParCategorie.class);

            Root<CommandeProduit> ligne = cq.from(CommandeProduit.class);
            Join<CommandeProduit, Commande> commande = ligne.join("commande");
            Join<CommandeProduit, Produit> produit = ligne.join("produit");
            Join<Produit, Categorie> categorie = produit.join("categorie");

            Expression<BigDecimal> prix = produit.get("prix");
            @SuppressWarnings("unchecked")
            Expression<BigDecimal> quantite = (Expression<BigDecimal>) (Expression<?>) ligne.get("quantite");
            Expression<BigDecimal> montantLigne = cb.prod(prix, quantite);
            Expression<BigDecimal> total = cb.sum(montantLigne);

            cq.select(cb.construct(VenteParCategorie.class, categorie.get("nom"), total))
                    .where(cb.between(commande.<LocalDate>get("dateCommande"), debut, fin))
                    .groupBy(categorie.get("id"), categorie.get("nom"))
                    .orderBy(cb.desc(total));

            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }
}