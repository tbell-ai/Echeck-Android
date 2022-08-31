package kr.co.tbell.echeck.views.fragment.mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.views.activity.MainActivity;
import kr.co.tbell.echeck.views.activity.MypageActivity;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

// 주택용(저압/고압)
public class Mypage1Fragment extends Fragment {

    private MypageActivity mypageActivity;

    private TextView nicknameView;
    private TextView useInfoView;
    private TextView houseInfoView;
    private TextView beforeElectView;
    private TextView periodInfoView;
    private TextView firstDiscountView;
    private TextView secondDiscountView;
    private Button goMainButton;

    private EcheckDatabaseManager dbHandler;
    private static Mypage1Fragment instance;

    public static Mypage1Fragment newInstance() {
        instance = new Mypage1Fragment();
        return instance;
    }

    public Mypage1Fragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mypageActivity = (MypageActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage1, container, false);

        nicknameView = rootView.findViewById(R.id.my_nickname);
        useInfoView = rootView.findViewById(R.id.use_info);
        houseInfoView = rootView.findViewById(R.id.house_info);
        beforeElectView = rootView.findViewById(R.id.before_elect_info);
        periodInfoView = rootView.findViewById(R.id.period_info);
        firstDiscountView = rootView.findViewById(R.id.discount1_info);
        secondDiscountView = rootView.findViewById(R.id.discount2_info);
        goMainButton = rootView.findViewById(R.id.go_main);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        mypageActivity.setToolbar();

        // 1. 내 정보 받아옴
        User user = dbHandler.getUser();
        // 2. 내 할인정보 받아옴
        List<House> houses = dbHandler.getHouse();

        dbHandler.closeDatabase();

        // 3. 정보 화면에 표시
        nicknameView.setText(user.getNickname());
        useInfoView.setText(user.getElectUse());
        houseInfoView.setText(user.getElectHouse());
        beforeElectView.setText(user.getElectBefore());
        periodInfoView.setText(user.getElectPeriod());
        firstDiscountView.setText(houses.get(0).getHouseDiscount1());
        secondDiscountView.setText(houses.get(0).getHouseDiscount2());

        goMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                mypageActivity.finish();
            }
        });

        return rootView;
    }
}
