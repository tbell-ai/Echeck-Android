package kr.co.tbell.echeck.views.fragment.product;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.constant.ColumnContract.ColumnEntry;
import kr.co.tbell.echeck.model.dto.HomeProduct;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.activity.HomeProductActivity;
import kr.co.tbell.echeck.views.dialog.ProductDeleteDialog;

public class HomeProductViewFragment extends Fragment {

    private HomeProduct product;

    private TextView productName;
    private TextView usagePattern;
    private TextView dayHour;
    private Button prev;
    private Button update;
    private ImageView delete;

    private EcheckDatabaseManager dbHandler;
    long productId;

    private HomeProductActivity homeProductActivity;
    private static HomeProductViewFragment instance;

    public HomeProductViewFragment() {
    }

    public static HomeProductViewFragment newInstance() {
        return instance = new HomeProductViewFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        homeProductActivity = (HomeProductActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_product_view, container, false);

        productName = rootView.findViewById(R.id.view_product_name);
        usagePattern = rootView.findViewById(R.id.view_product_usage);
        dayHour = rootView.findViewById(R.id.view_product_day_hour);
        prev = rootView.findViewById(R.id.home_product_prev);
        update = rootView.findViewById(R.id.home_product_update);
        delete = rootView.findViewById(R.id.delete_icon);

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        Bundle bundle = getArguments();
        product = (HomeProduct)bundle.getSerializable("product");
        productId = product.getId();

        productName.setText(product.getProduct());
        usagePattern.setText(product.getUsagePattern());
        dayHour.setText(product.getDayHour());

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeProductListFragment fragment = new HomeProductListFragment();
                homeProductActivity.onProductPageChange(fragment, "prev", null);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeProduct curProduct = product;
                HomeProductUpdateFragment fragment = new HomeProductUpdateFragment();
                homeProductActivity.onProductPageChange(fragment, "next", curProduct);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDeleteDialog dialog = new ProductDeleteDialog(getContext(), new ProductDeleteDialog.DialogListener() {
                    @Override
                    public void onCheckClicked() {

                        int result = dbHandler.deleteData("product", ColumnEntry._ID + "=?", new String[]{Long.toString(productId)});

                        if(result > 0) {
                            HomeProductListFragment fragment = new HomeProductListFragment();
                            homeProductActivity.onProductPageChange(fragment, "prev", null);
                        } else {
                            Toast.makeText(getContext(), "예기치 못한 오류로 삭제에 실패했습니다.\n다시 시도해 주십시오.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                dialog.showDialog();
                dbHandler.closeDatabase();
            }
        });

        return rootView;
    }
}