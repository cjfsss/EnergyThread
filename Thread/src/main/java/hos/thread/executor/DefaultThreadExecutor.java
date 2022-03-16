package hos.thread.executor;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: DefaultTaskExecutor </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/9/6 13:50
 */
class DefaultThreadExecutor extends ThreadExecutor {

    private static final String LOG_TAG = "DefaultThreadExecutor";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 3;
    private static final int BACKUP_POOL_SIZE = 5;

    private final Object mLock = new Object();


    private final ExecutorService mDiskIO = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            30L, TimeUnit.SECONDS,
            new PriorityBlockingQueue<Runnable>(),
            new ThreadFactory() {
                private static final String THREAD_NAME_STEM = "hos_disk_io_%d";

                private final AtomicInteger mThreadId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName(String.format(THREAD_NAME_STEM, mThreadId.getAndIncrement()));
                    return t;
                }
            });

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };
    // Used only for rejected executions.
    // Initialization protected by sRunOnSerialPolicy lock.
    private static ThreadPoolExecutor sBackupExecutor;
    private static LinkedBlockingQueue<Runnable> sBackupExecutorQueue;

    private static final RejectedExecutionHandler sRunOnSerialPolicy =
            new RejectedExecutionHandler() {
                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                    android.util.Log.w(LOG_TAG, "Exceeded ThreadPoolExecutor pool size");
                    // As a last ditch fallback, run it on an executor with an unbounded queue.
                    // Create this executor lazily, hopefully almost never.
                    synchronized (this) {
                        if (sBackupExecutor == null) {
                            sBackupExecutorQueue = new LinkedBlockingQueue<Runnable>();
                            sBackupExecutor = new ThreadPoolExecutor(
                                    BACKUP_POOL_SIZE, BACKUP_POOL_SIZE, KEEP_ALIVE_SECONDS,
                                    TimeUnit.SECONDS, sBackupExecutorQueue, sThreadFactory);
                            sBackupExecutor.allowCoreThreadTimeOut(true);
                        }
                    }
                    sBackupExecutor.execute(r);
                }
            };

    @Nullable
    private volatile Handler mMainHandler;

    /**
     * 多线程池
     */
    @Nullable
    private ExecutorService mMultiThread;

    @NonNull
    private ExecutorService getExecutorService() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), sThreadFactory);
        threadPoolExecutor.setRejectedExecutionHandler(sRunOnSerialPolicy);
        return threadPoolExecutor;
    }

    @Override
    public ExecutorService getMultiThread() {
        if (mMultiThread == null) {
            mMultiThread = getExecutorService();
        }
        return mMultiThread;
    }

    @Override
    public ExecutorService getThread() {
        return mDiskIO;
    }

    @NonNull
    @Override
    public Handler getHandler() {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = getHandlerMain(null);
                }
            }
        }
        //noinspection ConstantConditions
        return mMainHandler;
    }

    @NonNull
    @Override
    public Handler getHandlerMain(@Nullable Handler.Callback callback) {
        return new Handler(Looper.getMainLooper(), callback);
    }

    @Override
    public void clearCallback() {
        getHandler().removeCallbacksAndMessages(null);
    }

    @Override
    public boolean postDelayed(@NonNull Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    @Override
    public boolean postAtTime(@NonNull Runnable runnable, long uptimeMillis) {
        return getHandler().postAtTime(runnable, uptimeMillis);
    }

    @Override
    public void postToMain(@NonNull Runnable runnable) {
        getHandler().post(runnable);
    }

    @Override
    public void postIo(int priority, @NonNull Runnable runnable) {
        mDiskIO.execute(new PriorityRunnable(priority, runnable));
    }

    @Override
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return mDiskIO.submit(task);
    }

    @Override
    public <T> Future<T> submit(int priority, Runnable task, T result) {
        return mDiskIO.submit(new PriorityRunnable(priority, task), result);
    }

    @Override
    public Future<?> submit(int priority, Runnable task) {
        return mDiskIO.submit(new PriorityRunnable(priority, task));
    }

}
