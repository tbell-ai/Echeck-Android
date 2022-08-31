package kr.co.tbell.echeck.views.fragment.info;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.constant.ColumnContract;
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.dto.InfoData;
import kr.co.tbell.echeck.views.activity.InfoActivity;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class Info5Fragment extends Fragment {

    private InfoActivity infoActivity;
    private InfoData info;

    private Button prev;
    private Button success;
    private Spinner discount1;
    private Spinner discount2;

    private EcheckDatabaseManager dbHandler;
    private House house;

    private static Info5Fragment instance;

    private Info5Fragment() {}

    public static Info5Fragment newInstance() {
        instance = new Info5Fragment();
        return instance;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Activity에 접근하기 위해 사용
        infoActivity = (InfoActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info5, container, false);

        Bundle bundle = getArguments();
        info = (InfoData) bundle.getSerializable("infoData");

        prev = rootView.findViewById(R.id.info5_prev);
        success = rootView.findViewById(R.id.info5_success);
        discount1 = rootView.findViewById(R.id.house_discount1);
        discount2 = rootView.findViewById(R.id.house_discount2);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        house = dbHandler.getOneHouse(info.getCurrentHouseId());

        final String[] houseDiscountList1 = getResources().getStringArray(R.array.house_discount_item1);
        final String[] houseDiscountList2 = getResources().getStringArray(R.array.house_discount_item2);

        /**
         * 장애인/유공자 선택시, 다른 할인과 중복할 수 없음을 안내함
         */
        discount2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(houseDiscountList2[position].contains("유공자") || houseDiscountList2[position].contains("장애인")) {
                    discount1.setSelection(0);
                    discount1.setBackground(getResources().getDrawable(R.drawable.bg_spinner_inactive, null));
                    discount1.setEnabled(false);
                    Toast.makeText(getContext(), "장애인/유공자 할인은 다른 할인을 중복 선택할 수 없습니다.", Toast.LENGTH_LONG).show();
                } else {
                    discount1.setBackground(getResources().getDrawable(R.drawable.bg_spinner_task, null));
                    discount1.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // DB에 입력된 대가족/생명유지장치 할인 정보로 Spinner 값을 선택하여 표시함
        for(int j=0; j<houseDiscountList1.length; j++) {
            if(house.getHouseDiscount1().equals(houseDiscountList1[j])) {
                discount1.setSelection(j);
            }
        }

        // DB에 입력된 복지 할인 정보로 Spinner 값을 선택하여 표시함
        for(int k=0; k<houseDiscountList2.length; k++) {
            if(house.getHouseDiscount2().equals(houseDiscountList2[k])) {
                discount2.setSelection(k);
            }
        }

        /**
         * 이전 화면 이동 버튼 클릭시, 이전 화면으로 이동, 파라미터는 없음
         */
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoActivity.onInfoPageChange(Info4Fragment.newInstance(), "prev", null);
            }
        });

        /**
         * 완료 버튼 클릭시, 선택된 할인 저장하며 이전화면으로 이동
         */
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoData param = new InfoData();
                param.setHouseCount(info.getHouseCount());

                ContentValues values = new ContentValues();

                String discountType1 = discount1.getSelectedItem().toString();
                String discountType2 = discount2.getSelectedItem().toString();

                if(discountType2.contains("유공자") || discountType2.contains("장애인")) {
                    discountType1 = "해당사항없음";
                }

                values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT1, discountType1);
                values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT2, discountType2);

                int result = dbHandler.updateData("house", values, "_id=?", new String[]{Long.toString(info.getCurrentHouseId())});

                if (result > 0) {
                    Toast.makeText(getContext(), "해당 가구의 할인 정보가 변경되었습니다!", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("db_error", "update error");
                }

                dbHandler.closeDatabase();
                infoActivity.onInfoPageChange(Info4Fragment.newInstance(), "prev", param);
            }
        });

        return rootView;
    }
}
