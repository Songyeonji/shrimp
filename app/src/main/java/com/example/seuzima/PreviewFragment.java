package com.example.seuzima;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class PreviewFragment extends Fragment {

    private String start_text;
    private String dest_text;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_preview, container, false);
        return rootview;

    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        start_text = this.getArguments().getString("start");
        dest_text = this.getArguments().getString("dest");

        TextView start_textView = view.findViewById(R.id.text_start);
        TextView dest_textView = view.findViewById(R.id.text_dest);

        start_textView.setText(start_text);
        dest_textView.setText(dest_text);

    }
}