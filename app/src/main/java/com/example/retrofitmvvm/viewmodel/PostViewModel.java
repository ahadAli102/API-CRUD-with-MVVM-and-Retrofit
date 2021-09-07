package com.example.retrofitmvvm.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.retrofitmvvm.model.Post;
import com.example.retrofitmvvm.repository.PostRepository;

import java.util.List;
import java.util.Map;

public class PostViewModel extends AndroidViewModel {
    private PostRepository mRepository;
    public LiveData<List<Post>> mPosts;
    public  LiveData<Map<Integer,Post>> mPutPost;
    public  LiveData<Map<Integer,Integer>> mDeletePost;
    public  LiveData<Map<Integer,Map<Integer,Post>>> mUpdatePost;
    public static final String TAG = "MyTag:ViewModel ";

    public PostViewModel(@NonNull Application application) {
        super(application);
        if(mRepository==null){
            Log.d(TAG, "SignInViewModel: called on "+Thread.currentThread().getName());
            mRepository = new PostRepository();
        }
    }

    public void getPosts() {
        Log.d(TAG, "getPosts: called on : "+Thread.currentThread().getName());
        //get post from repository
        mPosts = mRepository.getPosts();
    }

    public void putPost(Post post, Integer id) {
        Log.d(TAG, "putPost: called on : "+Thread.currentThread().getName());
        mPutPost = mRepository.putPost(post,id);
    }
    public void deletePost(final Post post,final int position){
        mDeletePost = mRepository.deletePost(post,position);
    }

    public void updatePost(Post post, int updatePosition) {
        mUpdatePost = mRepository.updatePost(post,updatePosition);
    }
}
