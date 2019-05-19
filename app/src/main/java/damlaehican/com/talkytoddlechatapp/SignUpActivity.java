package damlaehican.com.talkytoddlechatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {


    ProgressBar progressBar;
    EditText editAd, editSifre, editEposta;
    ImageView imageUser;

    Uri uriPhoto;


    private FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Kaydol");

        if(Build.VERSION.SDK_INT >23){
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editEposta = findViewById(R.id.et_newUserMail);
        editSifre = findViewById(R.id.et_newPassword);
        editAd = findViewById(R.id.et_username_surname);
        progressBar = findViewById(R.id.progressBar);



        //Fotoğraf işlemleri
        imageUser = findViewById(R.id.img_user);
        imageUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 100 && data != null){

            uriPhoto = data.getData();

            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPhoto);
                imageUser.setImageBitmap(bitmap);

            }catch (Exception ex){
                ex.printStackTrace();
            }



        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //Yeni kullanıcı oluşturma
    public void signUp(View view){

        if (uriPhoto == null ||
                editAd.getText().toString().isEmpty()||
                editEposta.getText().toString().isEmpty()||
                editSifre.getText().toString().isEmpty() ){

            Toast.makeText(getApplicationContext(), "Tüm Alanları Doldurunuz", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(editEposta.getText().toString(), editSifre.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            setUserInfo();

                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            
                        }else{
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

    void setUserInfo(){
        UUID  uuid = UUID.randomUUID();
        final String path = "image/"+uuid+".jpg";

        storageReference.child(path).putFile(uriPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String uid = firebaseAuth.getUid();
                        String name = editAd.getText().toString();
                        String url = uri.toString();
                        String mail = editEposta.getText().toString();




                        databaseReference.child(Child.users).push().setValue(
                                new UserInfo(uid, name, url, mail)
                        );


                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);

                        startActivity(intent);

                    }
                });

            }
        });



    }
}
