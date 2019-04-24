package damlaehican.com.talkytoddlechatapp;

public class CustomChatAdapter {

    private String s_userName, s_msgText;

    public CustomChatAdapter(String s_userName, String s_msgText) {
        this.s_userName = s_userName;
        this.s_msgText = s_msgText;
    }

    public void setS_userName(String s_userName) {
        this.s_userName = s_userName;
    }

    public void setS_msgText(String s_msgText) {
        this.s_msgText = s_msgText;
    }

    public String getS_userName() {
        return s_userName;
    }

    public String getS_msgText() {
        return s_msgText;
    }
}
