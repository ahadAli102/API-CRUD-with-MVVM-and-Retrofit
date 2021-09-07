package com.example.retrofitmvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.retrofitmvvm.R;
import com.example.retrofitmvvm.model.Comment;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

    private List<Comment> mComments;

    public CommentAdapter() {

    }
    public void setComments(List<Comment> mComments){
        this.mComments = mComments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.single_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.title.setText("Post ID: "+mComments.get(position).getPostId()+"\n"
                +"ID: "+mComments.get(position).getId()+"\n"+"Title: "+mComments.get(position).getName()+"\n"+
                "Text: "+mComments.get(position).getEmail()+"\n"+"Body: "+mComments.get(position).getText());

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title= itemView.findViewById(R.id.singleTitleId);

        }
    }
}
