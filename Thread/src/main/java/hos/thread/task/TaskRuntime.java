package hos.thread.task;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hos.thread.BuildConfig;
import hos.thread.executor.TS;
import hos.thread.hander.MH;
import hos.thread.utils.ThreadLog;

/**
 * <p>Title: TaskRuntime </p>
 * <p>Description: taskFlow 运行时调度器  </p>
 * <p> 1、根据task的属性以不同的策略（线程、同步、延迟）调度任务  </p>
 * <p> 2、校验 依赖树是否存在环形依赖  </p>
 * <p> 3、校验依赖库中是否存在相同的taskId任务  </p>
 * <p> 4、统计所有task任务运行信息 （线程状态、开始时间、耗时时间、是否是阻塞任务） </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:19
 */
class TaskRuntime {
    /**
     * 通过addBlockTask(String id) 指定启动阶段 需要阻塞完成的任务，只有当blockTaskId中的任务都执行完成了
     * 才会是否application中的阻塞 才会来切launchActivity
     */
    
    private static final List<String> blockTaskId = new ArrayList<>();
    /**
     * 如果blockTaskId集合中的任务还没有执行完成，那么在主线程中的任务会添加到waitingTasks集合里去
     * 目的是为了优先保证 阻塞任务的优先完成，尽可能早的拉起launchActivity
     */
    
    private static final List<Task> waitingTasks = new ArrayList<>();
    /**
     * 记录下所有任务运行时的信息 key 任务ID，
     */
    
    private static final Map<String, TaskRuntimeInfo> taskRuntimeInfoMap = new HashMap<>();
    
    static Comparator<Task> taskComparator = new Comparator<Task>() {
        @Override
        public int compare(Task task1, Task task2) {
            return Utils.compareTask(task1, task2);
        }
    };

    public static void addBlockTask( String id) {
        if (!TextUtils.isEmpty(id) && !blockTaskId.contains(id)) {
            blockTaskId.add(id);
        }
    }

    public static void addBlockTasks( String... ids) {
        if (ids != null && ids.length != 0) {
            for (String id : ids) {
                addBlockTask(id);
            }
        }
    }

    public static void removeBlockTask( String id) {
        blockTaskId.remove(id);
    }

    public static boolean hasBlockTasks() {
        return blockTaskId.iterator().hasNext();
    }

    public static boolean hasWaitingTasks() {
        return waitingTasks.iterator().hasNext();
    }

    public static void setThreadName( Task task,  String threadName) {
        TaskRuntimeInfo taskRuntimeInfo = getTaskRuntimeInfo(task.getId());
        if (taskRuntimeInfo != null) {
            taskRuntimeInfo.setThreadName(threadName);
        }
    }

    public static void setStateInfo( Task task) {
        TaskRuntimeInfo taskRuntimeInfo = getTaskRuntimeInfo(task.getId());
        if (taskRuntimeInfo != null) {
            if (task.getThrowable() != null) {
                taskRuntimeInfo.setThrowable(task.getThrowable());
            }
            taskRuntimeInfo.setStateTime(task.getState(), System.currentTimeMillis());
        }
    }

    
    public static TaskRuntimeInfo getTaskRuntimeInfo( String id) {
        return taskRuntimeInfoMap.get(id);
    }

    public static void executeTask( Task task) {
        if (task.isAsyncTask()) {
            TS.postIo(task.getPriority(), task);
        } else {
            // 主线程
            // 延迟任务 但是如果这个延迟任务，它存在这后置任务（A延迟任务）->B -> C（Black Task）
            if (task.getDelayMills() > 0 && !hasBlockBehindTask(task)) {
                // 执行这里就说明后置任务没有阻塞任务，可以直接做延迟
                MH.postDelayed(task, task.getDelayMills());
                return;
            }
            // 如果阻塞任务列表接下来没有阻塞任务就执行，有阻塞任务就加到等待队列中
            if (!hasBlockTasks()) {
                task.run();
            } else {
                addWaitingTask(task);
            }
        }
    }

    /**
     * 把一个主线程上的任务，但又不影响launchActivity启动 添加到waitingTasks等待队列中
     *
     * @param task
     */
    private static void addWaitingTask( Task task) {
        if (!waitingTasks.contains(task)) {
            waitingTasks.add(task);
        }
    }

