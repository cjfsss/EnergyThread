package hos.thread.task;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>Title: TaskProject </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:16
 */
public class TaskProject extends Task {
    private Task startTask;
    private Task endTask;
    private List<String> taskIds;

    public TaskProject(String id) {
        super(id);
    }

    @NonNull
    Task getStartTask() {
        return startTask;
    }

    @NonNull
    Task getEndTask() {
        return endTask;
    }

    @NonNull
    List<String> getTaskIds() {
        return taskIds;
    }

    @Override
    public void start() {
        startTask.start();
    }

    @Override
    protected void run(@NonNull String id) {
        // 不需要处理的
    }

    @Override
    protected void behind(@NonNull Task behindTask) {
        // 当咱们给任务组添加后置任务的时候，那么这个任务应该添加到   组当中谁的后面？？？
        // 把新来的后置任务  添加到  任务组结束节点上面去 这样的话，任务组里面所有的任务都结束了
        // 这个后置任务才会执行
        endTask.behind(behindTask);
    }

    @Override
    protected void dependOn(@NonNull Task dependTask) {
        startTask.dependOn(dependTask);
    }

    @Override
    protected void removeDependence(@NonNull Task dependTask) {
        startTask.removeDependence(dependTask);
    }

    @Override
    protected void removeBehind(@NonNull Task behindTask) {
        endTask.removeBehind(behindTask);
    }

    public static class Builder {
        @NonNull
        private final TaskFactory mTaskFactory;
        @NonNull
        private final Task mStartTask;
        @NonNull
        private final Task mEndTask;
        @NonNull
        private final TaskProject mTaskProject;
        // 默认该任务组中 所有任务优先级的  最高的
        private int mPriority = 0;
        /**
         * 本次添加进来的这个task 是否吧start 节点当做依赖
         * 那如果这个task  它存在于其他task的依赖关系，那就不能直接加到start 节点的后面了，而需要通过dependOn来指定依赖关系
         */
        private boolean mCurrentTaskShouldDependOnStartTask = true;
        @Nullable
        private Task mCurrentAddTask;

        public Builder(@NonNull String projectName, @NonNull ITaskCreator taskCreator) {
            mTaskFactory = new TaskFactory(taskCreator);
            mTaskProject = new TaskProject(projectName);
            mStartTask = new CriticalTask(projectName + "_start");
            mEndTask = new CriticalTask(projectName + "_end");
        }

        @NonNull
        public Builder add(@NonNull String id) {
            Task task = mTaskFactory.getTask(id);
            if (task.getPriority() > mPriority) {
                mPriority = task.getPriority();
            }
            return add(task);
        }

        @NonNull
        private Builder add(@NonNull Task task) {
            if (mCurrentTaskShouldDependOnStartTask && mCurrentAddTask != null) {
                mStartTask.behind(mCurrentAddTask);
            }
            mCurrentAddTask = task;
            mCurrentTaskShouldDependOnStartTask = true;
            mCurrentAddTask.behind(mEndTask);
            return this;
        }

        @NonNull
        public Builder dependOn(@NonNull String id) {
            return dependOn(mTaskFactory.getTask(id));
        }

        @NonNull
        private Builder dependOn(@NonNull Task task) {
            // 确定刚才我们添加进来的  mCurrentAddTask  和task 的依赖关系 ------- mCurrentAddTask依赖于task
            if (mCurrentAddTask == null) {
                throw new RuntimeException(" please call Builder.add() ");
            }
            task.behind(mCurrentAddTask);
            // start --task10 --mCurrentAddTask(task  11) end
            mEndTask.removeDependence(task);
            mCurrentTaskShouldDependOnStartTask = false;
            return this;
        }

        @NonNull
        public TaskProject build() {
            if (mCurrentAddTask == null) {
                mStartTask.behind(mEndTask);
            } else {
                if (mCurrentTaskShouldDependOnStartTask) {
                    mStartTask.behind(mCurrentAddTask);
                }
            }
            mTaskProject.taskIds = mTaskFactory.getIds();
            mStartTask.setPriority(mPriority);
            mEndTask.setPriority(mPriority);
            mTaskProject.startTask = mStartTask;
            mTaskProject.endTask = mEndTask;
            return mTaskProject;
        }

        @NonNull
        public static TaskProject concurrence(@NonNull String projectName, @NonNull Task... taskList) {
            return concurrence(projectName, Arrays.asList(taskList));
        }

        @NonNull
        public static TaskProject concurrence(@NonNull String projectName, @NonNull List<Task> taskList) {
            ITaskCreator taskCreator = new ITaskCreator() {
                @NonNull
                @Override
                public Task createTask(@NonNull String taskName) {
                    for (Task task : taskList) {
                        if (TextUtils.equals(taskName, task.getId())) {
                            return task;
                        }
                    }
                    return taskList.get(taskList.size() - 1);
                }
            };
            Builder builder = new Builder(projectName, taskCreator);
            for (Task task : taskList) {
                if (!task.isAsyncTask()) {
                    // 这里是并发创建的，必须是异步
                    throw new RuntimeException("please task async");
                }
                builder.add(task.getId());
            }
            return builder.build();
        }
    }

    private static class TaskFactory {
        // 利用  TaskCreator 创建task，并管理
        @NonNull
        private final ITaskCreator taskCreator;
        @NonNull
        private final Map<String, Task> mCacheTasks = new HashMap<>();

        private TaskFactory(@NonNull ITaskCreator taskCreator) {
            this.taskCreator = taskCreator;
        }

        @NonNull
        private Task getTask(@NonNull String id) {
            Task task = mCacheTasks.get(id);
            if (task != null) {
                return task;
            }
            task = taskCreator.createTask(id);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(task, "create task fail make sure ITaskCreator can task  with only taskId");
            } else {
                Objects.requireNonNull(task);
            }
            mCacheTasks.put(id, task);
            return task;
        }

        @NonNull
        private List<String> getIds() {
            Set<String> keySet = mCacheTasks.keySet();
            if (keySet.isEmpty()) {
                throw new RuntimeException("please call TaskProject.Builder.add() Or TaskProject.Builder.concurrence()");
            }
            return Arrays.asList(keySet.toArray(new String[]{}));
        }
    }

    static class CriticalTask extends Task {

        public CriticalTask(@NonNull String id) {
            super(id);
        }

        @Override
        protected void run(@NonNull String id) {
            // 不做任何处理
        }
    }
}
