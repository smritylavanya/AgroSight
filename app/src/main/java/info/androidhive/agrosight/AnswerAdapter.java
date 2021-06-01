package info.androidhive.agrosight;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        holder.fullName.setText(ans.getFirstname()+" "+ans.getLastname());
        holder.up.setText(String.valueOf(ans.getUpvote()));
        holder.down.setText(String.valueOf(ans.getDownvote()));
        User user = new User(context);
        if(ans.uid != Integer.valueOf(user.getUserId())){
            holder.sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ChatActivity.class);
                    i.putExtra("to_id", ans.uid);
                    i.putExtra("name", ans.getFirstname()+" "+ans.getLastname());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        }else{
            holder.sendMessageButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ques, fullName,up,down;
        ImageButton sendMessageButton;
        View itView;
        public ViewHolder(View itemView) {
            super(itemView);
            ques = itemView.findViewById(R.id.question);
            fullName = itemView.findViewById(R.id.answerCardFullName);
            up=itemView.findViewById(R.id.uptext);
            down=itemView.findViewById(R.id.downtext);
            sendMessageButton = itemView.findViewById(R.id.messageButton);
            itView = itemView;
        }
    }
}
