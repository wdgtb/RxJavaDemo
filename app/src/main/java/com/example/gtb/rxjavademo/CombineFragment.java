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
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by gtb on 16/8/15.
 */
public class CombineFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_combine, null);
        ButterKnife.bind(this, view);
        return view;
    }


    private Observable<String> zipWithObserver() {
        return createObserver(2).zipWith(createObserver(3), (s, s2) -> s + "-" + s2);
    }

    private Observable<String> zipObserver() {
        return Observable
            .zip(createObserver(2), createObserver(3), createObserver(4), (s, s2, s3) -> s + "-" + s2 + "-" + s3);
    }

    private Observable<String> createObserver(int index) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 1; i <= index; i++) {
                    tvResult.setText(tvResult.getText() + "emitted:" + index + "-" + i + "\n");
                    subscriber.onNext(index + "-" + i);
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }


    @OnClick(R.id.btn_zip)
    void zip() {
        zipObserver().observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> tvResult.setText(tvResult.getText() + "zip:" + s + "\n"));
    }

    @OnClick(R.id.btn_zipWith)
    void zipWith() {
        zipWithObserver().observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> tvResult.setText(tvResult.getText() + "zipWith:" + s + "\n"));
    }

    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }
}
