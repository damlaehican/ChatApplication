package damlaehican.com.talkytoddlechatapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private StorageReference mStorageRef;

    String friendMail;
    Button btn_AddFriend;


    DatabaseReference mRef, userRef, mailRef, imageRef;

    ListView listView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    PostClass adapter;

    ArrayList<String> useremailFromFB;
    ArrayList<String> userimageFromFB;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mRef = FirebaseDatabase.getInstance().getReference();


        listView = findViewById(R.id.listView);

        useremailFromFB = new ArrayList<String>();
        userimageFromFB = new ArrayList<String>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("allUsers");

        adapter = new PostClass(useremailFromFB,userimageFromFB, this);

        listView.setAdapter(adapter);
        getDataFromFirebase();


    }

    public void getDataFromFirebase(){
        DatabaseReference newReference = firebaseDatabase.getReference("allUsers");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                    useremailFromFB.add(hashMap.get("useremail"));
                    userimageFromFB.add(hashMap.get("url"));
                    adapter.notifyDataSetChanged();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return true;



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.settings){

            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);


            //Arkadaş ekleme
        }else if(item.getItemId() == R.id.addFriend){

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.add_friend_dialog, null);
            mBuilder.setView(dialogView);
            final AlertDialog dialogAddNewFriend = mBuilder.create();
            dialogAddNewFriend.show();

            final EditText et_friendMail = dialogView.findViewById(R.id.friendMail);
            Button btn_AddFriend = dialogView.findViewById(R.id.idAddFriend);
            btn_AddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!et_friendMail.getText().toString().isEmpty()){

                        friendMail = et_friendMail.getText().toString();
                        et_friendMail.setText("");
                        dialogAddNewFriend.hide();


                        mRef.orderByChild("mail").equalTo(friendMail)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for(DataSnapshot postsSnapShot : dataSnapshot.getChildren()){

                                            Map<String, String>mapAddFriend = (Map<String, String>) postsSnapShot.getValue();
                                            DatabaseReference friendRef = mRef.child(mAuth.getCurrentUser().getUid()).child("friends");
                                            friendRef.push().setValue(mapAddFriend.get("mail"));

                                        }



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                        Toast.makeText(EntryActivity.this, "Error", Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                }
            });





        }else if(item.getItemId() == R.id.logOut){

        }
        return super.onOptionsItemSelected(item);
    }
}



