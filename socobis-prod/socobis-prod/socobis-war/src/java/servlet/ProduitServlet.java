package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import fabrication.Fabrication;
import produits.IngredientsLib;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/produit")
public class ProduitServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            IngredientsLib ingredientsLib = new IngredientsLib();
            ingredientsLib.setNomTable("AS_INGREDIENTS_PRODUIT_FINIE");
            IngredientsLib[] of = (IngredientsLib []) CGenUtil.rechercher(ingredientsLib,null,null,null,"");

            Gson gson = new Gson();

            String jsonResponse = gson.toJson(of);
            System.out.println(jsonResponse);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
