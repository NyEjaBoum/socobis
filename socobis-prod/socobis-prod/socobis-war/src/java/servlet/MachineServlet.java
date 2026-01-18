package servlet;

import bean.CGenUtil;
import com.google.gson.Gson;
import machine.Machine;
import produits.IngredientsLib;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/machine/list")
public class MachineServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Machine t = new Machine();
            t.setNomTable("MACHINE");
            Machine[] of = (Machine[]) CGenUtil.rechercher(t,null,null,null,"");

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
