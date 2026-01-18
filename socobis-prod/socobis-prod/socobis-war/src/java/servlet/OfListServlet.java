package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import fabrication.Of;
import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Logger;

@WebServlet(name = "OfListServlet", urlPatterns = {"/of"})
public class OfListServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(OfListServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection c = null;
        try {
            // Récupérer les paramètres de filtre
            String dateMin = req.getParameter("dateMin");
            String dateMax = req.getParameter("dateMax");
            String search = req.getParameter("search");
            String etatStr = req.getParameter("etat");
            String pageStr = req.getParameter("page");
            String nppStr = req.getParameter("npp");

            // Valeurs par défaut
            int page = 1;
            int npp = 10;
            Integer etat = null;

            // Date min par défaut = aujourd'hui
            if (dateMin == null || dateMin.isEmpty()) {
                dateMin = new java.sql.Date(System.currentTimeMillis()).toString();
            }
            if(dateMax == null || dateMax.isEmpty()) {
                dateMax = dateMin;
            }

            try {
                if (pageStr != null && !pageStr.isEmpty()) {
                    page = Integer.parseInt(pageStr);
                }
                if (nppStr != null && !nppStr.isEmpty()) {
                    npp = Integer.parseInt(nppStr);
                }
                if (etatStr != null && !etatStr.isEmpty()) {
                    etat = Integer.parseInt(etatStr);
                }
            } catch (NumberFormatException e) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Paramètres numériques invalides");
                return;
            }

            c = new UtilDB().GetConn();

            Of crit = new Of();
            crit.setNomTable("OFABLIB");

            // Construire la clause WHERE
            StringBuilder where = new StringBuilder(" 1=1 ");

            if (dateMin != null && !dateMin.isEmpty()) {
                where.append(" AND TRUNC(daty) >= TO_DATE('").append(dateMin).append("', 'YYYY-MM-DD')");
            }
            if (dateMax != null && !dateMax.isEmpty()) {
                where.append(" AND TRUNC(daty) <= TO_DATE('").append(dateMax).append("', 'YYYY-MM-DD')");
            }
            if (search != null && !search.isEmpty()) {
                where.append(" AND (UPPER(id) LIKE '%").append(search.toUpperCase()).append("%'")
                        .append(" OR UPPER(libelle) LIKE '%").append(search.toUpperCase()).append("%')");
            }
            if (etat != null) {
                where.append(" AND etat = ").append(etat);
            }

            // Calcul de l'offset pour la pagination
            int offset = (page - 1) * npp;

            // Requête avec pagination pour Oracle
            String query = "SELECT * FROM (" +
                    "  SELECT t.*, ROWNUM rnum FROM (" +
                    "    SELECT * FROM OFABLIB WHERE " + where.toString() +
                    "    ORDER BY daty DESC, id DESC" +
                    "  ) t WHERE ROWNUM <= " + (offset + npp) +
                    ") WHERE rnum > " + offset;

            Of[] ofList = (Of[]) CGenUtil.rechercher(crit, query, c);

            // Récupérer le nombre total pour la pagination
//            String countQuery = "SELECT COUNT(*) as total FROM OFABLIB WHERE " + where.toString();
            Integer total = ofList.length;

            // Créer la réponse
            OfResponse response = new OfResponse();
            response.data = ofList;
            response.page = page;
            response.npp = npp;
            response.total = total != null ? total : 0;

            // Convertir en JSON
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur: " + e.getMessage());
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    // Classe interne pour la réponse
    private static class OfResponse {
        Of[] data;
        int page;
        int npp;
        int total;

        // Getters pour Gson
        public Of[] getData() { return data; }
        public int getPage() { return page; }
        public int getNpp() { return npp; }
        public int getTotal() { return total; }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}