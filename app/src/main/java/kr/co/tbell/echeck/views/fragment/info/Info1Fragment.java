package kr.co.tbell.echeck.views.fragment.info;

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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import kr.co.tbell.echeck.R;

import kr.co.tbell.echeck.constant.ColumnContract;
import kr.co.tbell.echeck.model.dto.InfoData;
import kr.co.tbell.echeck.views.activity.InfoActivity;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.dialog.InfoGuideDialog;


public class Info1Fragment extends Fragment {

    private InfoActivity infoActivity;
    private Button next;
    private ImageButton guide;
    private EditText nickname;
    private EditText beforeElect;
    private EditText nowPeriod;
    private Spinner useSpinner;

    private String getNickname;
    private String getBeforeElect;
    private String getNowPeriod;

    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateListener;

    private EcheckDatabaseManager dbHandler;

    private static Info1Fragment instance;

    private Info1Fragment() {}

    public static Info1Fragment newInstance() {
        if(instance == null) {
            instance = new Info1Fragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info1, container, false);

        nickname = rootView.findViewById(R.id.nickname_input);
        beforeElect = rootView.findViewById(R.id.before_elect_input);
        nowPeriod = rootView.findViewById(R.id.elect_period_input);
        useSpinner = rootView.findViewById(R.id.use_input);
        next = rootView.findViewById(R.id.info1_next);
        guide = rootView.findViewById(R.id.info_guide);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        // 전력지침 입력 가이드 팝업
        InfoGuideDialog dialog = new InfoGuideDialog(getContext());
        dialog.showDialog();

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

                nowPeriod.setText(sdf.format(mCalendar.getTime()));
            }
        };

        /**
         * 닉네임 EditText Feild에 입력된 텍스트의 변경을 체크하는 리스너, 입력 후 값을 저장
         */
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
         * 전월지침 EditText Feild에 입력된 텍스트의 변경을 체크하는 리스너, 입력 후 값을 저장
         */
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

        /**
         * 전월 사용량 종료기간 EditText Feild에 입력된 텍스트의 변경을 체크하는 리스너, 입력 후 값을 저장
         */
        nowPeriod.addTextChangedListener(new TextWatcher() {
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
         * 전월 사용량 종료기간 EditText Feild를 클릭하면 달력 다이얼로그가 노출되며, 날짜 선택이 가능함
         */
        nowPeriod.setOnClickListener(new View.OnClickListener() {
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

        /**
         * 다음 버튼 클릭 이벤트, 다음 화면으로 넘어감
         * 1주택 수 가구일 경우, 우선 로컬 DB에 저장하고 다음 화면으로 넘어감
         * 주택용일 경우, 값을 넘기며 다음 화면으로 넘어감
         */
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String used = useSpinner.getSelectedItem().toString();

                if(!(checkNickname(getNickname) && checkBeforeElect(getBeforeElect))) {
                    Toast toast = Toast.makeText(getContext(), "입력값을 기준에 맞게 입력해주세요.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {

                    if(used.equals("1주택 수 가구")) {

                        // 1주택 수 가구는 DB에 USER 우선 삽입
                        ContentValues values = new ContentValues();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());

                        values.put(ColumnContract.ColumnEntry.COLUMN_NICKNAME, getNickname);
                        values.put(ColumnContract.ColumnEntry.COLUMN_BEFORE_ELECRT, getBeforeElect);
                        values.put(ColumnContract.ColumnEntry.COLUMN_NOW_PERIOD, getNowPeriod);
                        values.put(ColumnContract.ColumnEntry.COLUMN_USE, used);
                        values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE, "해당없음");
                        values.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_COUNT, "1");
                        values.put(ColumnContract.ColumnEntry.COLUMN_USER_CREATED_AT, date);

                        long newUserId = dbHandler.putData("user", values);

                        if (newUserId > 0) {
                            ContentValues values2 = new ContentValues();
                            String discountType1 = "해당사항없음";
                            String discountType2 = "해당사항없음";

                            values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN, "Y");
                            values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT1, discountType1);
                            values2.put(ColumnContract.ColumnEntry.COLUMN_HOUSE_DISCOUNT2, discountType2);
                            values2.put(ColumnContract.ColumnEntry.COLUMN_USER_ID, newUserId);

                            long newHouseId = dbHandler.putData("house", values2);
                            dbHandler.closeDatabase();

                            if (newHouseId > 0) {
                                infoActivity.onInfoPageChange(Info4Fragment.newInstance(), "next", null);
                            }
                        } else {
                            Log.d("db_error", "insert error");
                        }
                    } else {
                        InfoData info = new InfoData();
                        info.setNickname(getNickname);
                        info.setBeforeElect(getBeforeElect);
                        info.setNowPeriod(getNowPeriod);
                        info.setUseElect(used);

                        infoActivity.onInfoPageChange(Info2Fragment.newInstance(), "next", info);
                    }
                }

            }
        });

        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.showDialog();
            }
        });

        return rootView;
    }


    /**
     * 입력 여부를 체크하는 함수, 모든 필드에 값이 입력되면 버튼이 활성화 됨
     */
    private void checkValid() {
        if(getNickname != null && getBeforeElect != null && getNowPeriod != null) {
            if(getNickname.equals("") || getBeforeElect.equals("") || getNowPeriod.equals("")) {
                next.setEnabled(false);
                next.setBackground(getResources().getDrawable(R.drawable.inactive_button_radius, null));
            } else {
                next.setEnabled(true);
                next.setBackground(getResources().getDrawable(R.drawable.next_button_radius, null));
            }
        } else {
            next.setEnabled(false);
            next.setBackground(getResources().getDrawable(R.drawable.inactive_button_radius, null));
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
