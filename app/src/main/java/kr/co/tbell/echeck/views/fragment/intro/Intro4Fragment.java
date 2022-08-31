package kr.co.tbell.echeck.views.fragment.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.activity.AgreeActivity;

public class Intro4Fragment extends Fragment {

    private Button introNext;

    private static Intro4Fragment instance;

    public Intro4Fragment() {}

    public static Intro4Fragment newInstance() {
        if(instance == null) {
            instance = new Intro4Fragment();
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


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro4, container, false);

        introNext = rootView.findViewById(R.id.intro_next_btn);
        introNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgreeActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
