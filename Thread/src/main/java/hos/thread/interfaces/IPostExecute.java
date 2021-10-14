package hos.thread.interfaces;

import androidx.annotation.NonNull;

/**
 * <p>Title: IPostExecute </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/9/27 15:58
 */
public interface IPostExecute<Result> {

    void onPostExecute(@NonNull Result result);
}
