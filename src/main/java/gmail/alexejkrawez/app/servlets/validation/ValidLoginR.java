package gmail.alexejkrawez.app.servlets.validation;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static gmail.alexejkrawez.app.entities.ConnectionDAO.logger;
import static gmail.alexejkrawez.app.entities.UserDAO.isLoginExist;

public class ValidLoginR extends HttpServlet {

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
        try {
            jsonObj = (JSONObject) JSONValue.parseWithException(sb.toString());
            login = jsonObj.get("login").toString();

            jsonObj.clear();

            boolean valid = login.matches("^[a-zA-Z0-9_.-]+$");
            jsonObj.put("validation", valid);
        } catch (ParseException | NullPointerException e) {
            logger.error(e.getMessage(), e);
        }

        if (login != null) {
            if (login.length() < 6 | login.length() > 30) {
                jsonObj.put("length", false);

                try (PrintWriter writer = resp.getWriter()) {
                    jsonObj.writeJSONString(writer);
                } catch (NullPointerException | IOException e) {
                    logger.error(e.getMessage(), e);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                jsonObj.put("length", true);
            }

        } else {
            jsonObj.put("length", false);
        }

        String exists = isLoginExist(login);
        if (exists != null) {
            jsonObj.put("exists", true);
        } else {
            jsonObj.put("exists", false);
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