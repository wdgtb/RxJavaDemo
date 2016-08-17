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

/**
 * Created by gtb on 16/8/15.
 */
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

    //using的3个参数
    //创建这个一次性资源的函数
    //创建Observable的函数
    //释放资源的函数
    private Observable<Long> usingObserver() {
        return Observable.using(Animal::new, i -> Observable.timer(5000, TimeUnit.MILLISECONDS), Animal::release);
    }

    private class Animal {
        Subscriber subscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                tvResult.setText(tvResult.getText() + "animal onCompleted" + "\n");
            }

            @Override
            public void onError(Throwable e) {
                tvResult.setText(tvResult.getText() + "animal onError" + "\n");
            }

            @Override
            public void onNext(Object o) {
                tvResult.setText(tvResult.getText() + "animal eat" + "\n");
            }
        };

        public Animal() {
            tvResult.setText(tvResult.getText() + "create animal" + "\n");
            Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        }

        public void release() {
            tvResult.setText(tvResult.getText() + "animal released" + "\n");
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
                tvResult.setText(tvResult.getText() + "onCompleted" + "\n");
            }

            @Override
            public void onError(Throwable e) {
                tvResult.setText(tvResult.getText() + "onError" + "\n");
            }

            @Override
            public void onNext(Object o) {
                tvResult.setText(tvResult.getText() + "onNext" + "\n");
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
