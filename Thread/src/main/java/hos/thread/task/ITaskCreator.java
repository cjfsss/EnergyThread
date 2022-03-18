package hos.thread.task;

import androidx.annotation.NonNull;

/**
 * <p>Title: TaskCreater </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:31
 */
public interface ITaskCreator {
    @NonNull
    Task createTask(@NonNull String taskName);
}
