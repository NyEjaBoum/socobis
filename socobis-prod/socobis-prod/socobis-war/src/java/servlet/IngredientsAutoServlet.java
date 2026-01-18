// IngredientsAutoServlet.java
package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import produits.IngredientsLib;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/ingredients-auto")
public class IngredientsAutoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            IngredientsLib ingredients = new IngredientsLib();
            ingredients.setNomTable("ST_INGREDIENTSAUTO");
            IngredientsLib[] list = (IngredientsLib[]) CGenUtil.rechercher(ingredients, null, null, null, "");

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(list);

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