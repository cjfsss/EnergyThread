package hos.thread.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

/**
 * <p>Title: IDoInBackgroundLiveData </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2021/9/27 16:02
 */
public interface IDoInBackgroundLiveData<Params, Progress, Result> {
    Result doInBackground(@NonNull MutableLiveData<Progress> publishProgress, @Nullable List<Params> params);
}
