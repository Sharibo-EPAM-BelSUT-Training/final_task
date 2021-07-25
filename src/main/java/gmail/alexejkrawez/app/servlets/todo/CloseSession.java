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

public class CloseSession extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        JSONObject jsonObj = new JSONObject();

        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        if (session != null & req.isRequestedSessionIdValid()) {
            session.invalidate();
            jsonObj.put("status", true);

            logger.info("Session by user " + user.getUserId() + " close.");
        } else {
            jsonObj.put("status", true);

            logger.warn("Session by user " + user.getUserId() + " close by invalidation.");
        }

        user = null;

        try (PrintWriter writer = resp.getWriter()) {
            jsonObj.writeJSONString(writer);

        } catch (NullPointerException | IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}
