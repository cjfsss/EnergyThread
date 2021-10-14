package hos.thread.interfaces;

/**
 * <p>Title: IProgressUpdate </p>
 * <p>Description: 进度监听 </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/10/14 10:04
 */
public interface IProgressUpdate<Progress> {

    void onProgressUpdate(Progress values);
}
