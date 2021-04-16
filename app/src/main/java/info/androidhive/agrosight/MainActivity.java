package info.androidhive.agrosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    Button login;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.login1);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
        signup = (Button) findViewById(R.id.signup1);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opensignup();
            }
        });
        User user = new User(this);
        System.out.println(user.getToken());
        if (!user.getToken().equals("")){
            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
    public void openLogin()
    {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
    }
    public void opensignup()
    {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
    }

}
