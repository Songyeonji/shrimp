package com.example.seuzima;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        TextView loc_title = view.findViewById(R.id.loc_title);
        TextView loc_addr = view.findViewById(R.id.loc_addr);
        TextView loc_tel = view.findViewById(R.id.loc_tel);
        TextView loc_link = view.findViewById(R.id.loc_link);
        TextView loc_category = view.findViewById(R.id.text_category);


        // mapview에서 받아온 위치 정보(이름, 주소, 위경도) 가져와서 문자열 및 double 변수에 저장
        String title = this.getArguments().getString("loc_name");
        String addr = this.getArguments().getString("loc_addr");
        Double latitude = this.getArguments().getDouble("loc_lat");
        Double longitude = this.getArguments().getDouble("loc_lon");
        String category = this.getArguments().getString("loc_category");
        String link = this.getArguments().getString("loc_link");
        String tel = this.getArguments().getString("loc_tel");
        //"loc_lon", longitude

        // 장소 이름, 주소는 각각 textview에 저장
        loc_title.setText(title);
        loc_addr.setText(addr);
        loc_category.setText(category);
        if (tel==null||tel.equals("")) {

            view.findViewById(R.id.tel_layout).setVisibility(View.GONE);
        } else {
            loc_tel.setText(tel);
        }
        if (link==null||link.equals("")) {
            view.findViewById(R.id.web_layout).setVisibility(View.GONE);

        } else {
            loc_link.setText(link);
        }



    }
}