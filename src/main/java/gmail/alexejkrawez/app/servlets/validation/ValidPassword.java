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

public class ValidPassword extends HttpServlet {

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
        String password = null;
        try {
            jsonObj = (JSONObject) JSONValue.parseWithException(sb.toString());
            password = jsonObj.get("password").toString();

            jsonObj.clear();

            boolean valid = password.matches("^[a-zA-Z0-9]+$");
            jsonObj.put("validation", valid);
        } catch (ParseException | NullPointerException e) {
            logger.error(e.getMessage(), e);
        }

        if (password != null) {
            if (password.length() < 8 | password.length() > 30) {
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

        try (PrintWriter writer = resp.getWriter()) {
            jsonObj.writeJSONString(writer);
        } catch (NullPointerException | IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}
