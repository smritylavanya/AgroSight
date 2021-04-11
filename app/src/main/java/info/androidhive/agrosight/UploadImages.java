package info.androidhive.agrosight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;

public class UploadImages extends AppCompatActivity {
    private ListView listView;
    FrameLayout progressLayout;
    private MaterialButton btnChoose, btnUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
            listView = findViewById(R.id.listView);

            progressLayout = findViewById(R.id.progress_overlay);
            btnChoose = findViewById(R.id.btnChoose);
            btnUpload = findViewById(R.id.btnUpload);
    }

}
