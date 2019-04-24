package damlaehican.com.talkytoddlechatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    String friendMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Bundle bundleExtras = getIntent().getExtras();
        friendMail = bundleExtras.getString("kullaniciMail");


    }
}
