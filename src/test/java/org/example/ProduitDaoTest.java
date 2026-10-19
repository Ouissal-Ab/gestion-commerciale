package org.example;

import org.example.entities.Categorie;
import org.example.entities.Produit;
import org.example.service.CrudService;
import org.example.service.ProduitService;
import org.hibernate.LazyInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ProduitDaoTest {

    private static CrudService<Categorie, Long> catService;
    private static ProduitService prodService;

    @BeforeClass
    public static void init() {
        catService = new CrudService<>(Categorie.class);
        prodService = new ProduitService();
    }

    @Test
    public void testCreateProduitAvecCategorie() {
        Categorie cat = new Categorie("Papeterie-test");
        catService.create(cat);
        Produit p = new Produit("Stylo", new BigDecimal("1.20"), cat);
        prodService.create(p);
        assertNotNull(p.getId());

        Produit fetched = prodService.getByIdWithCategorie(p.getId());
        assertEquals("Papeterie-test", fetched.getCategorie().getNom());
    }

    @Test(expected = LazyInitializationException.class)
    public void testLazyHorsSessionLeveUneException() {
        Categorie cat = new Categorie("Lazy-test");
        catService.create(cat);
        Produit p = new Produit("Gomme", new BigDecimal("0.80"), cat);
        prodService.create(p);

        Produit detache = prodService.getById(p.getId());
        detache.getCategorie().getNom();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrixNegatifRefuse() {
        Categorie cat = new Categorie("Validation-test");
        catService.create(cat);
        prodService.create(new Produit("Erreur", new BigDecimal("-5"), cat));
    }

    @Test
    public void testCriteriaEtPagination() {
        Categorie cat = new Categorie("Criteria-test");
        catService.create(cat);
        for (int i = 1; i <= 5; i++) {
            prodService.create(new Produit("Cahier-criteria " + i, new BigDecimal(i * 10), cat));
        }
        assertEquals(3, prodService.search("cahier-criteria", new BigDecimal("20"), new BigDecimal("40")).size());
        assertEquals(5, prodService.findByCategorie(cat.getId()).size());
        assertEquals(2, prodService.findPage(1, 2).size());
    }
}