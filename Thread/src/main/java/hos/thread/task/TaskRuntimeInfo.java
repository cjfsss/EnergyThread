package hos.thread.task;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>Title: TaskRuntimeInfo </p>
 * <p>Description: 用于记录每一个task信息 </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:22
 */
class TaskRuntimeInfo {
    @NonNull
    private final SparseArray<Long> stateTime = new SparseArray<Long>();
    private boolean isBlockTask = false;
    private String threadName;
    private Throwable throwable;
    private final Task task;

    public TaskRuntimeInfo(@NonNull Task task) {
        this.task = task;
    }

    public void setStateTime(int state, long time) {
        this.stateTime.put(state, time);
    }

    public Long getStateTime(int state) {
        return stateTime.get(state);
    }

    public boolean isSameTask(@Nullable Task task) {
        return task != null && task == this.task;
    }

    @NonNull
    public Task getTask() {
        return task;
    }

    public boolean isBlockTask() {
        return isBlockTask;
    }

    public void setBlockTask(boolean blockTask) {
        isBlockTask = blockTask;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getThreadName() {
        return threadName;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return "TaskRuntimeInfo{" +
                "stateTime=" + stateTime +
                ", isBlockTask=" + isBlockTask +
                ", threadName='" + threadName + '\'' +
                ", throwable=" + throwable +
                ", task=" + task +
                '}';
    }
}
