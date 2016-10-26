package com.classic.wages.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import cn.qy.util.activity.R;
import com.bumptech.glide.Glide;
import com.classic.adapter.interfaces.ImageLoad;

public class GlideImageLoad implements ImageLoad {

    @Override public void load(@NonNull Context context, @NonNull ImageView imageView,
                               @NonNull String imageUrl) {
        Glide.with(context)
             .load(imageUrl)
             .placeholder(R.drawable.ic_github)
             .into(imageView);
    }
}
