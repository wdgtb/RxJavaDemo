package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gtb.Util.TextViewUtil;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class UtilityFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;
    private Observable<Long> observable;
    private Subscriber subscriber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_utility, null);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * using 创建一个只在Observable的生命周期内存在的一次性资源
     * 3个参数:
     * 创建这个一次性资源的函数
     * 创建Observable的函数
     * 释放资源的函数
     */
    private Observable<Long> usingObserver() {
        return Observable.using(Animal::new, i -> Observable.timer(5000, TimeUnit.MILLISECONDS), Animal::release);
    }

    private class Animal {
        Subscriber subscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                TextViewUtil.setText(tvResult, "animal onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                TextViewUtil.setText(tvResult, "animal onError");
            }

            @Override
            public void onNext(Object o) {
                TextViewUtil.setText(tvResult, "animal eat");
            }
        };

        public Animal() {
            TextViewUtil.setText(tvResult, "create animal");
            Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        }

        public void release() {
            TextViewUtil.setText(tvResult, "animal released");
            subscriber.unsubscribe();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        observable = usingObserver();
        subscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                TextViewUtil.setText(tvResult, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                TextViewUtil.setText(tvResult, "onError");
            }

            @Override
            public void onNext(Object o) {
                TextViewUtil.setText(tvResult, "onNext");
            }
        };
    }

    @OnClick(R.id.btn_using)
    void using() {
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    @OnClick(R.id.btn_release)
    void release() {
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }
}
