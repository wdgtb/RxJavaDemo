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

/**
 * Created by gtb on 16/8/15.
 */
public class CustomFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom, null);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Rxjava允许我们来自定义操作符来满足我们特殊的需求。
     * 如果我们的自定义操作符想要作用到Observable发射出来的数据上，使用lift操作符
     * 如果我们的自定义操作符想要改变整个的Observable，使用compose操作符。
     */


    /**
     * 自定义Operator在发射任何数据之前都要使用!subscriber.isUnsubscribed()来检查Subscriber的状态，如果没有任何Subscriber订阅就没有必要去发射数据了
     * 自定义Operator要遵循Observable的核心原则：
     * 可以多次调用Subscriber的onNext方法，但是同一个数据只能调用一次。
     * 可以调用Subscriber的onComplete或者onError方法，但是这两个是互斥的，调用了一个就不能再调用另外一个了，并且一旦调用了任何一个方法就不能再调用onNext方法了。
     * 如果无法保证遵守以上两条，可以对自定义操作符加上serialize操作符，这个操作符会强制发射正确的数据。
     * 自定义Operator内部不能阻塞住。
     */
    //lift 自定义操作符
    private Observable<String> liftObserver() {
        Observable.Operator<String, String> myOperator = new Observable.Operator<String, String>() {
            @Override
            public Subscriber<? super String> call(Subscriber<? super String> subscriber) {
                return new Subscriber<String>(subscriber) {
                    @Override
                    public void onCompleted() {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext("myOperator:" + s);
                        }
                    }
                };
            }

        };
        return Observable.just(1, 2, 3).map(integer -> "map1:" + integer).lift(myOperator).map(s -> "map2:" + s);
    }

    @OnClick(R.id.btn_lift)
    void lift() {
        liftObserver().subscribe(s -> tvResult.setText(tvResult.getText() + "lift:" + s + "\n"));
    }


    /**
     * 如果通过compose组合多个操作符就能达到目的就不要自己去写新的代码来实现，在Rxjava的源码中就有很多这样的例子，如：
     * first()操作符是通take(1).single()来实现的。
     * ignoreElements()是通过 filter(alwaysFalse())来实现的。
     * reduce(a) 是通过 scan(a).last()来实现的。
     * 当有异常的时候，不能继续发射正常的数据，要立刻调用Subscriber的onError方法将异常抛出去。
     * 注意发射数据为null的情况，这和完全不发射数据不是一回事。
     */
    //Compose操作符是将源Observable按照自定义的方式转化成另外一个新的Observable。
    //可以说compose是对Observable进行操作的而lift是对Subscriber进行操作的，作用点是不同的。
    private Observable<String> composeObserver() {
        Observable.Transformer<Integer, String> myTransformer = new Observable.Transformer<Integer, String>() {
            @Override
            public Observable<String> call(Observable<Integer> integerObservable) {
                return integerObservable
                    .map(integer -> "myTransforer:" + integer);
            }
        };
        return Observable.just(1, 2, 3).compose(myTransformer);
    }

    @OnClick(R.id.btn_compose)
    void compose() {
        composeObserver().subscribe(s -> tvResult.setText(tvResult.getText() + "compose:" + s + "\n"));
    }


    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }
}
