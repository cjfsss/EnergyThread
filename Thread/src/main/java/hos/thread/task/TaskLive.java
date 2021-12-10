package hos.thread.task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import hos.thread.executor.ThreadTaskExecutor;
import hos.thread.interfaces.IDoInBackground;
import hos.thread.interfaces.IPostExecute;
import hos.thread.interfaces.IProgressUpdate;

/**
 * <p>Title: Task </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2020/2/21 11:23
 */
@SuppressWarnings({"deprecation"})
public class TaskLive<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> implements IProgressUpdate<Progress> {

    @NonNull
    private final MutableLiveData<Boolean> mPreExecute = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Progress> mProgressUpdate = new MutableLiveData<>();

    private IDoInBackground<Params, Progress, Result> mDoInBackground;
    @NonNull
    private final MutableLiveData<Result> mPostExecuteLive = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Integer> mPostExecuteLiveIndex = new MutableLiveData<>();

    private final List<Params> paramList = new LinkedList<>();

    private int index;

    public TaskLive() {
    }

    public TaskLive(int index) {
        this.index = index;
    }

    @SafeVarargs
    public TaskLive(IDoInBackground<Params, Progress, Result> mDoInBackground, @NonNull LifecycleOwner owner, @NonNull Observer<Result> postExecute, @NonNull Observer<Progress> progressUpdate, @Nullable Params... params) {
        this.mDoInBackground = mDoInBackground;
        this.mProgressUpdate.observe(owner, progressUpdate);
        param(params);
    }

    @SafeVarargs
    public TaskLive(IDoInBackground<Params, Progress, Result> mDoInBackground, @NonNull LifecycleOwner owner, @NonNull Observer<Result> postExecute, @Nullable Params... params) {
        this.mDoInBackground = mDoInBackground;
        this.mPostExecuteLive.observe(owner, postExecute);
        param(params);

    }

    @SafeVarargs
    public TaskLive(IDoInBackground<Params, Progress, Result> mDoInBackground, @Nullable Params... params) {
        this.mDoInBackground = mDoInBackground;
        param(params);
    }

    public TaskLive<Params, Progress, Result> setIndex(int index) {
        this.index = index;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPreExecute.postValue(true);
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        if (isCancelled()) {
            return;
        }
        if (values.length > 0) {
            mProgressUpdate.postValue(values[0]);
        }
    }

    @SafeVarargs
    @Override
    public final Result doInBackground(@Nullable Params... params) {
        if (params != null) {
            paramList.addAll(Arrays.asList(params));
        }
        return mDoInBackground == null ? null : mDoInBackground.doInBackground(this, paramList);
    }

    @Override
    protected void onPostExecute(@NonNull Result result) {
        super.onPostExecute(result);
        mPostExecuteLive.postValue(result);
        mPostExecuteLiveIndex.postValue(index);
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
    public TaskLive<Params, Progress, Result> setPreExecute(@NonNull LifecycleOwner owner, @NonNull Observer<Boolean> observer) {
        mPreExecute.observe(owner, observer);
        return this;
    }

    @NonNull
    public TaskLive<Params, Progress, Result> setProgressUpdate(@NonNull LifecycleOwner owner, @NonNull Observer<Progress> observer) {
        mProgressUpdate.observe(owner, observer);
        return this;
    }

    @NonNull
    public TaskLive<Params, Progress, Result> setDoInBackground(@NonNull IDoInBackground<Params, Progress, Result> doInBackground) {
        mDoInBackground = doInBackground;
        return this;
    }

    @NonNull
    public TaskLive<Params, Progress, Result> setPostExecute(@NonNull LifecycleOwner owner, @NonNull Observer<Result> observer) {
        mPostExecuteLive.observe(owner, observer);
        return this;
    }

    @NonNull
    public TaskLive<Params, Progress, Result> setPostExecuteIndex(@NonNull LifecycleOwner owner, @NonNull Observer<Integer> observer) {
        mPostExecuteLiveIndex.observe(owner, observer);
        return this;
    }

    @SafeVarargs
    public final TaskLive<Params, Progress, Result> param(@Nullable Params... objects) {
        paramList.clear();
        if (objects != null) {
            paramList.addAll(Arrays.asList(objects));
        }
        return this;
    }

    public final TaskLive<Params, Progress, Result> param(@Nullable List<Params> objects) {
        paramList.clear();
        if (objects != null) {
            paramList.addAll(objects);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onProgressUpdate(Progress values) {
        publishProgress(values);
    }

    public void clear(){
        mDoInBackground = null;
        paramList.clear();
    }
}
