package gmail.alexejkrawez.app.entities;

import gmail.alexejkrawez.app.model.Note;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO extends ConnectionDAO {

    private static final String ADD_NOTE = "INSERT INTO notes (user_id, note, status, target_date) " +
        "VALUES (?, ?, ?, CASE " +
        "WHEN status = 1 THEN DATE_FORMAT((CURRENT_TIMESTAMP), '%Y-%m-%d 00:00:00') " +
        "WHEN status = 2 THEN DATE_FORMAT((CURRENT_TIMESTAMP + INTERVAL 1 DAY), '%Y-%m-%d 00:00:00') " +
        "WHEN status = 3 THEN DATE_FORMAT((CURRENT_TIMESTAMP - INTERVAL 1 DAY), '%Y-%m-%d 00:00:00') END);";

    private static final String ADD_NOTE_FILE = "INSERT INTO notes (user_id, note, file_path, status, target_date) " +
            "VALUES (?, ?, ?, ?, CASE " +
            "WHEN status = 1 THEN DATE_FORMAT((CURRENT_TIMESTAMP), '%Y-%m-%d 00:00:00') " +
            "WHEN status = 2 THEN DATE_FORMAT((CURRENT_TIMESTAMP + INTERVAL 1 DAY), '%Y-%m-%d 00:00:00') " +
            "WHEN status = 3 THEN DATE_FORMAT((CURRENT_TIMESTAMP - INTERVAL 1 DAY), '%Y-%m-%d 00:00:00') END);";

    private static final String UPDATE_STATUS_BY_DATE = "UPDATE notes SET notes.status = CASE " +
            "WHEN notes.user_id = ? AND notes.status != 4 AND notes.status != 5 " +
            "AND DATEDIFF(DATE_FORMAT((CURRENT_TIMESTAMP), '%Y-%m-%d 00:00:00'), DATE_FORMAT((notes.target_date), '%Y-%m-%d 00:00:00')) = 0 " +
            "THEN 1 " +
            "WHEN notes.user_id = ? AND notes.status != 4 AND notes.status != 5 " +
            "AND DATEDIFF(DATE_FORMAT((CURRENT_TIMESTAMP), '%Y-%m-%d 00:00:00'), DATE_FORMAT((notes.target_date), '%Y-%m-%d 00:00:00')) = -1 " +
            "THEN 2 " +
            "WHEN notes.user_id = ? AND notes.status != 4 AND notes.status != 5 " +
            "AND DATEDIFF(DATE_FORMAT((CURRENT_TIMESTAMP), '%Y-%m-%d 00:00:00'), DATE_FORMAT((notes.target_date), '%Y-%m-%d 00:00:00')) != 0 " +
            "AND DATEDIFF(DATE_FORMAT((CURRENT_TIMESTAMP), '%Y-%m-%d 00:00:00'), DATE_FORMAT((notes.target_date), '%Y-%m-%d 00:00:00')) != -1 " +
            "THEN 3 " +
            "ELSE notes.status END " +
            "ORDER BY notes.date;";

    private static final String UPDATE_STATUS_OK = "UPDATE notes SET notes.status = 5, notes.date = CURRENT_TIMESTAMP " +
            "WHERE notes.user_id = ? AND notes.id = ? " +
            "ORDER BY notes.id DESC LIMIT 1;";

    private static final String UPDATE_STATUS_IN_TRASH = "UPDATE notes SET notes.status = 4, notes.date = CURRENT_TIMESTAMP " +
            "WHERE notes.user_id = ? AND notes.id = ? " +
            "ORDER BY notes.id DESC LIMIT 1;";

    private static final String UPDATE_STATUS_TODAY = "UPDATE notes SET notes.status = 1, notes.date = CURRENT_TIMESTAMP " +
            "WHERE notes.user_id = ? AND notes.id = ? " +
            "ORDER BY notes.id DESC LIMIT 1;";

    private static final String UPDATE_NOTE_FILE = "UPDATE notes SET notes.file_path = null, notes.date = CURRENT_TIMESTAMP " +
            "WHERE notes.user_id = ? AND notes.id = ? " +
            "ORDER BY notes.id DESC LIMIT 1;";

    private static final String DELETE_NOTE = "DELETE FROM notes WHERE notes.user_id = ? AND notes.id = ?;";

    private static final String DELETE_NOTES_STATUS_IN_TRASH = "DELETE FROM notes WHERE notes.user_id = ? AND notes.status = 4;";

    private static final String SELECT_USER_NOTES_ORDER_DATE = "SELECT notes.user_id, notes.id, notes.date, " +
            "notes.target_date, notes.note, notes.file_path, notes.status " +
            "FROM notes " +
            "WHERE notes.user_id = ? AND notes.status != 5 " +
            "ORDER BY notes.date;";

    private static final String SELECT_LAST_USER_NOTE = "SELECT notes.user_id, notes.id, notes.date, " +
            "notes.target_date, notes.note, notes.file_path, notes.status " +
            "FROM notes " +
            "WHERE notes.user_id = ? " +
            "ORDER BY id DESC LIMIT 1;";


    synchronized public static boolean createNote(int user_id, String text, int status) {

        try (PreparedStatement ps = getConnection().prepareStatement(ADD_NOTE)) {
            ps.setInt(1, user_id);
            ps.setString(2, text);
            ps.setInt(3, status);
            ps.executeUpdate();
            logger.info("Note is created.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized public static boolean createNoteWithFile(int user_id, String text, String filePath, int status) {

        try (PreparedStatement ps = getConnection().prepareStatement(ADD_NOTE_FILE)) {
            ps.setInt(1, user_id);
            ps.setString(2, text);
            ps.setString(3, filePath);
            ps.setInt(4, status);
            ps.executeUpdate();
            logger.info("Note with file is created.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized public static List<Note> getNotesOrderDate(int user_id) {
        List<Note> notes = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(SELECT_USER_NOTES_ORDER_DATE)) {
            ps.setInt(1, user_id);

            ResultSet rs = ps.executeQuery();
            fillList(notes, rs);
        } catch (SQLException e) {
            logger.error("user_id: " + user_id);
            logger.error(e.getMessage(), e);
        }

        return notes;
    }

    synchronized public static Note getLastNote(int user_id) {
        List<Note> notes = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(SELECT_LAST_USER_NOTE)) {
            ps.setInt(1, user_id);

            ResultSet rs = ps.executeQuery();
            fillList(notes, rs);
        } catch (SQLException e) {
            logger.error("user_id: " + user_id);
            logger.error(e.getMessage(), e);
        }

        if (!notes.isEmpty()) {
            return notes.get(0);
        }

        return null;
    }

    synchronized public static boolean setStatusOk(int user_id, int id) {

        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_STATUS_OK)) {
            ps.setInt(1, user_id);
            ps.setInt(2, id);
            ps.executeUpdate();
            logger.info("Note " + id + " status is ok.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized public static boolean setStatusInTrash(int user_id, int id) {

        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_STATUS_IN_TRASH)) {
            ps.setInt(1, user_id);
            ps.setInt(2, id);
            ps.executeUpdate();
            logger.info("Note " + id + " status is delete.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized public static boolean setStatusToday(int user_id, int id) {

        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_STATUS_TODAY)) {
            ps.setInt(1, user_id);
            ps.setInt(2, id);
            ps.executeUpdate();
            logger.info("Note " + id + " status is today.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized public static boolean DeleteNote(int user_id, int id) {

        try (PreparedStatement ps = getConnection().prepareStatement(DELETE_NOTE)) {
            ps.setInt(1, user_id);
            ps.setInt(2, id);
            ps.executeUpdate();
            logger.info("With status 4 note " + id + " deleted.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized public static boolean DeleteNoteFile(int user_id, int id) {

        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_NOTE_FILE)) {
            ps.setInt(1, user_id);
            ps.setInt(2, id);
            ps.executeUpdate();
            logger.info("File in note " + id + " deleted.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized public static boolean deleteDeletedNotes(int user_id) {

        try (PreparedStatement ps = getConnection().prepareStatement(DELETE_NOTES_STATUS_IN_TRASH)) {
            ps.setInt(1, user_id);
            ps.executeUpdate();
            logger.info("All notes status 4 by user " + user_id + " deleted.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized public static boolean updateStatusByDate(int user_id) {

        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_STATUS_BY_DATE)) {
            ps.setInt(1, user_id);
            ps.setInt(2, user_id);
            ps.setInt(3, user_id);
            ps.executeUpdate();
            logger.info("Status is updated.");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    synchronized private static void fillList(List<Note> notes, ResultSet rs) throws SQLException {
        while (rs.next()) {
            int dbUserId = rs.getInt("user_id");
            int dbId = rs.getInt("id");
            String dbDate = rs.getString("date");
            String dbTargetDate = rs.getString("target_date");
            String dbText = rs.getString("note");
            String dbFile_path = rs.getString("file_path");
            int dbStatus = rs.getInt("status");
            notes.add(new Note(dbUserId, dbId, dbDate, dbTargetDate, dbText, dbFile_path, dbStatus));
        }
    }


}
