package damlaehican.com.talkytoddlechatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.SQLOutput;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText et_userName, et_password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        et_userName = findViewById(R.id.et_userMail);
        et_password = findViewById(R.id.et_userPassword);


        FirebaseUser user  = mAuth.getCurrentUser();

        if(user != null){

            Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
            startActivity(intent);
        }




    }

    //giriş metodu
    public void signIn(View view){

        mAuth.signInWithEmailAndPassword(et_userName.getText().toString(), et_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Intent intent =new Intent(getApplicationContext(), EntryActivity.class);
                    startActivity(intent);
                }else{

                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }

    public void createUser(View view){

        Intent intent = new Intent(getApplicationContext(), CreateUserActivity.class);
        startActivity(intent);

    }
}
