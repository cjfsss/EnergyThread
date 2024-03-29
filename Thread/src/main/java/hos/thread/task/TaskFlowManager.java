package hos.thread.task;

import android.os.Looper;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import hos.thread.executor.TS;
import hos.thread.hander.MH;
import hos.thread.utils.ThreadLog;

/**
 * <p>Title: TaskFlowManager </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 21:29
 */
public class TaskFlowManager {


    private final ReentrantLock lock = new ReentrantLock();

    private volatile CountDownLatch mCountDownLatch;
    private volatile int mTotalCount;

    private long currentCount;

    private long errorCount;
    private long oldCount;

    private static boolean isFinish = true;
    private boolean isSuccessFul;

    private TaskFlowManager() {
        isSuccessFul = true;
    }

    public static TaskFlowManager create() {
        return new TaskFlowManager();
    }

    public static String newId() {
        return "task_" + System.currentTimeMillis() + new Random().nextInt(99);
    }

    public TaskFlowManager addBlockTask(String taskId) {
        TaskRuntime.addBlockTask(taskId);
        return this;
    }

    public TaskFlowManager addBlockTasks(String... taskIds) {
        TaskRuntime.addBlockTasks(taskIds);
        return this;
    }

    public static boolean isFinish() {
        return isFinish;
    }

    public void start(Task task) {
        start(task, null);
    }

    //project任务组，也有可能是独立的-个task

    public void start(Task task, TaskListener.ProgressUpdate progressUpdate) {
        if (!isFinish()) {
            // 还没有结束，运行时
            ThreadLog.e(TaskRuntimeListener.TAG, "task running");
            return;
        }
        // 开始
        isFinish = false;
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new RuntimeException("start method must be invoke on MainThread");
        }
        final Task startTask;
        if (task instanceof TaskProject) {
            List<String> taskIds = ((TaskProject) task).getTaskIds();
            mCountDownLatch = new CountDownLatch(mTotalCount = taskIds.size());
            currentCount = mTotalCount;
            addProgressListener(progressUpdate);
            startTask = ((TaskProject) task).getStartTask();
        } else {
            startTask = task;
        }
        // 处理环形依赖，并初始化信息
        TaskRuntime.traversalDependencyTreeAndInit(startTask);
        // 添加线程监听
        if (task instanceof TaskProject) {
            addTaskListener(startTask);
        }
        startTask.start();
        TS.postIo(new Runnable() {
            @Override
            public void run() {
                while (TaskRuntime.hasBlockTasks()) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {

                    }
                    //主线程唤醒之后，存在着等待队列的任务
                    //那么让等待队列中的任务执行
                    while (TaskRuntime.hasBlockTasks()) {
                        if (TaskRuntime.hasWaitingTasks()) {
                            startWaitTask();
                        }
                    }
                }
            }
        });
    }

    private void startWaitTask() {
        MH.postToMain(new Runnable() {
            @Override
            public void run() {
                TaskRuntime.runWaitingTasks();
            }
        });
    }

    public static void concurrence(TaskProject task, TaskListener.ProgressUpdate progressUpdate) {
        List<String> taskIds = task.getTaskIds();
        TaskFlowManager.create().addBlockTasks(taskIds.toArray(new String[]{}))
                .start(task, progressUpdate);
    }

    private void addProgressListener(TaskListener.ProgressUpdate progressUpdate) {
        TS.postIo(10, new Runnable() {
            @SuppressWarnings("BusyWait")
            @Override
            public void run() {
                while (currentCount != 0) {
                    // 线程数量循环，每隔1秒执行一次
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (oldCount != currentCount) {
                        int currentProgress = (int) ((mTotalCount - currentCount) * 100 / mTotalCount);
                        oldCount = currentCount;
                        ThreadLog.e(TaskRuntimeListener.TAG, "progress：" + currentProgress);
                        onProgress(progressUpdate, currentProgress);
                    }
                }
                // 线程结束，完成所有任务
                if (isSuccessFul) {
                    ThreadLog.e(TaskRuntimeListener.TAG, "progress：200");
                    onProgress(progressUpdate, 200);
                } else {
                    ThreadLog.e(TaskRuntimeListener.TAG, "progress：400  errorCount:" + errorCount);
                    onProgress(progressUpdate, 400);
                }
                TaskRuntime.onDestroy();
                isFinish = true;
            }
        });
    }

    private void addTaskListener(Task task) {
        for (Task behindTask : task.getBehindTasks()) {
            if (behindTask instanceof TaskProject.CriticalTask || behindTask instanceof TaskProject) {
                continue;
            }
            behindTask.addTaskListener(new TaskListener.Finished() {

                @Override
                public void onError(Task task) {
                    lock.lock();
                    try {
                        errorCount++;
                        isSuccessFul = false;
                    } finally {
                        lock.unlock();
                    }
                }

                @Override
                public void onFinished(Task task) {
                    lock.lock();
                    try {
                        mCountDownLatch.countDown();
                        currentCount = mCountDownLatch.getCount();
                    } finally {
                        lock.unlock();
                    }
                }
            });
            addTaskListener(behindTask);
        }
    }

    public void onProgress(TaskListener.ProgressUpdate progressUpdate, Integer progress) {
        if (progressUpdate == null) {
            return;
        }
        MH.postToMain(new Runnable() {
            @Override
            public void run() {
                progressUpdate.onProgressUpdate(progress);
            }
        });
    }

}
