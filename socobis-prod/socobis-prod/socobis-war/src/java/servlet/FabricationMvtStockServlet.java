// FabricationMvtStockServlet.java
package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import fabrication.Fabrication;
import stock.MvtStock;
import stock.MvtStockEntreeAvecReste;
import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/api/fabrication/mvt-stock")
public class FabricationMvtStockServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Connection c = null;
        try {
            String fabricationId = req.getParameter("idFab");
            String typeMvtStock = req.getParameter("typeMvtStock");
            String idMagasin = req.getParameter("idMagasin");

            if (fabricationId == null || fabricationId.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID de fabrication manquant\"}");
                return;
            }

            c = new UtilDB().GetConn();

            Fabrication fab = new Fabrication();
            fab.setId(fabricationId);

            MvtStock mvtStock;
            if (typeMvtStock != null && !typeMvtStock.isEmpty()) {
                if (idMagasin != null && !idMagasin.isEmpty()) {
                    mvtStock = fab.genererMvtStock(idMagasin, typeMvtStock, c);
                } else {
                    mvtStock = fab.genererMvtStock(typeMvtStock, c);
                }
            } else {
                mvtStock = fab.genererMvtStock(c);
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(mvtStock);

            resp.getWriter().write(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
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
}