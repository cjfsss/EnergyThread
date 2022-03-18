package hos.thread.task;

import androidx.annotation.NonNull;

/**
 * <p>Title: Utils </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:43
 */
class Utils {
    /**
     * 比较两个任务的先后顺序，优先级越高越先执行
     */
    public static int compareTask(@NonNull Task task1, @NonNull Task task2) {
        return Integer.compare(task2.getPriority(), task1.getPriority());
    }
}
