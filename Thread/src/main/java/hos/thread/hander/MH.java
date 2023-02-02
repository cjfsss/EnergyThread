package hos.thread.hander;

import android.os.Handler;




/**
 * <p>Title: MH </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/31 21:54
 */
public class MH {

    private MH() {
    }

    public static Handler getHandler() {
        return MainHandler.getInstance().getHandler();
    }

    public static Handler getHandlerMain( Handler.Callback callback) {
        return MainHandler.getInstance().getHandlerMain(callback);
    }

    public static void clear() {
        MainHandler.getInstance().clearCallback();
    }

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    public static boolean postDelayed( final Runnable runnable, final long delayMillis) {
        return MainHandler.getInstance().postDelayed(runnable, delayMillis);
    }

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    public static boolean postAtTime( final Runnable runnable, final long uptimeMillis) {
        return MainHandler.getInstance().postAtTime(runnable, uptimeMillis);
    }

    /**
     * 切换到主线程
     *
     * @param runnable 主线程
     */
    public static void postToMain( final Runnable runnable) {
        MainHandler.getInstance().postToMain(runnable);
    }

    /**
     * 在主线程上运行
     *
     * @param runnable 主线程
     */
    public static void postOnMain( final Runnable runnable) {
        MainHandler.getInstance().postOnMain(runnable);
    }

    public void sendAtFrontOfQueue( final Runnable runnable) {
        MainHandler.getInstance().sendAtFrontOfQueue(runnable);
    }

    public void remove( final Runnable runnable) {
        MainHandler.getInstance().remove(runnable);
    }

    /**
     * Returns true if the current thread is the main thread, false otherwise.
     *
     * @return true if we are on the main thread, false otherwise.
     */
    public static boolean isMainThread() {
        return MainHandler.getInstance().isMainThread();
    }
}
