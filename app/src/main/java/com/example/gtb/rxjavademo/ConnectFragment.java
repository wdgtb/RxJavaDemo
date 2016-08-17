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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Created by gtb on 16/8/15.
 */
public class ConnectFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;
    private ConnectableObservable<Long> observable;
    private Subscription subscription;
    private Action1 action1;
    private Action1 action2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        action1 = o -> tvResult.setText(tvResult.getText() + "action1:" + o + "\n");
        action2 = o -> {
            tvResult.setText(tvResult.getText() + "action2:" + o + "\n");
            if ((long) o == 3) {
                observable.observeOn(AndroidSchedulers.mainThread()).subscribe(action1);
            }
        };
    }

    /**
     * Connect 指示一个可连接的Observable开始发射数据给订阅者
     */
    //Publish 将一个普通的Observable转换为可连接的
    private ConnectableObservable<Long> publishObserver() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        observable.observeOn(Schedulers.newThread());
        return observable.publish();
    }


    @OnClick(R.id.btn_publish)
    void publish() {
        observable = publishObserver();
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(action2);
        subscription = observable.connect();
    }

    //Replay操作符返回一个Connectable Observable 对象并且可以缓存其发射过的数据
    private ConnectableObservable<Long> relayCountObserver() {
        Observable<Long> obser = Observable.interval(1, TimeUnit.SECONDS);
        obser.observeOn(Schedulers.newThread());
        return obser.replay(2);//缓存个数2
    }

    private ConnectableObservable<Long> relayTimeObserver() {
        Observable<Long> obser = Observable.interval(1, TimeUnit.SECONDS);
        obser.observeOn(Schedulers.newThread());
        return obser.replay(3, TimeUnit.SECONDS);//缓存3秒内的
    }


    @OnClick(R.id.btn_reply)
    void reply() {
        observable = relayCountObserver();
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(action2);
        subscription = observable.connect();
    }

    @OnClick(R.id.btn_reply_time)
    void replyTime() {
        observable = relayTimeObserver();
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(action2);
        subscription = observable.connect();
    }


    @OnClick(R.id.btn_stop)
    void stop() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }
}
