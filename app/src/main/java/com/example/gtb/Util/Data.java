package com.example.gtb.Util;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.example.gtb.model.Item;
import com.example.gtb.network.HttpResultFunc;
import com.example.gtb.network.Network;
import com.example.gtb.rxjavademo.R;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class Data {
    private static Data instance;
    private static final int DATA_SOURCE_MEMORY = 1;
    private static final int DATA_SOURCE_DISK = 2;
    private static final int DATA_SOURCE_NETWORK = 3;

    @IntDef({DATA_SOURCE_MEMORY, DATA_SOURCE_DISK, DATA_SOURCE_NETWORK})
    @interface DataSource {
    }

    BehaviorSubject<List<Item>> cache;

    private int dataSource;

    private Data() {
    }

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    private void setDataSource(@DataSource int dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSourceText() {
        int dataSourceTextRes;
        switch (dataSource) {
            case DATA_SOURCE_MEMORY:
                dataSourceTextRes = R.string.data_source_memory;
                break;
            case DATA_SOURCE_DISK:
                dataSourceTextRes = R.string.data_source_disk;
                break;
            case DATA_SOURCE_NETWORK:
                dataSourceTextRes = R.string.data_source_network;
                break;
            default:
                dataSourceTextRes = R.string.data_source_network;
        }
        return App.getInstance().getString(dataSourceTextRes);
    }

    public void loadFromNetwork() {
        Network.getApi().getTopMovie(0, 10)
            .subscribeOn(Schedulers.io())
            .map(new HttpResultFunc<List<Item>>())
            .doOnNext(new Action1<List<Item>>() {
                @Override
                public void call(List<Item> items) {
                    Database.getInstance().writeItems(items);
                }
            })
            .subscribe(new Action1<List<Item>>() {
                @Override
                public void call(List<Item> items) {
                    cache.onNext(items);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
    }

    /**
     * 当Observer订阅了一个BehaviorSubject，它一开始就会释放Observable最近释放的一个数据对象，当还没有任何数据释放时，它则是一个默认值。接下来就会释放Observable释放的所有数据。
     * 如果Observable因异常终止，BehaviorSubject将不会向后续的Observer释放数据，但是会向Observer传递一个异常通知。
     */
    public Subscription subscribeData(@NonNull Observer<List<Item>> observer) {
        if (cache == null) {
            cache = BehaviorSubject.create();
            Observable.create(new Observable.OnSubscribe<List<Item>>() {
                @Override
                public void call(Subscriber<? super List<Item>> subscriber) {
                    List<Item> items = Database.getInstance().readItems();
                    if (items == null) {
                        setDataSource(DATA_SOURCE_NETWORK);
                        loadFromNetwork();
                    } else {
                        setDataSource(DATA_SOURCE_DISK);
                        subscriber.onNext(items);
                    }
                }
            }).subscribeOn(Schedulers.io())
                .subscribe(cache);
        } else {
            setDataSource(DATA_SOURCE_MEMORY);
        }
        //doOnSubscribe() 它和Subscriber.onStart()同样是在subscribe()调用后而且在事件发送前执行，但区别在于它可以指定线程。
        //可以加进度条
        return cache.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void clearMemoryCache() {
        cache = null;
    }

    public void clearMemoryAndDiskCache() {
        clearMemoryCache();
        Database.getInstance().delete();
    }
}
