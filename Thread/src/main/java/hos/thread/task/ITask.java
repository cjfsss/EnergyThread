package hos.thread.task;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

/**
 * <p>Title: IPreExecute </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2020/2/21 11:15
 */
public interface ITask {

    interface IDoInBackground<Params, Progress, Result> {
        Result doInBackground(@NonNull MutableLiveData<Progress> publishProgress, @Nullable Params... params);
    }
}
