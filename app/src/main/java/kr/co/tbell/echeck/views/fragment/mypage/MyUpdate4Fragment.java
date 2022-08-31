package kr.co.tbell.echeck.views.fragment.mypage;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.constant.ColumnContract;
import kr.co.tbell.echeck.constant.ColumnContract.ColumnEntry;
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.HouseItem;
import kr.co.tbell.echeck.model.dto.InfoData;
import kr.co.tbell.echeck.views.activity.MypageActivity;
import kr.co.tbell.echeck.views.adapter.RecyclerUpdateAdapter;
import kr.co.tbell.echeck.views.dialog.HouseDeleteDialog;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class MyUpdate4Fragment extends Fragment {

    private MypageActivity mypageActivity;
    private InfoData info;
    private User user;

    private Button prev;
    private Button success;
    private Button houseAdd;
    private List<House> houses;
    private List<HouseItem> list = new ArrayList<HouseItem>();
    private RecyclerView updateListView;
    private RecyclerUpdateAdapter listAdapter;

    private EcheckDatabaseManager dbHandler;
    private int count;

    private static MyUpdate4Fragment instance;

    public static MyUpdate4Fragment newInstance() {
        instance = new MyUpdate4Fragment();
        return instance;
    }

    private MyUpdate4Fragment() {}

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_myupdate4, container, false);

        Bundle bundle = getArguments();
        info = (InfoData) bundle.getSerializable("infoData");

        mypageActivity.changeToolbar();
        prev = rootView.findViewById(R.id.update4_prev);
        success = rootView.findViewById(R.id.update4_success);
        houseAdd = rootView.findViewById(R.id.house_add);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        user = dbHandler.getUser();
        houses = dbHandler.getHouse();
        count = houses.size();

        updateListView = rootView.findViewById(R.id.update_list);
        updateListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        for(int i=0; i<count; i++) {
            HouseItem houseItem = new HouseItem();
            houseItem.setTitleText(Integer.toString(i+1) + "번 가구");
            list.add(houseItem);
        }

        listAdapter = new RecyclerUpdateAdapter(getActivity(), list);
        listAdapter.setHouseItemClickListener(new RecyclerUpdateAdapter.UpdateItemClickListener() {
            @Override
            public void onUpdateItemClick(View v, int position) {

                info.setHouseCount(Integer.toString(count));
                info.setCurrentHouseId(houses.get(position).getId());

                mypageActivity.onMyPageChange(MyUpdate5Fragment.newInstance(), "next", info);
            }

            @Override
            public void onDeleteItemClick(View v, int position) {
                final Long currentId = houses.get(position).getId();

                if(list.size() == 1) {
                    Toast.makeText(getContext(), "최소 1개 이상의 가구 정보는 입력해야 합니다.", Toast.LENGTH_LONG).show();
                } else {

                    HouseDeleteDialog dialog = new HouseDeleteDialog(getContext(), new HouseDeleteDialog.DialogListener() {
                        @Override
                        public void onCheckClicked() {

                            int result = dbHandler.deleteData("house", ColumnEntry._ID + "=?", new String[]{Long.toString(currentId)});

                            if (result > 0) {
                                list.remove(position);
                                for (int i = 0; i < list.size(); i++) {
                                    list.get(i).setTitleText(Integer.toString(i + 1) + "번 가구");
                                }

                                listAdapter.notifyItemRemoved(position);
                                listAdapter.notifyDataSetChanged();

                                ContentValues values2 = new ContentValues();
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                String date = df.format(Calendar.getInstance().getTime());

                                values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_COUNT, Integer.toString(list.size()));
                                values2.put(ColumnContract.ColumnEntry.COLUMN_USER_CREATED_AT, date);

                                // user 가구수 update!
                                int userUpdateResult = dbHandler.updateData("user", values2, ColumnContract.ColumnEntry._ID + " = ?", new String[]{Long.toString(user.getId())});

                                if (userUpdateResult > 0) {
                                    Log.d("db_success", "delete success");
                                } else {
                                    Log.d("db_error", "update error");
                                }
                            } else {
                                Toast.makeText(getContext(), "예기치 못한 오류로 삭제에 실패했습니다.\n다시 시도해 주십시오.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    dialog.showDialog();
                    houses = dbHandler.getHouse();
                }
            }
        });
        updateListView.setAdapter(listAdapter);

        houseAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size() == 9) {
                    Toast.makeText(getContext(), "1주택 수 가구 최대 선택 가구수는 9가구 입니다.", Toast.LENGTH_LONG).show();
                } else {
                    HouseItem houseItem = new HouseItem();
                    houseItem.setTitleText(Integer.toString(list.size() + 1) + "번 가구");
                    list.add(houseItem);

                    listAdapter.notifyItemInserted(0);
                    listAdapter.notifyDataSetChanged();

                    ContentValues values = new ContentValues();
                    String discountType1 = "해당사항없음";
                    String discountType2 = "해당사항없음";

                    values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN, "Y");
                    values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT1, discountType1);
                    values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT2, discountType2);
                    values.put(ColumnContract.ColumnEntry.COLUMN_USER_ID, Long.toString(user.getId()));

                    long newHouseId = dbHandler.putData("house", values);

                    if(newHouseId > 0) {

                        ContentValues values2 = new ContentValues();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());

                        values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_COUNT, Integer.toString(list.size()));
                        values2.put(ColumnContract.ColumnEntry.COLUMN_USER_CREATED_AT, date);

                        // user 가구수 update!
                        int userUpdateResult = dbHandler.updateData("user", values2, ColumnContract.ColumnEntry._ID + " = ?", new String[]{Long.toString(user.getId())});

                        if(userUpdateResult > 0) {
                            houses = dbHandler.getHouse();
                            Toast.makeText(getContext(), "새로운 가구가 추가되었습니다.\n할인 정보를 선택해주세요!", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("db_error", "update error");
                        }

                    } else {
                        Log.d("db_error", "insert error");
                    }
                }
                houses = dbHandler.getHouse();
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
                System.out.println("info : " + info.toString());
                values.put(ColumnContract.ColumnEntry.COLUMN_NICKNAME, info.getNickname());
                values.put(ColumnContract.ColumnEntry.COLUMN_BEFORE_ELECRT, info.getBeforeElect());
                values.put(ColumnContract.ColumnEntry.COLUMN_NOW_PERIOD, info.getNowPeriod());
                values.put(ColumnContract.ColumnEntry.COLUMN_USER_CREATED_AT, date);

                int userUpdateResult = dbHandler.updateData("user", values, ColumnContract.ColumnEntry._ID + " = ?", new String[]{Long.toString(info.getUserId())});

                if (userUpdateResult > 0) {
                    mypageActivity.onMyPageChange(Mypage2Fragment.newInstance(), "prev", null);
                } else {
                    dbHandler.closeDatabase();
                    Log.d("db_error", "update error");
                }

            }
        });

        return rootView;
    }
}