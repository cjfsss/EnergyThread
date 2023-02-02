package hos.thread.task;


import android.os.Build;
import android.os.Trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import hos.thread.BuildConfig;
import hos.thread.utils.ThreadLog;

/**
 * <p>Title: Task </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:37
 */
public abstract class Task implements Runnable, Comparable<Task> {
    
    private final String id;
    private final boolean isAsyncTask;
    private final long delayMills;
    private int priority;

    private long executeTime;// 任务执行时间
    private int state;// 任务执行状态
    // 用于运行时log，输出统计，输出当前的task, 依赖了哪些前置任务，这些前置任务的名称，我们将它存储在这里
    
    private final List<String> dependTasksName = new ArrayList<>();
    // 当前Task依赖的那些前置任务，只有当dependTasks中的所有任务执行完成，当前任务才可以执行
    
    private final List<Task> dependTasks = new ArrayList<>();
    // 当前Task被哪些后置任务依赖，只有当当前任务执行完成，behindTasks集合中任务才可以执行
    
    private final List<Task> behindTasks = new ArrayList<>();
    
    private final List<TaskListener> taskListeners = new ArrayList<>();
    
    private final List<Object> paramList = new ArrayList<>();
    // 运行时日志
    
    private TaskRuntimeListener taskRuntimeListener;

    
    private Throwable throwable;

    public void addTaskListener( TaskListener listener) {
        if (!taskListeners.contains(listener)) {
            taskListeners.add(listener);
        }
    }

    public void removeTaskListener( TaskListener listener) {
        taskListeners.remove(listener);
    }


    public Task( String id, boolean isAsyncTask, long delayMills, int priority, Object params) {
        this.id = id;
        this.isAsyncTask = isAsyncTask;
        this.delayMills = delayMills;
        this.priority = priority;
        if (params != null) {
            if (params instanceof List) {
                this.paramList.addAll((Collection<?>) params);
            } else {
                this.paramList.add(params);
            }
        }
        if (BuildConfig.DEBUG) {
            taskRuntimeListener = new TaskRuntimeListener();
        }
    }

    public Task( String id, boolean isAsyncTask, long delayMills, int priority) {
        this(id, isAsyncTask, delayMills, priority, null);
    }

    public Task( String id, boolean isAsyncTask, Object params) {
        this(id, isAsyncTask, 0, 0, params);
    }

    public Task( String id, boolean isAsyncTask) {
        this(id, isAsyncTask, null);
    }

    public Task( String id, Object params) {
        this(id, true, params);
    }

    public Task( String id) {
        this(id, true);
    }

    
    public String getId() {
        return id;
    }

    int getPriority() {
        return priority;
    }

    void setPriority(int priority) {
        this.priority = priority;
    }

    void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    long getDelayMills() {
        return delayMills;
    }

    public boolean isAsyncTask() {
        return isAsyncTask;
    }

    public int getState() {
        return state;
    }

    
    List<Task> getDependTasks() {
        return dependTasks;
    }

    
    List<Task> getBehindTasks() {
        return behindTasks;
    }

    
    List<String> getDependTasksName() {
        return dependTasksName;
    }

    
    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isSuccessFul() {
        return throwable == null;
    }

    
    public Task param( Object... params) {
        paramList.addAll(Arrays.asList(params));
        return this;
    }

    
    public List<Object> getParamList() {
        return paramList;
    }

    public void start() {
        if (state != TaskStatus.IDLE) {
            throw new RuntimeException("cannot run task " + id + " again");
        }
        toStart();
        executeTime = System.currentTimeMillis();
        TaskRuntime.executeTask(this);
    }

    private void toStart() {
        state = TaskStatus.START;
        TaskRuntime.setStateInfo(this);
        for (TaskListener listener : taskListeners) {
            listener.onStart(this);
        }
        if (taskRuntimeListener != null) {
            taskRuntimeListener.onStart(this);
        }
    }

