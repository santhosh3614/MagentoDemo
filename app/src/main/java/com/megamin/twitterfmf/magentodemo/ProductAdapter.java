//package com.megamin.twitterfmf.magentodemo;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.LinearLayout;
//
//import java.util.ArrayList;
//
///**
// * Created by 3140 on 03-12-2015.
// */
//public class ProductAdapter extends BaseAdapter {
//
//    private boolean isDetail = true;
//    Context context;
//    ArrayList<ArrayList<ProductModel>> arrayListArrayList;
//    LayoutInflater layoutInflater;
//
//    public ProductAdapter(Context context, ArrayList<ArrayList<ProductModel>> arrayListArrayList) {
//        this.context = context;
//        this.arrayListArrayList = arrayListArrayList;
//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    public ProductAdapter(Context applicationContext, boolean b, ArrayList<ArrayList<ProductModel>> arrayListArrayList) {
//        this(applicationContext, arrayListArrayList);
//        this.isDetail = b;
//    }
//
//    @Override
//    public int getCount() {
//        return arrayListArrayList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    ViewHolder viewHolder;
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = layoutInflater.inflate(isDetail ? R.layout.row_product :
//                    R.layout.row_product_with_img, parent, false);
//            viewHolder.linearLayout = (ProductsView) convertView.findViewById(R.id.productView);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        viewHolder.linearLayout.removeAllViews();
//        viewHolder.linearLayout.initView(arrayListArrayList.get(position));
//
//        return convertView;
//    }
//
//    class ViewHolder {
//        ProductsView linearLayout;
//    }
//}
