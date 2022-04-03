package hos.thread.executor;

import android.os.Handler;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import hos.thread.hander.MainHandler;

/**
 * <p>Title: TaskExecutor </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/9/6 13:50
 */
public abstract class ThreadExecutor {

    public abstract ExecutorService getMultiThread();

    public abstract ExecutorService getThread();

    @NonNull
    @Deprecated
    public Handler getHandler() {
        return MainHandler.getInstance().getHandler();
    }

    @NonNull
    @Deprecated
    public Handler getHandlerMain(@Nullable Handler.Callback callback) {
        return MainHandler.getInstance().getHandlerMain(callback);
    }

    @Deprecated
    public void clearCallback() {
        MainHandler.getInstance().clearCallback();
    }

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    @Deprecated
    public boolean postDelayed(@NonNull final Runnable runnable, final long delayMillis) {
        return MainHandler.getInstance().postDelayed(runnable, delayMillis);
    }

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    @Deprecated
    public boolean postAtTime(@NonNull final Runnable runnable, final long uptimeMillis) {
        return MainHandler.getInstance().postAtTime(runnable, uptimeMillis);
    }

    /**
     * 切换到主线程
     *
     * @param runnable 主线程
     */
    @Deprecated
    public void postToMain(@NonNull final Runnable runnable) {
        MainHandler.getInstance().postToMain(runnable);
    }

    /**
     * 在主线程上运行
     *
     * @param runnable 主线程
     */
    @Deprecated
    public void postOnMain(@NonNull final Runnable runnable) {
        MainHandler.getInstance().postOnMain(runnable);
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public void postIo(@NonNull final Runnable runnable) {
        postIo(0, runnable);
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public abstract void postIo(@IntRange(from = 0, to = 10) int priority, @NonNull final Runnable runnable);

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public void postOnIo(@NonNull final Runnable runnable) {
        if (MainHandler.getInstance().isMainThread()) {
            postIo(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * Returns true if the current thread is the main thread, false otherwise.
     *
     * @return true if we are on the main thread, false otherwise.
     */
    @Deprecated
    public boolean isMainThread() {
        return MainHandler.getInstance().isMainThread();
    }

    public abstract <T> Future<T> submit(Callable<T> task);

    public <T> Future<T> submit(Runnable task, T result) {
        return submit(0, task, result);
    }

    public Future<?> submit(Runnable task) {
        return submit(0, task);
    }

    public abstract <T> Future<T> submit(@IntRange(from = 0, to = 10) int priority, Runnable task, T result);

    public abstract Future<?> submit(@IntRange(from = 0, to = 10) int priority, Runnable task);

    public abstract void pause();

    public abstract void resume();

}
