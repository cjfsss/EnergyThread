package hos.thread.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import hos.thread.interfaces.IDoInBackgroundLiveData;


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
        new TaskLive<CountDownLatch, Integer, Boolean>()
                .setDoInBackground(new IDoInBackgroundLiveData<CountDownLatch, Integer, Boolean>() {
                    @Override
                    public Boolean doInBackground(@NonNull MutableLiveData<Integer> publishProgress, @Nullable List<CountDownLatch> countDownLatches) {
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
        for (TaskLive<Params, Progress, Result> task : mTaskList) {
            task.setPostExecute(owner, (Observer<Result>) o -> {
                // 一个任务完成
                mCountDownLatch.countDown();
                long currentCount = mCountDownLatch.getCount();
                mCurrentProgress = (int) (mTotalCount - currentCount) * 100 / mTotalCount;
                if (mProgressUpdate != null) {
                    mProgressUpdate.postValue(mCurrentProgress);
                }
            }).startOnExecutor();
        }
        setViewActive(owner, observer);
    }

    public final void start(@NonNull LifecycleOwner owner, @NonNull Observer<Boolean> observer) {
        if (mTaskList == null) {
            return;
        }
        for (TaskLive<Params, Progress, Result> task : mTaskList) {
            task.setPostExecute(owner, (Observer<Result>) o -> {
                // 一个任务完成
                mCountDownLatch.countDown();
                long currentCount = mCountDownLatch.getCount();
                mCurrentProgress = (int) (mTotalCount - currentCount) * 100 / mTotalCount;
                if (mProgressUpdate != null) {
                    mProgressUpdate.postValue(mCurrentProgress);
                }
            }).start();
        }
        setViewActive(owner, observer);
    }

    public void clear() {
        if (mTaskList != null) {
            mTaskList.clear();
        }
    }
}
