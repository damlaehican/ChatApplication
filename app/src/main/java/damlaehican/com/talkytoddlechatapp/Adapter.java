package damlaehican.com.talkytoddlechatapp;

public class Adapter {

    private String s_name, s_image;

    public Adapter(String s_name, String s_image) {
        this.s_name = s_name;
        this.s_image = s_image;
    }

    public String getS_name() {

        return s_name;
    }

    public String getS_image() {

        return s_image;
    }

    public void setS_name(String s_name) {

        this.s_name = s_name;
    }

    public void setS_image(String s_image) {

        this.s_image = s_image;
    }
}
