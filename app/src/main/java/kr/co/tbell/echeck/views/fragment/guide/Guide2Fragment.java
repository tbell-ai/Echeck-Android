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

public class Guide2Fragment extends Fragment {

    SavingGuideActivity savingGuideActivity;
    Button next;
    Button prev;

    private static Guide2Fragment instance;

    public static Guide2Fragment newInstance() {
        instance = new Guide2Fragment();
        return instance;
    }

    private Guide2Fragment() {}

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

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_guide2, container, false);

        prev = rootView.findViewById(R.id.guide_prev);
        next = rootView.findViewById(R.id.guide_next);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingGuideActivity.onInfoPageChange(Guide1Fragment.newInstance(), "prev");
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingGuideActivity.onInfoPageChange(Guide3Fragment.newInstance(), "next");
            }
        });

        return rootView;
    }
}
