package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import fabrication.Of;
import fabrication.OfFille;
import historique.MapUtilisateur;
import servlet.dto.OfFilleRequestDTO;
import servlet.dto.OfRequestDTO;
import user.UserEJB;
import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.logging.Logger;

@WebServlet("/api/of/create")
public class CreateOfServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CreateOfServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Connection c = null;
        try {
            // Lire le JSON de la requête
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Gson gson = new Gson();
            OfRequestDTO ofRequest = gson.fromJson(sb.toString(), OfRequestDTO.class);

            // Récupérer l'utilisateur depuis la session
            HttpSession session = req.getSession(false);
            if (session == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\": \"Session non authentifiée\"}");
                return;
            }

            UserEJB u = (UserEJB) session.getAttribute("u");
            if (u == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\": \"Utilisateur non authentifié\"}");
                return;
            }

            // Créer la connexion à la base de données
            c = new UtilDB().GetConn();
            c.setAutoCommit(false);

            // 1. Créer l'OF (mere)
            Of of = new Of();
            of.setCible(ofRequest.getCible());
            of.setRemarque(ofRequest.getRemarque());
            of.setLibelle(ofRequest.getLibelle());
            of.setIdBc(ofRequest.getIdBc());
            of.setBesoin(Date.valueOf(ofRequest.getBesoin()));
            of.setDaty(Date.valueOf(ofRequest.getDaty()));
            of.setLancePar(ofRequest.getLancePar());

            // Générer l'ID pour l'OF
            of.construirePK(c);

            // 2. Créer les OfFille
            OfFille[] ofFilles = new OfFille[ofRequest.getProduits().size()];
            for (int i = 0; i < ofRequest.getProduits().size(); i++) {
                OfFilleRequestDTO filleDTO = ofRequest.getProduits().get(i);

                OfFille ofFille = new OfFille();
                ofFille.setIdIngredients(filleDTO.getIdIngredients());
                ofFille.setRemarque(filleDTO.getRemarque());
                ofFille.setLibelle(filleDTO.getLibelle());
                ofFille.setIdunite(filleDTO.getIdunite());
                ofFille.setQte(filleDTO.getQte());
                ofFille.setIdBcFille(filleDTO.getIdBcFille());

                // Générer l'ID pour l'OfFille
                ofFille.construirePK(c);
                ofFille.setIdMere(of.getId());

                ofFilles[i] = ofFille;
            }

            // 3. Appeler la méthode createObjectMultiple
            // Note: Vous devrez peut-être adapter selon votre implémentation UserEJB
            // Si vous n'avez pas accès à UserEJB, utilisez l'approche directe
            of.setOfFilles(ofFilles);

            // Alternative si UserEJB n'est pas accessible :
            // Créer l'OF d'abord
            of.insertToTableWithHisto(u.getUser().getTuppleID(), c);

            // Créer les filles
            for (OfFille ofFille : ofFilles) {
                ofFille.createObject(u.getUser().getTuppleID(), c);
            }

            // Valider la transaction
            c.commit();

            // Retourner la réponse
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(of));

        } catch (Exception e) {
            e.printStackTrace();
            if (c != null) {
                try {
                    c.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");

        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
}