package info.androidhive.agrosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnLogin;
    RequestQueue queue;
    FrameLayout progressLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.usernamelogin);
        etPassword = findViewById(R.id.passwordlogin);
        btnLogin = findViewById(R.id.login);
        progressLayout = findViewById(R.id.progress_overlay);
        queue = Volley.newRequestQueue(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    login();
                }
            }
        });
    }

    public void login(){
        showLoading();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Config.URLs.loginUrl,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        // response
                        hideLoading();
                        try {
                            Log.i("response", response);
                            JSONObject json = new JSONObject(response);
                            User user = new User(Login.this);
                            user.setToken(json.getString("token"));
                            user.setUserId(String.valueOf(json.getInt("id")));
                            user.setfName(json.getString("f_name"));
                            user.setlName(json.getString("l_name"));
                            user.setEmail(json.getString("email"));
                            user.setPhone(json.getString("phone"));
                            intentToHome();
                        } catch (JSONException e) {
                            //TODO: MALFORMED RESPONSE - SHOW ERROR
                            Alerter.create(Login.this)
                                    .setTitle("Error")
                                    .setText("MALFORMED RESPONSE")
                                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                        String body = "Error Not encoded";
                        System.out.println(error.toString());
                        int statusCode;
                        try {
                            //get status code here
                            statusCode = error.networkResponse.statusCode;
                        }catch (Exception e){
                            Alerter.create(Login.this)
                                    .setTitle("Error")
                                    .setText("Something went wrong with the network")
                                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .show();
                            return;
                        }
                        //get response body and parse with appropriate encoding
                        if(error.networkResponse.data!=null) {
                            try {
                                JSONObject errorJson = new JSONObject(new String(error.networkResponse.data,"UTF-8"));
                                body = errorJson.toString(4);
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                                //TODO: Malformed Response - SHOW ERROR AND RETURN
                                Alerter.create(Login.this)
                                        .setTitle("Error")
                                        .setText("Malformed Response")
                                        .setIcon(R.drawable.ic_baseline_error_outline_24)
                                        .setBackgroundColorRes(R.color.errorColor)
                                        .show();
                                return;
                            }
                        }
                        Log.d("Volley Error Response",body);
                        if (statusCode == 404){
                            //TODO: Duplicate entry for email - SHOW ERROR
                            Alerter.create(Login.this)
                                    .setTitle("Error")
                                    .setText("Email/phone not found!")
                                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .show();
                        }else if(statusCode == 422){
                            Alerter.create(Login.this)
                                    .setTitle("Error")
                                    .setText("Validation Failed")
                                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .show();
                        }else if(statusCode == 401){
                            Alerter.create(Login.this)
                                    .setTitle("Error")
                                    .setText("Incorrect password")
                                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .show();
                        }else{
                            Alerter.create(Login.this)
                                    .setTitle("Error")
                                    .setText("Internal Server Error")
                                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("emailPhone", etUsername.getText().toString());
                params.put("password", etPassword.getText().toString());
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/json");
                return params;
            }
        };
        queue.add(postRequest);

    }
    void intentToHome(){
        Intent i = new Intent(Login.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    void showLoading(){
        progressLayout.setVisibility(View.VISIBLE);
    }
    void hideLoading(){
        progressLayout.setVisibility(View.GONE);
    }

    public boolean validate(){
        if (etUsername.getText().toString().equals("")){
            etUsername.setError("Enter Valid Email");
            etUsername.requestFocus();
            return false;
        }
        if (etPassword.getText().toString().length()<8){
            etPassword.setError("Enter Valid Password");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }


}
