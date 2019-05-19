package damlaehican.com.talkytoddlechatapp;

public class UserInfo {
    String uid;
    String name;
    String photoUrl;
    String mail;


    public UserInfo(String uid, String name, String photoUrl, String mail) {
        this.uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.mail = mail;


    }

    public  UserInfo(){

    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getMail() {return mail;}





    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setMail(String mail) {this.mail = mail;}


}
