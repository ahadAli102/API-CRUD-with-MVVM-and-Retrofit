package com.example.retrofitmvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retrofitmvvm.R;
import com.example.retrofitmvvm.adapter.CommentAdapter;
import com.example.retrofitmvvm.model.Comment;
import com.example.retrofitmvvm.model.Post;
import com.example.retrofitmvvm.repository.CommentRepository;
import com.example.retrofitmvvm.viewmodel.CommentViewModel;
import com.example.retrofitmvvm.viewmodel.PostViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "MyTag:ComActivity";
    private Post mPost;
    private RecyclerView mRecyclerView;
    private TextView mTxt;
    private ProgressBar mProgressBar;
    private CommentAdapter mAdapter;
    private List<Comment> mComments;
    private CommentViewModel mViewModel;
    private GetComments mTask;
    public static final String COMPLETED="Complete";
    public static final String INCOMPLETE="Not complete";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mPost = (Post) getIntent().getSerializableExtra(MainActivity.POST_CODE);
        init();
        loadComments();
    }


    @SuppressLint("SetTextI18n")
    private void setText(){
        mTxt.setText("User Id : "+mPost.getUserId()+"\n"+"Post Id : "+mPost.getId()+"\n"+"Title : "+mPost.getTitle()+"\n"+"Text : "+mPost.getText());

    }
    private void init(){
        mProgressBar = findViewById(R.id.progressBar);
        visibleProgressBar();
        mRecyclerView = findViewById(R.id.commentRecyclerId);
        mTxt = findViewById(R.id.commentTextView);

    }
    private void visibleProgressBar(){
        Log.d(TAG, "visibleProgressBar: called");
        mProgressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar(){
        Log.d(TAG, "hideProgressBar: called");
        mProgressBar.setVisibility(View.GONE);
    }
    private void loadComments() {
        mTask = new GetComments();
        mTask.execute();
    }
    private void setRecyclerView(){
        Log.d(TAG, "setRecyclerView: called");
        mComments = new ArrayList<>();
        mAdapter = new CommentAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mViewModel.mComments.observe(this, new Observer<Map<Integer, List<Comment>>>() {
            @Override
            public void onChanged(Map<Integer, List<Comment>> commentsMap) {
                hideProgressBar();
                setText();
                int key=CommentRepository.GET_COMMENTS_FAILED;
                int mapKay = 0;
                for(Integer integer : commentsMap.keySet()){
                    mapKay = integer;
                    break;
                }
                Log.d(TAG, "onChanged: "+mapKay);
                if(!commentsMap.containsKey(key)){
                    mComments.addAll(commentsMap.get(mapKay));
                    mAdapter.setComments(mComments);
                    mAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(CommentActivity.this, "Failed to get Comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initViewModel() {
        Log.d(TAG, "initViewModel: called");
        mViewModel= new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(CommentViewModel.class);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: called");
        initViewModel();
        super.onStart();
    }

    class GetComments extends AsyncTask<Void,String,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String message;
            Log.d(TAG, "doInBackground: called on : "+Thread.currentThread().getName());
            if(mViewModel==null){
                initViewModel();
            }
            try{
                mViewModel.getComments(mPost.getId());
                Log.d(TAG, "doInBackground: "+CommentRepository.GET_COMMENTS);
                while (!CommentRepository.GET_COMMENTS){

                }
                Log.d(TAG, "doInBackground: "+CommentRepository.GET_COMMENTS);
                message = CommentActivity.COMPLETED;
            }
            catch (Exception e){
                message = CommentActivity.INCOMPLETE;
            }
            Log.d(TAG, "doInBackground: "+message);
            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            Log.d(TAG, "onPostExecute: called : "+message);
            if(message.equals(CommentActivity.COMPLETED)){
                postCommentToUi(message);
            }
        }
    }

    private void postCommentToUi(String message) {
        Log.d(TAG, "postCommentToUi: called");
        if(message.equals(CommentActivity.COMPLETED)){
            setRecyclerView();
        }
    }
}