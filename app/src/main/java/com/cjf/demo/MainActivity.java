package com.cjf.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cjf.demo.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import hos.thread.task.ITask;
import hos.thread.task.Task;
import hos.thread.task.TaskManager;

public class MainActivity extends AppCompatActivity {

    @Nullable
    private ActivityMainBinding mActivityMainBinding;

    @NonNull
    private ActivityMainBinding getActivityMainBinding() {
        if (mActivityMainBinding == null) {
            return mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        }
        return mActivityMainBinding;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityMainBinding().getRoot());
        getActivityMainBinding().btnThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

    }

    private void start() {
        List<Task<String, Integer, Boolean>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new Task<String, Integer, Boolean>()
                    .setDoInBackground(new ITask.IDoInBackground<String, Integer, Boolean>() {
                        @Override
                        public Boolean doInBackground(@NonNull MutableLiveData<Integer> publishProgress, @Nullable String... strings) {
                            try {
                                Thread.sleep(3000);
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    })
                    .setProgressUpdate(this, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {

                        }
                    })
                    .setPostExecute(this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {

                        }
                    }));
        }

        new TaskManager<String, Integer, Boolean>().setTaskList(list)
                .setProgressUpdate(this, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer progress) {
                        Log.i("TAG", "progress: "+progress);
                        getActivityMainBinding().tvProgressInfo.setText("进度：" + progress);
                    }
                })
                .startOnExecutor(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isSuccess) {
                        getActivityMainBinding().tvSuccessInfo.setText("是否成功：" + isSuccess);
                    }
                });
    }
}