    @Override
    public void run() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.beginSection(id);
        }
        toRunning();
        // 真正执行任务的方法
        try {
            run(id);
        } catch (Throwable e) {
            throwable = e;
            ThreadLog.e(TaskRuntimeListener.TAG, "error", e);
            toError();
        }
        toFinished();
        // 通知其他后置任务去执行
        notifyBehindTasks();
        recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }
    }

    protected void recycle() {
        dependTasks.clear();
        behindTasks.clear();
        taskListeners.clear();
        paramList.clear();
        taskRuntimeListener = null;
    }

    private void notifyBehindTasks() {
        // 通知后置任务尝试去执行
        if (!behindTasks.isEmpty()) {
            if (behindTasks.size() > 1) {
                Collections.sort(behindTasks, TaskRuntime.taskComparator);
            }
            // 遍历behindTasks后置任务，通知他们，告诉他们你的一个前置依赖任务已经执行完成
            for (Task behindTask : behindTasks) {
                // A behindTask -> (B,C ) A执行完成后，B/C才可以执行
                behindTask.dependTaskFinished(this);
            }
        }
    }

    private void dependTaskFinished( Task dependTask) {
        // A behindTask -> (B,C ) A执行完成后，B/C才可以执行
        // task->B,C   dependTask -> A
        if (dependTasks.isEmpty()) {
            return;
        }
        // 把A从  B/C的前置依赖任务中 集合移除
        dependTasks.remove(dependTask);
        // B/C所有的前置任务，是否都执行完成了
        if (dependTasks.isEmpty()) {
            start();
        }
    }

    // 给当前task   添加一个  前置依赖任务
    protected void dependOn( Task dependTask) {
        Task task = dependTask;
        if (task != this) {
            if (dependTask instanceof TaskProject) {
                task = ((TaskProject) dependTask).getEndTask();
            }
            dependTasks.add(task);
            dependTasksName.add(task.id);
            // 当前task  依赖了dependTask，那么我们还需要把 dependTask-里面的behindTask  添加进当前任务
            if (!task.behindTasks.contains(this)) {
                task.behindTasks.add(this);
            }
        }
    }

    // 给当前任务移除一个前置任务
    protected void removeDependence( Task dependTask) {
        Task task = dependTask;
        if (dependTask != this) {
            if (dependTask instanceof TaskProject) {
                task = ((TaskProject) dependTask).getEndTask();
            }
            dependTasks.remove(task);
            dependTasksName.remove(task.id);
            // 把当前task 从dependTask的  后置依赖任务集合behindTasks中移除
            // 达到解除  两个任务依赖关系的目的
            task.behindTasks.remove(this);
        }
    }

    // 给当前任务添加一个后置依赖任务，他和前置任务是相反的
    protected void behind( Task behindTask) {
        Task task = behindTask;
        if (behindTask != this) {
            if (behindTask instanceof TaskProject) {
                task = ((TaskProject) behindTask).getStartTask();
            }
            // 这个是把 behindTask 添加到当前的task中
            behindTasks.add(task);
            // 把当前任务添加到 behindTask中
            behindTask.dependOn(this);
        }
    }

    // 从当前task中移除一个后置任务
    protected void removeBehind( Task behindTask) {
        Task task = behindTask;
        if (behindTask != this) {
            if (behindTask instanceof TaskProject) {
                task = ((TaskProject) behindTask).getStartTask();
            }
            behindTasks.remove(task);
            behindTask.removeDependence(this);
        }
    }

    private void toRunning() {
        state = TaskStatus.RUNNING;
        TaskRuntime.setStateInfo(this);
        TaskRuntime.setThreadName(this, Thread.currentThread().getName());
        for (TaskListener listener : taskListeners) {
            listener.onRunning(this);
        }
        if (taskRuntimeListener != null) {
            taskRuntimeListener.onRunning(this);
        }
    }

    private void toError() {
        state = TaskStatus.ERROR;
        TaskRuntime.setStateInfo(this);
        for (TaskListener listener : taskListeners) {
            listener.onError(this);
        }
        if (taskRuntimeListener != null) {
            taskRuntimeListener.onError(this);
        }
    }

    private void toFinished() {
        state = TaskStatus.FINISHED;
        TaskRuntime.setStateInfo(this);
        TaskRuntime.removeBlockTask(getId());
        for (TaskListener listener : taskListeners) {
            listener.onFinished(this);
        }
        if (taskRuntimeListener != null) {
            taskRuntimeListener.onFinished(this);
        }
    }

    protected abstract void run( String id);

    @Override
    public int compareTo(Task other) {
        return Utils.compareTask(this, other);
    }
}
