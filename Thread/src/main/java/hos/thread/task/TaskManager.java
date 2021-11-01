package hos.thread.task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
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
public class TaskManager<Params, Progress, Result> {

    private static final String TAG = "TaskManager";

    private CountDownLatch mCountDownLatch;
    private int mCurrentProgress;
    private int mTotalCount;
    private IProgressUpdate<Integer> mProgressUpdateThread;
    private List<TaskThread<Params, Progress, Result>> mTaskList;
    private AsyncTask<CountDownLatch, Integer, Boolean> countDownLatchAsyncTask;

    private TaskManager<Params, Progress, Result> setTotalCount(int totalCount) {
        mTotalCount = totalCount;
        return this;
    }


    public TaskManager<Params, Progress, Result> setProgressUpdate(@NonNull IProgressUpdate<Integer> observer) {
        mProgressUpdateThread = observer;
        return this;
    }

    /**
     * 线程执行
     *
     * @param taskList 要执行的
     * @return
     */
    public TaskManager<Params, Progress, Result> setTaskList(@NonNull final List<TaskThread<Params, Progress, Result>> taskList) {
        mTaskList = taskList;
        return setTotalCount(mTaskList.size());
    }

    /**
     * 添加计数器任务
     */
    private void setViewActive(@NonNull IPostExecute<Boolean> observer) {
        // 计数器
        mCountDownLatch = new CountDownLatch(mTotalCount);
        countDownLatchAsyncTask = new TaskThread<CountDownLatch, Integer, Boolean>()
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
                .setPostExecute(new IPostExecute<Boolean>() {
                    @Override
                    public void onPostExecute(@NonNull Boolean isSuccess) {
                        clear();
                        observer.onPostExecute(isSuccess);
                    }
                }).startOnExecutor(mCountDownLatch);
    }

    public final void startOnExecutor(@NonNull IPostExecute<Boolean> observer) {
        if (mTaskList == null) {
            return;
        }
        for (TaskThread<Params, Progress, Result> task : mTaskList) {
            task.setPostExecute(new IPostExecute<Result>() {
                @Override
                public void onPostExecute(@NonNull Result result) {
                    // 一个任务完成
                    mCountDownLatch.countDown();
                    long currentCount = mCountDownLatch.getCount();
                    mCurrentProgress = (int) (mTotalCount - currentCount) * 100 / mTotalCount;
                    if (mProgressUpdateThread != null) {
                        mProgressUpdateThread.onProgressUpdate(mCurrentProgress);
                    }
                }
            }).startOnExecutor();
        }
        setViewActive(observer);
    }

    public final void start(@NonNull IPostExecute<Boolean> observer) {
        if (mTaskList == null) {
            return;
        }
        for (TaskThread<Params, Progress, Result> task : mTaskList) {
            task.setPostExecute(new IPostExecute<Result>() {
                @Override
                public void onPostExecute(@NonNull Result result) {
                    // 一个任务完成
                    mCountDownLatch.countDown();
                    long currentCount = mCountDownLatch.getCount();
                    mCurrentProgress = (int) (mTotalCount - currentCount) * 100 / mTotalCount;
                    if (mProgressUpdateThread != null) {
                        mProgressUpdateThread.onProgressUpdate(mCurrentProgress);
                    }
                }
            }).start();
        }
        setViewActive(observer);
    }

    /**
     * 取消运行
     * @param mayInterruptIfRunning 是否终止运行
     */
    public void cancel(boolean mayInterruptIfRunning) {
        for (TaskThread<Params, Progress, Result> taskThread : mTaskList) {
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
