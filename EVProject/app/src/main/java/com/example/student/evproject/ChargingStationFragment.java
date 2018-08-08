package com.example.student.evproject;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChargingStationFragment extends Fragment {
 //FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_menu1, container, false);
    WebView webView1;

    public ChargingStationFragment() {
        // Required empty public constructor\

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_charging_station, container, false);
        webView1 = fl.findViewById(R.id.wv_chstation);
        webView1.loadUrl("https://ev.or.kr/mobile/monitor/srchStationList?gubun=1&curX=954137.8926812053&curY=1951720.1137670989&poNm=1");

        return fl;
    }


}
