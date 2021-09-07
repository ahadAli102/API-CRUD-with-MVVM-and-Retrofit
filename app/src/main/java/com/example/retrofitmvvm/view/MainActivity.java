package com.example.retrofitmvvm.view;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.Observer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.retrofitmvvm.R;
import com.example.retrofitmvvm.adapter.PostAdapter;
import com.example.retrofitmvvm.model.Comment;
import com.example.retrofitmvvm.model.Post;
import com.example.retrofitmvvm.repository.PostRepository;
import com.example.retrofitmvvm.viewmodel.PostViewModel;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity implements PostAdapter.ClickInterface{

    private static final String TAG = "MyTag:Main: ";
    public static PostViewModel mPostViewModel;
    private RecyclerView mRecyclerView;
    private List<Post> mPosts;
    private PostAdapter mAdapter;
    private GetData mTask;
    private DeleteTask mDeleteTask;
    private PostTask mPostTask;
    private UpdateTask mUpdateTask;
    private Post mPutPost;
    private Post mUpdatePost;
    private Integer mId;
    private SearchView mSearch;
    private static int mDeletePosition = 0;
    private static int mUpdatePosition = 0;
    public static final String POST_CODE="10000001111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intRecyclerView();
        loadPost();
        search();
    }
    private void search(){
        mSearch = findViewById(R.id.searchView);
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
    private void intRecyclerView(){
        Log.d(TAG, "intRecyclerView: called");
        mPosts = new ArrayList<>();
        mRecyclerView = findViewById(R.id.postRecycleId);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PostAdapter(mPosts,this);
        mRecyclerView.setAdapter(mAdapter);
    }
    private void loadPost(){
        Log.d(TAG, "loadPost: called");
        mTask = new GetData();
        mTask.execute();
    }
    private void observePost(){
        Log.d(TAG, "observePost: called");
        mPostViewModel.mPosts.observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                mPosts.addAll(posts);
                mAdapter.setPostList(posts);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: called");
        initViewModel();
        super.onStart();
    }

    private void initViewModel() {
        Log.d(TAG, "initViewModel: called");
        mPostViewModel= new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(PostViewModel.class);
    }

    public void floatingActionButtonClickListener(View view) {
        Log.d(TAG, "floatingActionButtonClickListener: called");
        //add post
        showPostDialog();
    }
    private void showPostDialog(){
        Log.d(TAG, "showPostDialog: called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.post_layout, null);
        builder.setView(customLayout);
        final EditText userEditText = customLayout.findViewById(R.id.userEditTextId);
        final EditText postEditText = customLayout.findViewById(R.id.postEditTextId);
        final EditText titleEditText = customLayout.findViewById(R.id.titleEditTextId);
        final EditText textEditText = customLayout.findViewById(R.id.textEditTextId);
        builder.setPositiveButton("Apply",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*private int userId;
                private Integer id;
                private String title;
                @SerializedName("body")
                private String text;*/
                try{
                    putPost((int)Integer.parseInt(userEditText.getText().toString().trim()),
                            Integer.parseInt(postEditText.getText().toString().trim()),
                            titleEditText.getText().toString(),textEditText.getText().toString());
                }catch (ClassCastException e){
                    Toast.makeText(MainActivity.this, "Please enter data correctly", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void putPost(int userId, Integer id, String title,String text) {
        Log.d(TAG, "putPost: called");
        mPutPost = new Post(userId,title,text);
        //mPostViewModel.putPost(post,id);
        mId = id;
        mPostTask = new PostTask();
        mPostTask.execute();
    }
    private void putPost(){
        Log.d(TAG, "putPost: called");
        mPostViewModel.mPutPost.observe(this, new Observer<Map<Integer, Post>>() {
            @Override
            public void onChanged(Map<Integer, Post> booleanPostMap) {
                Log.d(TAG, "onChanged: "+booleanPostMap.containsKey(200));
                Post post = booleanPostMap.get(200);
                Toast.makeText(MainActivity.this, ""+post.getId()+" "+post.getText(), Toast.LENGTH_SHORT).show();
                mPosts.add(0,booleanPostMap.get(200));
                mAdapter.setPostList(mPosts);
                mAdapter.notifyItemInserted(0);
            }
        });
    }
    private void updatePost(final int position){
        mUpdatePosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.update_post_layout, null);
        final EditText titleEditText = customLayout.findViewById(R.id.titleUpdateEditTextId);
        final EditText textEditText = customLayout.findViewById(R.id.textUpdateEditTextId);
        builder.setView(customLayout);
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: called");
                Post post = mPosts.get(position);
                post.setText(textEditText.getText().toString());
                post.setTitle(titleEditText.getText().toString());
                mUpdatePost = post;
                if(mUpdateTask==null)
                    Log.d(TAG, "onClick: (mUpdateTask=null");
                mUpdateTask = new UpdateTask();
                mUpdateTask.execute();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }
    private void performAfterUpdatingTask(){
        mPostViewModel.mUpdatePost.observe(this, new Observer<Map<Integer, Map<Integer, Post>>>() {
            @Override
            public void onChanged(Map<Integer, Map<Integer, Post>> updatePost) {
                Set<Integer> keys = updatePost.keySet();
                Log.d(TAG, "onChanged: "+keys);
                Toast.makeText(MainActivity.this, "Update response code : "+keys, Toast.LENGTH_SHORT).show();
                if(!updatePost.containsKey(PostRepository.UPDATE_FAILED)){
                    Map<Integer,Post> postMap = updatePost.get(200);
                    int newKay = mUpdatePosition;
                    Log.d(TAG, "onChanged: mUpdatePosition : "+newKay);
                    for(Integer integer: postMap.keySet()){
                        newKay = integer;
                        Log.d(TAG, "onChanged: update key : "+newKay);
                        break;
                    }
                    Log.d(TAG, "onChanged: update key : "+newKay);
                    mPosts.set(newKay,postMap.get(newKay));
                    mAdapter.setPostList(mPosts);
                    mAdapter.notifyItemChanged(newKay);
                }else{
                    Toast.makeText(MainActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void deletePost(final int position){
        mDeletePosition = position;
        mDeleteTask = new DeleteTask();
        mDeleteTask.execute();
    }
    private void performAfterDeletionTask(){
        mPostViewModel.mDeletePost.observe(this, new Observer<Map<Integer, Integer>>() {
            @Override
            public void onChanged(Map<Integer, Integer> deleteMassage) {
                Set<Integer> keys = deleteMassage.keySet();
                Log.d(TAG, "onChanged: called "+keys);
                Toast.makeText(MainActivity.this, "Response code : "+keys, Toast.LENGTH_SHORT).show();
                if(!deleteMassage.containsKey(PostRepository.DELETE_FAILED)){
                    Log.d(TAG, "onChanged: old size of activity data : "+mPosts.size());
                    Log.d(TAG, "onChanged: old size of adapter data : "+mAdapter.getSize());
                    int position = deleteMassage.get(200);
                    mPosts.remove(position);
                    mAdapter.setPostList(mPosts);
                    Log.d(TAG, "onChanged: new size of activity data : "+mPosts.size());
                    Log.d(TAG, "onChanged: new size of adapter data : "+mAdapter.getSize());
                    mAdapter.notifyItemRemoved(position);
                    Log.d(TAG, "onChanged: "+deleteMassage.get(200));
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this,"On Click "+position,Toast.LENGTH_SHORT);
        Intent intent = new Intent(MainActivity.this, CommentActivity.class);
        intent.putExtra(POST_CODE, mPosts.get(position));
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(this,"On Long Click "+position,Toast.LENGTH_SHORT);
        CharSequence sequences[] = new String[]{"UPDATE", "DELETE"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(sequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    updatePost(position);
                }else if (which==1){
                    deletePost(position);
                }
            }
        }).create().show();
    }
    class UpdateTask extends AsyncTask<Void,String,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String message;
            if(mPostViewModel==null){
                Log.d(TAG, "doInBackground: null");
                initViewModel();
            }
            try {
                Log.d(TAG, "doInBackground: all are ok");
                mPostViewModel.updatePost(mUpdatePost,mUpdatePosition);
                message = "Complete";
                //PostRepository.UPDATE_POST = false;
                Log.d(TAG, "doInBackground: PostRepository.UPDATE_POST : "+PostRepository.UPDATE_POST);
                while (!PostRepository.UPDATE_POST){
                    //Log.d(TAG, "doInBackground: PostRepository.GET_POST : "+PostRepository.DELETE_post);
                }

                Log.d(TAG, "doInBackground: PostRepository.UPDATE_POST : "+PostRepository.UPDATE_POST);
            }
            catch (ClassCastException e ){
                message = "incomplete";
                Log.w(TAG, "doInBackground: ", e);
            }
            catch (Exception e){
                message = "incomplete";
                Log.w(TAG, "doInBackground: ", e);
            }

            return message;
        }

        @Override
        protected void onPostExecute(String deleteMessage) {
            Log.d(TAG, "onPostExecute: "+deleteMessage);
            if(deleteMessage.equals("Complete")){
                putUpdateFinishMessageToUi();
            }
        }
        private void putUpdateFinishMessageToUi(){
            performAfterUpdatingTask();
        }

    }
    class DeleteTask extends AsyncTask<Void,String,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String message;
            if(mPostViewModel==null){
                Log.d(TAG, "doInBackground: null");
                initViewModel();
            }
            try {
                Log.d(TAG, "doInBackground: all are ok");
                mPostViewModel.deletePost(mPosts.get(mDeletePosition),mDeletePosition);
                message = "Complete";
                //PostRepository.GET_POST = false;
                Log.d(TAG, "doInBackground: PostRepository.DELETE_POST : "+PostRepository.DELETE_POST);
                while (!PostRepository.DELETE_POST){
                    //Log.d(TAG, "doInBackground: PostRepository.GET_POST : "+PostRepository.DELETE_post);
                }

                Log.d(TAG, "doInBackground: PostRepository.DELETE_POST : "+PostRepository.DELETE_POST);
            }
            catch (ClassCastException e ){
                message = "incomplete";
                Log.w(TAG, "doInBackground: ", e);
            }
            catch (Exception e){
                message = "incomplete";
                Log.w(TAG, "doInBackground: ", e);
            }

            return message;
        }

        @Override
        protected void onPostExecute(String deleteMessage) {
            Log.d(TAG, "onPostExecute: "+deleteMessage);
            if(deleteMessage.equals("Complete")){
                putDeleteFinishMessageToUi();
            }
        }
        private void putDeleteFinishMessageToUi(){
            performAfterDeletionTask();
        }

    }

    class PostTask extends AsyncTask<Object, String, String>{
        @Override
        protected String doInBackground(Object... objects) {
            Log.d(TAG, "doInBackground: called");
            String message;
            if(mPostViewModel==null){
                Log.d(TAG, "doInBackground: null");
                initViewModel();
            }
            try {
                Log.d(TAG, "doInBackground: all are ok");
                mPostViewModel.putPost(mPutPost,mId);
                message = "Complete";
                //PostRepository.GET_POST = false;
                Log.d(TAG, "doInBackground: PostRepository.GET_POST : "+PostRepository.GET_POST);
                while (!PostRepository.GET_POST){
                    //Log.d(TAG, "doInBackground: PostRepository.GET_POST : "+PostRepository.GET_POST);
                }

                Log.d(TAG, "doInBackground: PostRepository.GET_POST : "+PostRepository.GET_POST);
            }
            catch (ClassCastException e ){
                message = "incomplete";
                Log.w(TAG, "doInBackground: ", e);
            }
            catch (Exception e){
                message = "incomplete";
                Log.w(TAG, "doInBackground: ", e);
            }

            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            Log.d(TAG, "onPostExecute: "+message);
            if(message.equals("Complete")){
                putPostFinishMessageToUi();
            }
        }
    }

    private void putPostFinishMessageToUi() {
        Log.d(TAG, "putPostFinishMessageToUi: called");
        putPost();
    }

    class GetData extends AsyncTask<Void, String, String>{
        @Override
        protected String doInBackground(Void... activities) {
            if(mPostViewModel==null){
                Log.d(TAG, "doInBackground: null");
                initViewModel();
            }
            //PostRepository.GET_POSTS = false;
            mPostViewModel.getPosts();

            Log.d(TAG, "doInBackground: PostRepository.GET_POST : "+PostRepository.GET_POSTS);
            while (PostRepository.GET_POSTS==false){

            }
            Log.d(TAG, "doInBackground: PostRepository.GET_POST : "+PostRepository.GET_POSTS);
            return "null";
        }

        @Override
        protected void onPostExecute(String avoid) {
            putFinishMessageToUi();
        }
    }

    private void putFinishMessageToUi() {
        observePost();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
    }
}