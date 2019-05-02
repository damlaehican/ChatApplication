package damlaehican.com.talkytoddlechatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    ImageView profileImage;
    EditText profileName;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef, userRef, mailRef, imageRef;
    private FirebaseAuth mAuth;
    StorageReference mStorageRef;

    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.iw_profileImage);
        profileName = findViewById(R.id.et_profileName);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference();
        mailRef = FirebaseDatabase.getInstance().getReference();
        imageRef = userRef.child("userImage");

        mStorageRef = FirebaseStorage.getInstance().getReference();


    }


    public void upload(View view){



        StorageReference storageReference = mStorageRef.child("userImages/");
        storageReference.putFile(selectedImage);

        userRef = myRef.child(mAuth.getCurrentUser().getUid().toString());
        mailRef = userRef.child("mail");
        mailRef.setValue(mAuth.getCurrentUser().getEmail());








    }

    public void selectImage(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);


            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){

            selectedImage = data.getData();

            StorageReference path = mStorageRef.child("userImages").child(mAuth.getCurrentUser().getEmail());
            path.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.setValue(taskSnapshot.getStorage().getDownloadUrl().toString());
                }
            });


            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                profileImage.setImageBitmap(bitmap);
            }catch(IOException e){
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);


    }
}
