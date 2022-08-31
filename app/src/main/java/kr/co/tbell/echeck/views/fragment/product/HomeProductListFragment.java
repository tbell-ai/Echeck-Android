package kr.co.tbell.echeck.views.fragment.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.dto.HomeProduct;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.activity.EnergyResultActivity;
import kr.co.tbell.echeck.views.activity.HomeProductActivity;
import kr.co.tbell.echeck.views.adapter.RecyclerProductAdapter;

public class HomeProductListFragment extends Fragment {

    private Button productAdd;
    private Button goAnalysis;

    private RecyclerView recyclerView;
    private RecyclerProductAdapter adapter;

    private List<HomeProduct> products;
    private List<String> list = new ArrayList<>();
    private EcheckDatabaseManager dbHandler;

    private HomeProductActivity homeProductActivity;
    private static HomeProductListFragment instance;

    public HomeProductListFragment() {
    }

    public static HomeProductListFragment newInstance() {
        return instance = new HomeProductListFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        homeProductActivity = (HomeProductActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_product_list, container, false);

        productAdd = rootView.findViewById(R.id.product_add);
        goAnalysis = rootView.findViewById(R.id.go_analysis);
        recyclerView = rootView.findViewById(R.id.product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        products = dbHandler.getProduct();

        for(HomeProduct product : products) {
            list.add(product.getProduct());
        }

        adapter = new RecyclerProductAdapter(getContext(), list);
        adapter.setProductItemClickListener(new RecyclerProductAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                HomeProduct selProduct = products.get(position);
                HomeProductViewFragment fragment = new HomeProductViewFragment();
                homeProductActivity.onProductPageChange(fragment, "next", selProduct);
            }
        });

        dbHandler.closeDatabase();
        recyclerView.setAdapter(adapter);

        productAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeProductInputFragment fragment = new HomeProductInputFragment();
                homeProductActivity.onProductPageChange(fragment, "next", null);
            }
        });

        goAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EnergyResultActivity.class);

                System.out.println("elect33 : " + homeProductActivity.getElect().toString());
                intent.putExtra("target", homeProductActivity.getElect());
                startActivity(intent);
            }
        });

        return rootView;
    }
}