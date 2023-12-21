package com.example.seuzima;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class RoutePreviewActivity extends AppCompatActivity {

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_preview);
        addFragment();
    }

    private void addFragment() {
        // FragmentManager를 통해 Fragment의 트랜잭션 시작
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // FrameLayout에 추가할 Fragment 생성
        mapFragment = new MapFragment();

        // Fragment를 FrameLayout에 추가
        fragmentTransaction.replace(R.id.map_layout,mapFragment);

        // 트랜잭션 완료
        fragmentTransaction.commit();
    }
}