package hos.thread.task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
public class TaskThread<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> implements IProgressUpdate<Progress> {


    private IDoInBackground<Params, Progress, Result> mDoInBackground;
    @Nullable
    private IPostExecute<Result> mPostExecute = null;
    @Nullable
    private IProgressUpdate<Progress> mProgressUpdate = null;

    private final List<Params> paramList = new LinkedList<>();

    public TaskThread() {
    }

    @SafeVarargs
    public TaskThread(IDoInBackground<Params, Progress, Result> mDoInBackground, @Nullable IPostExecute<Result> mPostExecute, @Nullable IProgressUpdate<Progress> mProgressUpdate, @Nullable Params... params) {
        this.mDoInBackground = mDoInBackground;
        this.mPostExecute = mPostExecute;
        this.mProgressUpdate = mProgressUpdate;
        param(params);
    }

    @SafeVarargs
    public TaskThread(IDoInBackground<Params, Progress, Result> mDoInBackground, @Nullable IPostExecute<Result> mPostExecute, @Nullable Params... params) {
        this(mDoInBackground, mPostExecute, null, params);
    }

    @SafeVarargs
    public TaskThread(IDoInBackground<Params, Progress, Result> mDoInBackground, @Nullable Params... params) {
        this(mDoInBackground, null, null, params);
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        if (isCancelled()) {
            return;
        }
        if (mProgressUpdate != null && values.length > 0) {
            mProgressUpdate.onProgressUpdate(values[0]);
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
        if (mPostExecute != null) {
            mPostExecute.onPostExecute(result);
        }
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
    public TaskThread<Params, Progress, Result> setDoInBackground(@NonNull IDoInBackground<Params,Progress, Result> doInBackground) {
        mDoInBackground = doInBackground;
        return this;
    }

    @NonNull
    public TaskThread<Params, Progress, Result> setPostExecute(@NonNull IPostExecute<Result> observer) {
        mPostExecute = observer;
        return this;
    }

    @NonNull
    public TaskThread<Params, Progress, Result> setProgressUpdate(@NonNull IProgressUpdate<Progress> progressUpdate) {
        mProgressUpdate = progressUpdate;
        return this;
    }

    @SafeVarargs
    public final TaskThread<Params, Progress, Result> param(@Nullable Params... objects) {
        paramList.clear();
        if (objects != null) {
            paramList.addAll(Arrays.asList(objects));
        }
        return this;
    }

    public final TaskThread<Params, Progress, Result> param(@Nullable List<Params> objects) {
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
}
