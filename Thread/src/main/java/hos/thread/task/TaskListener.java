package hos.thread.task;

import androidx.annotation.NonNull;

/**
 * <p>Title: TaskListener </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2022/3/16 20:52
 */
public interface TaskListener {
    void onStart(@NonNull Task task);

    void onRunning(@NonNull Task task);

    void onError(@NonNull Task task);

    void onFinished(@NonNull Task task);

    abstract class Running implements TaskListener {

        @Override
        public void onStart(@NonNull Task task) {
        }

    }

    abstract class Finished implements TaskListener {

        @Override
        public void onStart(@NonNull Task task) {
        }

        @Override
        public void onRunning(@NonNull Task task) {
        }
    }

    interface ProgressUpdate {
        /**
         * 进度监听
         * @param progress 200 成功  400 失败
         */
        void onProgressUpdate(@NonNull Integer progress);
    }
}
