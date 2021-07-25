package gmail.alexejkrawez.app.servlets.todo;

import gmail.alexejkrawez.app.model.Note;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static gmail.alexejkrawez.app.entities.ConnectionDAO.logger;

public class GetNotes extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);

        Integer list = (Integer) session.getAttribute("list");
        List<Note> notes = (List<Note>) session.getAttribute("notes");

        JSONObject jsonObj = new JSONObject();

        if (notes.isEmpty()) {
            jsonObj.put("0", null);
        } else {
            Integer i = 0;
            for (Note note : notes) {
                jsonObj.put(i.toString(), note);
                i++;
            }
        }

        jsonObj.put("list", list.toString());

        try (PrintWriter writer = resp.getWriter()) {
            jsonObj.writeJSONString(writer);

        } catch (NullPointerException | IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

}