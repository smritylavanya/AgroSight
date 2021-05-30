package info.androidhive.agrosight;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.fontawesome.FontDrawable;

public class AnswerActivity extends AppCompatActivity
{
    RecyclerView rv;
    private List<Answer> answerList;
    private RecyclerView.Adapter adapter;
    FrameLayout progressLayout;
    TextView tv;
    MaterialToolbar toolbar;
    boolean hasdata=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        int qid = getIntent().getIntExtra("id", -1);
        FloatingActionButton fab = findViewById(R.id.fab);
        answerList = new ArrayList<>();
        adapter = new AnswerAdapter(getApplicationContext(), answerList);
        progressLayout = findViewById(R.id.progress_overlay);
        toolbar = findViewById(R.id.qa_frag_toolbar);
        FontDrawable navDraw = new FontDrawable(getApplicationContext(), R.string.fa_user_circle, true, true);
        navDraw.setTextColor(ContextCompat.getColor(getApplication(), android.R.color.darker_gray));
        navDraw.setTextSize(35);
        toolbar.setNavigationIcon(navDraw);
        Drawable logo = ResourcesCompat.getDrawable(getResources(), R.drawable.logo_png, getApplicationContext().getTheme());
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        getData(qid);

        //For adding question data to answer activity card

        TextView t1, t2, t3, t4, t5, t6, t7, t8;
        t1 = findViewById(R.id.title);
        t2 = findViewById(R.id.firstname);
        t3 = findViewById(R.id.lastname);
        t4 = findViewById(R.id.datecreated);
        t5 = findViewById(R.id.dateupdated);
        t6 = findViewById(R.id.question);
        t7 = findViewById(R.id.uptext);
        t8 = findViewById(R.id.downtext);


        StringRequest jsonObjectRequest = new StringRequest(Config.URLs.getsingleQuestionUrl + qid,
                new Response.Listener<String>() {
                    //SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray data = obj.getJSONArray("data");
                            JSONObject ob = data.getJSONObject(0);

                            String t = ob.getString("title");
                            String f = ob.getString("u_f_name");
                            String l = ob.getString("u_l_name");
                            String dc = ob.getString("date_created");
                            String du = ob.getString("date_updated");
                            String q = ob.getString("question");
                            String u = ob.getString("upvotes");
                            String d = ob.getString("downvotes");

                            t1.setText(t);
                            t2.setText(f);
                            t3.setText(l);
                            t4.setText(dc);
                            t5.setText(du);
                            t6.setText(q);
                            t7.setText(u);
                            t8.setText(d);


                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                hideLoading();
            }
            })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            // Basic Authentication
            //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);
            User u=new User(AnswerActivity.this);
            headers.put("Authorization", "Bearer " + u.getToken());
            return headers;
        }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }





        private void getData(int qid)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
            showLoading();
            StringRequest jsonArrayRequest = new StringRequest(Config.URLs.fetchAnswerUrl+qid, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray data = obj.getJSONArray("data");
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject ob = data.getJSONObject(i);
                            Answer ans = new Answer(
                                    ob.getInt("id"),
                                    ob.getString("answer"),
                                    ob.getString("f_name"),
                                    ob.getString("l_name"),
                                    ob.getInt("upvotes"),
                                    ob.getInt("downvotes")
                            );
                            answerList.add(ans);

                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
//                        progressDialog.dismiss();
                        hideLoading();
                    }
                    adapter.notifyDataSetChanged();
//                progressDialog.dismiss();
                    hideLoading();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());
                    hasdata=false;
                    Toast.makeText(AnswerActivity.this,"You have reached the Last limit",Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
                    hideLoading();
                }
            })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    // Basic Authentication
                    //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);
                    User u=new User(AnswerActivity.this);
                    headers.put("Authorization", "Bearer " + u.getToken());
                    return headers;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonArrayRequest);
    }
    static Drawable resize(Drawable image, double scaleFactor) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        int dstW = (int)(b.getWidth()/scaleFactor);
        int dstH = (int)(b.getHeight()/scaleFactor);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, dstH, dstW, false);
        return new BitmapDrawable(Resources.getSystem(), bitmapResized);
    }
    void showLoading(){
        progressLayout.setVisibility(View.VISIBLE);
    }
    void hideLoading(){
        progressLayout.setVisibility(View.GONE);
    }
}
