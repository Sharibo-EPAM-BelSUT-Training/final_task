package gmail.alexejkrawez.app.servlets.todo;

import gmail.alexejkrawez.app.model.User;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static gmail.alexejkrawez.app.entities.ConnectionDAO.logger;

public class IsSessionValid extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        JSONObject jsonObj = new JSONObject();
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        long difference = -1L;

        if (session != null & req.isRequestedSessionIdValid()) {
            difference = (session.getLastAccessedTime() - session.getCreationTime()) / 1000;
            if (difference > session.getMaxInactiveInterval()) {
                jsonObj.put("status", false);
                session.invalidate();

                logger.info("Session by user " + user.getUserId() + " is invalid.");
            } else {
                difference = -1L;
                jsonObj.put("status", true);
            }
        } else {
            jsonObj.put("status", false);
            session.invalidate();
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