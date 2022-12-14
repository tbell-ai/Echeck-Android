package kr.co.tbell.echeck.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.dto.ElectDialogList;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.activity.MachineActivity;
import kr.co.tbell.echeck.views.adapter.RecyclerElectAdapter;

public class AnalysisTargetDialog {

    private Context context;
    private TextView noElectView;

    private Button goCamera;

    private List<ElectDialogList> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerElectAdapter adapter;

    private Elect elect;
    private List<Elect> elects;
    private EcheckDatabaseManager dbHandler;

    private AnalysisDialogListener analysisDialogListener;

    public AnalysisTargetDialog(Context context, AnalysisDialogListener analysisDialogListener) {
        this.context = context;
        this.analysisDialogListener = analysisDialogListener;
    }

    public interface AnalysisDialogListener {
        void onClicked(Elect elect);
    }


    public void showDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.analysis_target_dailog);

        noElectView = dialog.findViewById(R.id.measure_text);
        goCamera = dialog.findViewById(R.id.go_camera);
        recyclerView = dialog.findViewById(R.id.measure_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        dbHandler = EcheckDatabaseManager.getInstance(context);
        dbHandler.openDataBase();

        elects = dbHandler.getElect();

        if (elects.size() <= 0) {
            goCamera.setText("????????? ????????????");
        } else {
            goCamera.setText("??????");

            for (Elect elect : elects) {
                ElectDialogList item = new ElectDialogList(R.drawable.measure_list, elect.getCreatedAt(), elect.getElectAmount() + " kWh", elect.getElectCharge() + " ???");
                list.add(item);
            }

            adapter = new RecyclerElectAdapter(context, list);
            adapter.setElectItemClickListener(new RecyclerElectAdapter.ItemClickListener() {

                @Override
                public void onItemClick(View v, int position) {

                    // ????????? ?????? ??????
                    Toast.makeText(context, "????????? ?????? ????????? ???????????? ?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();

                    // ????????? ???????????? ?????? ?????? ?????? ??????
                    System.out.println("????????? ?????? ?????? : " + elects.get(position));
                    elect = elects.get(position);

                    if (elect != null) {
                        // ????????? ????????? ???????????? ??????
                        analysisDialogListener.onClicked(elect);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "????????? ????????? ???????????? ????????????.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            recyclerView.setAdapter(adapter);

        }

        dbHandler.closeDatabase();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.copyFrom(dialog.getWindow().getAttributes());

        windowManager.width = width * 95 / 100;
        windowManager.height = height * 90 / 100;


        goCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(elects.size() <= 0) {
                    Intent intent = new Intent(context, MachineActivity.class);
                    context.startActivity(intent);
                } else {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}
