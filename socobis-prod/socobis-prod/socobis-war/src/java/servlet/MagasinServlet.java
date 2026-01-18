package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import fabrication.Fabrication;
import magasin.Magasin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/magasin")
public class MagasinServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            Magasin magasin = new Magasin();
            magasin.setNomTable("magasin2");
            Magasin[] of = (Magasin[]) CGenUtil.rechercher(magasin,null,null,null,"");

            Gson gson = new Gson();

            String jsonResponse = gson.toJson(of);
            System.out.println(jsonResponse);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(jsonResponse);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
