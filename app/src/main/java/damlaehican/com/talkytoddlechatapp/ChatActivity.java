package damlaehican.com.talkytoddlechatapp;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    String friendMail, myMail, side1, side2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference msgMainRef;


        Bundle bundleExtras = getIntent().getExtras();
        friendMail = bundleExtras.getString("kullaniciMail");
        myMail = firebaseAuth.getCurrentUser().getEmail();
        side1 = myMail.substring(0, myMail.indexOf("@"));
        side2 = friendMail.substring(0, friendMail.indexOf("@"));

        msgMainRef = firebaseDatabase.getReference("messaging");



        final EditText et_msgContent = findViewById(R.id.msgContent);

        ImageView imgSend = findViewById(R.id.imgSend);
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = et_msgContent.getText().toString();
                String sides;

                if(side1.compareTo(side2) > 0){

                   sides = side1 + "--"  +side2;
                }else{

                    sides = side2 + "--" +side1;

                    msgMainRef.child(sides).push().setValue(myMail + " : " +message);
                }

            }
        });





    }
}
