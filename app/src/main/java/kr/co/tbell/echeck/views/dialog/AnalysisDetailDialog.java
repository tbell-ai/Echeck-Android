package kr.co.tbell.echeck.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.dto.HomeProduct;
import kr.co.tbell.echeck.views.adapter.RecyclerAnalysisAdapter;

public class AnalysisDetailDialog {

    private Context context;

    private TextView noElectView;
    private Button okay;

    private List<HomeProduct> list;
    private RecyclerView recyclerView;
    private RecyclerAnalysisAdapter adapter;

    public AnalysisDetailDialog(Context context, List<HomeProduct> list) {
        this.context = context;
        this.list = list;
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.analysis_detail_dailog);

        noElectView = dialog.findViewById(R.id.analysis_text);
        okay = dialog.findViewById(R.id.okay_button);
        recyclerView = dialog.findViewById(R.id.analysis_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        if (list.size() < 0) {
            noElectView.setHeight(150);
        } else {
            noElectView.setHeight(0);

            adapter = new RecyclerAnalysisAdapter(context, list);
            recyclerView.setAdapter(adapter);
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 90 / 100;

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
