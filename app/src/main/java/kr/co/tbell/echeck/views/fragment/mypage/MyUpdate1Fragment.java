package kr.co.tbell.echeck.views.fragment.mypage;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.constant.ColumnContract;
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.InfoData;
import kr.co.tbell.echeck.views.activity.MypageActivity;
import kr.co.tbell.echeck.views.dialog.UseUpdateDialog;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class MyUpdate1Fragment extends Fragment {

    private MypageActivity mypageActivity;
    private EditText nickname;
    private EditText beforeElect;
    private EditText beforePeriod;
    private Spinner useElect;
    private Button updateNext;

    private String originUse;
    private String getNickname;
    private String getBeforeElect;
    private String getNowPeriod;

    private User user;
    private List<House> houses;
    private Calendar mCalendar = Calendar.getInstance();
    private EcheckDatabaseManager dbHandler;
    private static MyUpdate1Fragment instance;

    public static MyUpdate1Fragment newInstance() {
        instance = new MyUpdate1Fragment();
        return instance;
    }

    private MyUpdate1Fragment() {}

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_myupdate1, container, false);

        mypageActivity.changeToolbar();
        nickname = rootView.findViewById(R.id.nickname_update);
        beforeElect = rootView.findViewById(R.id.before_elect_update);
        beforePeriod = rootView.findViewById(R.id.elect_period_update);
        useElect = rootView.findViewById(R.id.use_update);
        updateNext = rootView.findViewById(R.id.update1_next);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        user = dbHandler.getUser();
        houses = dbHandler.getHouse();

        nickname.setText(user.getNickname());
        beforeElect.setText(user.getElectBefore());
        beforePeriod.setText(user.getElectPeriod());
        originUse = user.getElectUse();
        getNickname = user.getNickname();
        getBeforeElect = user.getElectBefore();
        getNowPeriod = user.getElectPeriod();

        final String[] useList = getResources().getStringArray(R.array.use_items);
        useElect.setSelection(mypageActivity.matchData(useList, originUse));

        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                getNickname = s.toString();
                checkValid();
            }
        });

        beforeElect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                getBeforeElect = s.toString();
                checkValid();
            }
        });

        beforePeriod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                getNowPeriod = s.toString();
                checkValid();
            }
        });

        /**
         * 달력 다이얼로그를 초기화
         * 달력에서 필요한 날짜와 포맷, 그리고 선택시 취하는 행동을 정의
         */
        DatePickerDialog.OnDateSetListener mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH,  month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);

                beforePeriod.setText(sdf.format(mCalendar.getTime()));
            }
        };

        /**
         * 닉네임 EditText Feild가 포커스 되었을 때 변경을 체크하는 리스너, Validation 체크
         */
        nickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String data = nickname.getText().toString();
                if(hasFocus == false) {
                    checkNickname(data);
                }
            }
        });

        /**
         * 전월지침 EditText Feild가 포커스 되었을 때 변경을 체크하는 리스너, Validation 체크
         */
        beforeElect.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String data = beforeElect.getText().toString();
                if(hasFocus == false) {
                    checkBeforeElect(data);
                }
            }
        });

        /**
         * 전월 사용량 종료기간 EditText Feild를 클릭하면 달력 다이얼로그가 노출되며, 날짜 선택이 가능함
         */
        beforePeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(getContext(), "이전 달의 사용 종료 기간을 선택해주세요!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                Calendar cal1 = Calendar.getInstance();
                cal1.add(Calendar.DATE, -1);
                Date beforeDay = cal1.getTime();

                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.MONTH, -1);
                Date beforeMonth = cal2.getTime();

                DatePickerDialog dialog = new DatePickerDialog(getContext(), mDatePicker, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(beforeDay.getTime());
                dialog.getDatePicker().setMinDate(beforeMonth.getTime());
                dialog.show();
            }
        });

        updateNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String curUsed = useElect.getSelectedItem().toString();

                InfoData info = new InfoData();
                info.setNickname(getNickname);
                info.setBeforeElect(getBeforeElect);
                info.setNowPeriod(getNowPeriod);
                info.setUseElect(curUsed);
                info.setHouseType(user.getElectHouse());
                info.setUserId(user.getId());

                if(!(checkNickname(getNickname) && checkBeforeElect(getBeforeElect))) {
                    Toast toast = Toast.makeText(getContext(), "입력값을 기준에 맞게 입력하지 않으면 수정할 수 없습니다.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {

                    // 원래 용도에서 용도가 변경되지 않음
                    if (curUsed.equals(originUse)) {
                        info.setHouseCount(Integer.toString(houses.size()));
                        if (curUsed.equals("주택용(고압)") || curUsed.equals("주택용(저압)")) {
                            mypageActivity.onMyPageChange(MyUpdate2Fragment.newInstance(), "next", info);
                        } else if (curUsed.equals("1주택 수 가구")) {
                            mypageActivity.onMyPageChange(MyUpdate4Fragment.newInstance(), "next", info);
                        }
                    } else {

                        UseUpdateDialog dialog = new UseUpdateDialog(getContext(), new UseUpdateDialog.DialogListener() {
                            @Override
                            public void onCheckClicked() {
                                // 1. 기존 용도에 따른 가구정보 삭제(House Table 다 삭제)
                                dbHandler.deleteData("house", null, null);

                                ContentValues values = new ContentValues();
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                String date = df.format(Calendar.getInstance().getTime());

                                if (curUsed.equals("주택용(고압)") || curUsed.equals("주택용(저압)")) {
                                    values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE, "주거용");
                                } else if (curUsed.equals("1주택 수 가구")) {
                                    values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE, "null");
                                }

                                values.put(ColumnContract.ColumnEntry.COLUMN_USE, curUsed);
                                values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_COUNT, "1");
                                values.put(ColumnContract.ColumnEntry.COLUMN_USER_CREATED_AT, date);

                                // 2. 바뀐 용도 정보 업데이트
                                int userUpdateResult = dbHandler.updateData("user", values, ColumnContract.ColumnEntry._ID + " = ?", new String[]{Long.toString(user.getId())});

                                if (userUpdateResult > 0) {
                                    // 3. 다음 수정 화면에서 가구정보가 하나도 없으면 안되므로 기본적으로 1개 입력
                                    ContentValues values2 = new ContentValues();
                                    String discountType1 = "해당사항없음";
                                    String discountType2 = "해당사항없음";

                                    values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN, "Y");
                                    values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT1, discountType1);
                                    values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT2, discountType2);
                                    values2.put(ColumnContract.ColumnEntry.COLUMN_USER_ID, user.getId());

                                    long newHouseId = dbHandler.putData("house", values2);
                                    dbHandler.closeDatabase();

                                    // 4. 성공시, 다음 수정화면으로 분기처리
                                    if (newHouseId > 0) {
                                        info.setHouseCount("1");
                                        if (curUsed.equals("주택용(고압)") || curUsed.equals("주택용(저압)")) {
                                            mypageActivity.onMyPageChange(MyUpdate2Fragment.newInstance(), "next", info);
                                        } else if (curUsed.equals("1주택 수 가구")) {
                                            mypageActivity.onMyPageChange(MyUpdate4Fragment.newInstance(), "next", info);
                                        }
                                    }
                                } else {
                                    dbHandler.closeDatabase();
                                    Log.d("db_error", "update error");
                                }
                            }
                        });

                        dialog.showDialog();
                    }
                }
            }
        });

        return rootView;
    }

    public void checkValid() {
        if(getNickname != null && getBeforeElect != null && getNowPeriod != null) {
            if(getNickname.equals("") || getBeforeElect.equals("") || getNowPeriod.equals("")) {
                updateNext.setEnabled(false);
                updateNext.setBackground(getResources().getDrawable(R.drawable.inactive_button_radius, null));
            } else {
                updateNext.setEnabled(true);
                updateNext.setBackground(getResources().getDrawable(R.drawable.next_button_radius, null));
            }
        } else {
            updateNext.setEnabled(false);
            updateNext.setBackground(getResources().getDrawable(R.drawable.inactive_button_radius, null));
        }
    }

    private boolean checkNickname(String data) {
        Pattern ps = Pattern.compile("^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$");
        if(data.length() < 2 || data.length() > 6 ) {
            nickname.setText("");

            Toast toast = Toast.makeText(getContext(), "2~6자 한글, 영문만 입력 가능합니다.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return false;
        } else if(!ps.matcher(data).matches()) {
            nickname.setText("");

            Toast toast = Toast.makeText(getContext(), "2~6자 한글, 영문만 입력 가능합니다.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return false;
        }

        return true;
    }

    private boolean checkBeforeElect(String data) {
        Pattern ps = Pattern.compile("^[0-9]+$");
        if(data.length() < 1 || data.length() > 6 ) {
            beforeElect.setText("");

            Toast toast = Toast.makeText(getContext(), "1~5자리 0~9까지 숫자만 입력 가능합니다.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            return false;
        } else if(!ps.matcher(data).matches()) {
            beforeElect.setText("");

            Toast toast = Toast.makeText(getContext(), "1~5자리 0~9까지 숫자만 입력 가능합니다.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            return false;
        }

        return true;
    }
}