    /**
     * 检查延迟任务是否存在这后置任务 阻塞任务（等他们都执行完了，才能拉起 launchActivity）
     */
    private static boolean hasBlockBehindTask( Task task) {
        if (task instanceof TaskProject.CriticalTask) {
            // 开始节点，结束节点，这种情况是要忽略掉的
            return false;
        }
        List<Task> behindTasks = task.getBehindTasks();
        for (Task behindTask : behindTasks) {
            // 判断是不是阻塞任务
            TaskRuntimeInfo taskRuntimeInfo = getTaskRuntimeInfo(behindTask.getId());
            if (taskRuntimeInfo != null && taskRuntimeInfo.isBlockTask()) {
                return true;
            } else {
                return hasBlockBehindTask(behindTask);
            }
        }
        return false;
    }

    /**
     * 校验  依赖中是否存在环形依赖，依赖树中是否存在TaskId相同的依赖，初始化task中taskRuntimeInfo
     * 完成启动前的校验 和初始化
     */
    public static void traversalDependencyTreeAndInit( Task task) {
        List<Task> traversalVisitor = new LinkedList<>();
        traversalVisitor.add(task);
        innerTraversalDependencyTreeAndInit(task, traversalVisitor);
        for (String taskId : blockTaskId) {
            // 检查依赖任务是否存在于依赖树中
            if (!taskRuntimeInfoMap.containsKey(taskId)) {
                throw new RuntimeException(" block task " + taskId + " not in dependency tree");
            } else {
                TaskRuntimeInfo taskRuntimeInfo = getTaskRuntimeInfo(taskId);
                if (taskRuntimeInfo != null) {
                    traversalDependencyPriority(taskRuntimeInfo.getTask());
                }
            }
        }
    }

    // 优先级处理
    private static void traversalDependencyPriority( Task task) {
        if (task == null) {
            return;
        }
//        task.setPriority(Integer.MAX_VALUE);
//        for (Task dependTask : task.getDependTasks()) {
//            traversalDependencyPriority(dependTask);
//        }
    }

    private static void innerTraversalDependencyTreeAndInit( Task task,  List<Task> traversalVisitor) {
        // 初始化任务运行信息 并校验是否存在相同的 taskId
        TaskRuntimeInfo taskRuntimeInfo = getTaskRuntimeInfo(task.getId());
        if (taskRuntimeInfo == null) {
            taskRuntimeInfo = new TaskRuntimeInfo(task);
            if (blockTaskId.contains(task.getId())) {
                taskRuntimeInfo.setBlockTask(true);
            }
            taskRuntimeInfoMap.put(task.getId(), taskRuntimeInfo);
        } else {
            if (!taskRuntimeInfo.isSameTask(task)) {
                throw new RuntimeException("not allow to contain the same id " + task.getId());
            }
        }
        // 校验环形依赖
        List<Task> behindTasks = task.getBehindTasks();
        for (Task behindTask : behindTasks) {
            if (!traversalVisitor.contains(behindTask)) {
                traversalVisitor.add(behindTask);
            } else {
                throw new RuntimeException("not allow loopback dependency , task id " + task.getId());
            }
            if (BuildConfig.DEBUG && behindTask.getBehindTasks().isEmpty()) {
                // behindTask == end
                Iterator<Task> iterator = traversalVisitor.iterator();
                StringBuilder builder = new StringBuilder();
                while (iterator.hasNext()) {
                    builder.append(iterator.next().getId());
                    builder.append("-->");
                }
                ThreadLog.e(TaskRuntimeListener.TAG, builder.toString());
            }
            // start->task->task1->task2->task3->task4->task5->end
            // 对 task3 后面的依赖任务路径上的task 做环形依赖检查 初始化 TaskRuntimeInfo 信息
            innerTraversalDependencyTreeAndInit(behindTask, traversalVisitor);
            traversalVisitor.remove(behindTask);
        }
    }

    public static void runWaitingTasks() {
        if (hasWaitingTasks()) {
            if (waitingTasks.size() > 1) {
                Collections.sort(waitingTasks, taskComparator);
            }
            if (hasBlockTasks()) {
                Task head = waitingTasks.remove(0);
                head.run();
            } else {
                for (Task waitingTask : waitingTasks) {
                    MH.postDelayed(waitingTask, waitingTask.getDelayMills());
                }
                waitingTasks.clear();
            }
        }
    }

    static void onDestroy() {
        blockTaskId.clear();
        waitingTasks.clear();
        taskRuntimeInfoMap.clear();
    }

}
