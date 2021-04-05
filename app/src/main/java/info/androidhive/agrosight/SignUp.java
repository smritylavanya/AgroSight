package info.androidhive.agrosight;

//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class SignUp extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//    }
//}

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText first_name;
    EditText last_name;
    EditText phone_number;
    EditText email;
    EditText password;
    Button btn_sign_up;
    RequestQueue queue;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        first_name = findViewById(R.id.firstname);
        last_name = findViewById(R.id.lastname);
        phone_number = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_sign_up = (Button)findViewById(R.id.signup1);
        queue = Volley.newRequestQueue(this);

        btn_sign_up.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                checkDataEntered();
                String url = "http://192.168.213.181/user/register";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {

                            @Override
                            public void onResponse(String response) {
                                // response

                                //store it in shared preference

                                try {
                                    Log.i("response", response);
                                    JSONObject json = new JSONObject(response);
                                    String status =json.getString("status");
                                    //String token = json.getString("token");
                                    if(status.equals("200"))
                                    {
                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                "User  created",
                                                Toast.LENGTH_SHORT);

                                        toast.show();

                                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                "User not created",
                                                Toast.LENGTH_SHORT);

                                        toast.show();

                                    }


//                                    Log.d("token", token);
//                                    sharedPreferences = getSharedPreferences("com.example.bookstore", Context.MODE_PRIVATE);
//                                    sharedPreferences.edit().putString("token",token).apply();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                //connect to home activity
//                                Intent intent = new Intent (getApplicationContext(),MainActivity.class);
//                                startActivity(intent);

                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                                //show error on screen


                            }
                        }
                )
                {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("first_name", first_name.getText().toString());
                        params.put("last_name", last_name.getText().toString());
                        params.put("phone_number", phone_number.getText().toString());
                        params.put("email", email.getText().toString());
                        params.put("password", password.getText().toString());

                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                        return params;
                    }
                };
                queue.add(postRequest);
                //openlogin();
            }
        });
    }

    boolean isEmail(EditText text){
        CharSequence email=text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    boolean isEmpty(EditText text){
        CharSequence str=text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    void checkDataEntered(){
        if (isEmpty(first_name)){
            Toast t=Toast.makeText(this, "This field cannot be empty!",Toast.LENGTH_SHORT);
            t.show();
        }
        if (isEmpty(last_name)){
            Toast t=Toast.makeText(this, "This field cannot be empty!",Toast.LENGTH_SHORT);
            t.show();
        }
        if (isEmail(email)==false){
            email.setError("Enter a valid Email");
        }
    }
    public void openlogin()
    {
        Intent intent=new Intent(this,Login.class);
        startActivity(intent);
    }

    public void switchlogin(View view)
    {
        TextView tv= findViewById(R.id.signin);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);

            }
        });
    }
}

