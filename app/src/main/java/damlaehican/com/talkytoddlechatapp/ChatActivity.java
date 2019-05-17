package damlaehican.com.talkytoddlechatapp;

import android.net.MailTo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    String friendMail, myMail, taraf1, taraf2;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mailRef, receiverRef, senderRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();



        Bundle bundle = getIntent().getExtras();
        friendMail = bundle.getString("kullaniciMail");
        myMail = firebaseAuth.getCurrentUser().getEmail();
        taraf1 = myMail.substring(0, myMail.indexOf("@"));
        taraf2 = myMail.substring(0, friendMail.indexOf("@"));

        mailRef = firebaseDatabase.getReference("messaging");




        final EditText et_msgIcerik = findViewById(R.id.msgIcerik);
        ImageView imgSend = findViewById(R.id.imgSend);
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = et_msgIcerik.getText().toString();
                String taraflar;

                if(taraf1.compareTo(taraf2)<0){

                    taraflar = "sender:"+taraf1+"/"+taraf2;
                }else{

                    taraflar = "receiver:"+taraf2 +"/"+taraf1;

                    mailRef.child(taraflar).push().setValue(myMail + " : "+message);

                }

            }
        });






    }
}
