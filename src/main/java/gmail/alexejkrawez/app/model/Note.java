package gmail.alexejkrawez.app.model;

public class Note {

    private int user_id;
    private int id;
    private String date;
    private String target_date;
    private String note;
    private String file_path;
    private int status;

    public Note(int user_id, int id, String date, String target_date, String note, String file_path, int status) {
        this.user_id = user_id;
        this.id = id;
        this.date = date;
        this.target_date = target_date;
        this.note = note;
        this.file_path = file_path;
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTarget_date() {
        return target_date;
    }
    public void setTarget_date(String target_date) {
        this.target_date = target_date;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public String getFile_path() {
        return file_path;
    }
    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "{" +
                "\"user_id\":\"" + user_id + "\"" +
                ", \"id\":\"" + id + "\"" +
                ", \"date\":\"" + date + "\"" +
                ", \"target_date\":\"" + target_date + "\"" +
                ", \"note\":\"" + note + "\"" +
                ", \"file_path\":\"" + file_path + "\"" +
                ", \"status\":\"" + status + "\"" +
                '}';
    }
}
