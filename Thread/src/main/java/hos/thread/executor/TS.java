package hos.thread.executor;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * <p>Title: ThreadStatic </p>
 * <p>Description: 线程静态方法 </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/12/28 17:11
 */
public final class TS {

    private TS() {
    }

    public static void setDelegate(@Nullable ThreadExecutor taskExecutor) {
        ThreadTaskExecutor.getInstance().setDelegate(taskExecutor);
    }

    @NonNull
    public static Handler getHandler() {
        return ThreadTaskExecutor.getInstance().getHandler();
    }

    @NonNull
    public static Handler getHandlerMain(@Nullable Handler.Callback callback) {
        return ThreadTaskExecutor.getInstance().getHandlerMain(callback);
    }

    @NonNull
    public static ThreadExecutor addHandlerCallback(@NonNull Handler.Callback callback) {
        return ThreadTaskExecutor.getInstance().addHandlerCallback(callback);
    }

    @NonNull
    public static ThreadExecutor removeHandlerCallback(@NonNull Handler.Callback callback) {
        return ThreadTaskExecutor.getInstance().removeHandlerCallback(callback);
    }

    public static void clear() {
        ThreadTaskExecutor.getInstance().clear();
    }

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    public static boolean postDelayed(@NonNull final Runnable runnable, final long delayMillis) {
        return ThreadTaskExecutor.getInstance().postDelayed(runnable, delayMillis);
    }

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    public static boolean postAtTime(@NonNull final Runnable runnable, final long uptimeMillis) {
        return ThreadTaskExecutor.getInstance().postAtTime(runnable, uptimeMillis);
    }

    /**
     * 切换到主线程
     *
     * @param runnable 主线程
     */
    public static void postToMain(@NonNull final Runnable runnable) {
        ThreadTaskExecutor.getInstance().postToMain(runnable);
    }

    /**
     * 在主线程上运行
     *
     * @param runnable 主线程
     */
    public static void postOnMain(@NonNull final Runnable runnable) {
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
    public static void postIo(@NonNull final Runnable runnable) {
        ThreadTaskExecutor.getInstance().postIo(runnable);
    }

    /**
     * Returns true if the current thread is the main thread, false otherwise.
     *
     * @return true if we are on the main thread, false otherwise.
     */
    public static boolean isMainThread() {
        return ThreadTaskExecutor.getInstance().isMainThread();
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return ThreadTaskExecutor.getInstance().submit(task);
    }

    public static <T> Future<T> submit(Runnable task, T result) {
        return ThreadTaskExecutor.getInstance().submit(task, result);
    }

    public static Future<?> submit(Runnable task) {
        return ThreadTaskExecutor.getInstance().submit(task);
    }

    public static void removeCallbacks(@NonNull Runnable r) {
        ThreadTaskExecutor.getInstance().removeCallbacks(r);
    }

    public static void removeCallbacks(@NonNull Runnable r, @Nullable Object token) {
        ThreadTaskExecutor.getInstance().removeCallbacks(r, token);
    }
}
