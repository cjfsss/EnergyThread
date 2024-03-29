package hos.thread.task;


/**
 * <p>Title: TaskStatus </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:37
 */
public interface TaskStatus {
    int IDLE = 0;// 静止
    int START = 1;// 开始
    int RUNNING = 2;// 运行
    int ERROR = 3;// 错误
    int FINISHED = 4;// 结束
}
