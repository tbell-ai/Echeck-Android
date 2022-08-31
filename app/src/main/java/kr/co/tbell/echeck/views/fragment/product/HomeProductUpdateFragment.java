package kr.co.tbell.echeck.views.fragment.product;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.constant.ColumnContract.ColumnEntry;
import kr.co.tbell.echeck.model.dto.HomeProduct;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.activity.HomeProductActivity;

public class HomeProductUpdateFragment extends Fragment {

    private Spinner product;
    private Spinner pattern;
    private Spinner time;
    private Button prev;
    private Button create;

    private HomeProduct curProduct;
    private EcheckDatabaseManager dbHandler;

    private HomeProductActivity homeProductActivity;
    private static HomeProductUpdateFragment instance;


    public HomeProductUpdateFragment() { }

    public static HomeProductUpdateFragment newInstance() {
        return instance = new HomeProductUpdateFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        homeProductActivity = (HomeProductActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_product_update, container, false);

        product = rootView.findViewById(R.id.product_update);
        pattern = rootView.findViewById(R.id.product_usage_update);
        time = rootView.findViewById(R.id.usage_hour_update);
        prev = rootView.findViewById(R.id.home_product_prev);
        create = rootView.findViewById(R.id.home_product_update);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        Bundle bundle = getArguments();
        curProduct = (HomeProduct)bundle.getSerializable("product");

        final String[] productList = getResources().getStringArray(R.array.home_product_list);
        final String[] patternList = getResources().getStringArray(R.array.cycle_list);
        final String[] timeList = getResources().getStringArray(R.array.hour_list);

        product.setSelection(matchData(productList, curProduct.getProduct()));
        pattern.setSelection(matchData(patternList, curProduct.getUsagePattern()));
        time.setSelection(matchData(timeList, curProduct.getDayHour()));

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

                ContentValues values = new ContentValues();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date = df.format(Calendar.getInstance().getTime());

                values.put(ColumnEntry.COLUMN_NAME, product.getSelectedItem().toString());
                values.put(ColumnEntry.COLUMN_PATTERN, pattern.getSelectedItem().toString());
                values.put(ColumnEntry.COLUMN_DAY_HOUR, time.getSelectedItem().toString());
                values.put(ColumnEntry.COLUMN_PRODUCT_CREATED_AT, date);

                int userUpdateResult = dbHandler.updateData("product", values, ColumnEntry._ID + " = ?", new String[]{Long.toString(curProduct.getId())});

                if(userUpdateResult > 0) {
                    HomeProduct updateProduct = dbHandler.getOneProduct(curProduct.getId());
                    HomeProductViewFragment fragment = new HomeProductViewFragment();
                    homeProductActivity.onProductPageChange(fragment, "next", updateProduct);
                } else {

                }
            }
        });

        return rootView;
    }

    public int matchData(String[] searchList, String matchData) {
        int result = 0;

        for(int i=0; i<searchList.length; i++) {
            if(searchList[i].equals(matchData)) {
                result = i;
            }
        }
        return result;
    }
}