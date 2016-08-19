package com.example.gtb.Util;

import android.widget.TextView;

public class TextViewUtil {
    public TextViewUtil() {

    }

    public static void setText(TextView view, String s) {
        view.setText(view.getText().toString() + s + "\n");
    }
}
