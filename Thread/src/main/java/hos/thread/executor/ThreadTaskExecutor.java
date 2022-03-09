package hos.thread.executor;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <p>Title: ThreadTaskExecutor </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/9/6 13:49
 */
public class ThreadTaskExecutor extends ThreadExecutor {
    private static volatile ThreadTaskExecutor sInstance;

    @NonNull
    private ThreadExecutor mDelegate;

    @NonNull
    private ThreadExecutor mDefaultTaskExecutor;

    @NonNull
    private static final Executor sMainThreadExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            getInstance().postToMain(command);
        }
    };

    @NonNull
    private static final Executor sIOThreadExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            getInstance().postIo(command);
        }
    };

    private ThreadTaskExecutor() {
        mDefaultTaskExecutor = new DefaultThreadExecutor();
        mDelegate = mDefaultTaskExecutor;
    }

    /**
     * Returns an instance of the task executor.
     *
     * @return The singleton ArchTaskExecutor.
     */
    @NonNull
    public static ThreadTaskExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (ThreadTaskExecutor.class) {
            if (sInstance == null) {
                sInstance = new ThreadTaskExecutor();
            }
        }
        return sInstance;
    }

    /**
     * Sets a delegate to handle task execution requests.
     * <p>
     * If you have a common executor, you can set it as the delegate and App Toolkit components will
     * use your executors. You may also want to use this for your tests.
     * <p>
     * Calling this method with {@code null} sets it to the default TaskExecutor.
     *
     * @param taskExecutor The task executor to handle task requests.
     */
    public void setDelegate(@Nullable ThreadExecutor taskExecutor) {
        mDelegate = taskExecutor == null ? mDefaultTaskExecutor : taskExecutor;
    }

    @NonNull
    public static Executor getMainThreadExecutor() {
        return sMainThreadExecutor;
    }

    @NonNull
    public static Executor getIOThreadExecutor() {
        return sIOThreadExecutor;
    }

    @Override
    public ExecutorService getMultiThread() {
        return mDelegate.getMultiThread();
    }

    @Override
    public ExecutorService getThread() {
        return mDelegate.getThread();
    }

    @NonNull
    @Override
    public Handler getHandler() {
        return mDelegate.getHandler();
    }

    @NonNull
    @Override
    public Handler getHandlerMain(Handler.Callback callback) {
        return mDelegate.getHandlerMain(callback);
    }

    @NonNull
    @Override
    public ThreadExecutor addHandlerCallback(@NonNull Handler.Callback callback) {
        mDelegate.addHandlerCallback(callback);
        return this;
    }

    @NonNull
    @Override
    public ThreadExecutor removeHandlerCallback(@NonNull Handler.Callback callback) {
        mDelegate.removeHandlerCallback(callback);
        return this;
    }

    @Override
    public void clearCallback() {
        mDelegate.clearCallback();
    }

    @Override
    public boolean postDelayed(@NonNull Runnable runnable, long delayMillis) {
        return mDelegate.postDelayed(runnable, delayMillis);
    }

    @Override
    public boolean postAtTime(@NonNull Runnable runnable, long uptimeMillis) {
        return mDelegate.postAtTime(runnable, uptimeMillis);
    }

    @Override
    public void postToMain(@NonNull Runnable runnable) {
        mDelegate.postToMain(runnable);
    }

    @Override
    public void postIo(@NonNull Runnable runnable) {
        mDelegate.postIo(runnable);
    }

    @Override
    public boolean isMainThread() {
        return mDelegate.isMainThread();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return mDelegate.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return mDelegate.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return mDelegate.submit(task);
    }

    @Override
    public void removeCallbacks(@NonNull Runnable r) {
        if (mDelegate == null) {
            return;
        }
        mDelegate.removeCallbacks(r);
    }

    @Override
    public void removeCallbacks(@NonNull Runnable r, @Nullable Object token) {
        if (mDelegate == null) {
            return;
        }
        mDelegate.removeCallbacks(r, token);
    }
}
