package kr.co.tbell.echeck.views.fragment.info;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.dto.InfoData;
import kr.co.tbell.echeck.views.activity.InfoActivity;
import kr.co.tbell.echeck.views.activity.MainActivity;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.constant.ColumnContract.ColumnEntry;

public class Info2Fragment extends Fragment {

    private InfoActivity infoActivity;
    private InfoData info;

    private Button prev;
    private Button success;
    private Spinner houseInput;
    private Spinner discount1;
    private Spinner discount2;

    private EcheckDatabaseManager dbHandler;

    private static Info2Fragment instance;

    public Info2Fragment() {}

    public static Info2Fragment newInstance() {
        if(instance == null) {
            instance = new Info2Fragment();
        }
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info2, container, false);

        Bundle bundle = getArguments();
        info = (InfoData) bundle.getSerializable("infoData");

        prev = rootView.findViewById(R.id.info2_prev);
        success = rootView.findViewById(R.id.info2_success);
        houseInput = rootView.findViewById(R.id.house_input);
        discount1 = rootView.findViewById(R.id.discount1);
        discount2 = rootView.findViewById(R.id.discount2);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        final String[] discountList2 = getResources().getStringArray(R.array.discount_item2);

        /**
         * Spinner에서 비주거용 선택시, 할인 선택 비활성화 시킴
         */
        houseInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(houseInput.getItemAtPosition(position).toString().equals("비주거용")) {
                    discount1.setBackground(getResources().getDrawable(R.drawable.bg_spinner_inactive, null));
                    discount2.setBackground(getResources().getDrawable(R.drawable.bg_spinner_inactive, null));
                    discount1.setEnabled(false);
                    discount2.setEnabled(false);
                } else {
                    discount1.setBackground(getResources().getDrawable(R.drawable.bg_spinner_task, null));
                    discount2.setBackground(getResources().getDrawable(R.drawable.bg_spinner_task, null));
                    discount1.setEnabled(true);
                    discount2.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        /**
         * 장애인/유공자 선택시, 다른 할인과 중복할 수 없음을 안내함
         */
        discount2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(discountList2[position].contains("유공자") || discountList2[position].contains("장애인")) {
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

        /**
         * 이전 버튼 클릭 이벤트, 이전 화면으로 돌아감
         */
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoActivity.onInfoPageChange(Info1Fragment.newInstance(), "prev", null);
            }
        });

        /**
         * 완료 버튼 클릭시, 로컬 DB에 저장하고 메인 화면으로 진입
         */
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date = df.format(Calendar.getInstance().getTime());

                String house = houseInput.getSelectedItem().toString();
                String discountType1 = discount1.getSelectedItem().toString();
                String discountType2 = discount2.getSelectedItem().toString();

                if(house.equals("비주거용")) {
                    discountType1 = "해당사항없음";
                    discountType2 = "해당사항없음";
                }

                values.put(ColumnEntry.COLUMN_NICKNAME, info.getNickname());
                values.put(ColumnEntry.COLUMN_BEFORE_ELECRT, info.getBeforeElect());
                values.put(ColumnEntry.COLUMN_NOW_PERIOD, info.getNowPeriod());
                values.put(ColumnEntry.COLUMN_USE, info.getUseElect());
                values.put(ColumnEntry.COLUMN_HOUSE, house);
                values.put(ColumnEntry.COLUMN_HOUSE_COUNT, "1");
                values.put(ColumnEntry.COLUMN_USER_CREATED_AT, date);

                long newUserId = dbHandler.putData("user", values);

                if (newUserId > 0) {

                    if(discountType2.contains("유공자") || discountType2.contains("장애인")) {
                        discountType1 = "해당사항없음";
                    }

                    ContentValues values2 = new ContentValues();

                    values2.put(ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN, "Y");
                    values2.put(ColumnEntry.COLUMN_HOUSE_DISCOUNT1, discountType1);
                    values2.put(ColumnEntry.COLUMN_HOUSE_DISCOUNT2, discountType2);
                    values2.put(ColumnEntry.COLUMN_USER_ID, newUserId);

                    long newHouseId = dbHandler.putData("house", values2);
                    dbHandler.closeDatabase();

                    if (newHouseId > 0) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Log.d("db_error", "insert error");
                }
            }
        });
        return rootView;
    }
}
