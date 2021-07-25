package gmail.alexejkrawez.app.servlets;

import gmail.alexejkrawez.app.entities.NoteDAO;
import gmail.alexejkrawez.app.entities.UserDAO;
import gmail.alexejkrawez.app.model.Note;
import gmail.alexejkrawez.app.model.User;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static gmail.alexejkrawez.app.entities.ConnectionDAO.logger;

public class Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        BufferedReader br = req.getReader();
        String str;

        while ((str = br.readLine()) != null) {
            sb.append(str);
        }

        JSONObject jsonObj = new JSONObject();
        String login = null;
        String password = null;
        try {
            jsonObj = (JSONObject) JSONValue.parseWithException(sb.toString());
            login = jsonObj.get("login").toString();
            password = jsonObj.get("password").toString();

            jsonObj.clear();
        } catch (ParseException | NullPointerException e) {
            logger.error(e.getMessage(), e);
        }

        User user = UserDAO.getUser(login, password);

        if (user == null) {
            jsonObj.put("user", false);
        } else {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            jsonObj.put("user", true);
            jsonObj.put("sessionId", session.getId());

            boolean status = NoteDAO.updateStatusByDate(user.getUserId());

            if (status) {
                List<Note> notes = NoteDAO.getNotesOrderDate(user.getUserId());
                session.setAttribute("notes", notes);
                session.setAttribute("list", 1);
            } else {
                logger.warn("Notes update failed.");
            }
        }

        try (PrintWriter writer = resp.getWriter()) {
            jsonObj.writeJSONString(writer);

        } catch (NullPointerException | IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
