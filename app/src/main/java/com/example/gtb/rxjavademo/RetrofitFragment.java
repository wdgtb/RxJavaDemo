package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gtb.Util.Data;
import com.example.gtb.model.Item;
import com.example.gtb.adapter.ItemListAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;

public class RetrofitFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;
    @Bind(R.id.cacheRv)
    RecyclerView cacheRv;
    ItemListAdapter adapter = new ItemListAdapter();
    private Subscription subscription;
    private long startingTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retrofit, null);
        ButterKnife.bind(this, view);
        cacheRv.setAdapter(adapter);
        cacheRv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        return view;
    }

    @OnClick(R.id.btn_load)
    void load() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        startingTime = System.currentTimeMillis();
        subscription = Data.getInstance()
            .subscribeData(new Observer<List<Item>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(List<Item> items) {
                    int loadingTime = (int) (System.currentTimeMillis() - startingTime);
                    tvResult.setText(getString(R.string.loading_time_and_source, loadingTime, Data.getInstance().getDataSourceText()));
                    adapter.setItems(items);
                }
            });
    }

    @OnClick(R.id.btn_clearMemoryCache)
    void clearMemoryCache() {
        Data.getInstance().clearMemoryCache();
        adapter.setItems(null);
        Toast.makeText(getActivity(), "内存缓存已清空", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_clearMemoryAndDiskCache)
    void clearMemoryAndDiskCache() {
        Data.getInstance().clearMemoryAndDiskCache();
        adapter.setItems(null);
        Toast.makeText(getActivity(), "内存缓存和磁盘缓存都已清空", Toast.LENGTH_SHORT).show();
    }
}
