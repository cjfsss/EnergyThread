package hos.thread.executor;


import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import hos.thread.hander.MH;

/**
 * <p>Title: ThreadTaskExecutor </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/9/6 13:49
 */
final class ThreadTaskExecutor extends ThreadExecutor {
    private static volatile ThreadTaskExecutor sInstance;

    
    private ThreadExecutor mDelegate;

    
    private final ThreadExecutor mDefaultTaskExecutor;

    
    private static final Executor sMainThreadExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            MH.postToMain(command);
        }
    };

    
    private static final Executor sIOThreadExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            getInstance().postIo(command);
        }
    };

    private ThreadTaskExecutor() {
        mDefaultTaskExecutor = new DefaultThreadExecutor();
        mDelegate = mDefaultTaskExecutor;
    }

    /**
     * Returns an instance of the task executor.
     *
     * @return The singleton ArchTaskExecutor.
     */
    
    public static ThreadTaskExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (ThreadTaskExecutor.class) {
            if (sInstance == null) {
                sInstance = new ThreadTaskExecutor();
            }
        }
        return sInstance;
    }

    /**
     * Sets a delegate to handle task execution requests.
     * <p>
     * If you have a common executor, you can set it as the delegate and App Toolkit components will
     * use your executors. You may also want to use this for your tests.
     * <p>
     * Calling this method with {@code null} sets it to the default TaskExecutor.
     *
     * @param taskExecutor The task executor to handle task requests.
     */
    public void setDelegate( ThreadExecutor taskExecutor) {
        mDelegate = taskExecutor == null ? mDefaultTaskExecutor : taskExecutor;
    }

    
    public static Executor getMainThreadExecutor() {
        return sMainThreadExecutor;
    }

    
    public static Executor getIOThreadExecutor() {
        return sIOThreadExecutor;
    }

    @Override
    public ExecutorService getMultiThread() {
        return mDelegate.getMultiThread();
    }

    @Override
    public ExecutorService getThread() {
        return mDelegate.getThread();
    }

    @Override
    public void postIo( Runnable runnable) {
        mDelegate.postIo(runnable);
    }

    @Override
    public void postIo(int priority,  Runnable runnable) {
        mDelegate.postIo(priority, runnable);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return mDelegate.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return mDelegate.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return mDelegate.submit(task);
    }

    @Override
    public <T> Future<T> submit(int priority, Runnable task, T result) {
        return mDelegate.submit(priority, task, result);
    }

    @Override
    public Future<?> submit(int priority, Runnable task) {
        return mDelegate.submit(priority, task);
    }

    @Override
    public void pause() {
        mDelegate.pause();
    }

    @Override
    public void resume() {
        mDelegate.resume();
    }

}
