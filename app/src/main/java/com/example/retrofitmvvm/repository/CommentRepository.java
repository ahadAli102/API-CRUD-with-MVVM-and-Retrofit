package com.example.retrofitmvvm.repository;


import androidx.lifecycle.MutableLiveData;

import com.example.retrofitmvvm.model.Comment;
import com.example.retrofitmvvm.request.APIRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentRepository {
    private MutableLiveData<Map<Integer, List<Comment>>> mComments;
    public static boolean GET_COMMENTS=false;
    public static final int GET_COMMENTS_FAILED=-1;

    public MutableLiveData<Map<Integer, List<Comment>>> getComments(int postId) {
        GET_COMMENTS = false;
        Map<Integer, List<Comment>> commentsMap = new HashMap<>();
        mComments = new MutableLiveData<>();
        Retrofit retrofit= new Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        APIRequest apiRequest= retrofit.create(APIRequest.class);
        Call<List<Comment>> call= apiRequest.getComment(postId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.isSuccessful()){
                    commentsMap.put(response.code(),response.body());
                    mComments.postValue(commentsMap);
                    GET_COMMENTS=true;
                }
                else{
                    commentsMap.put(GET_COMMENTS_FAILED,new ArrayList<>());
                    GET_COMMENTS=true;
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                commentsMap.put(GET_COMMENTS_FAILED,new ArrayList<>());
                GET_COMMENTS=true;
            }
        });

        return mComments;
    }
}
