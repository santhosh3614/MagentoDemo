package com.megamin.twitterfmf.magentodemo;//package com.megamin.twitterfmf.magentodemo;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Build;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
///**
// * Created by 3140 on 03-12-2015.
// */
//public class ProductsView extends LinearLayout {
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public ProductsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        setOrientation(VERTICAL);
//    }
//
//    public ProductsView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        setOrientation(VERTICAL);
//    }
//
//    public ProductsView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        setOrientation(VERTICAL);
//    }
//
//    public ProductsView(Context context) {
//        super(context);
//        setOrientation(VERTICAL);
//    }
//
//
//    public void initView(ArrayList<ProductModel> productModelArrayList) {
//        for (int i = 0; i < productModelArrayList.size(); i++) {
//
//            LinearLayout linearLayout = new LinearLayout(getContext());
//
//
//            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
//            linearLayout.setLayoutParams(layoutParams);
//            TextView type_tv = new TextView(getContext());
//            LinearLayout.LayoutParams layoutParams_type = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.3f);
//            type_tv.setLayoutParams(layoutParams_type);
//            type_tv.setTextColor(Color.BLACK);
//            type_tv.setText(productModelArrayList.get(i).getKey());
//            type_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
//            type_tv.setSingleLine();
//            type_tv.setEllipsize(TextUtils.TruncateAt.END);
//            TextView value_tv = new TextView(getContext());
//            value_tv.setText(": "+productModelArrayList.get(i).getValue());
//            LinearLayout.LayoutParams layoutParams_value = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.7f);
//
//            value_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
//            value_tv.setTextColor(Color.BLACK);
//            value_tv.setLayoutParams(layoutParams_value);
//            linearLayout.addView(type_tv);
//            linearLayout.addView(value_tv);
//
//            linearLayout.setOrientation(HORIZONTAL);
//
//            addView(linearLayout);
//
//
//        }
//        invalidate();
//    }
//}