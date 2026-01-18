// CreateFabricationServlet.java
package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import fabrication.Fabrication;
import fabrication.FabricationFille;
import historique.MapUtilisateur;
import servlet.dto.FabricationFilleRequestDTO;
import servlet.dto.FabricationRequestDTO;
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

@WebServlet("/api/fabrication/create")
public class CreateFabricationServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CreateFabricationServlet.class.getName());

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
            FabricationRequestDTO fabRequest = gson.fromJson(sb.toString(), FabricationRequestDTO.class);

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

            // 1. Créer la Fabrication (mere)
            Fabrication fabrication = new Fabrication();
            fabrication.setCible(fabRequest.getCible());
            fabrication.setRemarque(fabRequest.getRemarque());
            fabrication.setLibelle(fabRequest.getLibelle());
            fabrication.setIdBc(fabRequest.getIdBc());
            fabrication.setBesoin(Date.valueOf(fabRequest.getBesoin()));
            fabrication.setDaty(Date.valueOf(fabRequest.getDaty()));
            fabrication.setLancePar(fabRequest.getLancePar());
            fabrication.setIdOf(fabRequest.getIdOf());
            fabrication.setIdOffille(fabRequest.getIdOffille());
            fabrication.setFabricationPrec(fabRequest.getFabricationPrec());
            fabrication.setFabricationSuiv(fabRequest.getFabricationSuiv());
            fabrication.setEquipe(fabRequest.getEquipe());
            if (fabRequest.getNbPetris() != null) {
                fabrication.setNbPetris(fabRequest.getNbPetris());
            }

            // Générer l'ID pour la Fabrication
            fabrication.construirePK(c);

            // 2. Créer les FabricationFille
            FabricationFille[] fabFilles = new FabricationFille[fabRequest.getProduits().size()];
            for (int i = 0; i < fabRequest.getProduits().size(); i++) {
                FabricationFilleRequestDTO filleDTO = fabRequest.getProduits().get(i);

                FabricationFille fabFille = new FabricationFille();
                fabFille.setIdIngredients(filleDTO.getIdIngredients());
                fabFille.setRemarque(filleDTO.getRemarque());
                fabFille.setLibelle(filleDTO.getLibelle());
                fabFille.setIdunite(filleDTO.getIdunite());
                fabFille.setQte(filleDTO.getQte());
                fabFille.setIdBcFille(filleDTO.getIdBcFille());
                fabFille.setIdMachine(filleDTO.getIdMachine());
                fabFille.setOperateur(filleDTO.getOperateur());
//                fabFille.setNiveau(filleDTO.getNiveau());
//                fabFille.setPu(filleDTO.getPu());

                // Générer l'ID pour la FabricationFille
                fabFille.construirePK(c);
                fabFille.setIdMere(fabrication.getId());

                fabFilles[i] = fabFille;
            }

            // 3. Sauvegarder la fabrication et ses filles
            fabrication.insertToTableWithHisto(u.getUser().getTuppleID(), c);
            for (FabricationFille fabFille : fabFilles) {
                fabFille.createObject(u.getUser().getTuppleID(), c);
            }

            // Valider la transaction
            c.commit();

            // Retourner la réponse
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(fabrication));

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