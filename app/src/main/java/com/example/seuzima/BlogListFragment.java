package com.example.seuzima;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class BlogListFragment extends LinearLayout {

    public BlogListFragment(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public BlogListFragment(Context context) {
        super(context);

        init(context);
    }
    private void init(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_searched_view,this,true);
    }
}