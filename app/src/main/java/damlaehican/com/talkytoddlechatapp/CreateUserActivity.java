package damlaehican.com.talkytoddlechatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class CreateUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

     EditText et_usernameSurname, et_newUsername, et_newPassword;
     Button newUser;
     RadioButton radioButton_women, radioButton_men, radioButton_other;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mAuth = FirebaseAuth.getInstance();

        et_usernameSurname = findViewById(R.id.et_username_surname);
        et_newUsername = findViewById(R.id.et_newUserMail);
        et_newPassword = findViewById(R.id.et_newPassword);



        newUser = findViewById(R.id.btn_signUp);

    }

        public void signUp(View v){

            mAuth.createUserWithEmailAndPassword(et_newUsername.getText().toString(), et_newPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                                startActivity(intent);
                            }else{

                                Toast.makeText(CreateUserActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }




    }

