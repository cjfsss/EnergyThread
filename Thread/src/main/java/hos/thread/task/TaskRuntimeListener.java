package hos.thread.task;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import hos.thread.BuildConfig;

/**
 * <p>Title: TaskRunrimeListener </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:25
 */
public class TaskRuntimeListener implements TaskListener {
    static final String TAG = "TaskFlow";
    static final String START_METHOD = "---onStart---";
    static final String RUNNING_METHOD = "---onRunning---";
    static final String ERROR_METHOD = "---onError---";
    static final String FINISHED_METHOD = "---onFinished---";

    static final String DEPENDENCIES = "依赖任务";
    static final String THREAD_NAME = "线程名称";
    static final String START_TIME = "开始执行时刻";
    static final String WAITING_TIME = "等待执行耗时";
    static final String TASK_CONSUME = "任务执行耗时";
    static final String ERROR_TIME = "错误耗时";
    static final String IS_BLOCK_TASK = "是否是阻塞任务";
    static final String IS_SUCCESS = "是否成功";
    static final String ERROR_INFO = "错误信息";
    static final String FINISHED_TIME = "任务结束时刻";
    static final String WRAPPER = "\n";
    static final String HALF_LINE = "==================";

    @Override
    public void onStart(@NonNull Task task) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, task.getId() + START_METHOD);
        }
    }

    @Override
    public void onRunning(@NonNull Task task) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, task.getId() + RUNNING_METHOD);
        }
    }

    @Override
    public void onError(@NonNull Task task) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, task.getId() + ERROR_METHOD);
        }
    }

    @Override
    public void onFinished(@NonNull Task task) {
        if (BuildConfig.DEBUG) {
            logTaskRuntimeInfo(task);
        }
    }

    private void logTaskRuntimeInfo(@NonNull Task task) {
        TaskRuntimeInfo taskRuntimeInfo = TaskRuntime.getTaskRuntimeInfo(task.getId());
        if (taskRuntimeInfo == null) {
            return;
        }

        long startTime = taskRuntimeInfo.getStateTime(TaskStatus.START);
        long runningTime = taskRuntimeInfo.getStateTime(TaskStatus.RUNNING);
        long errorTime = taskRuntimeInfo.getStateTime(TaskStatus.ERROR);
        long finishedTime = taskRuntimeInfo.getStateTime(TaskStatus.FINISHED);
        Throwable throwable = taskRuntimeInfo.getThrowable();
        String message = "";
        if (throwable != null) {
            message = throwable.getMessage();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(WRAPPER);
        builder.append(TAG);
        builder.append(WRAPPER);
        builder.append(WRAPPER);
        builder.append(HALF_LINE);
        if (task instanceof TaskProject) {
            builder.append("TaskProject" + FINISHED_METHOD);
        } else {
            builder.append("task [").append(task.getId()).append("]").append(FINISHED_METHOD);
        }
        builder.append(HALF_LINE);
        builder.append(WRAPPER);


        addTaskInfoLineInfo(builder, DEPENDENCIES, getTaskDependenciesInfo(task));
        addTaskInfoLineInfo(builder, IS_BLOCK_TASK, String.valueOf(taskRuntimeInfo.isBlockTask()));
        addTaskInfoLineInfo(builder, THREAD_NAME, taskRuntimeInfo.getThreadName());
        addTaskInfoLineInfo(builder, START_TIME, startTime + "ms");
        addTaskInfoLineInfo(builder, WAITING_TIME, (runningTime - startTime) + "ms");
        addTaskInfoLineInfo(builder, ERROR_TIME, (errorTime - runningTime) + "ms");
        addTaskInfoLineInfo(builder, TASK_CONSUME, (finishedTime - runningTime) + "ms");
        addTaskInfoLineInfo(builder, IS_SUCCESS, String.valueOf(task.isSuccessFul()));
        addTaskInfoLineInfo(builder, ERROR_INFO, message);
        addTaskInfoLineInfo(builder, FINISHED_TIME, finishedTime + "ms");
        builder.append(HALF_LINE + HALF_LINE + HALF_LINE + HALF_LINE);
        builder.append(WRAPPER);
        Log.d(TAG, builder.toString());
    }


    private void addTaskInfoLineInfo(
            @NonNull StringBuilder builder,
            @NonNull String key,
            @NonNull String value
    ) {
        builder.append("| ").append(key).append(": ").append(value).append(" \n");
    }

    @NonNull
    private String getTaskDependenciesInfo(@NonNull Task task) {
        StringBuilder builder = new StringBuilder();
        List<String> dependTasksName = task.getDependTasksName();
        for (String name : dependTasksName) {
            builder.append(name).append(" ");
        }
        return builder.toString();
    }
}
