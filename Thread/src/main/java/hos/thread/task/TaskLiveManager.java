package hos.thread.task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import hos.thread.interfaces.IDoInBackground;
import hos.thread.interfaces.IPostExecute;
import hos.thread.interfaces.IProgressUpdate;


/**
 * <p>Title: TaskManager </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2020/2/21 12:03
 */
public class TaskLiveManager<Params, Progress, Result> {

    private static final String TAG = "TaskManager";

    private CountDownLatch mCountDownLatch;
    private int mCurrentProgress;
    private int mTotalCount;
    private MutableLiveData<Integer> mProgressUpdate;
    private List<TaskLive<Params, Progress, Result>> mTaskList;
    private AsyncTask<CountDownLatch, Integer, Boolean> countDownLatchAsyncTask;

    private TaskLiveManager<Params, Progress, Result> setTotalCount(int totalCount) {
        mTotalCount = totalCount;
        return this;
    }

    public TaskLiveManager<Params, Progress, Result> setProgressUpdate(@NonNull LifecycleOwner owner, @NonNull Observer<Integer> observer) {
        this.mProgressUpdate = new MutableLiveData<>();
        mProgressUpdate.observe(owner, observer);
        return this;
    }


    /**
     * 线程执行
     *
     * @param taskList 要执行的
     * @return
     */
    public TaskLiveManager<Params, Progress, Result> setTaskList(@NonNull final List<TaskLive<Params, Progress, Result>> taskList) {
        mTaskList = taskList;
        return setTotalCount(mTaskList.size());
    }


    /**
     * 添加计数器任务
     */
    private void setViewActive(@NonNull LifecycleOwner owner, @NonNull Observer<Boolean> observer) {
        // 计数器
        mCountDownLatch = new CountDownLatch(mTotalCount);
        countDownLatchAsyncTask = new TaskLive<CountDownLatch, Integer, Boolean>()
                .setDoInBackground(new IDoInBackground<CountDownLatch, Integer, Boolean>() {

                    @Override
                    public Boolean doInBackground(IProgressUpdate<Integer> progressUpdate, @Nullable List<CountDownLatch> countDownLatches) {
                        try {
                            if (countDownLatches == null) {
                                throw new NullPointerException("param is null");
                            }
                            countDownLatches.get(0).await();
                            return true;//顺利完成
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                })
                .setPostExecute(owner, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isSuccess) {
                        clear();
                        observer.onChanged(isSuccess);
                    }
                }).startOnExecutor(mCountDownLatch);
    }

    public final void startOnExecutor(@NonNull LifecycleOwner owner, @NonNull Observer<Boolean> observer) {
        if (mTaskList == null) {
            return;
        }
        int size = mTaskList.size();
        for (int i = 0; i < size; i++) {
            TaskLive<Params, Progress, Result> task = mTaskList.get(i);
            task.setIndex(i).setPostExecute(owner, (Observer<Result>) o -> {
                // 一个任务完成
                mCountDownLatch.countDown();
                long currentCount = mCountDownLatch.getCount();
                mCurrentProgress = (int) (mTotalCount - currentCount) * 100 / mTotalCount;
                if (mProgressUpdate != null) {
                    mProgressUpdate.postValue(mCurrentProgress);
                }
            }).setPostExecuteIndex(owner, new Observer<Integer>() {
                @SuppressWarnings("SuspiciousMethodCalls")
                @Override
                public void onChanged(Integer index) {
                    if (index != null && mTaskList instanceof Vector) {
                        mTaskList.remove(index);
                    }
                }
            }).startOnExecutor();
        }
        setViewActive(owner, observer);
    }

    public final void start(@NonNull LifecycleOwner owner, @NonNull Observer<Boolean> observer) {
        if (mTaskList == null) {
            return;
        }
        int size = mTaskList.size();
        for (int i = 0; i < size; i++) {
            TaskLive<Params, Progress, Result> task = mTaskList.get(i);
            task.setIndex(i).setPostExecute(owner, (Observer<Result>) o -> {
                // 一个任务完成
                mCountDownLatch.countDown();
                long currentCount = mCountDownLatch.getCount();
                mCurrentProgress = (int) (mTotalCount - currentCount) * 100 / mTotalCount;
                if (mProgressUpdate != null) {
                    mProgressUpdate.postValue(mCurrentProgress);
                }
            }).setPostExecuteIndex(owner, new Observer<Integer>() {
                @SuppressWarnings("SuspiciousMethodCalls")
                @Override
                public void onChanged(Integer index) {
                    if (index != null && mTaskList instanceof Vector) {
                        mTaskList.remove(index);
                    }
                }
            }).start();
        }
        setViewActive(owner, observer);
    }

    /**
     * 取消运行
     *
     * @param mayInterruptIfRunning 是否终止运行
     */
    public void cancel(boolean mayInterruptIfRunning) {
        for (TaskLive<Params, Progress, Result> taskThread : mTaskList) {
            if (taskThread != null && !taskThread.isCancelled()) {
                taskThread.cancel(mayInterruptIfRunning);
            }
        }
        if (countDownLatchAsyncTask != null && !countDownLatchAsyncTask.isCancelled()) {
            countDownLatchAsyncTask.cancel(mayInterruptIfRunning);
        }
    }

    public void clear() {
        if (mTaskList != null) {
            mTaskList.clear();
        }
    }
}
