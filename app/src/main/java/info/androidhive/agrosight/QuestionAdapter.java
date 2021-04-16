package info.androidhive.agrosight;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>
{
    private Context context;
    private List<Questions> list;

    public QuestionAdapter(Context context, List<Questions> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cardlayout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Questions que = list.get(position);
        holder.Title.setText(que.getTitle());
        holder.ques.setText(que.getQuestion());
        holder.first.setText(que.getFirstname());
        holder.last.setText(que.getLastname());
        holder.up.setText(String.valueOf(que.getUpvote()));
        holder.down.setText(String.valueOf(que.getDownvote()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Title, ques, first, last,up,down;

        public ViewHolder(View itemView) {
            super(itemView);
            Title=itemView.findViewById(R.id.title);
            ques = itemView.findViewById(R.id.question);
            first = itemView.findViewById(R.id.firstname);
            last = itemView.findViewById(R.id.lastname);
            up=itemView.findViewById(R.id.uptext);
            down=itemView.findViewById(R.id.downtext);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), AnswerActivity.class));
                }
            });
        }
    }
}
