package org.example.service;

import org.example.dao.RapportDAO;
import org.example.dto.VenteParCategorie;

import java.time.YearMonth;
import java.util.List;

public class RapportService {

    private final RapportDAO rapportDAO = new RapportDAO();

    public List<VenteParCategorie> rapportMoisPrecedent() {
        return rapportDAO.chiffreAffairesMoisPrecedent();
    }

    public List<VenteParCategorie> rapport(YearMonth mois) {
        if (mois == null) throw new IllegalArgumentException("Le mois est obligatoire");
        return rapportDAO.chiffreAffairesParCategorie(mois);
    }
}