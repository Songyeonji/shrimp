package com.example.seuzima;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class PreviewFragment extends Fragment {

    private String start_text;
    private String dest_text;

    TextView start_textView;
    TextView dest_textView;
    TextView hour_textView;
    TextView hourN_textView;
    TextView min_textView;
    TextView minN_textView;
    TextView dist_textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_preview, container, false);
        return rootview;

    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        start_text = this.getArguments().getString("start");
        dest_text = this.getArguments().getString("dest");

        start_textView = view.findViewById(R.id.text_start);
        dest_textView = view.findViewById(R.id.text_dest);
        hour_textView = view.findViewById(R.id.text_hour);
        hourN_textView = view.findViewById(R.id.text_hourN);

        min_textView = view.findViewById(R.id.text_min);
        minN_textView = view.findViewById(R.id.text_minN);

        dist_textView=view.findViewById(R.id.text_dist);

        set_text();
    }

    private void set_text() {
        start_textView.setText(start_text);
        dest_textView.setText(dest_text);

        int duration = NAVI_API.duration;
        int distance = NAVI_API.distance;

        Log.d("dur2:", String.valueOf(NAVI_API.duration));
        Log.d("dis2:", String.valueOf(NAVI_API.distance));
        if (duration>=60) {
            int hour = duration/60;
            int min = duration-hour*60;

            hourN_textView.setText(String.valueOf(hour));
            minN_textView.setText(String.valueOf(min));
        } else {
            hourN_textView.setVisibility(View.GONE);
            hour_textView.setVisibility(View.GONE);

            minN_textView.setText(String.valueOf(duration));
        }

        if (distance<1000) {
            dist_textView.setText(String.valueOf(distance)+"m");
        } else {
            Double km = Double.valueOf(Math.round(distance/1000*100)/100.0);
            dist_textView.setText(String.valueOf(km)+"km");
        }
    }
}