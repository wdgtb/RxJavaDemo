package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RxbindingFragment extends Fragment {

    @Bind(R.id.tv_result)
    TextView tvResult;
    @Bind(R.id.btn_click)
    TextView btnClick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxbinding, null);
        ButterKnife.bind(this, view);

        RxView.clicks(btnClick).subscribe(aVoid -> {
            tvResult.setText("btn click");
        });
        RxTextView.textChanges(tvResult)
            .filter(charSequence -> tvResult.getText().toString().trim().length() > 0)
            .subscribe(charSequence -> {
                Toast.makeText(getActivity(), "tv changes", Toast.LENGTH_SHORT).show();
            });

        return view;
    }
}
