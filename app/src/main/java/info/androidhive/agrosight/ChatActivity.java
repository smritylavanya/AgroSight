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
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    private int recId;
    LottieAnimationView typingAnimation;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        name = getIntent().getStringExtra("name");
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
                    if (jsonObject.get("type")=="image"){
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

    private class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            runOnUiThread(() -> {
                Toast.makeText(ChatActivity.this,
                        "Socket Connection Successful!",
                        Toast.LENGTH_SHORT).show();
                initializeView();
            });

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            runOnUiThread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    jsonObject.put("isSent", false);
                    messageAdapter.addItem(jsonObject);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void initializeView() {
        typingAnimation = findViewById(R.id.typingAnimation);
        messageEdit = findViewById(R.id.messageEdit);
        sendBtn = findViewById(R.id.sendBtn);
        pickImgBtn = findViewById(R.id.pickImgBtn);

        recyclerView = findViewById(R.id.recyclerView);

        messageAdapter = new MessageAdapter(ChatActivity.this, getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        messageEdit.addTextChangedListener(this);

        sendBtn.setOnClickListener(v -> {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", name);
                jsonObject.put("message", messageEdit.getText().toString());
                webSocket.emit("message",messageEdit.getText().toString(), "text");
                jsonObject.put("isSent", true);
                messageAdapter.addItem(jsonObject);
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                resetMessageEdit();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        pickImgBtn.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            startActivityForResult(Intent.createChooser(intent, "Pick image"),
                    IMAGE_REQUEST_ID);

        });

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
            messageAdapter.addItem(jsonObject);
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
