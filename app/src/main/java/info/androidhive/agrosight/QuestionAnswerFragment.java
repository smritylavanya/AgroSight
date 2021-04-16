package info.androidhive.agrosight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionAnswerFragment extends Fragment {
    RecyclerView rv;
    //CustomAdapter ad;
    private List<Questions> questionsList;
    private RecyclerView.Adapter adapter;
    public int page=1;
    FrameLayout progressLayout;
    boolean hasdata=true;
    private String url = "http://10.0.2.2:5000/qa/get-paged-questions/";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_ques_ans, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        questionsList=new ArrayList<>();
        adapter = new QuestionAdapter(getActivity().getApplicationContext(),questionsList);
        progressLayout=view.findViewById(R.id.progress_overlay);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewActivity();
            }
        });

        rv = (RecyclerView)view.findViewById(R.id.rv);
        //adapter = new CustomAdapter(getActivity(),Question,FirstName,LastName);
        //rv.setAdapter(ad);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        getData(url,1);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if ((!recyclerView.canScrollVertically(1))&& hasdata == true)
                {
                    page=page+1;
                    getData(url,page);
                }

            }
        });

        return view;
    }

    public void openNewActivity(){
        Intent intent = new Intent(getActivity(), PostQuestionActivity.class);
        startActivity(intent);
    }
    private void getData(String url, int pnumber)
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        showLoading();

        StringRequest jsonArrayRequest = new StringRequest(url+pnumber, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray data = obj.getJSONArray("data");
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject ob = data.getJSONObject(i);
                            Questions quee = new Questions(
                                    ob.getString("title"),
                                    ob.getString("question"),
                            ob.getString("f_name"),
                            ob.getString("l_name"),
                            ob.getInt("upvotes"),
                            ob.getInt("downvotes")
                             );
                            questionsList.add(quee);

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
                Toast.makeText(getContext(),"You have reached the Last limit",Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
                hideLoading();
            }
        }){
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        // Basic Authentication
        //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);
        User u=new User(getContext());
        headers.put("Authorization", "Bearer " + u.getToken());
        return headers;
    }
    };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);

    }
    void showLoading(){
        progressLayout.setVisibility(View.VISIBLE);
    }
    void hideLoading(){
        progressLayout.setVisibility(View.GONE);
    }
}