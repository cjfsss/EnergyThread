package hos.thread.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * <p>Title: IDoInBackground </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/9/27 15:53
 */
public interface IDoInBackground<Params,  Result> {
    Result doInBackground(@Nullable List<Params> params);
}
