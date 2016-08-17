package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by gtb on 16/8/15.
 */
public class TransformFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transform, null);
        ButterKnife.bind(this, view);
        return view;
    }

    //buffer 达到缓存后统一发出,error时直接发出
    //点击按钮达到指定次数后，触发某事件
    private Observable<List<Integer>> bufferObserver() {
        return Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .buffer(2, 3);//count为2，skip为3
    }

    private Observable<List<Long>> bufferTimeObserver() {
        return Observable.interval(1, TimeUnit.SECONDS)
            .buffer(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread());
    }

    @OnClick(R.id.btn_buffer)
    void buffer() {
        bufferObserver().subscribe(i -> {
            tvResult.setText(tvResult.getText() + "buffer:" + i + "\n");
        });
    }

    @OnClick(R.id.btn_buffer_time)
    void bufferTime() {
        bufferTimeObserver().subscribe(i -> {
            tvResult.setText(tvResult.getText() + "bufferTime:" + i + "\n");
        });
    }


    //map是在一个item被发射之后，到达map处经过转换变成另一个item然后继续往下走；
    //flapMap是item被发射之后，到达flatMap处经过转换变成一个Observable，而这个Observable并不会直接被发射出去，而是会立即被激活，然后把它发射出的每个item都传入流中，再继续走下去。
    // 最后的顺序可能会交错,顺序有严格的要求的话可以使用concatMap
    //1.经过Observable的转换，相当于重新开了一个异步的流
    //2.item被分散了，个数发生了变化。
    //3.map是1对1的转化,flatMap可以实现1对多的转化,例如输出Observable.from

    //FlatMap
    private Observable<Integer> flatMapObserver() {
        return Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .flatMap(Observable::just);
    }

    //map
    private Observable<Integer> mapObserver() {
        return Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .map(integer -> integer);
    }

    @OnClick(R.id.btn_flatMap)
    void flatMap() {
        flatMapObserver().subscribe(i -> {
            tvResult.setText(tvResult.getText() + "flat map:" + i + "\n");
        });
    }

    @OnClick(R.id.btn_map)
    void map() {
        mapObserver().subscribe(i -> {
            tvResult.setText(tvResult.getText() + "map:" + i + "\n");
        });
    }


    //Scan操作一个序列,类似递归
    private Observable<Integer> scanObserver() {
        Integer[] s = new Integer[]{2, 2, 2, 2, 2};
        return Observable.from(Arrays.asList(s))
            .scan((x, y) -> x * y)
            .observeOn(AndroidSchedulers.mainThread());
    }

    @OnClick(R.id.btn_scan)
    void scan() {
        scanObserver().subscribe(i -> {
            tvResult.setText(tvResult.getText() + "scan:" + i + "\n");
        });
    }


    @OnClick(R.id.btn_clean)
    void clean() {
        tvResult.setText("");
    }
}
