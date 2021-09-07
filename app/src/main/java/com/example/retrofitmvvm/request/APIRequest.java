package com.example.retrofitmvvm.request;


import com.example.retrofitmvvm.model.Comment;
import com.example.retrofitmvvm.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIRequest {

    @GET("posts")
    Call<List<Post>> getPost();

    @GET("posts")
    Call<List<Post>> getPost(@Query("userId") int[] userId);

    @POST("posts")
    Call<Post> createPost(@Body Post post);

    @GET("posts/{id}/comments")
    Call<List<Comment>> getComment(@Path("id") int commentId);

    //put post
    @PUT("posts/{id}")
    Call<Post> putPost(@Path("id") int id,@Body Post post); // completely replace....

    // patch post...
    @PATCH("posts/{id}")
    Call<Post> patchPost(@Path("id") int id,@Body Post post); // update existing filed....

    // delete post
    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);  // delete existing field
}
