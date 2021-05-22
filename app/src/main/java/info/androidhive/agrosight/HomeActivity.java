package info.androidhive.agrosight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SmoothBottomBar bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(navigationItemSelectedListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,new QuestionAnswerFragment()).commit();
    }

    private OnItemSelectedListener navigationItemSelectedListner =new OnItemSelectedListener() {
        @Override
        public boolean onItemSelect(int i) {
            System.out.println(i);
            Fragment selectedFragment=null;
            switch (i)
            {
                case 0:
                    selectedFragment=new QuestionAnswerFragment();
                    break;
                case 1:
                    selectedFragment=new MyQuestionFragment();
                    break;
                case 2:
                    selectedFragment=new OthersQuestionFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,selectedFragment).commit();
            return true;
        }

//        @Override
//        public boolean onItemSelected(@NonNull MenuItem item) {
//
//            Fragment selectedFragment=null;
//            switch (item.getItemId())
//            {
//                case R.id.Q_A:
//                    selectedFragment=new QuestionAnswerFragment();
//                    break;
//                case R.id.My_Question:
//                    selectedFragment=new MyQuestionFragment();
//                    break;
//                case R.id.Others:
//                    selectedFragment=new OthersQuestionFragment();
//                    break;
//
//            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,selectedFragment).commit();
//            return true;
//        }

    };
}