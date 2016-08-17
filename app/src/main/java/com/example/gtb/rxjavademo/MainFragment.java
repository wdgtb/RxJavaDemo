package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.bind(this, view);
        return view;
    }

    private void open(Fragment fragment) {
        final String tag = fragment.getClass().toString();
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .addToBackStack(tag)
            .replace(R.id.main_content, fragment, tag)
            .commit();
    }


    @OnClick(R.id.btn_create)
    void btnCreate() {
        open(new CreateFragment());
    }

    @OnClick(R.id.btn_transform)
    void btnTransform() {
        open(new TransformFragment());
    }

    @OnClick(R.id.btn_filter)
    void btnFilter() {
        open(new FilterFragment());
    }

    @OnClick(R.id.btn_combine)
    void btnCombine() {
        open(new CombineFragment());
    }

    @OnClick(R.id.btn_error)
    void btnError() {
        open(new ErrorFragment());
    }

    @OnClick(R.id.btn_utility)
    void btnUtility() {
        open(new UtilityFragment());
    }

    @OnClick(R.id.btn_connect)
    void btnConnect() {
        open(new ConnectFragment());
    }

    @OnClick(R.id.btn_custom)
    void btnCustom() {
        open(new CustomFragment());
    }
}
