package kr.co.tbell.echeck.views.fragment.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import kr.co.tbell.echeck.R;

public class Intro1Fragment extends Fragment {

    private static Intro1Fragment instance;

    public Intro1Fragment() {}

    public static Intro1Fragment newInstance() {
        if(instance == null) {
            instance = new Intro1Fragment();
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

        return inflater.inflate(R.layout.fragment_intro1, container, false);
    }
}
