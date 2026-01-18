// ValidateObjectServlet.java (plus générique)
package servlet;

import bean.ClassMAPTable;
import user.UserEJB;
import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Logger;

import com.google.gson.Gson;

@WebServlet("/api/object/validate")
public class ValidateObjectServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ValidateObjectServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Connection c = null;
        try {
            // Récupérer les paramètres
            String objectId = req.getParameter("id");
            String className = req.getParameter("className");
            String nomTable = req.getParameter("nomTable");

            if (objectId == null || objectId.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID de l'objet manquant\"}");
                return;
            }

            if (className == null || className.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Nom de la classe manquant\"}");
                return;
            }

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

            // Utiliser le code fourni pour valider l'objet
            ClassMAPTable t = (ClassMAPTable) (Class.forName(className).newInstance());
            t.setValChamp(t.getAttributIDName(), objectId);
            if (nomTable != null && !nomTable.isEmpty()) {
                t.setNomTable(nomTable);
            }

            ClassMAPTable validatedObject = (ClassMAPTable) u.validerObject(t);

            // Valider la transaction
            c.commit();

            // Retourner la réponse
            Gson gson = new Gson();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Success EHHH");

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