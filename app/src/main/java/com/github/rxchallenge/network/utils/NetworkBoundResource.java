package com.github.rxchallenge.network.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Sebastian Schipor
 */
@SuppressWarnings({"unchecked"})
abstract public class NetworkBoundResource<ResultType, RequestType> {

    public abstract Flowable<ResultType> loadFomDB();

    public abstract Boolean isApiCallRequired(ResultType result);

    public abstract Single<RequestType> getApiCall();

    public abstract Completable saveResponse(RequestType response);

    public LiveData<RepoResponse<ResultType>> toLiveData() {
        return result;
    }

    private MutableLiveData<RepoResponse<ResultType>> result = new MutableLiveData();

    private CompositeDisposable disposable;

    public NetworkBoundResource(CompositeDisposable compositeDisposable) {
        disposable = compositeDisposable;
        //notify start loader
        result.postValue(RepoResponse.loading());

        //listen for changes in the DB
        disposable.add(loadFomDB()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultType>() {
                    @Override
                    public void accept(ResultType resultType) throws Exception {
                        if (isApiCallRequired(resultType)) {
                            makeApiCall();
                        } else {
                            result.postValue(RepoResponse.success(resultType));
                        }
                    }
                })
        );
    }

    private void makeApiCall() {
        disposable.add(getApiCall().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<RequestType>() {
                    @Override
                    public void onSuccess(RequestType resultType) {
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

    private void storeApiResult(RequestType resultType) {
        disposable.add(saveResponse(resultType)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }
}
