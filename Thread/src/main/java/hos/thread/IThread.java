package hos.thread;

import hos.thread.executor.TS;
import hos.thread.hander.MH;

/**
 * <p>Title: IThread </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2023-02-02 18:06
 */
public interface IThread {

    default void postIo(Runnable runnable) {
        TS.postIo(runnable);
    }

    default void postIo(int priority, final Runnable runnable) {
        TS.postIo(priority, runnable);
    }

    default void postOnIo(Runnable runnable) {
        TS.postOnIo(runnable);
    }

    default void postToMain(Runnable runnable) {
        MH.postToMain(runnable);
    }

    default void postOnMain(Runnable runnable) {
        MH.postOnMain(runnable);
    }

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    default boolean postDelayed(final Runnable runnable, final long delayMillis) {
        return MH.postDelayed(runnable, delayMillis);
    }

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    default boolean postAtTime(final Runnable runnable, final long uptimeMillis) {
        return MH.postAtTime(runnable, uptimeMillis);
    }

    default boolean isMainThread() {
        return MH.isMainThread();
    }
}
