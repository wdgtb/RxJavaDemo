package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gtb.Util.TextViewUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

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

    /**
     * retry 如果Observable发射了一个错误通知，重新订阅它，期待它正常终止。在发生错误的时候会重新进行订阅,而且可以重复多次，所以发射的数据可能会产生重复。如果重复指定次数还有错误的话就会将错误返回给观察者
     * 当 .repeat() 接收到 .onCompleted() 事件后触发重订阅。
     * 当 .retry() 接收到 .onError() 事件后触发重订阅。
     */
    private Observable<Integer> retryObserver() {
        return createObserver().retry(1);//重复1次
    }

    /**
     * retryWhen 指示Observable遇到错误时，将错误传递给另一个Observable来决定是否要重新给订阅这个Observable，新Observable处理错误，老的继续流程。
     */
    private Observable<Integer> retryWhenObserver() {
        return createObserver().retryWhen(observable ->
            observable.zipWith(Observable.just(1, 2), new Func2<Throwable, Integer, Object>() {
                @Override
                public Object call(Throwable throwable, Integer integer) {
                    TextViewUtil.setText(tvResult, throwable.getMessage() + integer);
                    return throwable.getMessage() + integer;
                }
            }));
    }

    private Observable<Integer> createObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                TextViewUtil.setText(tvResult, "subscribe");
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
                TextViewUtil.setText(tvResult, "retry-onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                TextViewUtil.setText(tvResult, "retry-onError:" + e.getMessage());
            }

            @Override
            public void onNext(Integer i) {
                TextViewUtil.setText(tvResult, "retry-onNext:" + i);
            }
        });
    }

    @OnClick(R.id.btn_retryWhen)
    void retryWhen() {
        retryWhenObserver().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                TextViewUtil.setText(tvResult, "retryWhen-onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                TextViewUtil.setText(tvResult, "retryWhen-onError:" + e.getMessage());
            }

            @Override
            public void onNext(Integer i) {
                TextViewUtil.setText(tvResult, "retryWhen-onNext:" + i);
            }
        });
    }

    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }
}
