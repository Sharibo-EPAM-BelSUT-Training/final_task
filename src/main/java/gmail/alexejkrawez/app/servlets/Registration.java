package gmail.alexejkrawez.app.servlets;

import gmail.alexejkrawez.app.entities.UserDAO;
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

public class Registration extends HttpServlet {

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
        try {
            jsonObj = (JSONObject) JSONValue.parseWithException(sb.toString());
            String email = jsonObj.get("email").toString();
            String login = jsonObj.get("login").toString();
            String password = jsonObj.get("password").toString();
            String password2 = jsonObj.get("password2").toString();
            jsonObj.clear();

            if (login.length() < 6 | login.length() > 30) {
                jsonObj.put("length", false);
            } else {
                jsonObj.put("length", true);
            }

            if (password.length() < 8 | password.length() > 30) {
                jsonObj.put("length", false);
            } else {
                jsonObj.put("length", true);
            }

            boolean validEmail = email.matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
            boolean validLogin = login.matches("^[a-zA-Z0-9_.-]+$");
            boolean validPassword = password.matches("^[a-zA-Z0-9]+$");
            boolean validPassword2 = password2.matches("^[a-zA-Z0-9]+$");

            jsonObj.put("validEmail", validEmail);
            jsonObj.put("validLogin", validLogin);
            jsonObj.put("validPassword", validPassword);
            jsonObj.put("validPassword2", validPassword2);


            int dbResp = -1;
            if (password.equals(password2)) {
                jsonObj.put("passwordsEqual", true);

                if (validEmail & validLogin & validPassword & validPassword2) {
                    dbResp = UserDAO.createUser(email, login, password);
                }
            } else {
                jsonObj.put("passwordEqual", false);
            }

            jsonObj.put("resp", dbResp);

        } catch (ParseException | NullPointerException e) {
            logger.error(e.getMessage(), e);
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