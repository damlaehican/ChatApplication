package damlaehican.com.talkytoddlechatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {


    LayoutInflater layoutInflater;

    Adapter adapter;
    ArrayList<ListItem> list = new ArrayList<>();

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String aliciUid;
    String aliciName;
    String gonderenUid;

    ListView listView;
    EditText editMesaj;

    ChatInbox chatInbox;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);




        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = findViewById(R.id.listView);
        editMesaj = findViewById(R.id.editMesaj);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        gonderenUid = firebaseAuth.getUid();

        aliciUid = getIntent().getExtras().getString("aliciUid");
        aliciName = getIntent().getExtras().getString("aliciName");
        setTitle(aliciName);

        adapter = new Adapter();
        listView.setAdapter(adapter);

        Toast.makeText(this, aliciName+ " " +aliciUid, Toast.LENGTH_LONG).show();

        createChat();

        findViewById(R.id.btnGonder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(Child.CHATS).push().setValue(
                        new Chats(chatInbox.getInboxKey(), gonderenUid, editMesaj.getText().toString()), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable final DatabaseError databaseError, @NonNull final DatabaseReference databaseReferenceResult) {
                                 editMesaj.setText(" ");
                                final String mesajKey = databaseReferenceResult.getKey();

                                databaseReference.child(Child.CHAT_LAST).orderByChild("inboxKey").equalTo(chatInbox.getInboxKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                                    databaseReference.child(Child.CHAT_LAST).child(snapshot.getKey()).child("mesajKey").setValue(mesajKey);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                databaseReference.child(Child.CHAT_INBOX).orderByChild("inboxKey").equalTo(chatInbox.getInboxKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                                            if (aliciUid.equals(snapshot.child("gonderenUid").getValue().toString())){

                                                databaseReference.child(Child.CHAT_INBOX).child(snapshot.getKey()).child("okundu").setValue("0");

                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                );
            }
        });
    }
    public void createChat(){

        databaseReference.child(Child.CHAT_INBOX).orderByChild("gonderenUid").equalTo(gonderenUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                            if (snapshot.getValue(ChatInbox.class).getAliciUid().equals(aliciUid)){
                                chatInbox = snapshot.getValue(ChatInbox.class);


                            }
                        }
                        chatInboxAndChatLast();

                        chats();

                        chatLast();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    void chatInboxAndChatLast() {

        if (chatInbox == null){
            String key = databaseReference.push().getKey();
            databaseReference.child(Child.CHAT_INBOX).push().setValue(
                    new ChatInbox(key, gonderenUid, aliciUid, "0")
            );

            databaseReference.child(Child.CHAT_INBOX).push().setValue(
                    new ChatInbox(key, aliciUid, gonderenUid , "0")
            );

            chatInbox = new ChatInbox(key, gonderenUid, aliciUid,"0");


            databaseReference.child(Child.CHAT_LAST).push().setValue(
                    new ChatLast(key, "")
            );

        }


    }


    void chats() {


        databaseReference.child(Child.CHATS).orderByChild("inboxKey").equalTo(chatInbox.getInboxKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                                Chats chats = snapshot.getValue(Chats.class);
                                list.add(new ListItem(chats.getGonderenUid(), chats.getMesaj()));

                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    void chatLast() {


        databaseReference.child(Child.CHAT_LAST).orderByChild( "inboxKey").equalTo(chatInbox.getInboxKey())
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                databaseReference.child(Child.CHATS).child(dataSnapshot.child("mesajKey").getValue().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Chats chats = dataSnapshot.getValue(Chats.class);
                        list.add(new ListItem(chats.getGonderenUid(), chats.getMesaj()));
                        adapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }








    class Adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size() ;
        }


        @Override
        public Object getItem(int position) {
            return list.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null){
                view = layoutInflater.inflate(R.layout.control_row_item_talk, null);


            }

            LinearLayout linearRow= view.findViewById(R.id.linearRow);
            LinearLayout linearTalk = view.findViewById(R.id.linearTalk);

            if (list.get(position).getGonderenUid().equals(gonderenUid)){
                linearTalk.setBackgroundResource(R.drawable.draw_talk_ben);
                linearRow.setGravity(Gravity.RIGHT);

            }else{
                linearTalk.setBackgroundResource(R.drawable.draw_talk_o);
                linearRow.setGravity(Gravity.LEFT);


            }


            TextView textMesaj = view.findViewById(R.id.textMesaj);
            textMesaj.setText(list.get(position).getMesaj());




            return view;
        }
    }

    class ListItem{
        String gonderenUid;
        String mesaj;

        public ListItem(String gonderenUid, String mesaj) {
            this.gonderenUid = gonderenUid;
            this.mesaj = mesaj;
        }

        public String getGonderenUid() {
            return gonderenUid;
        }

        public String getMesaj() {
            return mesaj;
        }
    }
}
