package info.androidhive.agrosight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.fontawesome.FontDrawable;

public class QuestionAnswerFragment extends Fragment {
    RecyclerView rv;
    //CustomAdapter ad;
    private List<Questions> questionsList;
    private RecyclerView.Adapter adapter;
    public int page=1;
    FrameLayout progressLayout;
    MaterialToolbar toolbar;
    boolean hasdata=true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_ques_ans, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        questionsList=new ArrayList<>();
        adapter = new QuestionAdapter(getActivity().getApplicationContext(),questionsList);
        progressLayout=view.findViewById(R.id.progress_overlay);
        toolbar = view.findViewById(R.id.qa_frag_toolbar);
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search by tags...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("seeThis:"+query);
                getDataByTag(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            questionsList.clear();
                            getData(1);
                        }
                    });
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                startActivity(i);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.chat){
                    Intent i = new Intent(getContext(), MessagesHome.class);
                    startActivity(i);
                }
                return false;
            }
        });
        FontDrawable navDraw = new FontDrawable(getActivity(),R.string.fa_user_circle,true, true);
        navDraw.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
        navDraw.setTextSize(35);
        toolbar.setNavigationIcon(navDraw);
        Drawable logo = ResourcesCompat.getDrawable(getResources(),R.drawable.logo_png, getActivity().getTheme());
//        toolbar.setLogo(logo);

//        toolbar.logo
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
        getData(1);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if ((!recyclerView.canScrollVertically(1))&& hasdata == true)
                {
                    page=page+1;
                    getData(page);
                }
            }
        });

        return view;
    }

    public void openNewActivity(){
        Intent intent = new Intent(getActivity(), PostQuestionActivity.class);
        startActivity(intent);
    }
    private void getData(int pnumber) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        showLoading();

        StringRequest jsonArrayRequest = new StringRequest(Config.URLs.fetchQuestionsUrl+pnumber, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray data = obj.getJSONArray("data");
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject ob = data.getJSONObject(i);
                            Questions quee = new Questions(
                                    ob.getInt("id"),
                                    ob.getString("title"),
                                    ob.getString("question"),
                                    ob.getString("f_name"),
                                    ob.getString("l_name"),
                                    ob.getInt("upvotes"),
                                    ob.getInt("downvotes"),
                                    ob.getInt("u_id")
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
    private void getDataByTag(String tag) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        showLoading();
        questionsList.clear();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        StringRequest jsonArrayRequest = new StringRequest(Config.URLs.getQuestionsByTagUrl+tag, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray data = obj.getJSONArray("data");
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject ob = data.getJSONObject(i);
                        Questions quee = new Questions(
                                ob.getInt("id"),
                                ob.getString("title"),
                                ob.getString("question"),
                                ob.getString("f_name"),
                                ob.getString("l_name"),
                                ob.getInt("upvotes"),
                                ob.getInt("downvotes"),
                                ob.getInt("u_id")
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
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("No Entries found")
                        .setIcon(R.drawable.ic_baseline_error_outline_24)
                        .setBackgroundColorRes(R.color.errorColor)
                        .show();
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