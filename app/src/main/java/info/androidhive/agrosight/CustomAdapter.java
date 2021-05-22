package info.androidhive.agrosight;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context mContext;
    private String[] mquestion;
    private String[] mFirstName;
    private String[] mLastName;

    CustomAdapter(Context mContext, String[] question,String[] firstname,String[] lastname) {
        this.mContext = mContext;
        this.mquestion = question;
        this.mFirstName = firstname;
        this.mLastName=lastname;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        holder.question.setText(mquestion[i]);
        holder.firstname.setText(mFirstName[i]);
        holder.lastname.setText(mLastName[i]);
    }
    @Override
    public int getItemCount() {
        return mquestion.length;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        TextView firstname;
        TextView lastname;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.question = itemView.findViewById(R.id.question);
            this.firstname =  itemView.findViewById(R.id.firstname);
            this.lastname =  itemView.findViewById(R.id.lastname);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), AnswerActivity.class));
                }
            });

        }
    }
}
