package info.androidhive.agrosight;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private Context context;
    private List<Answer> list;

    public AnswerAdapter(Context context, List<Answer> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cardlayoutforanswers, parent, false);

        return new ViewHolder(v);
    }

    public void onBindViewHolder(AnswerAdapter.ViewHolder holder, int position) {
        Answer ans = list.get(position);

        holder.ques.setText(ans.getAnswer());
        holder.first.setText(ans.getFirstname());
        holder.last.setText(ans.getLastname());
        holder.up.setText(String.valueOf(ans.getUpvote()));
        holder.down.setText(String.valueOf(ans.getDownvote()));
        holder.itView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), AnswerActivity.class);
                i.putExtra("id", ans.getId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(i);
//                    itemView.getContext().startActivity(new Intent(itemView.getContext(), AnswerActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Title, ques, first, last,up,down;
        View itView;
        public ViewHolder(View itemView) {
            super(itemView);

            ques = itemView.findViewById(R.id.question);
            first = itemView.findViewById(R.id.firstname);
            last = itemView.findViewById(R.id.lastname);
            up=itemView.findViewById(R.id.uptext);
            down=itemView.findViewById(R.id.downtext);
            itView = itemView;
        }
    }
}
