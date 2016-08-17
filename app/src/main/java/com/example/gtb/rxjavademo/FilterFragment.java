package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gtb on 16/8/15.
 */
public class FilterFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, null);
        ButterKnife.bind(this, view);
        return view;
    }

    //debounce 过滤掉发射速率过快等请求 用于搜索等
    private Observable<Integer> debounceObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(i);
                    }
                    int sleep = 100;
                    if (i % 3 == 0) {
                        sleep = 300;
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
            .debounce(200, TimeUnit.MILLISECONDS);//小于0.2秒的会被过滤掉
    }


    @OnClick(R.id.btn_debounce)
    void debounce() {
        debounceObserver().observeOn(AndroidSchedulers.mainThread())
            .subscribe(i -> tvResult.setText(tvResult.getText() + "debounce:" + i + "\n"));
    }


    private Observable<Integer> FilterObserver() {
        return Observable.just(0, 1, 2, 3, 4, 5).filter(i -> i < 3);
    }

    @OnClick(R.id.btn_filter)
    void filter() {
        FilterObserver().subscribe(i -> tvResult.setText(tvResult.getText() + "filter:" + i + "\n"));
    }


    private Observable<Integer> createObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 20; i++) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });
    }

    //Sample操作符会定时地发射源Observable最近发射的数据
    private Observable<Integer> sampleObserver() {
        return createObserver().sample(100, TimeUnit.MILLISECONDS);
    }

    //ThrottleFirst操作符则会定期发射这个时间段里源Observable发射的第一个数据
    //用于按钮,网络请求等的防抖动
    private Observable<Integer> throttleFirstObserver() {
        return createObserver().throttleFirst(100, TimeUnit.MILLISECONDS);
    }

    @OnClick(R.id.btn_sample)
    void sample() {
        sampleObserver().observeOn(AndroidSchedulers.mainThread())
            .subscribe(i -> tvResult.setText(tvResult.getText() + "sample:" + i + "\n"));
    }


    @OnClick(R.id.btn_throttleFirst)
    void throttleFirst() {
        throttleFirstObserver().observeOn(AndroidSchedulers.mainThread())
            .subscribe(i -> tvResult.setText(tvResult.getText() + "throttleFirst:" + i + "\n"));
    }


    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }
}
