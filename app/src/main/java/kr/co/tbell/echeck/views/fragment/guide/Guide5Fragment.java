package kr.co.tbell.echeck.views.fragment.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.activity.MainActivity;
import kr.co.tbell.echeck.views.activity.SavingGuideActivity;

public class Guide5Fragment extends Fragment {

    SavingGuideActivity savingGuideActivity;
    Button prev;
    Button main;

    private static Guide5Fragment instance;

    public static Guide5Fragment newInstance() {
        instance = new Guide5Fragment();
        return instance;
    }

    private Guide5Fragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        savingGuideActivity = (SavingGuideActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_guide5, container, false);

        prev = rootView.findViewById(R.id.guide_prev);
        main = rootView.findViewById(R.id.go_main);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingGuideActivity.onInfoPageChange(Guide4Fragment.newInstance(), "prev");
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}