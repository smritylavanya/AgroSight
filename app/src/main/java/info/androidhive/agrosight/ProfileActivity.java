package info.androidhive.agrosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    Button logoutButton;
    TextView fullName,email,phonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logoutButton = findViewById(R.id.btn_logout);
        User user = new User(this);
        fullName = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phonenumber = findViewById(R.id.phonenumber);
        fullName.setText(user.getfName()+" "+user.getlName());
        email.setText("Email: "+user.getEmail());
        phonenumber.setText("Phone Number: "+user.getPhone());
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.removeUser();
                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }
}