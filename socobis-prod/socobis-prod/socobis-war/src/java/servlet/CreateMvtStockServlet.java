// CreateMvtStockServlet.java
package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import stock.MvtStock;
import stock.MvtStockFille;
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

import servlet.dto.MvtStockRequestDTO;
import servlet.dto.MvtStockFilleRequestDTO;

@WebServlet("/api/mvt-stock/create")
public class CreateMvtStockServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CreateMvtStockServlet.class.getName());

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
            MvtStockRequestDTO mvtRequest = gson.fromJson(sb.toString(), MvtStockRequestDTO.class);

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

            // 1. Créer le MvtStock (mere)
            MvtStock mvtStock = new MvtStock();
            mvtStock.setDesignation(mvtRequest.getDesignation());
            mvtStock.setIdMagasin(mvtRequest.getIdMagasin());
            mvtStock.setIdPoint(mvtRequest.getIdPoint());
            mvtStock.setIdTypeMvStock(mvtRequest.getIdTypeMvStock());
            mvtStock.setIdobjet(mvtRequest.getIdobjet());
            mvtStock.setFabPrecedent(mvtRequest.getFabPrecedent());
            mvtStock.setDaty(Date.valueOf(mvtRequest.getDaty()));

            // Générer l'ID pour le MvtStock
            mvtStock.construirePK(c);

            // 2. Créer les MvtStockFille
            MvtStockFille[] mvtFilles = new MvtStockFille[mvtRequest.getProduits().size()];
            for (int i = 0; i < mvtRequest.getProduits().size(); i++) {
                MvtStockFilleRequestDTO filleDTO = mvtRequest.getProduits().get(i);

                MvtStockFille mvtFille = new MvtStockFille();
                mvtFille.setIdProduit(filleDTO.getIdProduit());
                mvtFille.setDesignation(filleDTO.getDesignation());
                mvtFille.setEntree(filleDTO.getEntree());
                mvtFille.setSortie(filleDTO.getSortie());
                mvtFille.setPu(filleDTO.getPu());
                mvtFille.setMvtSrc(filleDTO.getMvtSrc());

                // Générer l'ID pour le MvtStockFille
                mvtFille.construirePK(c);
                mvtFille.setIdMvtStock(mvtStock.getId());

                mvtFilles[i] = mvtFille;
            }

            // 3. Sauvegarder le mouvement de stock et ses filles
            mvtStock.createObject(u.getUser().getTuppleID(),c);
            System.out.println("mvtStock created"+mvtStock.getTuppleID());
            for(MvtStockFille mvtFille : mvtFilles) {
                mvtFille.setIdMvtStock(mvtStock.getTuppleID());
                mvtFille.setMvtSrc(mvtStock.getTuppleID());
                mvtFille.createObject(u.getUser().getTuppleID(),c);
            }

            // Valider la transaction
            c.commit();

            // Retourner la réponse
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(mvtStock));

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