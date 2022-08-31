package kr.co.tbell.echeck.views.fragment.usage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.ElectList;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.adapter.RecyclerResultAdapter;
import kr.co.tbell.echeck.views.dialog.ListDialog;

public class Usage2Fragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ElectList> list = new ArrayList<>();
    private RecyclerResultAdapter adapter;

    private Context mContext;
    private List<Elect> elects;
    private EcheckDatabaseManager dbHandler;

    private static Usage2Fragment instance;

    public Usage2Fragment() {}

    public static Usage2Fragment newInstance() {
        instance = new Usage2Fragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_usage2, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        elects = dbHandler.getElect();

        for(int i=0; i<elects.size(); i++) {
            String year = elects.get(i).getCreatedAt().substring(0, 4);
            String month = elects.get(i).getCreatedAt().substring(5, 7);
            if(month.substring(0, 1).equals("0")) {
                month = month.substring(1, 2);
            }
            ElectList listItem = new ElectList(R.drawable.measure_list,  year + "년 " + month + "월", elects.get(i).getElectAmount() + " kWh");
            list.add(listItem);
        }

        dbHandler.closeDatabase();
        adapter = new RecyclerResultAdapter(getActivity(), list);
        adapter.setElectItemClickListener(new RecyclerResultAdapter.ElectItemClickListener() {
            @Override
            public void onElectItemClick(View v, int position) {
                Elect elect = elects.get(position);

                ListDialog dialog = new ListDialog(getContext(), elect);
                dialog.showDialog();
            }
        });
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}