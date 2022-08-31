package kr.co.tbell.echeck.views.fragment.mypage;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.constant.ColumnContract;
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.dto.InfoData;
import kr.co.tbell.echeck.views.activity.MypageActivity;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class MyUpdate2Fragment extends Fragment {

    private MypageActivity mypageActivity;
    private InfoData info;

    private Button prev;
    private Button success;
    private Spinner houseSelect;
    private Spinner discount1Select;
    private Spinner discount2Select;

    private List<House> houses;
    private EcheckDatabaseManager dbHandler;
    private static MyUpdate2Fragment instance;

    public static MyUpdate2Fragment newInstance() {
        instance = new MyUpdate2Fragment();
        return instance;
    }

    private MyUpdate2Fragment() {}

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_myupdate2, container, false);

        Bundle bundle = getArguments();
        info = (InfoData) bundle.getSerializable("infoData");

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        mypageActivity.changeToolbar();
        prev = rootView.findViewById(R.id.update2_prev);
        success = rootView.findViewById(R.id.update2_success);
        houseSelect = rootView.findViewById(R.id.house_update);
        discount1Select = rootView.findViewById(R.id.discount1_update);
        discount2Select = rootView.findViewById(R.id.discount2_update);

        houses = dbHandler.getHouse();
        final String[] houseList = getResources().getStringArray(R.array.house_items);
        final String[] discount1List = getResources().getStringArray(R.array.discount_item1);
        final String[] discount2List = getResources().getStringArray(R.array.discount_item2);

        houseSelect.setSelection(mypageActivity.matchData(houseList, info.getHouseType()));
        discount1Select.setSelection(mypageActivity.matchData(discount1List, houses.get(0).getHouseDiscount1()));
        discount2Select.setSelection(mypageActivity.matchData(discount2List, houses.get(0).getHouseDiscount2()));

        houseSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(houseSelect.getItemAtPosition(position).toString().equals("비주거용")) {
                    discount1Select.setBackground(getResources().getDrawable(R.drawable.bg_spinner_inactive, null));
                    discount2Select.setBackground(getResources().getDrawable(R.drawable.bg_spinner_inactive, null));
                    discount1Select.setEnabled(false);
                    discount2Select.setEnabled(false);
                } else {
                    discount1Select.setBackground(getResources().getDrawable(R.drawable.bg_spinner_task, null));
                    discount2Select.setBackground(getResources().getDrawable(R.drawable.bg_spinner_task, null));
                    discount1Select.setEnabled(true);
                    discount2Select.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        /**
         * 장애인/유공자 선택시, 다른 할인과 중복할 수 없음을 안내함
         */
        discount2Select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(discount2List[position].contains("유공자") || discount2List[position].contains("장애인")) {
                    discount1Select.setSelection(0);
                    discount1Select.setBackground(getResources().getDrawable(R.drawable.bg_spinner_inactive, null));
                    discount1Select.setEnabled(false);
                    Toast.makeText(getContext(), "장애인/유공자 할인은 다른 할인을 중복 선택할 수 없습니다.", Toast.LENGTH_LONG).show();
                } else {
                    discount1Select.setBackground(getResources().getDrawable(R.drawable.bg_spinner_task, null));
                    discount1Select.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypageActivity.onMyPageChange(MyUpdate1Fragment.newInstance(), "prev", null);
            }
        });


        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date = df.format(Calendar.getInstance().getTime());

                String house = houseSelect.getSelectedItem().toString();
                String discountType1 = discount1Select.getSelectedItem().toString();
                String discountType2 = discount2Select.getSelectedItem().toString();

                if(house.equals("비주거용")) {
                    discountType1 = "해당사항없음";
                    discountType2 = "해당사항없음";
                }

                values.put(ColumnContract.ColumnEntry.COLUMN_NICKNAME, info.getNickname());
                values.put(ColumnContract.ColumnEntry.COLUMN_BEFORE_ELECRT, info.getBeforeElect());
                values.put(ColumnContract.ColumnEntry.COLUMN_NOW_PERIOD, info.getNowPeriod());
                values.put(ColumnContract.ColumnEntry.COLUMN_USE, info.getUseElect());
                values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE, house);
                values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_COUNT, "1");
                values.put(ColumnContract.ColumnEntry.COLUMN_USER_CREATED_AT, date);

                int userUpdateResult = dbHandler.updateData("user", values, ColumnContract.ColumnEntry._ID + " = ?", new String[]{Long.toString(info.getUserId())});

                if (userUpdateResult > 0) {
                    ContentValues values2 = new ContentValues();

                    values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN, "Y");
                    values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT1, discountType1);
                    values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT2, discountType2);
                    values2.put(ColumnContract.ColumnEntry.COLUMN_USER_ID, info.getUserId());

                    int houseUpdateResult = dbHandler.updateData("house", values2, "_id=?", new String[]{Long.toString(houses.get(0).getId())});

                    if (houseUpdateResult > 0) {
                        dbHandler.closeDatabase();
                        mypageActivity.onMyPageChange(Mypage1Fragment.newInstance(), "prev", null);
                    }
                } else {
                    dbHandler.closeDatabase();
                    Log.d("db_error", "update error");
                }
            }
        });

        return rootView;
    }
}
