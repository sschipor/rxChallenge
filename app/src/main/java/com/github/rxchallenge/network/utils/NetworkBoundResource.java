package com.github.rxchallenge.network.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Completable;
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
abstract public class NetworkBoundResource<ResultType> {

    public abstract Flowable<ResultType> loadFomDB();

    public abstract Boolean isApiCallRequired(ResultType result);

    public abstract Single<ResultType> getApiCall();

    public abstract Completable saveResponse(ResultType response);

    public LiveData<RepoResponse<ResultType>> toLiveData() {
        return result;
    }

    private MutableLiveData<RepoResponse<ResultType>> result = new MutableLiveData();

    private CompositeDisposable disposable;

    protected NetworkBoundResource(CompositeDisposable compositeDisposable) {
        disposable = compositeDisposable;
        //notify start loader
        result.postValue(RepoResponse.loading());

        //listen for changes in the DB
        Flowable<ResultType> dbLoadTask = loadFomDB();
        if (dbLoadTask != null) {
            //there is a valid db load task to be executed first
            disposable.add(dbLoadTask
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resultType -> {
                        if (isApiCallRequired(resultType)) {
                            makeApiCall();
                        } else {
                            result.postValue(RepoResponse.success(resultType));
                        }
                    })
            );
        } else {
            //there is no db load task -- check if api call is required
            if (isApiCallRequired(null)) {
                makeApiCall();
            } else {
                //there is no db load and neither api call -- return null
                result.postValue(RepoResponse.success(null));
            }
        }
    }

    private void makeApiCall() {
        Single<ResultType> apiCallTask = getApiCall();
        if (apiCallTask != null) {
            disposable.add(apiCallTask.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ResultType>() {
                        @Override
                        public void onSuccess(ResultType resultType) {
                            //store the response in db -- new changes will emit in the previous observer
                            storeApiResult(resultType);
                        }

                        @Override
                        public void onError(Throwable e) {
                            result.postValue(RepoResponse.error(e.getMessage()));
                        }
                    })
            );
        }
    }

    private void storeApiResult(ResultType resultType) {
        Completable saveResponseTask = saveResponse(resultType);
        if (saveResponseTask != null) {
            disposable.add(saveResponseTask
                    .subscribeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            );
        }
    }
}
