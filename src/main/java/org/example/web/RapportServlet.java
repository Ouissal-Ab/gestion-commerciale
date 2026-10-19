package org.example.web;

import org.example.dto.VenteParCategorie;
import org.example.service.RapportService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

/** GET /rapport, /rapport?mois=2026-09, /rapport?format=json */
@WebServlet("/rapport")
public class RapportServlet extends HttpServlet {

    private final RapportService rapportService = new RapportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        YearMonth mois;
        String moisParam = req.getParameter("mois");
        try {
            mois = (moisParam == null || moisParam.isBlank())
                    ? YearMonth.now().minusMonths(1)
                    : YearMonth.parse(moisParam);
        } catch (DateTimeParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format attendu : AAAA-MM");
            return;
        }

        List<VenteParCategorie> lignes = rapportService.rapport(mois);
        BigDecimal total = lignes.stream()
                .map(VenteParCategorie::getChiffreAffaires)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        if ("json".equalsIgnoreCase(req.getParameter("format"))) {
            resp.setContentType("application/json");
            StringBuilder sb = new StringBuilder("{\"mois\":\"" + mois + "\",\"lignes\":[");
            for (int i = 0; i < lignes.size(); i++) {
                VenteParCategorie v = lignes.get(i);
                if (i > 0) sb.append(',');
                sb.append("{\"categorie\":\"").append(v.getCategorie().replace("\"", "\\\""))
                        .append("\",\"chiffreAffaires\":").append(v.getChiffreAffaires().toPlainString()).append('}');
            }
            sb.append("],\"total\":").append(total.toPlainString()).append('}');
            out.write(sb.toString());
            return;
        }

        resp.setContentType("text/html");
        out.println("<!doctype html><html lang='fr'><head><meta charset='utf-8'><title>Rapport</title>");
        out.println("<style>body{font-family:sans-serif;margin:2rem}table{border-collapse:collapse}"
                + "td,th{border:1px solid #ccc;padding:.4rem .8rem}</style></head><body>");
        out.println("<h1>Chiffre d'affaires par catégorie — " + mois + "</h1>");
        if (lignes.isEmpty()) {
            out.println("<p>Aucune vente sur cette période.</p>");
        } else {
            out.println("<table><tr><th>Catégorie</th><th>Chiffre d'affaires</th></tr>");
            for (VenteParCategorie v : lignes) {
                out.println("<tr><td>" + v.getCategorie().replace("<", "&lt;") + "</td><td>"
                        + v.getChiffreAffaires() + "</td></tr>");
            }
            out.println("<tr><th>Total</th><th>" + total + "</th></tr></table>");
        }
        out.println("</body></html>");
    }
}