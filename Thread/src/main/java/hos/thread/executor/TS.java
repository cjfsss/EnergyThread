package hos.thread.executor;

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

    public static void setDelegate(ThreadExecutor taskExecutor) {
        ThreadTaskExecutor.getInstance().setDelegate(taskExecutor);
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public static void postIo(final Runnable runnable) {
        ThreadTaskExecutor.getInstance().postIo(runnable);
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public static void postIo(int priority, final Runnable runnable) {
        ThreadTaskExecutor.getInstance().postIo(priority, runnable);
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public static void postOnIo(final Runnable runnable) {
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

    public static <T> Future<T> submit(int priority, Runnable task, T result) {
        return ThreadTaskExecutor.getInstance().submit(priority, task, result);
    }

    public static Future<?> submit(int priority, Runnable task) {
        return ThreadTaskExecutor.getInstance().submit(priority, task);
    }

    public static void pause() {
        ThreadTaskExecutor.getInstance().pause();
    }

    public static void resume() {
        ThreadTaskExecutor.getInstance().resume();
    }
}
