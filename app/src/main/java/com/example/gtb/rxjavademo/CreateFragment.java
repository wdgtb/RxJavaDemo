package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by gtb on 16/8/15.
 */
public class CreateFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;
    private Observable<Long> observableDefer;
    private Observable<Long> observableJust;
    private Subscription subscribeAuto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, null);
        ButterKnife.bind(this, view);
        return view;
    }

    //Create
    private Observable<Integer> createObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    for (int i = 0; i < 5; i++) {
                        int temp = new Random().nextInt(10);
                        if (temp > 8) {
                            subscriber.onError(new Throwable("value >8"));
                            break;
                        } else {
                            subscriber.onNext(temp);
                        }
                        if (i == 4) {
                            subscriber.onCompleted();
                        }
                    }
                }
            }
        });
    }

    @OnClick(R.id.btn_create)
    void create() {
        createObserver().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                tvResult.setText(tvResult.getText() + "onComplete!" + "\n");
            }

            @Override
            public void onError(Throwable e) {
                tvResult.setText(tvResult.getText() + "onError:" + e.getMessage() + "\n");
            }

            @Override
            public void onNext(Integer integer) {
                tvResult.setText(tvResult.getText() + "onNext:" + integer + "\n");
            }
        });
    }


    //Range
    private Observable<Integer> rangeObserver() {
        //初始值和个数
        return Observable.range(1, 5);
    }

    @OnClick(R.id.btn_range)
    void range() {
        rangeObserver().subscribe(integer -> {
            tvResult.setText(tvResult.getText() + String.valueOf(integer) + "\n");
        });
    }


    //Defer只有当有Subscriber来订阅的时候才会创建一个新的Observable对象,每次订阅都会得到一个刚创建的最新的Observable对象,确保Observable对象里的数据是最新的
    private Observable<Long> deferObserver() {
        return Observable.defer(() -> Observable.just(System.currentTimeMillis()));
    }

    //Just
    private Observable<Long> justObserver() {
        return Observable.just(System.currentTimeMillis());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        observableDefer = deferObserver();
        observableJust = justObserver();
    }

    @OnClick(R.id.btn_defer)
    void defer() {
        observableDefer.subscribe(time -> {
            tvResult.setText(tvResult.getText() + String.valueOf(time) + "\n");
        });
    }

    @OnClick(R.id.btn_just)
    void just() {
        observableJust.subscribe(time -> {
            tvResult.setText(tvResult.getText() + String.valueOf(time) + "\n");
        });
    }


    //Interval,取代timer,可以做viewPage的轮询
    private Observable<Long> intervalObserver() {
        return Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            //延时1秒 ，每间隔1秒
            .observeOn(AndroidSchedulers.mainThread());
    }


    @OnClick(R.id.btn_start_loop)
    public void startLoop() {
        autoLoop();
    }

    @OnClick(R.id.btn_stop_loop)
    public void stopLoop() {
        if (subscribeAuto != null && !subscribeAuto.isUnsubscribed()) {
            subscribeAuto.unsubscribe();
        }
    }

    private void autoLoop() {
        if (subscribeAuto == null || subscribeAuto.isUnsubscribed()) {
            subscribeAuto = intervalObserver()
                .subscribe(aLong -> {
                    tvResult.setText(tvResult.getText() + String.valueOf(Math.random()) + "\n");
                });
        }
    }


    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }

}
