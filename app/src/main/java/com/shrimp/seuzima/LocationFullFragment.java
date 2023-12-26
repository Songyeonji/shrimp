package com.shrimp.seuzima;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationFullFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_location_full, container, false);

        ImageView bttnDelete = rootview.findViewById(R.id.delete_bttn);
        bttnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.show_searchingLayout();
            }
        });
        return rootview;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView loc_category = view.findViewById(R.id.text_category);

        TextView textView_title = view.findViewById(R.id.loc_title);
        TextView textView_addr = view.findViewById(R.id.loc_addr);
        TextView textView_weekOpen = view.findViewById(R.id.weekOpen);
        TextView textView_weekClose = view.findViewById(R.id.weekClose);
        TextView textView_satOpen = view.findViewById(R.id.satOpen);
        TextView textView_satClose = view.findViewById(R.id.satClose);
        TextView textView_holiOpen = view.findViewById(R.id.holiOpen);
        TextView textView_holiClose = view.findViewById(R.id.holiClose);
        TextView textView_baseRate = view.findViewById(R.id.baseRate);
        TextView textView_baseTime = view.findViewById(R.id.baseTime);
        TextView textView_addRate = view.findViewById(R.id.addRate);
        TextView textView_addTime = view.findViewById(R.id.addTime);

        FrameLayout show_full_view = view.findViewById(R.id.show_full_view);



        // mapview에서 받아온 위치 정보(이름, 주소, 위경도) 가져와서 문자열 및 double 변수에 저장
        String title = this.getArguments().getString("loc_name");
        String addr = this.getArguments().getString("loc_addr");
        Double latitude = this.getArguments().getDouble("loc_lat");
        Double longitude = this.getArguments().getDouble("loc_lon");
        String weekOpen = this.getArguments().getString("weekOpen");
        String weekClose = this.getArguments().getString("weekClose");
        String satOpen = this.getArguments().getString("satOpen");
        String satClose = this.getArguments().getString("satClose");
        String holiOpen = this.getArguments().getString("holiOpen");
        String holiClose = this.getArguments().getString("holiClose");
        String baseRate = this.getArguments().getString("baseRate");
        String baseTime = this.getArguments().getString("baseTime");
        String addRate = this.getArguments().getString("addRate");
        String addTime = this.getArguments().getString("addTime");
        String category = this.getArguments().getString("loc_category");
        //"loc_lon", longitude

        // 장소 이름, 주소는 각각 textview에 저장
        textView_title.setText(title);
        textView_addr.setText(addr);
        textView_weekOpen.setText(weekOpen);
        textView_weekClose.setText(weekClose);
        textView_satOpen.setText(satOpen);
        textView_satClose.setText(satClose);
        textView_holiOpen.setText(holiOpen);
        textView_holiClose.setText(holiClose);
        textView_baseRate.setText(baseRate);
        textView_baseTime.setText(baseTime);
        textView_addRate.setText(addRate);
        textView_addTime.setText(addTime);

        loc_category.setText(category);


    }
}