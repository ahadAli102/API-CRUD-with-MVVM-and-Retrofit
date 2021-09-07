package com.example.retrofitmvvm.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrofitmvvm.R;
import com.example.retrofitmvvm.model.Post;

import java.util.ArrayList;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>{

    private List<Post> mPostList;
    private List<Post> mMainPostList;
    public ClickInterface mClickInterface;
    public static final String TAG = "MyTag:PostAdapter ";

    public PostAdapter(List<Post> mPostList,ClickInterface mClickInterface) {
        Log.d(TAG, "PostAdapter: called");
        this.mClickInterface = mClickInterface;
        this.mPostList = mPostList;
        this.mMainPostList = new ArrayList<>(mPostList);
        //setHasStableIds(true);
    }

    public void setPostList(List<Post> mPostList) {
        //this.mPostList.clear();
        this.mPostList = mPostList;
        this.mMainPostList = new ArrayList<>(mPostList);
    }
    public int getSize(){
        return mPostList.size();
    }

    /*@Override
    public int getItemViewType(int position) {
        return position;
    }*/

    /*@Override
    public long getItemId(int position) {
        return position;
    }*/

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.single_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.title.setText("User ID: "+mPostList.get(position).getUserId()+"\n"
                +"Post ID: "+mPostList.get(position).getId()+"\n"+"Title: "+mPostList.get(position).getTitle()+"\n"+
                "Text: "+mPostList.get(position).getText());
        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public Filter getFilter(){
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence ch) {
            List<Post> filterPost = new ArrayList<>();
            final String filterPattern = ch.toString().toLowerCase();
            if(ch==null || ch.length()==0){
                filterPost.addAll(mMainPostList);
            }
            else {
                for(Post m : mMainPostList){
                    String uiD = String.valueOf(m.getUserId());
                    String piD = String.valueOf(m.getId());
                    if(uiD.contains(filterPattern) || piD.contains(filterPattern) ){
                        filterPost.add(m);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values=filterPost;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPostList.clear();
            mPostList.addAll((List<Post>)results.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private final TextView title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title= itemView.findViewById(R.id.singleTitleId);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View v) {
            mClickInterface.onItemClick(getBindingAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mClickInterface.onLongItemClick(getBindingAdapterPosition());
            return false;
        }
    }
    public interface ClickInterface {
        void onItemClick(int position);
        void onLongItemClick(int position);
    }
}


