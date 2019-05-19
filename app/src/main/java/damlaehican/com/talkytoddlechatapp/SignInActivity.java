package damlaehican.com.talkytoddlechatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class SignInActivity extends AppCompatActivity {

    public static FirebaseAuth firebaseAuth;


    EditText editEposta, editSifre;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("Giriş Yap");

        firebaseAuth = FirebaseAuth.getInstance();


        editEposta = findViewById(R.id.et_userMail);
        editSifre = findViewById(R.id.et_userPassword);


        FirebaseUser user  = firebaseAuth.getCurrentUser();

        if(user != null){

            Intent intent = new Intent(getApplicationContext(), HomeActivity .class);
            startActivity(intent);
        }






    }

    public void signIn(View view){

        if (editSifre.getText().toString().isEmpty() || editSifre.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), "Tüm Alanlar Eksiksiz Olmalıdır", Toast.LENGTH_LONG).show();
                return;
            
        }



        firebaseAuth.signInWithEmailAndPassword(editEposta.getText().toString(), editSifre.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){


                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);

                        }else{
                            Toast.makeText(SignInActivity.this, "HATA !", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }



    public void createUser(View view){

        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);

    }
}
