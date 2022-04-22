package com.example.connekt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Post> mPosts;

    public PhotoAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mPosts.get(position);
        Picasso.get().load(post.getImage_url()).placeholder(R.mipmap.ic_launcher).into(holder.iv_image);

//        holder.iv_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString(Constant.POST_ID, post.getPost_id()).apply();
//                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
//
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.im_image);

        }
    }
}
