package kr.co.tbell.echeck.views.fragment.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import kr.co.tbell.echeck.R;

public class Intro3Fragment extends Fragment {

    private static Intro3Fragment instance;

    public Intro3Fragment() {}

    public static Intro3Fragment newInstance() {
        if(instance == null) {
            instance = new Intro3Fragment();
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

        return inflater.inflate(R.layout.fragment_intro3, container, false);
    }
}
