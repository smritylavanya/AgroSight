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
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

//        first_name.setText("fname");
//        last_name.setText("lname");
//        phone_number.setText("8888888885");
//        email.setText("user5@gmail.com");
//        password.setText("demopassword");

        btn_sign_up.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
//                if (checkDataEntered()) return;
                StringRequest postRequest = new StringRequest(Request.Method.POST, Config.URLs.registerUrl,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                Alerter.create(SignUp.this)
                                        .setTitle("Success")
                                        .setText("Successfully Registered! Please Login to continue")
                                        .setBackgroundColorRes(R.color.alerter_default_success_background)
                                        .show();
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                },4000);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String body = "Error Not encoded";
                                System.out.println(error.toString());
                                int statusCode;
                                try {
                                    //get status code here
                                    statusCode = error.networkResponse.statusCode;
                                }catch (Exception e){
                                    //TODO: Something went wrong with network - SHOW ERROR AND RETURN
                                    AlertBox.alertDialogShow(SignUp.this,"Something went wrong with the network");
                                    return;
                                }
                                //get response body and parse with appropriate encoding
                                if(error.networkResponse.data!=null) {
                                    System.out.println(error.networkResponse.data);
                                    try {
                                        JSONObject errorJson = new JSONObject(new String(error.networkResponse.data,"UTF-8"));
                                        body = errorJson.toString(4);
                                    } catch (UnsupportedEncodingException | JSONException e) {
                                        e.printStackTrace();
                                        //TODO: Malformed Response - SHOW ERROR AND RETURN
                                        AlertBox.alertDialogShow(SignUp.this,"Malformed Error");
                                        return;
                                    }
                                }
                                Log.d("Volley Error Response",body);
                                if (statusCode == 409){
                                    //TODO: Duplicate entry for email - SHOW ERROR
                                    AlertBox.alertDialogShow(SignUp.this,"Duplicate entry for E-mail");
                                }else{
                                    //TODO: Internal Server error - SHOW ERROR
                                    AlertBox.alertDialogShow(SignUp.this,"Internal Server Error");
                                }
                                AlertBox.alertDialogShow(SignUp.this,"Internal Server Error");
                            }
                        }
                )
                {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("fName", first_name.getText().toString());
                        params.put("lName", last_name.getText().toString());
                        params.put("phone", phone_number.getText().toString());
                        params.put("email", email.getText().toString());
                        params.put("password", password.getText().toString());
                        return params;
                    }
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Map<String,String> params = new HashMap<String, String>();
//                        params.put("Content-Type","application/json");
//                        return params;
//                    }
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
    boolean checkDataEntered(){
        if (isEmpty(first_name)){
            Toast t=Toast.makeText(this, "This field cannot be empty!",Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if (isEmpty(last_name)){
            Toast t=Toast.makeText(this, "This field cannot be empty!",Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if (isEmail(email)==false){
            email.setError("Enter a valid Email");
            email.requestFocus();
            return false;
        }
        return true;
    }
    public void openlogin()
    {
        Intent intent=new Intent(this,Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

