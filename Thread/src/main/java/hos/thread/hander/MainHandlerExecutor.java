package hos.thread.hander;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>Title: MainExecutor </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/31 16:39
 */
public interface MainHandlerExecutor {

    @NonNull
    Handler getHandler();

    @NonNull
    Handler getHandlerMain(@Nullable Handler.Callback callback);

    void clearCallback();

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    boolean postDelayed(@NonNull final Runnable runnable, final long delayMillis);

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    boolean postAtTime(@NonNull final Runnable runnable, final long uptimeMillis);

    /**
     * 切换到主线程
     *
     * @param runnable 主线程
     */
    void postToMain(@NonNull final Runnable runnable);

    /**
     * 在主线程上运行
     *
     * @param runnable 主线程
     */
    default void postOnMain(@NonNull final Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            postToMain(runnable);
        }
    }

    void sendAtFrontOfQueue(@NonNull final Runnable runnable);

    void remove(@NonNull final Runnable runnable);

    boolean isMainThread();
}
