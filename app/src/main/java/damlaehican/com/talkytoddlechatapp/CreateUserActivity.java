package damlaehican.com.talkytoddlechatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateUserActivity extends AppCompatActivity {

    EditText emailText, passwordText, nameText;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.et_newUserMail);
        passwordText = findViewById(R.id.et_newPassword);
        nameText = findViewById(R.id.et_username_surname);
    }

    //Yeni kullanıcı oluşturma
    public void signUp(View view){

        mAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                            startActivity(intent);
                            
                        }else{

                            Toast.makeText(CreateUserActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }
}
