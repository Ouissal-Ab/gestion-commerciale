package org.example.dto;

import java.math.BigDecimal;

public class VenteParCategorie {

    private final String categorie;
    private final BigDecimal chiffreAffaires;

    public VenteParCategorie(String categorie, BigDecimal chiffreAffaires) {
        this.categorie = categorie;
        this.chiffreAffaires = chiffreAffaires == null ? BigDecimal.ZERO : chiffreAffaires;
    }

    public String getCategorie() { return categorie; }
    public BigDecimal getChiffreAffaires() { return chiffreAffaires; }

    @Override
    public String toString() {
        return categorie + " : " + chiffreAffaires;
    }
}