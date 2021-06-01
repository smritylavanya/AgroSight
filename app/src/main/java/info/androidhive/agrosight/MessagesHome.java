package info.androidhive.agrosight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesHome extends AppCompatActivity {
    FrameLayout loadingLayout;
    MaterialToolbar toolbar;
    int totalEntries;
    User user;
    RecyclerView recyclerView;
    DialogAdapter dialogAdapter = null;
    List<DialogModel> dialogList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_home);
        recyclerView = findViewById(R.id.message_home_rv);
        user = new User(this);
        loadingLayout = findViewById(R.id.progress_overlay);
        toolbar = findViewById(R.id.message_home_toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getMetaData();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (dialogAdapter != null){
            dialogList.clear();
            getMetaData();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void getMetaData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URLs.getDialogMetaDataUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    List<Integer> idList = new ArrayList<>();
                    for (int i =0; i< jsonArray.length(); i++){
                        int id = -1;
                        if (jsonArray.getJSONObject(i).getInt("to_id")==Integer.parseInt(user.getUserId())){
                            id = jsonArray.getJSONObject(i).getInt("from_id");
                        }else{
                            id = jsonArray.getJSONObject(i).getInt("to_id");
                        }
                        if (id>0 && !idList.contains(id)){
                            idList.add(id);
                        }
                    }
                    totalEntries = idList.size();
                    for(Integer id: idList){
                        addDialogById(id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                System.out.println();
                if (error.networkResponse.statusCode==403){
                    Intent i = new Intent(MessagesHome.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+user.getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
    }
    private void addDialogById(int id){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URLs.getDialogUrl+"/"+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resJson = new JSONObject(response);
                    DialogModel m = new DialogModel(
                            Integer.valueOf(user.getUserId()) == resJson.getInt("from_id")?
                                    resJson.getInt("to_id"):
                                    resJson.getInt("from_id"),
                        resJson.getString( "type"),
                        resJson.getString( "data"),
                        resJson.getInt( "unseen_count"),
                        resJson.getString( "f_name"),
                        resJson.getString( "l_name")
                    );
                    dialogList.add(m);
                    if(dialogList.size()>=totalEntries){
                        renderList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                System.out.println();
                if (error.networkResponse.statusCode==403){
                    Intent i = new Intent(MessagesHome.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+user.getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
    }
    private void renderList() {
        dialogAdapter = new DialogAdapter(MessagesHome.this, dialogList);
        recyclerView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();
    }
}