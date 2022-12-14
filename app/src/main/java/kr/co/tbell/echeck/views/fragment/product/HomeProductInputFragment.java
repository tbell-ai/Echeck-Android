package kr.co.tbell.echeck.views.fragment.product;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.activity.HomeProductActivity;
import kr.co.tbell.echeck.constant.ColumnContract.ColumnEntry;

public class HomeProductInputFragment extends Fragment {

    private Spinner product;
    private Spinner pattern;
    private Spinner time;
    private Button prev;
    private Button create;

    private EcheckDatabaseManager dbHandler;

    private HomeProductActivity homeProductActivity;
    private static HomeProductInputFragment instance;

    public HomeProductInputFragment() {  }

    public static HomeProductInputFragment newInstance() {
        return instance = new HomeProductInputFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        homeProductActivity = (HomeProductActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_product_input, container, false);

        product = rootView.findViewById(R.id.product_select);
        pattern = rootView.findViewById(R.id.product_usage_select);
        time = rootView.findViewById(R.id.usage_hour_select);
        prev = rootView.findViewById(R.id.home_product_prev);
        create = rootView.findViewById(R.id.home_product_create);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeProductListFragment fragment = new HomeProductListFragment();
                homeProductActivity.onProductPageChange(fragment, "prev", null);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(product.getSelectedItem().toString().equals("?????? ??????")) {
                    Toast.makeText(getContext(), "???????????? ????????? ?????? ???????????????. ??????????????? ??????????????????!", Toast.LENGTH_LONG).show();
                } else if(pattern.getSelectedItem().toString().equals("???????????? ??????")) {
                    Toast.makeText(getContext(), "???????????? ????????? ?????? ???????????????. ??????????????? ??????????????????!", Toast.LENGTH_LONG).show();
                } else if(time.getSelectedItem().toString().equals("????????????")) {
                    Toast.makeText(getContext(), "??? ?????? ???????????? ????????? ?????? ???????????????. ??? ?????? ?????????????????? ??????????????????!", Toast.LENGTH_LONG).show();
                } else {
                    ContentValues values = new ContentValues();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());

                    values.put(ColumnEntry.COLUMN_NAME, product.getSelectedItem().toString());
                    values.put(ColumnEntry.COLUMN_PATTERN, pattern.getSelectedItem().toString());
                    values.put(ColumnEntry.COLUMN_DAY_HOUR, time.getSelectedItem().toString());
                    values.put(ColumnEntry.COLUMN_PRODUCT_CREATED_AT, date);

                    long newProductId = dbHandler.putData("product", values);

                    if (newProductId > 0) {
                        Toast.makeText(getContext(), "????????? ???????????? ??????????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                        HomeProductListFragment fragment = new HomeProductListFragment();
                        homeProductActivity.onProductPageChange(fragment, "next", null);
                    } else {
                        Log.d("db_error", "insert error");
                    }
                }
            }
        });

        return rootView;
    }
}