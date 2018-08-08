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
public class UserFragment extends Fragment {
    WebView wv;
    FrameLayout user_frag;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        user_frag = (FrameLayout)inflater.inflate(R.layout.fragment_user, container, false);
        wv = user_frag.findViewById(R.id.user_wv);
        wv.loadUrl("http://70.12.114.147/ws/admin.do");
        return user_frag;
    }

}
