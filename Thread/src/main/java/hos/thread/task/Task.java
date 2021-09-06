package hos.thread.task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import hos.thread.executor.ThreadTaskExecutor;

/**
 * <p>Title: Task </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2020/2/21 11:23
 */
public class Task<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    @NonNull
    private final MutableLiveData<Boolean> mPreExecute = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Progress> mProgressUpdate = new MutableLiveData<>();

    private ITask.IDoInBackground<Params, Progress, Result> mDoInBackground;
    @NonNull
    private final MutableLiveData<Result> mPostExecute = new MutableLiveData<>();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPreExecute.postValue(true);
    }

    @SafeVarargs
    @Override
    public final Result doInBackground(@Nullable Params... params) {
        return mDoInBackground == null ? null : mDoInBackground.doInBackground(mProgressUpdate, params);
    }

    @Override
    protected void onPostExecute(@NonNull Result result) {
        super.onPostExecute(result);
        mPostExecute.postValue(result);
    }

    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> startOnExecutor(Params... params) {
        return super.executeOnExecutor(ThreadTaskExecutor.getInstance().getMultiThread(), params);
    }

    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> start(Params... params) {
        return super.execute(params);
    }

    @NonNull
    public Task<Params, Progress, Result> setPreExecute(@NonNull LifecycleOwner owner, @NonNull Observer<Boolean> observer) {
        mPreExecute.observe(owner, observer);
        return this;
    }

    @NonNull
    public Task<Params, Progress, Result> setProgressUpdate(@NonNull LifecycleOwner owner, @NonNull Observer<Progress> observer) {
        mProgressUpdate.observe(owner, observer);
        return this;
    }

    @NonNull
    public Task<Params, Progress, Result> setDoInBackground(@NonNull ITask.IDoInBackground<Params, Progress, Result> doInBackground) {
        mDoInBackground = doInBackground;
        return this;
    }

    @NonNull
    public Task<Params, Progress, Result> setPostExecute(@NonNull LifecycleOwner owner, @NonNull Observer<Result> observer) {
        mPostExecute.observe(owner, observer);
        return this;
    }

}
