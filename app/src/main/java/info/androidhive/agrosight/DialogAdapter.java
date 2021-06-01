package info.androidhive.agrosight;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogCardViewHolder> {
    private Context mContext;
    private List<DialogModel> dialogList;
    enum DIALOG_MESSAGE_TYPE{
        IMAGE,
        TEXT
    }

    public DialogAdapter(Context mContext, List<DialogModel> dialogList) {
        this.mContext = mContext;
        this.dialogList = dialogList;
    }

    @Override
    public DialogCardViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_card, parent, false);
        DialogCardViewHolder myViewHolder = new DialogCardViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(final DialogCardViewHolder dialogHolder, final int i) {
        DialogModel m = dialogList.get(i);
        dialogHolder.name.setText(m.getfName()+" "+m.getlName());
        if(m.getType().equals("text")){
            dialogHolder.msg.setText(m.getData());
        }else{
            dialogHolder.msg.setText("\uD83D\uDCCE IMAGE");
        }
        if(m.getUnseenCount()>0){
            dialogHolder.unseenCount.setText(String.valueOf(m.getUnseenCount()));
        }else{
            dialogHolder.unSeenCountLayout.setVisibility(View.INVISIBLE);
        }
        dialogHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ChatActivity.class);
                i.putExtra("name", m.getfName()+" "+m.getlName());
                User user = new User(mContext);
                i.putExtra("to_id", m.getId());


                mContext.startActivity(i);
            }
        });

    }
    @Override
    public int getItemCount() {
        return dialogList.size();
    }
    public static class DialogCardViewHolder extends RecyclerView.ViewHolder {
        TextView name, msg, unseenCount;
        FrameLayout unSeenCountLayout;
        View card;

        public DialogCardViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.dialog_full_name);
            this.msg =  itemView.findViewById(R.id.dialogMessage);
            this.unseenCount =  itemView.findViewById(R.id.unseenCount);
            this.unSeenCountLayout =  itemView.findViewById(R.id.unseenCountLayout);
            this.card =  itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), AnswerActivity.class));
                }
            });
        }
    }
}
