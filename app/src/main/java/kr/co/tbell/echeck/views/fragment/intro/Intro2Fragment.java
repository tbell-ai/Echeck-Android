package kr.co.tbell.echeck.views.fragment.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import kr.co.tbell.echeck.R;

public class Intro2Fragment extends Fragment {

    private static Intro2Fragment instance;

    public Intro2Fragment() {}

    public static Intro2Fragment newInstance() {
        if(instance == null) {
            instance = new Intro2Fragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_intro2, container, false);
    }
}
