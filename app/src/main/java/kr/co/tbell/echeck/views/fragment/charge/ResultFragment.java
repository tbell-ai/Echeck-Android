package kr.co.tbell.echeck.views.fragment.charge;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.constant.ColumnContract;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.Calculator;
import kr.co.tbell.echeck.util.DateUtil;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.activity.RealtimeChargeActivity;
import kr.co.tbell.echeck.views.activity.SavingGuideActivity;
import kr.co.tbell.echeck.views.helper.ElectCalcHelper;

public class ResultFragment extends Fragment {

    private RealtimeChargeActivity mActivity;

    private TextView usageView;
    private TextView formula1;
    private TextView formula2;
    private TextView formula3;
    private TextView resultFormula;
    private Button goGuide;
    private Elect elect;

    private EcheckDatabaseManager dbHandler;
    private static ResultFragment instance;

    private ResultFragment() {}

    public static ResultFragment newInstance() {
        instance = new ResultFragment();
        return instance;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Activity에 접근하기 위해 사용
        mActivity = (RealtimeChargeActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_result, container, false);

        Bundle bundle = getArguments();
        elect = (Elect) bundle.getSerializable("usage");

        goGuide = rootView.findViewById(R.id.go_guide);
        usageView = rootView.findViewById(R.id.mesure_result);
        formula1 = rootView.findViewById(R.id.elect_formula);
        formula2 = rootView.findViewById(R.id.tax_formula);
        formula3 = rootView.findViewById(R.id.fund_formula);
        resultFormula = rootView.findViewById(R.id.total_formula);
        mActivity.changeToolbarTitle("예상 요금");
        int usage = Integer.parseInt(elect.getElectAmount());

        if(usage <= 0) {
            Toast.makeText(getContext(), "사용량이 잘못되었습니다.", Toast.LENGTH_LONG).show();
        } else {
            usageView.setText(usage + " kWh");
        }

        // 0. Database 초기화
        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();
        // 0-1. 내 정보 받아옴
        User user = dbHandler.getUser();
        // 0-2. 내 할인정보 받아옴
        List<House> houses = dbHandler.getHouse();


        // 1. 계산 클래스 초기화
        Calculator calc = new Calculator();
        calc.setUse(user.getElectUse());
        calc.setElectUsage(usage);
        // 1-1. 현재 계절 구함
        calc.setSeason(DateUtil.getSeason());
        // 1-2. 계산 시작 날짜 구함
        calc.setStart(DateUtil.getTomorrow(DateUtil.convertStringToDate(user.getElectPeriod())));
        calc.setEnd(DateUtil.getCurrentToday());
        calc.setHouses(houses);
        // 1-3. 계산 객체 초기화
        ElectCalcHelper electCalc = ElectCalcHelper.getInstance(calc);
        electCalc.init();
        List<String> formulas = electCalc.getFormula();


        // 2. DB에 결과 삽입~~ 삽입한 DB를 통계 화면에 찍어줘야 함!!
        ContentValues values = new ContentValues();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String createDate = df.format(Calendar.getInstance().getTime());

        // 2-1. 측정 날짜에 현재 월의 측정 내역이 있는지 확인, 있으면 업데이트, 없으면 삽입
        List<Elect> elects = dbHandler.getElect();
        String targetDate = createDate.substring(0, 7);
        String type = "create";
        String existsId = "";

        for(Elect elect : elects) {
            if(elect.getCreatedAt().toString().contains(targetDate)) {
                type = "update";
                existsId = elect.getId().toString();
            }
        }

        if(type.equals("create")) {
            values.put(ColumnContract.ColumnEntry.COLUMN_ELECT, usage);
            values.put(ColumnContract.ColumnEntry.COLUMN_CHARGE, electCalc.getResultCharge());
            values.put(ColumnContract.ColumnEntry.COLUMN_MEASURE, elect.getElectMeasure());
            values.put(ColumnContract.ColumnEntry.COLUMN_ELECT_CREATED_AT, createDate);

            long newUserId = dbHandler.putData("elect", values);

            if (newUserId > 0) {

                // 3. 전력량 요금 셋팅
                formula1.setText(formulas.get(0));

                // 4. 부가가치세 셋팅
                formula2.setText(formulas.get(1));

                // 5. 전력산업기반기금 셋팅
                formula3.setText(formulas.get(2));

                // 6. 전체 요금
                resultFormula.setText(formulas.get(3));

                Toast.makeText(getContext(), "전력 사용량 측정과 예상요금 계산이 완료되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "예상요금 계산에 실패하였습니다. 처음부터 재시도 해주세요", Toast.LENGTH_LONG).show();
            }
        } else {
            ContentValues updateValue = new ContentValues();
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String updateDate = df2.format(Calendar.getInstance().getTime());

            updateValue.put(ColumnContract.ColumnEntry.COLUMN_ELECT, usage);
            updateValue.put(ColumnContract.ColumnEntry.COLUMN_CHARGE, electCalc.getResultCharge());
            updateValue.put(ColumnContract.ColumnEntry.COLUMN_MEASURE, elect.getElectMeasure());
            updateValue.put(ColumnContract.ColumnEntry.COLUMN_ELECT_CREATED_AT, updateDate);

            // elect update
            int userUpdateResult = dbHandler.updateData("elect", updateValue, ColumnContract.ColumnEntry._ID + " = ?", new String[]{existsId});

            if (userUpdateResult > 0) {

                // 3. 전력량 요금 셋팅
                formula1.setText(formulas.get(0));

                // 4. 부가가치세 셋팅
                formula2.setText(formulas.get(1));

                // 5. 전력산업기반기금 셋팅
                formula3.setText(formulas.get(2));

                // 6. 전체 요금
                resultFormula.setText(formulas.get(3));

                Toast.makeText(getContext(), "전력 사용량 측정과 예상요금 계산이 완료되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "예상요금 계산에 실패하였습니다. 처음부터 재시도 해주세요", Toast.LENGTH_LONG).show();
            }
        }


        goGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SavingGuideActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
