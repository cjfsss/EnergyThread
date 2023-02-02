package hos.thread.hander;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


/**
 * <p>Title: MainHandler </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/30 23:38
 */
final class MainHandler implements MainHandlerExecutor {

    private final Object mLock = new Object();

    private static MainHandler sInstance;


    private volatile Handler mMainHandler;


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


    @Override
    public Handler getHandlerMain(Handler.Callback callback) {
        return new Handler(Looper.getMainLooper(), callback);
    }

    @Override
    public void clearCallback() {
        getHandler().removeCallbacksAndMessages(null);
    }

    @Override
    public boolean postDelayed(Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    @Override
    public boolean postAtTime(Runnable runnable, long uptimeMillis) {
        return getHandler().postAtTime(runnable, uptimeMillis);
    }

    @Override
    public void postToMain(Runnable runnable) {
        getHandler().post(runnable);
    }

    @Override
    public void sendAtFrontOfQueue(Runnable runnable) {
        Message msg = Message.obtain(getHandler(), runnable);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    @Override
    public void remove(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    @Override
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

}
