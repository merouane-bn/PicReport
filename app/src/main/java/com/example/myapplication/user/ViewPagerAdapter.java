package com.example.myapplication.user;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    ArrayList<Uri> imageList;
    LayoutInflater inflater;

    ViewPagerAdapter(Context context, ArrayList<Uri> imageList) {
        this.context = context;
        this.imageList = imageList;
        this.inflater = LayoutInflater.from(context); // Initialize the inflater

    }
    @Override
    public int getCount() {
        return imageList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.showimagelayout, container, false);
        ImageView imageView = view.findViewById(R.id.UploadImage);
        imageView.setImageURI(imageList.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((RelativeLayout) object).removeView(container);
    }
}
