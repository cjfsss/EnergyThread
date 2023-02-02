package hos.thread.executor;

import hos.thread.utils.ThreadLog;

/**
 * <p>Title: PriorityRunnable </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 9:21
 */
final class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {
    private final int priority;
    private final Runnable runnable;

    public PriorityRunnable(Runnable runnable) {
        this(0, runnable);
    }

    public PriorityRunnable(int priority, Runnable runnable) {
        this.priority = priority;
        this.runnable = runnable;
    }

    int getPriority() {
        return priority;
    }

    @Override
    public void run() {
        ThreadLog.d("run start");
        try {
            runnable.run();
        } catch (Exception e) {
            ThreadLog.e("run error", e);
            throw e;
        } finally {
            ThreadLog.d("run end");
        }
    }

    @Override
    public int compareTo(PriorityRunnable other) {
        return Integer.compare(other.priority, this.priority);
    }
}
