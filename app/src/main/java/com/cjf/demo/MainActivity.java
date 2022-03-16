package com.cjf.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.cjf.demo.databinding.ActivityMainBinding;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import hos.thread.executor.ThreadTaskExecutor;

public class MainActivity extends AppCompatActivity {

    @Nullable
    private ActivityMainBinding mActivityMainBinding;

    @NonNull
    private ActivityMainBinding getBinding() {
        if (mActivityMainBinding == null) {
            return mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        }
        return mActivityMainBinding;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getBinding().getRoot());
//        getBinding().btnThread.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startThread();
//            }
//        });
        // 工作线程
        ThreadTaskExecutor.getInstance().postIo(1,() -> {

        });
        ThreadTaskExecutor.getInstance().postIo(1, new Runnable() {
            @Override
            public void run() {
                
            }
        });
        ThreadTaskExecutor.getInstance()
                .postIo(() -> {

                });
        // 主线程
        ThreadTaskExecutor.getInstance()
                .postToMain(() -> {

                });
    }

//    private void start() {
//        // 添加工作任务
//        List<TaskLive<String, Integer, Boolean>> list = new Vector<>();
//        for (int i = 0; i < 100; i++) {
//            list.add(new TaskLive<String, Integer, Boolean>()
//                    .setDoInBackground(new IDoInBackground<String, Integer, Boolean>() {
//                        @Override
//                        public Boolean doInBackground(IProgressUpdate<Integer> progressUpdate, @Nullable List<String> strings) {
//                            try {
//                                Thread.sleep(3000);
//                                return true;
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            return false;
//                        }
//                    })
//                    .setProgressUpdate(this, new Observer<Integer>() {
//                        @Override
//                        public void onChanged(Integer integer) {
//
//                        }
//                    })
//                    .setPostExecute(this, new Observer<Boolean>() {
//                        @Override
//                        public void onChanged(Boolean aBoolean) {
//
//                        }
//                    }));
//        }
//        // 添加单独线程记录并行线程状态
//        new TaskLiveManager<String, Integer, Boolean>().setTaskList(list)
//                .setProgressUpdate(this, new Observer<Integer>() {
//                    @Override
//                    public void onChanged(Integer progress) {
//                        Log.i("TAG", "progress: " + progress);
//                        getBinding().tvProgressInfo.setText("进度：" + progress);
//                    }
//                })
//                .startOnExecutor(this, new Observer<Boolean>() {
//                    @Override
//                    public void onChanged(Boolean isSuccess) {
//                        getBinding().tvSuccessInfo.setText("是否成功：" + isSuccess);
//                    }
//                });
//    }
//
//    private void startThread() {
//        // 添加工作任务
//        List<TaskThread<String, Integer, Boolean>> list = new LinkedList<>();
//        for (int i = 0; i < 1000; i++) {
//            list.add(new TaskThread<String, Integer, Boolean>(i)
//                    .setDoInBackground(new IDoInBackground<String, Integer, Boolean>() {
//                        @Override
//                        public Boolean doInBackground(IProgressUpdate<Integer> progressUpdate, @Nullable List<String> strings) {
//                            try {
//                                Random random = new Random();
//                                int nextInt = random.nextInt(3);
//                                Thread.sleep(nextInt * 1000);
//                                return true;
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            return false;
//                        }
//                    })
//                    .setPostExecute(new IPostExecute<Boolean>() {
//                        @Override
//                        public void onPostExecute(int index, @NonNull Boolean aBoolean) {
//
//                        }
//                    }));
//        }
//        // 添加单独线程记录并行线程状态
//        new TaskManager<String, Integer, Boolean>().setTaskList(list)
//                .setProgressUpdate(new IProgressUpdate<Integer>() {
//                    @Override
//                    public void onProgressUpdate(Integer progress) {
//                        Log.i("TAG", "progress: " + progress);
//                        getBinding().tvProgressInfo.setText("进度：" + progress);
//                    }
//                })
//                .startOnExecutor(new IPostExecute<Boolean>() {
//                    @Override
//                    public void onPostExecute(int index, @NonNull Boolean isSuccess) {
//                        getBinding().tvSuccessInfo.setText("是否成功：" + isSuccess);
//                    }
//                });
//    }
}