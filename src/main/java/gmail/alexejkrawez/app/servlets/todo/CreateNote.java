package gmail.alexejkrawez.app.servlets.todo;

import gmail.alexejkrawez.app.entities.NoteDAO;
import gmail.alexejkrawez.app.model.Note;
import gmail.alexejkrawez.app.model.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.Charsets;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static gmail.alexejkrawez.app.entities.ConnectionDAO.logger;
import static java.lang.Integer.parseInt;

public class CreateNote extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setSizeThreshold(1024 * 1024 * 2); // буфер данных в байтах

        File tempDir = new File("/webapp/");
        factory.setRepository(tempDir);

        ServletFileUpload upload = new ServletFileUpload(factory); // загрузчик
        upload.setSizeMax(1024 * 1024 * 10); // файл максимум 10Mb

        JSONObject jsonObj = new JSONObject();
        String text = null;
        String status = "1";
        String fileName = null;
        boolean down;

        try {
            List items = upload.parseRequest(req);
            System.out.println(items);
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                System.out.println(item);

                if (item.isFormField()) {
                    jsonObj = (JSONObject) JSONValue.parseWithException(item.getString(StandardCharsets.UTF_8.name()));

                    text = jsonObj.get("text").toString();
                    status = jsonObj.get("status").toString();

                    if (text.equals("")) {
                        text = null;
                    } else {
                        text = text.replaceAll("\n", "#!");
                    }

                    jsonObj.clear();
                } else {
                    fileName = processUploadingFile(user.getUserId(), item);
                }
            }

        } catch (FileUploadBase.SizeLimitExceededException e) {
            //TODO сформировать запрос
            return;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (fileName == null) {
            down = NoteDAO.createNote(user.getUserId(), text, parseInt(status));
        } else {
            down = NoteDAO.createNoteWithFile(user.getUserId(), text, fileName, parseInt(status));
        }

        if (down) {
            Note note = NoteDAO.getLastNote(user.getUserId());
            if (note == null) {
                jsonObj.put("note", null);
            } else {
                List<Note> notes = (List<Note>) session.getAttribute("notes");
                notes.add(note);
                session.setAttribute("notes", notes);
                jsonObj.put("note", note);
            }

        } else {
            jsonObj.put("note", null);
        }

        try (PrintWriter writer = resp.getWriter()) {
            jsonObj.writeJSONString(writer);
        } catch (NullPointerException | IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    private String processUploadingFile(int user_id, FileItem item) throws Exception {

        Path path = Paths.get(getServletContext().getRealPath(new StringBuilder().append(File.separator)
                .append("usersFiles").append(File.separator).append(user_id).toString()));

        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }

        int random = (int) (Math.random() * 10000);
        if (random <= 999) {
            random += 999;
        }

        String fileName = random + item.getName();

        File downloading = new File(path + File.separator + fileName);
        item.write(downloading);

        return fileName;
    }

}




