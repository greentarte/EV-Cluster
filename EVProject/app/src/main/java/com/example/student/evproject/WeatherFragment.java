package com.example.student.evproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    WebView webView2;

    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_weather, container, false);

        webView2 = fl.findViewById(R.id.wv_weather);
        webView2.loadUrl("http://70.12.114.147/ws/weather.do");

        return fl;
    }

}
