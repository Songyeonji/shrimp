package com.example.seuzima;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class searched_sub extends LinearLayout {
    public searched_sub(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public searched_sub(Context context) {
        super(context);

        init(context);
    }
    private void init(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_searched_view,this,true);
    }
}
