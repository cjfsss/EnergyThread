package com.cjf.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cjf.demo.databinding.ActivityMainBinding;

import java.util.Random;

import hos.thread.executor.CallBackground;
import hos.thread.executor.ThreadTaskExecutor;
import hos.thread.task.ITaskCreator;
import hos.thread.task.Task;
import hos.thread.task.TaskFlowManager;
import hos.thread.task.TaskListener;
import hos.thread.task.TaskProject;

public class MainActivity extends AppCompatActivity {

    static final String TASK_BLOCK_1 = "block_task_1";
    static final String TASK_BLOCK_2 = "bLock_task_2";
    static final String TASK_BLOCK_3 = "block_task_3";
    static final String TASK_BLOCK_4 = "block_task_4";
    static final String TASK_ASYNC_1 = "async_task_1";
    static final String TASK_ASYNC_2 = "async_task_2";
    static final String TASK_ASYNC_3 = "async_task_3";
    static final String TASK_ASYNC_4 = "async_task_4";
    static final String TASK_ASYNC_5 = "async_task_5";
    static final String TASK_ASYNC_6 = "async_task_6";
    static final String TASK_ASYNC_7 = "async_task_7";
    static final String TASK_ASYNC_8 = "async_task_8";

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
        getBinding().btnThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConcurrence();
//                startThread();
            }
        });
        getBinding().btnThreadAppStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
        getBinding().btnThreadIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadTaskExecutor.getInstance().postIo(new CallBackground<String>() {

                    @Override
                    protected void onPrepare() {
                        getBinding().btnThreadIO.setText("????????????????????????");
                    }

                    @Override
                    protected String onBackground() throws Exception {
                        for (int i = 0; i < 100; i++) {
                            Thread.sleep(800 + new Random().nextInt(1000));
                            progressUpdate(i);
                        }
                        return "????????????????????????";
                    }

                    @Override
                    protected void onProgressUpdate(int value) {
                        super.onProgressUpdate(value);
                        getBinding().btnThreadIO.setText("???????????????" + value + "???");
                        getBinding().progressHorizontalIo.setProgress(value);
                    }

                    @Override
                    protected void onCompleted(@Nullable String s) {
                        getBinding().btnThreadIO.setText(s);
                    }

                    @Override
                    protected void onError(@NonNull Throwable e) {
                        getBinding().btnThreadIO.setText("????????????????????????");
                    }
                });
            }
        });
        // ????????????
        ThreadTaskExecutor.getInstance().postIo(1, () -> {

        });
        ThreadTaskExecutor.getInstance().postIo(1, new Runnable() {
            @Override
            public void run() {

            }
        });
        ThreadTaskExecutor.getInstance()
                .postIo(() -> {

                });
        // ?????????
        ThreadTaskExecutor.getInstance()
                .postToMain(() -> {

                });

    }

    public void startConcurrence() {
        // ??????
        Log.e("TaskStartUp", "start");
        TaskProject taskProject = TaskProject.Builder.concurrence("TaskStartUp",
                createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(),
                createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(),
                createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(),
                createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(),
                createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain(), createTaskMain());
        TaskFlowManager.concurrence(taskProject, new TaskListener.ProgressUpdate() {
            @Override
            public void onProgressUpdate(@NonNull Integer progress) {
                if (progress == 200) {
                    getBinding().btnThread.setText("?????????????????????)");
                } else if (progress == 400) {
                    getBinding().btnThreadAppStart.setText("?????????????????????)");
                } else {
                    getBinding().btnThread.setText("???????????????" + progress + ")");
                    getBinding().progressHorizontal.setProgress(progress);
                }
                Log.e("TaskFlow", "onProgressUpdate???" + progress);
            }
        });
        Log.e("TaskStartUp", "end");
    }

    public void start() {
        Log.e("TaskStartUp", "start");
        TaskProject project = new TaskProject.Builder("TaskStartUp", createTaskCreator())
                .add(TASK_BLOCK_1)
                .add(TASK_BLOCK_2)
                .add(TASK_BLOCK_3)
                .add(TASK_BLOCK_4)
                .add(TASK_ASYNC_1).dependOn(TASK_BLOCK_1)
                .add(TASK_ASYNC_2).dependOn(TASK_BLOCK_2)
                .add(TASK_ASYNC_3).dependOn(TASK_BLOCK_3)
                .add(TASK_ASYNC_4)
                .add(TASK_ASYNC_5).dependOn(TASK_ASYNC_2)
                .add(TASK_ASYNC_6).dependOn(TASK_ASYNC_3)
                .add(TASK_ASYNC_7).dependOn(TASK_ASYNC_2)
                .add(TASK_ASYNC_8).dependOn(TASK_ASYNC_3)
                .build();
        TaskFlowManager.create()
                .addBlockTask(TASK_BLOCK_1)
                .addBlockTask(TASK_BLOCK_2)
                .addBlockTask(TASK_BLOCK_3)
                .addBlockTask(TASK_BLOCK_4)
                .start(project, new TaskListener.ProgressUpdate() {
                    @Override
                    public void onProgressUpdate(@NonNull Integer progress) {
                        if (progress == 200) {
                            getBinding().btnThreadAppStart.setText("App???????????????)");
                        } else if (progress == 400) {
                            getBinding().btnThreadAppStart.setText("App???????????????)");
                        } else {
                            getBinding().btnThreadAppStart.setText("App?????????" + progress + ")");
                            getBinding().progressHorizontalApp.setProgress(progress);
                        }
                    }
                });
        Log.e("TaskStartUp", "end");
    }

    private ITaskCreator createTaskCreator() {
        return new ITaskCreator() {
            @NonNull
            @Override
            public Task createTask(@NonNull String taskName) {
                switch (taskName) {
                    case TASK_ASYNC_1:
                    case TASK_ASYNC_2:
                    case TASK_ASYNC_3:
                    case TASK_ASYNC_4:
                    case TASK_ASYNC_5:
                    case TASK_ASYNC_6:
                    case TASK_ASYNC_7:
                    case TASK_ASYNC_8:
                        return createTaskMain(taskName, true);
                    case TASK_BLOCK_1:
                    case TASK_BLOCK_2:
                    case TASK_BLOCK_3:
                    case TASK_BLOCK_4:
                        return createTaskMain(taskName, false);
                }
                return createTaskMain("default", false);
            }

        };

    }

    public Task createTaskMain() {
        return createTaskMain(TaskFlowManager.newId());
    }

    public Task createTaskMain(String taskName) {
        return createTaskMain(taskName, true);
    }

    public Task createTaskMain(String taskName, boolean isAsync) {
        return new Task(taskName, isAsync) {

            @Override
            protected void run(@NonNull String id) {
                try {
                    if (isAsync) {
                        Thread.sleep(2000 + new Random().nextInt(1000));
                    } else {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("TaskFlow", "task " + taskName + ", " + isAsync + ", finished");
            }
        };
    }

//    private void start() {
//        // ??????????????????
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
//        // ??????????????????????????????????????????
//        new TaskLiveManager<String, Integer, Boolean>().setTaskList(list)
//                .setProgressUpdate(this, new Observer<Integer>() {
//                    @Override
//                    public void onChanged(Integer progress) {
//                        Log.i("TAG", "progress: " + progress);
//                        getBinding().tvProgressInfo.setText("?????????" + progress);
//                    }
//                })
//                .startOnExecutor(this, new Observer<Boolean>() {
//                    @Override
//                    public void onChanged(Boolean isSuccess) {
//                        getBinding().tvSuccessInfo.setText("???????????????" + isSuccess);
//                    }
//                });
//    }
//
//    private void startThread() {
//        // ??????????????????
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
//        // ??????????????????????????????????????????
//        new TaskManager<String, Integer, Boolean>().setTaskList(list)
//                .setProgressUpdate(new IProgressUpdate<Integer>() {
//                    @Override
//                    public void onProgressUpdate(Integer progress) {
//                        Log.i("TAG", "progress: " + progress);
//                        getBinding().tvProgressInfo.setText("?????????" + progress);
//                    }
//                })
//                .startOnExecutor(new IPostExecute<Boolean>() {
//                    @Override
//                    public void onPostExecute(int index, @NonNull Boolean isSuccess) {
//                        getBinding().tvSuccessInfo.setText("???????????????" + isSuccess);
//                    }
//                });
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}