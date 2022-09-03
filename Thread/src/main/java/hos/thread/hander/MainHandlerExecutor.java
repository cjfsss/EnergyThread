package hos.thread.hander;

import android.os.Handler;




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

    
    Handler getHandler();

    
    Handler getHandlerMain( Handler.Callback callback);

    void clearCallback();

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    boolean postDelayed( final Runnable runnable, final long delayMillis);

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    boolean postAtTime( final Runnable runnable, final long uptimeMillis);

    /**
     * 切换到主线程
     *
     * @param runnable 主线程
     */
    void postToMain( final Runnable runnable);

    /**
     * 在主线程上运行
     *
     * @param runnable 主线程
     */
    default void postOnMain( final Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            postToMain(runnable);
        }
    }

    void sendAtFrontOfQueue( final Runnable runnable);

    void remove( final Runnable runnable);

    boolean isMainThread();
}
