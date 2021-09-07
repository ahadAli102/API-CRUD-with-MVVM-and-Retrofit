package com.example.retrofitmvvm.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.retrofitmvvm.model.Comment;
import com.example.retrofitmvvm.repository.CommentRepository;;
import java.util.List;
import java.util.Map;

public class CommentViewModel extends AndroidViewModel {
    private static final String TAG = "MyTag:CommentViewModel:";
    private CommentRepository mRepository;
    public LiveData<Map<Integer, List<Comment>>> mComments;
    public CommentViewModel(@NonNull Application application) {
        super(application);
        if(mRepository==null){
            Log.d(TAG, "SignInViewModel: called on "+Thread.currentThread().getName());
            mRepository = new CommentRepository();
        }
    }
    public void getComments(final int postId){
        mComments = mRepository.getComments(postId);
    }

}
