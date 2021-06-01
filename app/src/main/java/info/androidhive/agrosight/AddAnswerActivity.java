package info.androidhive.agrosight;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AddAnswerActivity extends AppCompatActivity {
    MaterialToolbar toolbar;
    TextView anPosterName, qPosterName, qTitle;
    EditText ansDesc;
    User user;
    Button ansBtn;
    Intent oi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer);
        toolbar = findViewById(R.id.postAnswer_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        anPosterName = findViewById(R.id.answerPosterUserName);
        qPosterName = findViewById(R.id.questionPosterUserName);
        qTitle = findViewById(R.id.anQuestionTitle);
        ansDesc = findViewById(R.id.answerDesc);
        ansBtn = findViewById(R.id.postAnswer);
        oi = getIntent();
        qTitle.setText(oi.getStringExtra("qTitle"));
        qPosterName.setText(oi.getStringExtra("qpName"));
        user = new User(this);
        anPosterName.setText(user.getfName()+" "+user.getlName());
        ansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    postAnswer();
                }
            }
        });
    }

    private void postAnswer() {
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST,Config.URLs.addAnswerurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Alerter.create(AddAnswerActivity.this)
                        .setTitle("Success")
                        .setText("Answer Posted redirecting in 4 seconds")
                        .setBackgroundColorRes(R.color.alerter_default_success_background)
                        .show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                },4000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 409){
                    Alerter.create(AddAnswerActivity.this)
                            .setTitle("Error")
                            .setText("Answer already submitted! Code:"+error.networkResponse.statusCode)
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .setBackgroundColorRes(R.color.errorColor)
                            .show();
                    return;
                }
                Alerter.create(AddAnswerActivity.this)
                        .setTitle("Error")
                        .setText("Unable To add answer! Code:"+error.networkResponse.statusCode)
                        .setIcon(R.drawable.ic_baseline_error_outline_24)
                        .setBackgroundColorRes(R.color.errorColor)
                        .show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Bearer " + user.getToken());
                return headers;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("q_id",String.valueOf(oi.getIntExtra("qId",-1)));
                params.put("answer", ansDesc.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(AddAnswerActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    boolean validate(){
        if (ansDesc.getText().toString().equals("")){
            Alerter.create(AddAnswerActivity.this)
                    .setTitle("Error")
                    .setText("The answer can not be blank")
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .setBackgroundColorRes(R.color.errorColor)
                    .show();
            return false;
        }
        return true;
    }


}