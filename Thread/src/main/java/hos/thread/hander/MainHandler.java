package hos.thread.hander;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>Title: MainHandler </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/30 23:38
 */
public class MainHandler implements MainHandlerExecutor {

    private final Object mLock = new Object();

    private static MainHandler sInstance;

    @Nullable
    private volatile Handler mMainHandler;

    @NonNull
    public static MainHandler getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (MainHandler.class) {
            if (sInstance == null) {
                sInstance = new MainHandler();
            }
        }
        return sInstance;
    }

    private MainHandler() {
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
    public void sendAtFrontOfQueue(@NonNull Runnable runnable) {
        Message msg = Message.obtain(getHandler(), runnable);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    @Override
    public void remove(@NonNull Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    @Override
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

}
