package damlaehican.com.talkytoddlechatapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

public class EntryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorageRef;

    DatabaseReference mRef, userRef, mailRef, imageRef;


    private final static int GALERY_INDEX = 2;

    String friendMail;
    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mAuth.removeAuthStateListener(mAuthListener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("allUsers");


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user == null){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

            }
        };

        //Veri okuma işlemi (arkadaşlar)
       mailRef = FirebaseDatabase.getInstance().getReference("allUsers").child(mAuth.getCurrentUser().getUid());
        mailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot dsFriends = dataSnapshot.child("friends");
                for(DataSnapshot friend : dsFriends.getChildren()){

                    String friendNames = friend.getValue(String.class);
                    System.out.println("ARKADAS : " +friendNames);

                    mRef.orderByChild("mail").equalTo(friendNames)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot dsFriendDetail : dataSnapshot.getChildren()){

                                        Map<String, String> mapAddFriendDetail = (Map<String, String>)dsFriendDetail.getValue();

                                        System.out.println("PHOTO URL : " +mapAddFriendDetail.get("userImage"));
                                    }



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





    }
        /////ÖNEMLİ




        @Override
        public boolean onCreateOptionsMenu (Menu menu){

            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.option_menu, menu);

            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){

            if (item.getItemId() == R.id.settings) {

            } else if (item.getItemId() == R.id.addFriend) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                View dialogView = getLayoutInflater().inflate(R.layout.add_friend_dialog, null);
                mBuilder.setView(dialogView);
                final AlertDialog dialogAddNewFriend = mBuilder.create();
                dialogAddNewFriend.show();

                final EditText et_friendMail = dialogView.findViewById(R.id.friendMail);
                Button btn_addFriend = dialogView.findViewById(R.id.idAddFriend);
                btn_addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!et_friendMail.getText().toString().isEmpty()) {
                            friendMail = et_friendMail.getText().toString();
                            et_friendMail.setText(" ");
                            dialogAddNewFriend.hide();

                            mRef.orderByChild("mail").equalTo(friendMail)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                                Map<String, String> mapAddFriend = (Map<String, String>) postSnapshot.getValue();


                                                DatabaseReference friendRef = mRef.child(mAuth.getCurrentUser().getUid()).child("friends");
                                                friendRef.push().setValue(mapAddFriend.get("mail"));
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(EntryActivity.this, "Aranan kişi bulunamadı", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                });


            } else if (item.getItemId() == R.id.logOut) {

                mAuth.signOut();


            } else if (item.getItemId() == R.id.addProfileImage) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALERY_INDEX);

                userRef = mRef.child(mAuth.getCurrentUser().getUid().toString());
                mailRef = userRef.child("mail");
                mailRef.setValue(mAuth.getCurrentUser().getEmail());
                imageRef = userRef.child("userImage");
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == GALERY_INDEX && resultCode == RESULT_OK) {

                Uri uri = data.getData();

                StorageReference path = mStorageRef.child("userImages").child(mAuth.getCurrentUser().getEmail());
                path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imageRef.setValue(taskSnapshot.getStorage().getDownloadUrl().toString());
                        Toast.makeText(EntryActivity.this, "Fotoğraf Yüklendi", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        }
    }




