package Services;

public class ChatRecord {

    private String role;
    private String message;

    public ChatRecord(String role, String message) {
        this.role = role;
        this.message = message;
    }
    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}
