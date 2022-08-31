package kr.co.tbell.echeck.views.fragment.mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.views.activity.MainActivity;
import kr.co.tbell.echeck.views.activity.MypageActivity;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

// 1주택 수 가구
public class Mypage2Fragment extends Fragment {

    private ArrayList<String> spinnerArray;
    private ArrayAdapter<String> spinnerArrayAdapter;

    private MypageActivity mypageActivity;

    private Spinner houseSpinner;
    private TextView nicknameView;
    private TextView useInfoView;
    private TextView beforeElectView;
    private TextView periodInfoView;
    private TextView houseCountView;
    private TextView discountTitle;
    private TextView discountDetail;
    private Button goMainButton;

    private EcheckDatabaseManager dbHandler;
    private static Mypage2Fragment instance;

    public static Mypage2Fragment newInstance() {
        instance = new Mypage2Fragment();
        return instance;
    }

    public Mypage2Fragment() {}

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage2, container, false);

        nicknameView = rootView.findViewById(R.id.my_nickname);
        useInfoView = rootView.findViewById(R.id.use_info);
        beforeElectView = rootView.findViewById(R.id.before_elect_info);
        periodInfoView = rootView.findViewById(R.id.period_info);
        discountTitle = rootView.findViewById(R.id.discount_title);
        discountDetail = rootView.findViewById(R.id.discount_detail);
        houseCountView = rootView.findViewById(R.id.house_count);
        houseSpinner = rootView.findViewById(R.id.select_house);
        goMainButton = rootView.findViewById(R.id.go_main);

        spinnerArray = new ArrayList<String>();
        spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        houseSpinner.setAdapter(spinnerArrayAdapter);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        mypageActivity.setToolbar();

        // 1. DB에서 유저 정보 받아오기
        User user = dbHandler.getUser();
        System.out.println("User : " + user.toString());
        for(int i=1; i<=Integer.parseInt(user.getHouseCount()); i++) {
            // 2. 가구수 정보 만큼 ArrayList Add
            spinnerArray.add("가구 " + i);
        }
        nicknameView.setText(user.getNickname());
        useInfoView.setText(user.getElectUse());
        beforeElectView.setText(user.getElectBefore());
        periodInfoView.setText(user.getElectPeriod());
        houseCountView.setText(user.getHouseCount());

        // 3. spinnerArrayAdapter 업데이트 해주기기
        spinnerArrayAdapter.notifyDataSetChanged();

        // 4. Spinner 클릭시, 가구 할인 정보 DB에서 받아오기
        houseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 1. 클릭한 Spinner의 가구수 정보의 Position으로
                String selectItem = (String) houseSpinner.getSelectedItem();
                List<House> houses = dbHandler.getHouse();
                Long houseId = houses.get(position).getId();

                // 2. 추출한 숫자로 해당 가구 DB Select(조건절을 HOUSE_NUMBER로 걸면됨)
                House house = dbHandler.getOneHouse(houseId);

                // 3. 해당 가구 할인 정보 TextView에 찍어주기기
                discountTitle.setText("가구 " + selectItem.charAt(3) + " 할인 등록 정보");
                discountDetail.setText(house.getHouseDiscount1() + "\n" + house.getHouseDiscount2());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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