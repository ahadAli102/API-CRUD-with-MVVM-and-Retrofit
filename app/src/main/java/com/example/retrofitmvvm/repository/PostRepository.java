package com.example.retrofitmvvm.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.retrofitmvvm.model.Post;
import com.example.retrofitmvvm.request.APIRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostRepository {
    private static final String TAG = "MyTag:Repository ";
    private APIRequest mApiRequest;
    private MutableLiveData<List<Post>> mPosts;
    private MutableLiveData<Map<Integer,Post>> mPutPost;
    private MutableLiveData<Map<Integer,Integer>> mDeletePost;
    private MutableLiveData<Map<Integer,Map<Integer,Post>>> mUpdatePost;
    public static boolean GET_POSTS = false;
    public static boolean GET_POST = false;
    public static boolean DELETE_POST = false;
    public static boolean UPDATE_POST = false;

    public static final Integer DELETE_FAILED= -1;
    public static final Integer UPDATE_FAILED= -1;

    public MutableLiveData<List<Post>> getPosts() {
        GET_POSTS = false;
        Log.d(TAG, "getPosts: called on : "+Thread.currentThread().getName());
        mPosts = new MutableLiveData<>();

        Retrofit retrofit= new Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        mApiRequest = retrofit.create(APIRequest.class);
        //Call<List<Post>> call = mApiRequest.getPost(new Integer[]{2,3});
        Call<List<Post>> call = mApiRequest.getPost();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                Log.d(TAG, "onResponse: ");
                Log.d(TAG, "onResponse: "+response.code());
                Log.d(TAG, "onResponse: "+response.isSuccessful());
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: ");
                    //print(response.body());
                    mPosts.setValue(response.body());
                    GET_POSTS = true;
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
            }
        });
        //GET_POSTS = true;
        return mPosts;
    }

    public MutableLiveData<Map<Integer,Post>> putPost(Post post, Integer id){
        GET_POST = false;
        mPutPost = new MutableLiveData<>();
        Log.d(TAG, "putPost: called on  "+Thread.currentThread().getName());
        Log.d(TAG, "putPost: "+GET_POST);
        Retrofit retrofit= new Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        mApiRequest = retrofit.create(APIRequest.class);
        //Post post= new Post(23,null,"My new Text Here");
        Map<Integer,Post> postMap = new HashMap<>();
        Call<Post> call = mApiRequest.putPost(id,post);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Log.d(TAG, "onResponse: "+response.isSuccessful());
                Log.d(TAG, "onResponse: "+response.code());
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: not successful");
                }else{
                    Log.d(TAG, "onResponse: successful");
                }
                Log.d(TAG, "onResponse: "+response.body().getText()+response.body().getId()+response.body().getTitle()+response.body().getUserId());
                postMap.put(response.code(),response.body());
                GET_POST = true;
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.w(TAG, "onFailure: ", t);
                postMap.put(500,post);
                GET_POST = true;
            }
        });
        /*Post post= new Post(23,null,"My new Text Here");
        Call<Post> call= apiClient.putPost(5,post);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(PutActivity.this, ""+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Post post1= response.body();
                textView.setText("Code: "+response.code()+"\n"+
                        "ID: "+post1.getId()+"\n"+
                        "User ID: "+post1.getUserId()+"\n"+
                        "Title: "+post1.getTitle()+"\n"+
                        "Text: "+post1.getText());
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                Toast.makeText(PutActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
        mPutPost.postValue(postMap);
        return mPutPost;
    }
    public MutableLiveData<Map<Integer,Integer>> deletePost(final Post post,final int position){
        DELETE_POST = false;
        mDeletePost = new MutableLiveData<>();
        final int id = post.getId();
        Map<Integer,Integer> deleteMessageMap = new HashMap<>();
        Retrofit retrofit= new Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        mApiRequest = retrofit.create(APIRequest.class);
        Call<Void> call= mApiRequest.deletePost(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                deleteMessageMap.put(response.code(),position);
                DELETE_POST = true;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                deleteMessageMap.put(DELETE_FAILED,position);
                DELETE_POST = true;
            }
        });
        mDeletePost.postValue(deleteMessageMap);
        return mDeletePost;
    }


    public MutableLiveData<Map<Integer, Map<Integer, Post>>> updatePost(Post post, int updatePosition) {
        UPDATE_POST = false;
        mUpdatePost = new MutableLiveData<>();
        Map<Integer,Map<Integer,Post>> updateMap = new HashMap<>();
        Map<Integer,Post> postMap = new HashMap<>();
        Post post1= new Post(23,null,"My new  Patch Text Here");
        int id = post.getId();
        Call<Post> call= mApiRequest.patchPost(id,post);
        call.enqueue(new Callback<Post>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(response.isSuccessful()){
                    postMap.put(updatePosition,response.body());
                    updateMap.put(response.code(),postMap);
                    UPDATE_POST = true;
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                postMap.put(updatePosition,post);
                updateMap.put(UPDATE_FAILED,postMap);
                UPDATE_POST = true;
            }
        });
        mUpdatePost.postValue(updateMap);
        return mUpdatePost;
    }

    private void print(List<Post> body){
        for(Post p : body )
            Log.d(TAG, "print: "+p.getId());
    }
}
