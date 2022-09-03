package hos.thread.executor;

import android.os.Handler;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import hos.thread.hander.MainHandler;

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

    public static void setDelegate( ThreadExecutor taskExecutor) {
        ThreadTaskExecutor.getInstance().setDelegate(taskExecutor);
    }

    
    @Deprecated
    public static Handler getHandler() {
        return MainHandler.getInstance().getHandler();
    }

    
    @Deprecated
    public static Handler getHandlerMain( Handler.Callback callback) {
        return MainHandler.getInstance().getHandlerMain(callback);
    }
    @Deprecated
    public static void clear() {
        MainHandler.getInstance().clearCallback();
    }

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    @Deprecated
    public static boolean postDelayed( final Runnable runnable, final long delayMillis) {
        return MainHandler.getInstance().postDelayed(runnable, delayMillis);
    }

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    @Deprecated
    public static boolean postAtTime( final Runnable runnable, final long uptimeMillis) {
        return MainHandler.getInstance().postAtTime(runnable, uptimeMillis);
    }

    /**
     * 切换到主线程
     *
     * @param runnable 主线程
     */
    @Deprecated
    public static void postToMain( final Runnable runnable) {
        MainHandler.getInstance().postToMain(runnable);
    }

    /**
     * 在主线程上运行
     *
     * @param runnable 主线程
     */
    @Deprecated
    public static void postOnMain( final Runnable runnable) {
        MainHandler.getInstance().postOnMain(runnable);
    }

    /**
     * Returns true if the current thread is the main thread, false otherwise.
     *
     * @return true if we are on the main thread, false otherwise.
     */
    @Deprecated
    public static boolean isMainThread() {
        return MainHandler.getInstance().isMainThread();
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public static void postIo( final Runnable runnable) {
        ThreadTaskExecutor.getInstance().postIo(runnable);
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public static void postIo( int priority,  final Runnable runnable) {
        ThreadTaskExecutor.getInstance().postIo(priority, runnable);
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public static void postOnIo( final Runnable runnable) {
        ThreadTaskExecutor.getInstance().postOnIo(runnable);
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

    public static <T> Future<T> submit( int priority, Runnable task, T result) {
        return ThreadTaskExecutor.getInstance().submit(priority, task, result);
    }

    public static Future<?> submit( int priority, Runnable task) {
        return ThreadTaskExecutor.getInstance().submit(priority, task);
    }

    public static void pause() {
        ThreadTaskExecutor.getInstance().pause();
    }

    public static void resume() {
        ThreadTaskExecutor.getInstance().resume();
    }
}
