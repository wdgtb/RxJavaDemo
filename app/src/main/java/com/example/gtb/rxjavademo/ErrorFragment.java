package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

/**
 * Created by gtb on 16/8/15.
 */
public class ErrorFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, null);
        ButterKnife.bind(this, view);
        return view;
    }

    private Observable<Integer> retryObserver() {
        return createObserver().retry(1);
    }

    private Observable<Integer> retryWhenObserver() {
        return createObserver().retryWhen(observable ->
            observable.zipWith(Observable.just(1, 2, 3), new Func2<Throwable, Integer, Object>() {

                @Override
                public Object call(Throwable throwable, Integer integer) {
                    tvResult.setText(tvResult.getText() + throwable.getMessage() + integer + "\n");
                    return throwable.getMessage() + integer;
                }
            }));
    }

    private Observable<Integer> createObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                tvResult.setText(tvResult.getText() + "subscribe" + "\n");
                for (int i = 0; i < 3; i++) {
                    if (i == 2) {
                        subscriber.onError(new Exception("Exception-"));
                    } else {
                        subscriber.onNext(i);
                    }
                }
            }
        });
    }

    @OnClick(R.id.btn_retry)
    void retry() {
        retryObserver().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                tvResult.setText(tvResult.getText() + "retry-onCompleted" + "\n");
            }

            @Override
            public void onError(Throwable e) {
                tvResult.setText(tvResult.getText() + "retry-onError:" + e.getMessage() + "\n");
            }

            @Override
            public void onNext(Integer i) {
                tvResult.setText(tvResult.getText() + "retry-onNext:" + i + "\n");
            }
        });
    }

    @OnClick(R.id.btn_retryWhen)
    void retryWhen() {
        retryWhenObserver().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                tvResult.setText(tvResult.getText() + "retryWhen-onCompleted" + "\n");
            }

            @Override
            public void onError(Throwable e) {
                tvResult.setText(tvResult.getText() + "retryWhen-onError:" + e.getMessage() + "\n");
            }

            @Override
            public void onNext(Integer i) {
                tvResult.setText(tvResult.getText() + "retryWhen-onNext:" + i + "\n");
            }
        });
    }

    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }
}
