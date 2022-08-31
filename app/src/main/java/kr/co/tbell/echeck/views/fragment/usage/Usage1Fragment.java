package kr.co.tbell.echeck.views.fragment.usage;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.constant.ColumnContract;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class Usage1Fragment extends Fragment {

    private TextView totalCharge;
    private TextView avgCharge;

    private List<Elect> elects;
    private EcheckDatabaseManager dbHandler;

    private int total;
    private int average;

    private static Usage1Fragment instance;

    public Usage1Fragment() {}

    public static Usage1Fragment newInstance() {
        instance = new Usage1Fragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_usage1, container, false);

        totalCharge = rootView.findViewById(R.id.total_charge);
        avgCharge = rootView.findViewById(R.id.avg_charge);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        elects = dbHandler.getElect();


        if (elects.size() > 0) {
            // 1. 전체 측정 요금
            for(int i=0; i<elects.size(); i++) {
                total = total + Integer.parseInt(elects.get(i).getElectCharge());
            }

            // 2. 월 평균 요금
            average = total / elects.size();
        } else {
            total = 0;
            average = 0;
        }
        avgCharge.setText(Integer.toString(average) + " 원");
        totalCharge.setText(Integer.toString(total) + " 원");


        // 3. 데이터베이스 클로즈
        dbHandler.closeDatabase();

        return rootView;
    }
}