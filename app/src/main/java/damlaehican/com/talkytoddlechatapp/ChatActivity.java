package damlaehican.com.talkytoddlechatapp;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference msgMainRef;

    String friendMail, myMail, side1, side2;
    String sides2;


    private CustomListAdapter adapter2;
    private List<CustomChatAdapter> mListAdapter2;


    ListView listView_messages;
    ArrayList<String> chatList;
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        Bundle bundleExtras = getIntent().getExtras();
        friendMail = bundleExtras.getString("kullaniciMail");
        myMail = firebaseAuth.getCurrentUser().getEmail();
        side1 = myMail.substring(0, myMail.indexOf("@"));
        side2 = friendMail.substring(0, friendMail.indexOf("@"));

        msgMainRef = firebaseDatabase.getReference("messaging");

        chatList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chatList);



        //READ MESSAGE

        mListAdapter2 = new ArrayList<>();
        adapter2 = new CustomListAdapter(getApplicationContext(), mListAdapter2);
        




        if (side1.compareTo(side2) > 0) {

            sides2 = side1 + "--" + side2;
        } else {

            sides2 = side2 + "--" + side1;


            listView_messages = findViewById(R.id.listMessages);
            listView_messages.setAdapter(arrayAdapter);


            msgMainRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    DataSnapshot dsSides = dataSnapshot.child(sides2);
                    for (DataSnapshot dsChat : dsSides.getChildren()) {

                        String chatText = dsChat.getValue(String.class);

                        String person = chatText.substring(0, chatText.indexOf(":")).trim();
                        if(person.compareTo(myMail) == 0){

                            System.out.println("Ben");

                        }else{
                            System.out.println("Karşı taraf");
                        }




                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            final EditText et_msgContent = findViewById(R.id.msgContent);

            ImageView imgSend = findViewById(R.id.imgSend);
            imgSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String message = et_msgContent.getText().toString();
                    String sides;

                    if (side1.compareTo(side2) > 0) {

                        sides = side1 + "--" + side2;
                    } else {

                        sides = side2 + "--" + side1;

                        msgMainRef.child(sides).push().setValue(myMail + " : " + message);
                    }

                }
            });


        }
    }
}
