package kr.co.tbell.echeck.views.fragment.guide;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.activity.SavingGuideActivity;

public class Guide4Fragment extends Fragment {

    SavingGuideActivity savingGuideActivity;
    Button next;
    Button prev;

    private static Guide4Fragment instance;

    public static Guide4Fragment newInstance() {
        instance = new Guide4Fragment();
        return instance;
    }

    private Guide4Fragment() {}

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

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_guide4, container, false);

        prev = rootView.findViewById(R.id.guide_prev);
        next = rootView.findViewById(R.id.guide_next);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingGuideActivity.onInfoPageChange(Guide3Fragment.newInstance(), "prev");
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingGuideActivity.onInfoPageChange(Guide5Fragment.newInstance(), "next");
            }
        });

        return rootView;
    }
}
