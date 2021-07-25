package gmail.alexejkrawez.app.model;

public class User {
    private int user_id;
    private String email;
    private String login;
    private String password;

    public User() {}

    public User(int user_id) {
        this.user_id = user_id;
    }

    public User(int user_id, String email, String login, String password) {
        this.user_id = user_id;
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public int getUserId() {
        return user_id;
    }
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{" +
                "\"user_id\":\"" + user_id + "\"" +
                ", \"email\":\"" + email + "\"" +
                ", \"login\":\"" + login + "\"" +
                ", \"password\":\"" + password + "\"" +
                '}';
    }


}
