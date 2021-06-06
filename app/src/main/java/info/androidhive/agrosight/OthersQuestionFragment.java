package info.androidhive.agrosight;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

import info.androidhive.fontawesome.FontDrawable;

public class OthersQuestionFragment extends Fragment {
    MaterialToolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_others_ques,container,false);
        toolbar = view.findViewById(R.id.qa_frag_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                startActivity(i);
            }
        });
        FontDrawable navDraw = new FontDrawable(getActivity(),R.string.fa_user_circle,true, true);
        navDraw.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
        navDraw.setTextSize(35);
        toolbar.setNavigationIcon(navDraw);

        Button btn = view.findViewById(R.id.btnUsefulLinks);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),LinksActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        return view;
    }
}
