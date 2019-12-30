package com.github.rxchallenge.network.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Sebastian Schipor
 */
@SuppressWarnings({"unchecked"})
abstract public class RepoBoundResource<ResultType> {

    private MutableLiveData<RepoResponse<ResultType>> result = new MutableLiveData();
    private CompositeDisposable compositeDisposable;

    /**
     * The logic of this class is:
     * - add listener for DB data to be transmitted downstream
     * - make api call if it's provided and store the response in DB. If no API call, return DB result
     * - the DB will react upon new items stored and send the result to VM
     *
     * @param compositeDisposable the VM's composite disposable
     */
    protected RepoBoundResource(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
        //notify start loader
        result.postValue(RepoResponse.loading());

        if (getApiCall() != null) {
            //make api call first to retrieve fresh data
            makeApiCall();
        } else {
            //only offline data was requested
            //listen for db changes to propagate the result
            addDbListener(loadFomDB());
        }
    }

    private void addDbListener(@NonNull Flowable<ResultType> dbLoadTask) {
        //there is a valid db load task to be executed first
        compositeDisposable.add(dbLoadTask
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resultType -> result.postValue(RepoResponse.success(resultType)),
                        error -> result.postValue(RepoResponse.error(error.getMessage()))
                )
        );
    }

    private void makeApiCall() {
        compositeDisposable.add(getApiCall().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResultType>() {
                    @Override
                    public void onSuccess(ResultType resultType) {
                        //store the response in db
                        saveResponse(resultType);
                        //add the db load task listener
                        addDbListener(loadFomDB());
                    }

                    @Override
                    public void onError(Throwable e) {
                        result.postValue(RepoResponse.error(e.getMessage()));
                    }
                })
        );
    }

    //abstract
    public abstract @NonNull
    Flowable<ResultType> loadFomDB();

    public abstract Single<ResultType> getApiCall();

    public abstract void saveResponse(ResultType response);

    //helper
    public LiveData<RepoResponse<ResultType>> toLiveData() {
        return result;
    }
}
