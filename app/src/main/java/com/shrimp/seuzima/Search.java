package com.shrimp.seuzima;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

public class Search extends AppCompatActivity {

    private searched_sub l_sub;

    // 사용자가 위치 정보를 검색하고 '엔터'를 치면 보여지는 정보 페이지
    public static LinearLayout loc_inform_view;

    // 사용자가 위치 정보를 검색하기 전('엔터'를 치기 전) 보여지는 최근 검색어 페이지
    public static LinearLayout recent_view;

    // 정보 페이지에 출력되는 위치 정보의 위경도를 저장하는 변수들
    public Double longi;
    public Double lati;

    public static Context context;

    // 검색하는 editText
    public static EditText search_bar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;

        Intent intent = getIntent();
        longi = intent.getDoubleExtra("longi",0);
        lati = intent.getDoubleExtra("lati",0);


        search_bar = (EditText) findViewById(R.id.edittext_search);
        recent_view = (LinearLayout) findViewById(R.id.recent_view);
        loc_inform_view = (LinearLayout) findViewById(R.id.loc_inform_view);



        // 검색 버튼 또는 엔터 키를 눌렀을 때 동작하도록 설정
        search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    String text = search_bar.getText().toString();
                    new NaverApiTask(text).execute();
//                    read_json(text);

                    return true;
                }
                return false;
            }
        });

        // edittext의 상태 동적 할당 받음 -> edittext에 글자 입력하면 위치 관련 정보 레이아웃이 보이도록함
        // -> 입력한 글자가 없으면 최근 검색어 레이아웃이 보이도록 함.
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String search_text = s.toString();
                if (search_text.isEmpty()) {
                    recent_view.setVisibility(View.VISIBLE);
                    loc_inform_view.setVisibility(View.GONE);
                } else {
                    recent_view.setVisibility(View.GONE);
                    loc_inform_view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search_text = s.toString();
                if (search_text.isEmpty()) {
                    recent_view.setVisibility(View.VISIBLE);
                    loc_inform_view.setVisibility(View.GONE);

                } else {
                    recent_view.setVisibility(View.GONE);
                    loc_inform_view.setVisibility(View.VISIBLE);

                    loc_inform_view.removeAllViewsInLayout();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search_text = s.toString();
                if (search_text.isEmpty()) {
                    recent_view.setVisibility(View.VISIBLE);
                    loc_inform_view.setVisibility(View.GONE);
                } else {
                    recent_view.setVisibility(View.GONE);
                    loc_inform_view.setVisibility(View.VISIBLE);
                }
            }
        });
    }




    // 사용자가 검색창에서 '엔터'를 눌렀을 때 실행되는 함수
    public void searched(String name, String address, int x, int y, String link, String tel, String category){


        loc_inform_view = (LinearLayout) findViewById(R.id.loc_inform_view);

        l_sub = new searched_sub(getApplicationContext());

        View l_sub = getLayoutInflater().inflate(R.layout.fragment_searched_view, null);
        TextView searched_name = l_sub.findViewById(R.id.loc_inform_text1);
        TextView searched_address = l_sub.findViewById(R.id.loc_inform_detail1);
        TextView searched_lati = l_sub.findViewById(R.id.longi1);
        TextView searched_longi = l_sub.findViewById(R.id.lati1);
        TextView searched_category = l_sub.findViewById(R.id.text_category);
        TextView searched_link = l_sub.findViewById(R.id.link);
        TextView searched_tel = l_sub.findViewById(R.id.tel);

        searched_name.setText(name);
        searched_address.setText(address);
        searched_lati.setText(String.valueOf(x));
        searched_longi.setText(String.valueOf(y));
        searched_category.setText(category);
        searched_link.setText(link);
        searched_tel.setText(tel);

        LinearLayout search_list = l_sub.findViewById(R.id.linear_list);
        search_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView click_name = search_list.findViewById(R.id.loc_inform_text1);
                TextView click_address = search_list.findViewById(R.id.loc_inform_detail1);
                TextView click_lati = l_sub.findViewById(R.id.longi1);
                TextView click_longi = l_sub.findViewById(R.id.lati1);
                TextView click_category = search_list.findViewById(R.id.text_category);
                TextView click_link = l_sub.findViewById(R.id.link);
                TextView click_tel = l_sub.findViewById(R.id.tel);

                String c_name = click_name.getText().toString();
                String c_addr = click_address.getText().toString();
                int c_x = Integer.parseInt(click_lati.getText().toString());
                int c_y = Integer.parseInt(click_longi.getText().toString());
                String c_category = click_category.getText().toString();
                String c_link = click_link.getText().toString();
                String c_tel = click_tel.getText().toString();

                Intent go_mapview = new Intent(Search.this, MainActivity.class);
                go_mapview.putExtra("loc_name", c_name);
                go_mapview.putExtra("loc_addr", c_addr);
                go_mapview.putExtra("loc_x", c_x);
                go_mapview.putExtra("loc_y", c_y);
                go_mapview.putExtra("loc_category", c_category);
                go_mapview.putExtra("loc_link", c_link);
                go_mapview.putExtra("loc_tel", c_tel);

                startActivity(go_mapview);
            }
        });

        runOnUiThread(() -> {
            loc_inform_view.addView(l_sub);
        });

    }

    private class NaverApiTask extends AsyncTask<Void, Void, String> {

        String name=null;
        NaverApiTask(String name) {
            this.name=name;
        }
        @Override
        protected String doInBackground(Void... voids) {
            // ApiExamSearchBlog 클래스의 main 메서드 호출
            try {
                SEARCH_API.main(name);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // 여기에서는 비동기적으로 호출되기 때문에 결과가 없거나 기다리지 않는 것이 좋음.
            return "ApiExamSearchBlog.main 호출 완료";
        }

        @Override
        protected void onPostExecute(String result) {
            // 결과로 UI 업데이트
            Log.d("result:", result);
        }
    }


    // '<' 버튼 클릭하면 실행되는 함수. 이전 '홈'화면으로 되돌아가는 코드
    public void before(View view) {
        Intent intent_before = new Intent(Search.this, MainActivity.class);
        startActivity(intent_before);
    }

}