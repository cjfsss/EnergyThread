package hos.thread.executor;

import android.os.Handler;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>Title: CallBackground </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 11:46
 */
public abstract class CallBackground<T> implements Runnable {

    private boolean isCancel = false;

    @Override
    public void run() {
        @NonNull
        Handler handler = ThreadTaskExecutor.getInstance().getHandler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isCancel()){
                    onCancel();
                    return;
                }
                onPrepare();
            }
        });
        if (isCancel()){
            postOnCancel();
            return;
        }
        @Nullable T result = null;
        @Nullable Throwable throwable = null;
        try {
            result = onBackground();
        } catch (Exception e) {
            throwable = e;
            result = null;
        } finally {
            @Nullable
            final T finalResult = result;
            final Throwable finalThrowable = throwable;
            //移除所有消息.防止需要执行onCompleted了，onPrepare还没被执行，那就不需要执行了
            handler.removeCallbacksAndMessages(null);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isCancel()){
                        onCancel();
                        return;
                    }
                    if (finalThrowable != null){
                        onError(finalThrowable);
                    } else {
                        try {
                            onCompleted(finalResult);
                        } catch (Exception e) {
                            onError(e);
                        }
                    }
                }
            });
        }
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void cancel() {
        isCancel = true;
    }

    private void postOnCancel(){
        ThreadTaskExecutor.getInstance().postToMain(new Runnable() {
            @Override
            public void run() {
                onCancel();
            }
        });
    }

    protected void onCancel() {

    }

    protected void progressUpdate(@IntRange(from = 0, to = 100) int value) {
        ThreadTaskExecutor.getInstance().postToMain(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(value);
            }
        });
    }

    protected void onProgressUpdate(@IntRange(from = 0, to = 100) int value) {

    }

    /**
     * 运行前
     */
    protected void onPrepare() {

    }

    /**
     * 运行在工作线程
     *
     * @return 返回结果
     */
    @Nullable
    protected abstract T onBackground();

    /**
     * 执行完成
     *
     * @param t 返回结果
     */
    protected abstract void onCompleted(@Nullable T t);

    /**
     * 异常
     * @param e 异常
     */
    protected abstract void onError(@NonNull Throwable e);
}
