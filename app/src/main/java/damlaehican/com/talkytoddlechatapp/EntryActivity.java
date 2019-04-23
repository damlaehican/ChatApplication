package damlaehican.com.talkytoddlechatapp;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private final static int galery_index = 2;

    DatabaseReference mRef, userRef, mailRef, imageRef;

    String friendMail;

    private ListAdapter adapter;
    private List<Adapter> mListAdapter;

    ListView listViewFriends;



    private StorageReference mStorageRef;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private FirebaseAuth.AuthStateListener mAuthListener;

   private Toolbar myToolbar;
    MenuInflater menuInflater;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        mListAdapter =  new ArrayList<>();

        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("allUsers");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null){

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference("allUsers").child(mAuth.getCurrentUser().getUid());
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot dsFriends = dataSnapshot.child("friends");
                for(DataSnapshot friend : dsFriends.getChildren()){

                    String friendName = friend.getValue(String.class);
                    System.out.println("Arkadas :" +friendName);

                     mRef.orderByChild("mail").equalTo(friendName)
                             .addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                     for(DataSnapshot dsFriendDetail : dataSnapshot.getChildren()){
                                         Map<String, String > mapFriendDetail = (Map<String, String>)dsFriendDetail.getValue();

                                         String friendPhotoLink = mapFriendDetail.get("userImage");

                                         mListAdapter.add(new Adapter(mapFriendDetail.get("mail"), friendPhotoLink));


                                     }
                                     listViewFriends.invalidateViews();

                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });

                     adapter = new ListAdapter(getApplicationContext(), mListAdapter);
                     listViewFriends = findViewById(R.id.listView_Friends);
                     listViewFriends.setAdapter(adapter);
                     listViewFriends.invalidateViews();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.settings) {
            Toast.makeText(getApplicationContext(), mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();

            //Toast
        } else if (item.getItemId() == R.id.addImages) {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, galery_index);

            userRef = mRef.child(mAuth.getCurrentUser().getUid().toString());
            mailRef = userRef.child("mail");
            mailRef.setValue(mAuth.getCurrentUser().getEmail());
            imageRef = userRef.child("userImage");


        } else if (item.getItemId() == R.id.addFriend) {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.add_friend_dialogue, null);
            mBuilder.setView(dialogView);
            final AlertDialog dialogAddNewFriend = mBuilder.create();
            dialogAddNewFriend.show();

            final EditText et_friendMail = dialogView.findViewById(R.id.friendMail);
            Button btn_addFriend = dialogView.findViewById(R.id.addFriend);
            btn_addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!et_friendMail.getText().toString().isEmpty()) {

                        friendMail = et_friendMail.getText().toString();
                        et_friendMail.setText("");
                        dialogAddNewFriend.hide();

                        mRef.orderByChild("mail").equalTo(friendMail)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                                                Map<String, String> mapAddFriend = (Map<String, String>) postSnapshot.getValue();

                                                DatabaseReference friendRef = mRef.child(mAuth.getCurrentUser().getUid()).child("friends");
                                                friendRef.push().setValue(mapAddFriend.get( "mail"));
                                            }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                        Toast.makeText(getApplicationContext(), "Hata", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }


                }
            });



        }else if(item.getItemId() == R.id.logOut){

            mAuth.signOut();

        }else if(item.getItemId() == R.id.suggest){



        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == galery_index && resultCode == RESULT_OK){

            Uri uri = data.getData();

            StorageReference path = mStorageRef.child("userImages").child(mAuth.getCurrentUser().getEmail());
            path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.setValue(taskSnapshot.getUploadSessionUri().toString());

                    Toast.makeText(EntryActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                }
            });




        }
    }
}
