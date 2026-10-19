package org.example.dao;

import org.example.entities.Produit;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO extends GenericDAO<Produit, Long> {

    public ProduitDAO() {
        super(Produit.class);
    }

    public List<Produit> findByCategorie(Long catId) {
        return executeRead(em -> em.createNamedQuery("Produit.findByCategorie", Produit.class)
                .setParameter("catId", catId)
                .getResultList());
    }

    public List<Produit> searchByLibelle(String term) {
        return executeRead(em -> em.createNamedQuery("Produit.searchByLibelle", Produit.class)
                .setParameter("term", term)
                .getResultList());
    }

    public Produit findByIdWithCategorie(Long id) {
        return executeRead(em -> {
            try {
                return em.createNamedQuery("Produit.findByIdWithCategorie", Produit.class)
                        .setParameter("id", id)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }

    public List<Produit> search(String term, BigDecimal minPrix, BigDecimal maxPrix) {
        return executeRead(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Produit> cq = cb.createQuery(Produit.class);
            Root<Produit> root = cq.from(Produit.class);

            List<Predicate> predicates = new ArrayList<>();
            if (term != null && !term.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get("libelle")), "%" + term.toLowerCase() + "%"));
            }
            if (minPrix != null) {
                predicates.add(cb.ge(root.<BigDecimal>get("prix"), minPrix));
            }
            if (maxPrix != null) {
                predicates.add(cb.le(root.<BigDecimal>get("prix"), maxPrix));
            }
            cq.select(root).where(predicates.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        });
    }

    public List<Produit> findPage(int pageNumber, int pageSize) {
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("pageNumber et pageSize doivent être >= 1");
        }
        return executeRead(em -> em.createQuery("SELECT p FROM Produit p ORDER BY p.id", Produit.class)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList());
    }

    public long count() {
        return executeRead(em -> em.createQuery("SELECT COUNT(p) FROM Produit p", Long.class).getSingleResult());
    }
}