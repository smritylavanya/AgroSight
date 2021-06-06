package info.androidhive.agrosight;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PostQuestionActivity extends AppCompatActivity
    implements View.OnClickListener
{
    TextView userName;
    User user;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;
    ChipGroup chipGroup;
    EditText etTags;
    EditText qTitle;
    EditText qDescription;
    ImageButton addTagBtn;
    MaterialToolbar toolbar;
    Button submitBtn = null;
    int imageNo = 1;
    boolean submitted = false;
    List<String> tagList = new ArrayList<>();
    List<byte[]> imageByteList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);
        userName = findViewById(R.id.posterUserName);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        toolbar=findViewById(R.id.postQuestion_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        chipGroup = findViewById(R.id.postQuestionChipGroup);
        etTags = findViewById(R.id.tagEditText);
        addTagBtn = findViewById(R.id.addTagButton);
        submitBtn = findViewById(R.id.postQuestion);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
        image4.setVisibility(View.GONE);
        image5.setVisibility(View.GONE);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        image5.setOnClickListener(this);
        user = new User(this);
        qTitle = findViewById(R.id.questionTitle);
        qDescription = findViewById(R.id.questionDescription);
        userName.setText(user.getfName() + " " + user.getlName());
        addTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTags.getText().toString().equals("")){
                    return;
                }
                String tag = etTags.getText().toString();
                if (!validateTag()){
                    etTags.setError("Please enter a valid tag");
                    etTags.requestFocus();
                    return;
                }
                if (tagList.contains(tag)){
                    return;
                }
                tagList.add(tag);
                Chip chip = new Chip(PostQuestionActivity.this);
                int paddingDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10,
                        getResources().getDisplayMetrics()
                );
                chip.setText("#"+tag);
                chip.setCheckable(false);
                chip.setCloseIconVisible(true);
                chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tagList.remove(tag);
                        chipGroup.removeView(chip);
                    }
                });
                etTags.setText("");
                chipGroup.addView(chip);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()){
                    submitForm();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            File file = ImagePicker.Companion.getFile(data);
            Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            switch (imageNo){
                case 1:
                    image1.setImageBitmap(imageBitmap);
                    image1.setEnabled(false);
                    image2.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    image2.setImageBitmap(imageBitmap);
                    image2.setEnabled(false);
                    image3.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    image3.setImageBitmap(imageBitmap);
                    image3.setEnabled(false);
                    image4.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    image4.setImageBitmap(imageBitmap);
                    image4.setEnabled(false);
                    image5.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    image5.setImageBitmap(imageBitmap);
                    image5.setEnabled(false);
                    break;
            }
            imageByteList.add(byteArrayFromBitmap(imageBitmap));
            imageNo++;
        }
    }

    private void submitForm(){
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Config.URLs.PostQuestion, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int qId = result.getInt("q_id");
                    // TODO: Intent to question view page with question Id <qId>
                    Alerter.create(PostQuestionActivity.this)
                            .setTitle("Success")
                            .setText("Question submitted")
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .setBackgroundColorRes(R.color.alerter_default_success_background)
                            .show();
                    Intent i = new Intent(PostQuestionActivity.this, AnswerActivity.class);
                    i.putExtra("id", qId);
                    submitted = true;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(i);
                        }
                    }, 2000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    Log.d("error result",result);
                    if (networkResponse.statusCode == 403) {
                        Intent i = new Intent(PostQuestionActivity.this, Login.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("request_code",403);
                        startActivity(i);
                    } else if (networkResponse.statusCode == 422) {
                        errorMessage = "Validation Failed";
                    } else if (networkResponse.statusCode == 500) {
                        errorMessage = "Internal Server Error";
                    }
                }
                Alerter.create(PostQuestionActivity.this)
                        .setTitle("Error")
                        .setText(errorMessage)
                        .setIcon(R.drawable.ic_baseline_error_outline_24)
                        .setBackgroundColorRes(R.color.errorColor)
                        .show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title",qTitle.getText().toString());
                params.put("question",qDescription.getText().toString());
                System.out.println(tagList.size());
                StringBuffer tags = new StringBuffer();
                for(String s: tagList){
                    tags.append(s);
                    tags.append(" ");
                }
                params.put("tag",tags.toString());

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                for(int i = 0; i<imageByteList.size(); i++){
                    params.put("image"+(i+1), new DataPart("file.png", imageByteList.get(i), "image/png"));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+user.getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    private boolean validateForm(){
        if (qTitle.getText().toString().equals("")){
            qTitle.setError("Field Required");
            qTitle.requestFocus();
            return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        ImagePicker.Companion.with(PostQuestionActivity.this)
                .compress(100)
                .crop(1,1)
                .start();
    }
    public static byte[] byteArrayFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    private boolean validateTag(){
        Pattern p = Pattern.compile("(?:\\s|^)[A-Za-z0-9\\-\\.\\_]+(?:\\s|$)");
        Matcher m = p.matcher(etTags.getText().toString());
        if (m.matches()){
            return true;
        }
        return false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (submitted){
            finish();
        }
    }
}
