package info.androidhive.agrosight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.appbar.MaterialToolbar;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity implements TextWatcher {

    private String name;
    private Socket webSocket;
    private EditText messageEdit;
    private View sendBtn, pickImgBtn;
    private RecyclerView recyclerView;
    private int IMAGE_REQUEST_ID = 1;
    private MessageAdapter messageAdapter;
    TextView typing;
    TextView chatRecName;
    MaterialToolbar toolbar;
    private int recId;
    LottieAnimationView typingAnimation;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user = new User(this);
        recId = getIntent().getIntExtra("to_id",-1);
        name = getIntent().getStringExtra("name");
        initiateSocketConnection();
    }

    private void initiateSocketConnection() {
        try{
            webSocket =SocketIOClient.getmSocket(user.getToken());
            webSocket.connect();
            webSocket.emit("join", recId);
            initializeView();
            attachListeners();


        }catch (URISyntaxException e){
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        webSocket.emit("typing",true);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String string = s.toString().trim();
        if (string.isEmpty()) {
            resetMessageEdit();
        } else {
            sendBtn.setVisibility(View.VISIBLE);
            pickImgBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void resetMessageEdit() {
        messageEdit.removeTextChangedListener(this);
        messageEdit.setText("");
        sendBtn.setVisibility(View.INVISIBLE);
        pickImgBtn.setVisibility(View.VISIBLE);
        messageEdit.addTextChangedListener(this);
    }

    private void attachListeners(){
        webSocket.on("message", new Emitter.Listener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void call(Object... args) {
                try {
                    String res = Arrays.stream(args).toArray()[0].toString();
                    System.out.println(res);
                    JSONObject jsonObject = new JSONObject(res);
                    if(jsonObject.getString("from").equals(user.getUserId())){
                        return;
                    }
                    JSONObject compObj = new JSONObject();
                    if (jsonObject.getString("type").equals("image")){
                        compObj.put("isNew", true);
                        compObj.put("name", name);
                        compObj.put("image", jsonObject.get("data"));
                    }else{
                        compObj.put("name", name);
                        compObj.put("message", jsonObject.get("data"));
                    }
                    compObj.put("isSent", false);
                    messageAdapter.addItem(compObj);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeView() {
        typingAnimation = findViewById(R.id.typingAnimation);
        messageEdit = findViewById(R.id.messageEdit);
        sendBtn = findViewById(R.id.sendBtn);
        pickImgBtn = findViewById(R.id.pickImgBtn);
        chatRecName = findViewById(R.id.chatRecName);
        recyclerView = findViewById(R.id.recyclerView);

        messageAdapter = new MessageAdapter(ChatActivity.this, getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecName.setText(name);
        messageEdit.addTextChangedListener(this);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", name);
                    jsonObject.put("message", messageEdit.getText().toString());
                    webSocket.emit("message",messageEdit.getText().toString(), "text");
                    System.out.println("message emitted");
                    jsonObject.put("isSent", true);
                    messageAdapter.addItem(jsonObject);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    resetMessageEdit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        pickImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                startActivityForResult(Intent.createChooser(intent, "Pick image"),
                        IMAGE_REQUEST_ID);
            }
        });
        loadOldMessages();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK) {

            try {
                InputStream is = getContentResolver().openInputStream(data.getData());
                Bitmap image = BitmapFactory.decodeStream(is);

                sendImage(image);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private void sendImage(Bitmap image) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

        String base64String = Base64.encodeToString(outputStream.toByteArray(),
                Base64.DEFAULT);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", "MyUserName");
            jsonObject.put("image", base64String);

//            webSocket.send(jsonObject.toString());
            webSocket.emit("message", base64String, "image");

            jsonObject.put("isSent", true);
            jsonObject.put("isNew", true);
            messageAdapter.addItem(jsonObject);
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocket.close();
    }

    void loadOldMessages(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Config.URLs.getAllMessagesUrl+recId, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resJson = new JSONObject(response);
                    JSONArray resArr = resJson.getJSONArray("data");
                    for (int i = 0; i<resArr.length(); i++){
                        JSONObject jsonObject = resArr.getJSONObject(i);
                        if(!jsonObject.getString("from_id").equals(user.getUserId())){
                            JSONObject compObj = new JSONObject();
                            if (jsonObject.getString("type").equals("image")){
                                compObj.put("name", name);
                                compObj.put("image", jsonObject.get("data"));
                            }else{
                                compObj.put("name", name);
                                compObj.put("message", jsonObject.get("data"));
                            }
                            compObj.put("isSent", false);
                            messageAdapter.addItem(compObj);
                        }else{
                            JSONObject compObj = new JSONObject();
                            if (jsonObject.getString("type").equals("image")){
                                compObj.put("isNew", false);
                                compObj.put("name", user.getfName());
                                compObj.put("image", jsonObject.get("data"));
                            }else{
                                compObj.put("name", user.getfName());
                                compObj.put("message", jsonObject.get("data"));
                            }
                            compObj.put("isSent", true);
                            messageAdapter.addItem(compObj);
                        }
                    }
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse.statusCode != 404){
                    Alerter.create(ChatActivity.this)
                            .setTitle("Error")
                            .setText("Unable To load previous messages!")
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .setBackgroundColorRes(R.color.errorColor)
                            .show();
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
}
