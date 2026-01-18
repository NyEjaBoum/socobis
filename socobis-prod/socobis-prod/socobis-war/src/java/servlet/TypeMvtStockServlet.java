// TypeMvtStockServlet.java
package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import stock.TypeMvtStock;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/type-mvt-stock")
public class TypeMvtStockServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            TypeMvtStock typeMvt = new TypeMvtStock();
            typeMvt.setNomTable("TYPEMVTSTOCK");
            TypeMvtStock[] types = (TypeMvtStock[]) CGenUtil.rechercher(typeMvt, null, null, null, "");

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(types);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}