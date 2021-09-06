package hos.thread.executor;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    public abstract boolean postDelayed(@NonNull final Runnable runnable, final long delayMillis);

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    public abstract boolean postAtTime(@NonNull final Runnable runnable, final long uptimeMillis);

    /**
     * 切换到主线程
     *
     * @param runnable 主线程
     */
    public abstract void  postToMain(@NonNull final Runnable runnable);

    /**
     * 在主线程上运行
     *
     * @param runnable 主线程
     */
    public void postOnMain(@NonNull final Runnable runnable) {

        if (isMainThread()) {
            runnable.run();
        } else {
            postToMain(runnable);
        }
    }


    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public abstract void postIo(@NonNull final Runnable runnable);

    /**
     * Returns true if the current thread is the main thread, false otherwise.
     *
     * @return true if we are on the main thread, false otherwise.
     */
    public abstract boolean isMainThread();
}
