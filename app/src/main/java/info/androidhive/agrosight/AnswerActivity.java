package info.androidhive.agrosight;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.GridView;
import android.widget.ImageView;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.androidhive.fontawesome.FontDrawable;

public class AnswerActivity extends AppCompatActivity
{
    RecyclerView rv;
    private List<Answer> answerList;
    private RecyclerView.Adapter adapter;
    FrameLayout progressLayout;
    MaterialToolbar toolbar;
    boolean hasdata=true;
    TextView qTitleTv, qDescTv, qUpText, qDownText, qFullName;
    String qpName;
    List<ImageView> imageViews = new ArrayList<>();
    Context ctx;
    int qid;
    TextView notAnswered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        qid = getIntent().getIntExtra("id", -1);
        ExtendedFloatingActionButton fab = findViewById(R.id.addAnswer);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, AddAnswerActivity.class);
                i.putExtra("qpName",qpName);
                i.putExtra("qTitle", qTitleTv.getText().toString());
                i.putExtra("qId", qid);
                startActivity(i);
            }
        });

        notAnswered = findViewById(R.id.notAnswered);
        notAnswered.setVisibility(View.GONE);
        imageViews.add(findViewById(R.id.img1));
        imageViews.add(findViewById(R.id.img2));
        imageViews.add(findViewById(R.id.img3));
        imageViews.add(findViewById(R.id.img4));
        imageViews.add(findViewById(R.id.img5));
        for(ImageView iv : imageViews){
            iv.setVisibility(View.GONE);
        }
        answerList = new ArrayList<>();
        ctx = this;
        adapter = new AnswerAdapter(getApplicationContext(), answerList);
        progressLayout = findViewById(R.id.progress_overlay);
        toolbar = findViewById(R.id.AnswerActivityToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FontDrawable navDraw = new FontDrawable(getApplicationContext(), R.string.fa_user_circle, true, true);
        navDraw.setTextColor(ContextCompat.getColor(getApplication(), android.R.color.darker_gray));
        navDraw.setTextSize(35);
        Drawable logo = ResourcesCompat.getDrawable(getResources(), R.drawable.logo_png, getApplicationContext().getTheme());
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        getData(qid);

        //For adding question data to answer activity card

        qFullName = findViewById(R.id.qFullName);
        qTitleTv = findViewById(R.id.qTitle);
        qDescTv = findViewById(R.id.qDesc);
        qUpText = findViewById(R.id.qUpText);
        qDownText = findViewById(R.id.qDowntext);


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
                            qpName = f+" "+l;
//                            String string = ob.getString("date_created");;
//                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
//                            Date dc = format.parse(string);
                            String dateInString = ob.getString("date_created");
                            LocalDate date = LocalDate.parse(dateInString, DateTimeFormatter.ISO_DATE_TIME);
                            String q = ob.getString("question");
                            String u = ob.getString("upvotes");
                            String d = ob.getString("downvotes");
                            qFullName.setText("-"+f+" "+l + " ("+date+")");
                            qTitleTv.setText(t);
                            qDescTv.setText(q);
                            qUpText.setText(u);
                            qDownText.setText(d);
                            loadImages();
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
    private void getData(int qid) {
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
                    answerList.clear();
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject ob = data.getJSONObject(i);
                        Answer ans = new Answer(
                                ob.getInt("id"),
                                ob.getString("answer"),
                                ob.getString("f_name"),
                                ob.getString("l_name"),
                                ob.getInt("upvotes"),
                                ob.getInt("downvotes"),
                                ob.getInt("u_id")
                        );
                        answerList.add(ans);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
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
                if (error.networkResponse.statusCode == 404){
                    notAnswered.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(ctx, "Error Loading Answers!", Toast.LENGTH_SHORT).show();
                }
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
    void loadImages(){
        StringRequest jsonArrayRequest = new StringRequest(Config.URLs.qaAttachmentsUrl+qid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray data = obj.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        ImageView iv = imageViews.get(i);
                        iv.setVisibility(View.VISIBLE);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BitmapDrawable bmpDrw = (BitmapDrawable) iv.getDrawable();
                                Bitmap bmp = bmpDrw.getBitmap();
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                Intent in1 = new Intent(AnswerActivity.this, PhotViewActivity.class);
                                in1.putExtra("image",byteArray);
                                startActivity(in1);
                            }
                        });
                        String imgUri = Config.URLs.imagePrefix+data.getJSONObject(i).getString("imageUrl");
                        Picasso.get().load(imgUri).resize(600,600).centerCrop().into(iv);
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
//                hideLoading();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(AnswerActivity.this,"No images found",Toast.LENGTH_SHORT).show();
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
    void showLoading(){
        progressLayout.setVisibility(View.VISIBLE);
    }
    void hideLoading(){
        progressLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(qid);
        notAnswered.setVisibility(View.GONE);
    }
}
