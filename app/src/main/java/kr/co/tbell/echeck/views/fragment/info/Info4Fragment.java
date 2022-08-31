package kr.co.tbell.echeck.views.fragment.info;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.HouseItem;
import kr.co.tbell.echeck.model.dto.InfoData;
import kr.co.tbell.echeck.views.activity.InfoActivity;
import kr.co.tbell.echeck.views.activity.MainActivity;
import kr.co.tbell.echeck.views.adapter.RecyclerUpdateAdapter;
import kr.co.tbell.echeck.views.dialog.HouseDeleteDialog;
import kr.co.tbell.echeck.views.dialog.InfoPrevDialog;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class Info4Fragment extends Fragment {

    private InfoActivity infoActivity;
    private User user;

    private Button prev;
    private Button success;
    private Button houseAdd;
    private List<HouseItem> list = new ArrayList<HouseItem>();
    private List<House> houses;
    private RecyclerView houseListView;
    private RecyclerUpdateAdapter listAdapter;

    private EcheckDatabaseManager dbHandler;
    private int count;

    private static Info4Fragment instance;

    public Info4Fragment() {}

    public static Info4Fragment newInstance() {
        instance = new Info4Fragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info4, container, false);

        prev = rootView.findViewById(R.id.info4_prev);
        success = rootView.findViewById(R.id.info4_success);
        houseAdd = rootView.findViewById(R.id.house_add);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        user = dbHandler.getUser();
        houses = dbHandler.getHouse();
        count = houses.size();

        houseListView = rootView.findViewById(R.id.house_list);
        houseListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        for(int i=0; i<count; i++) {
            HouseItem houseItem = new HouseItem();
            houseItem.setTitleText(Integer.toString(i+1) + "번 가구");
            list.add(houseItem);
        }

        listAdapter = new RecyclerUpdateAdapter(getActivity(), list);
        /**
         * 리사이클러뷰의 리스트 아이템 클릭을 체크하는 리스너
         */
        listAdapter.setHouseItemClickListener(new RecyclerUpdateAdapter.UpdateItemClickListener() {

            /**
             * 업데이트 아이콘 클릭시 호출되는 함수
             *
             * @param v                 클릭한 위치의 뷰
             * @param position          클릭한 아이템의 포지션
             */
            @Override
            public void onUpdateItemClick(View v, int position) {

                InfoData param = new InfoData();
                param.setHouseCount(Integer.toString(count));
                param.setCurrentHouseId(houses.get(position).getId());

                infoActivity.onInfoPageChange(Info5Fragment.newInstance(), "next", param);

            }

            /**
             * 딜리트 아이콘 클릭시 호출되는 함수
             *
             * @param v                 클릭한 위치의 뷰
             * @param position          클릭한 아이템의 포지션
             */
            @Override
            public void onDeleteItemClick(View v, int position) {
                final Long currentId = houses.get(position).getId();

                // 0. 가구수가 1보다 작으면 삭제할 수 없음, 최소 1개 이상의 가구 정보 필요
                if(list.size() == 1) {
                    Toast.makeText(getContext(), "최소 1개 이상의 가구 정보는 입력해야 합니다.", Toast.LENGTH_LONG).show();
                } else {

                    /**
                     * 삭제를 선택한 경우, 삭제 여부를 묻는 다이얼로그를 보여주고 확인을 선택할 경우 진행
                     */
                    HouseDeleteDialog dialog = new HouseDeleteDialog(getContext(), new HouseDeleteDialog.DialogListener() {
                        @Override
                        public void onCheckClicked() {

                            // 1. 해당 가구 삭제
                            int result = dbHandler.deleteData("house", ColumnContract.ColumnEntry._ID + "=?", new String[]{Long.toString(currentId)});

                            if (result > 0) {
                                // 2. 가구가 삭제되었으므로 리스트 다시 표시
                                list.remove(position);
                                for (int i = 0; i < list.size(); i++) {
                                    list.get(i).setTitleText(Integer.toString(i + 1) + "번 가구");
                                }

                                listAdapter.notifyItemRemoved(position);
                                listAdapter.notifyDataSetChanged();

                                // 3. 가구가 삭제 되었으므로, 유저의 가구수 정보를 업데이트 해줌
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
        houseListView.setAdapter(listAdapter);

        /**
         * 추가를 선택한 경우, 가구 리스트 마지막에 가구가 추가된다.
         */
        houseAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 0. 가구는 9개를 초과하여 추가할 수 없음
                if(list.size() == 9) {
                    Toast.makeText(getContext(), "1주택 수 가구 최대 선택 가구수는 9가구 입니다.", Toast.LENGTH_LONG).show();
                } else {
                    // 1. 리스트 마지막에 가구 추가
                    HouseItem houseItem = new HouseItem();
                    houseItem.setTitleText(Integer.toString(list.size() + 1) + "번 가구");
                    list.add(houseItem);

                    listAdapter.notifyItemInserted(0);
                    listAdapter.notifyDataSetChanged();

                    // 2. 로컬 DB에 새로운 가구 추가
                    ContentValues values = new ContentValues();
                    String discountType1 = "해당사항없음";
                    String discountType2 = "해당사항없음";

                    values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN, "Y");
                    values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT1, discountType1);
                    values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT2, discountType2);
                    values.put(ColumnContract.ColumnEntry.COLUMN_USER_ID, Long.toString(user.getId()));

                    long newHouseId = dbHandler.putData("house", values);

                    if(newHouseId > 0) {

                        // 3. 가구수 늘어났으니, User 정보의 가구수 업데이트
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

        /**
         * 가구 할인 정보가 이미 입력된 상태에서 이전으로 돌아갈 경우, 모든 정보를 삭제하고 이전 화면으로 돌아간다고 안내
         */
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoPrevDialog dialog = new InfoPrevDialog(getContext(), new InfoPrevDialog.DialogListener() {
                    @Override
                    public void onCheckClicked() {

                        // User가 저장이 완료된 상태이고 House도 데이터가 있을 수 있기 때문에, 모든 정보를 삭제하고 다시 처음으로 진입해야 함!
                        dbHandler.initInfo();
                        dbHandler.closeDatabase();

                        infoActivity.onInfoPageChange(Info1Fragment.newInstance(), "prev", null);
                    }
                });
                dialog.showDialog();
            }
        });

        /**
         * 모든 정보가 저장된 상태이므로 메인으로 바로 이동
         */
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.closeDatabase();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
