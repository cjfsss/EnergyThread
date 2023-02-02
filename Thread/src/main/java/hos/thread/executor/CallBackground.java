package hos.thread.executor;

import android.os.Handler;

import hos.thread.hander.MH;
import hos.thread.utils.ThreadLog;


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
        ThreadLog.e("threadName：" + Thread.currentThread().getName());
        MH.postToMain(new Runnable() {
            @Override
            public void run() {
                if (isCancel()) {
                    ThreadLog.d("onCancel");
                    // 取消
                    onCancel();
                    return;
                }
                // 运行前
                ThreadLog.d("onPrepare");
                onPrepare();
            }
        });
        if (isCancel()) {
            // 取消
            ThreadLog.d("onCancel");
            postOnCancel();
            return;
        }
        T result = null;
        Throwable throwable = null;
        try {
            // 运行
            ThreadLog.d("onBackground");
            result = onBackground();
            ThreadLog.d("onBackgroundSuccess");
        } catch (Exception e) {
            throwable = e;
        } finally {
            final T finalResult = result;
            final Throwable finalThrowable = throwable;
            //移除所有消息.防止需要执行onCompleted了，onPrepare还没被执行，那就不需要执行了
//            MH.getHandler().removeCallbacksAndMessages(null);
            MH.postToMain(new Runnable() {
                @Override
                public void run() {
                    if (isCancel()) {
                        // 取消
                        ThreadLog.d("onCancel");
                        onCancel();
                        return;
                    }
                    if (finalThrowable != null) {
                        // 失败
                        ThreadLog.e("onError", finalThrowable);
                        onError(finalThrowable);
                        return;
                    }
                    try {
                        // 执行完成
                        ThreadLog.d("onCompleted");
                        onCompleted(finalResult);
                    } catch (Exception e) {
                        // 异常
                        ThreadLog.e("onError", e);
                        onError(e);
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

    private void postOnCancel() {
        MH.postToMain(new Runnable() {
            @Override
            public void run() {
                onCancel();
            }
        });
    }

    protected void onCancel() {

    }

    protected void progressUpdate(int value) {
        MH.postToMain(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(value);
            }
        });
    }

    protected void onProgressUpdate(int value) {

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

    protected abstract T onBackground() throws Exception;

    /**
     * 执行完成
     *
     * @param t 返回结果
     */
    protected abstract void onCompleted(T t);

    /**
     * 异常
     *
     * @param e 异常
     */
    protected abstract void onError(Throwable e);
}
