package damlaehican.com.talkytoddlechatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxActivity extends AppCompatActivity {

    LayoutInflater layoutInflater;
    Adapter adapter;
    ArrayList<ListItem> list = new ArrayList<>();

    ListView listView;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Sohbetler");

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = findViewById(R.id.listView);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        adapter = new Adapter();
        listView.setAdapter(adapter);

        databaseReference.child(Child.CHAT_INBOX).orderByChild("gonderenUid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot infoInbox : dataSnapshot.getChildren()) {
                            databaseReference.child(Child.users).orderByChild("uid").equalTo(infoInbox.child("aliciUid").getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            list.clear();
                                            for (DataSnapshot infoUser : dataSnapshot.getChildren()) {
                                                list.add(new ListItem(
                                                        infoInbox.getKey(),
                                                        infoInbox.child("inboxKey").getValue().toString(),
                                                        infoInbox.child("okundu").getValue().toString(),
                                                        infoUser.child("uid").getValue().toString(),
                                                        infoUser.child("name").getValue().toString(),
                                                        infoUser.child("photoUrl").getValue().toString()));
                                            }
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).getOkundu().equals("1")) {
                    databaseReference.child(Child.CHAT_INBOX)
                            .child(list.get(position).getKey()).child("okundu").setValue("0");
                }

                Intent intent = new Intent(InboxActivity.this, ChatActivity.class);
                intent.putExtra("aliciUid", list.get(position).getAliciUid());
                intent.putExtra("aliciName", list.get(position).getName());
                startActivity(intent);
            }
        });
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
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
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.control_row_item_profil, null);
            }

            TextView textName = view.findViewById(R.id.textName);
            CircleImageView circleImageView = view.findViewById(R.id.circleImageView);

            textName.setText(list.get(position).getName());
            Helper.imageLoad(InboxActivity.this, list.get(position).getProfilUrl(), circleImageView);

            circleImageView.setBorderColor(getResources().getColor(R.color.colorProfileBorder));
            if (list.get(position).getOkundu().equals("1")) {
                circleImageView.setBorderWidth(15);
            } else {
                circleImageView.setBorderWidth(0);
            }

            return view;
        }
    }

    class ListItem {
        String key;
        String inboxKey;
        String okundu;
        String aliciUid;
        String name;
        String profilUrl;

        public ListItem(String key, String inboxKey, String okundu, String aliciUid, String name, String profilUrl) {
            this.key = key;
            this.inboxKey = inboxKey;
            this.okundu = okundu;
            this.aliciUid = aliciUid;
            this.name = name;
            this.profilUrl = profilUrl;
        }

        public String getKey() {
            return key;
        }

        public String getInboxKey() {
            return inboxKey;
        }

        public String getOkundu() {
            return okundu;
        }

        public String getAliciUid() {
            return aliciUid;
        }

        public String getName() {
            return name;
        }

        public String getProfilUrl() {
            return profilUrl;
        }
    }
}