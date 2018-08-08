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
public class MP3Fragment extends Fragment {
    WebView webView3;

    public MP3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_mp3, container, false);

        webView3 = fl.findViewById(R.id.wv_mp3);
        webView3.loadUrl("http://m.youtube.com");

        return fl;
    }

}
