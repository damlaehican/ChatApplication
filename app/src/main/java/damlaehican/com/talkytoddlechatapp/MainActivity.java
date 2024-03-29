package damlaehican.com.talkytoddlechatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Helper.isOnline()){

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);

        }
    }
}
