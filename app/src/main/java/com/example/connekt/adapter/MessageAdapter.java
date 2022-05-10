package com.example.connekt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.connekt.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.connekt.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT =0 ;
    public static final int MSG_TYPE_RIGHT =1 ;

    private Context mContext;
    private List<Chat> mChats;
    private String imageurl;

    FirebaseUser fuser;



    public MessageAdapter(Context mContext, List<Chat> mChats, String imageurl) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        holder.text_send.setText(chat.getMess());
        if(imageurl.equals("default"))
        {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else
        {
            Picasso.get().load(imageurl).into(holder.imageView);
        }
        holder.text_time.setText(chat.getTime());
        if(position==mChats.size()-1)
        {
            if(chat.isIsseen())
            {
                holder.isseen.setText("Seen");
            }else
            {
                holder.isseen.setText("Deliveried");
            }
        }else
        {
            holder.isseen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text_send;
        public ImageView imageView;
        public TextView text_time;
        public TextView isseen;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_user);
            text_send=itemView.findViewById(R.id.text_send);
            text_time=itemView.findViewById(R.id.text_time);
            isseen = itemView.findViewById(R.id.text_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChats.get(position).getSender().equals(fuser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
