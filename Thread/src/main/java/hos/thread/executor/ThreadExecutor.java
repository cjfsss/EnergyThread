package hos.thread.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import hos.thread.hander.MH;


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
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public void postIo( final Runnable runnable) {
        postIo(0, runnable);
    }

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public abstract void postIo( int priority,  final Runnable runnable);

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    public void postOnIo( final Runnable runnable) {
        if (MH.isMainThread()) {
            postIo(runnable);
        } else {
            runnable.run();
        }
    }

    public abstract <T> Future<T> submit(Callable<T> task);

    public <T> Future<T> submit(Runnable task, T result) {
        return submit(0, task, result);
    }

    public Future<?> submit(Runnable task) {
        return submit(0, task);
    }

    public abstract <T> Future<T> submit( int priority, Runnable task, T result);

    public abstract Future<?> submit( int priority, Runnable task);

    public abstract void pause();

    public abstract void resume();

}
