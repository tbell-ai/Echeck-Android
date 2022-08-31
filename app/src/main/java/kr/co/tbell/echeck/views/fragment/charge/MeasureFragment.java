package kr.co.tbell.echeck.views.fragment.charge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.EcheckApiResult;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;
import kr.co.tbell.echeck.views.activity.BeforeElectActivity;
import kr.co.tbell.echeck.views.activity.CameraActivity;
import kr.co.tbell.echeck.views.activity.RealtimeChargeActivity;
import kr.co.tbell.echeck.views.dialog.MeasureGuideDialog;
import kr.co.tbell.echeck.views.dialog.MeasureResultDialog;

public class MeasureFragment extends Fragment {

    private RealtimeChargeActivity mActivity;

    private TextView resultView;
    private EditText electInput;
    private Button next;
    private Button goCamera;

    private User user;
    private EcheckDatabaseManager dbHandler;

    private EcheckApiResult result;
    private int electUsage = 0;
    private String ocrResult;
    private String machine;
    private Elect elect;
    private int inputNumber = 0;

    private static MeasureFragment instance;

    public MeasureFragment() {}

    public static MeasureFragment newInstance() {
        instance = new MeasureFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_measure, container, false);

        ocrResult = "";
        Bundle bundle = getArguments();
        result = (EcheckApiResult) bundle.getSerializable("ocrResponse");
        machine = bundle.getString("machine");

        next = rootView.findViewById(R.id.go_calc);
        resultView = rootView.findViewById(R.id.mesure_result);
        electInput = rootView.findViewById(R.id.elect_input);
        goCamera = rootView.findViewById(R.id.go_camera);
        mActivity.changeToolbarTitle("측정 결과");

        dbHandler = EcheckDatabaseManager.getInstance(getContext());
        dbHandler.openDataBase();

        user = dbHandler.getUser();

        if(result == null || result.getResponseCode().equals("9999")) {
            // 1. 전력량계 OCR 인식이 실패한 경우 - resultView에 실패 메시지 출력(붉은 계열 칼라 사용)
            resultView.setText("전력량계 인식 실패");
            resultView.setTextColor(Color.parseColor("#D32F2F"));
            resultView.setTextSize(18);

            // 2. 다음 버튼 비활성화
            next.setBackgroundResource(R.drawable.inactive_button_radius);
            electInput.setEnabled(true);
            next.setEnabled(false);

            // 3. 수기입력 유도(수기입력 창 포커싱)
            MeasureGuideDialog dialog = new MeasureGuideDialog(getContext(), new MeasureGuideDialog.DialogListener() {
                @Override
                public void onCloseClicked() {
                    electInput.requestFocus();
                }
            });
            dialog.showDialog();

        } else if(result.getResponseCode().equals("0000")) {
            if (result.getPredictNum().size() == 4) {
                // 기계식
                ocrResult = result.getPredictNum().get(0).getPredict().charAt(0) + " " + result.getPredictNum().get(1).getPredict().charAt(0) + " " + result.getPredictNum().get(2).getPredict().charAt(0) + " " + result.getPredictNum().get(3).getPredict().charAt(0);
            } else if (result.getPredictNum().size() == 5) {
                // 전자식
                ocrResult = result.getPredictNum().get(0).getPredict().charAt(0) + " " + result.getPredictNum().get(1).getPredict().charAt(0) + " " + result.getPredictNum().get(2).getPredict().charAt(0) + " " + result.getPredictNum().get(3).getPredict().charAt(0) + " " + result.getPredictNum().get(4).getPredict().charAt(0);
            } else {
                resultView.setText("전력량계 인식 실패");
            }
            resultView.setText(ocrResult);
            resultView.setTextColor(Color.parseColor("#71707e"));
            next.setBackgroundResource(R.drawable.next_button_radius);
            next.setEnabled(true);
        }

        electInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputText = electInput.getText().toString();
                // 자릿수 체크
                if(inputText.length() > 6 || inputText.length() < 1) {

                    Toast toast = Toast.makeText(getContext(), "1~5자리 0~9까지 숫자만 입력 가능합니다.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }

                // 전력량계 인식 실패하여 반드시 수기입력 값이 필요한 경우를 대비하여 무조건 값을 입력하도록 EditText Feild의 변경 확인
                if(resultView.getText().equals("전력량계 인식 실패")) {
                    if(electInput.getText().toString().replace(" ", "").equals("")) {

                        next.setBackgroundResource(R.drawable.inactive_button_radius);
                        electInput.setEnabled(true);
                        next.setEnabled(false);

                        Toast toast = Toast.makeText(getContext(), "전력량계 인식에 실패한 경우 반드시 수기입력 값이 있어야 합니다.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                    } else {
                        next.setBackgroundResource(R.drawable.next_button_radius);
                        next.setEnabled(true);

                        // 수기입력 창에 값이 들어온 경우, 무조건 수기입력 창을 우선시 한다. (사용량을 수기입력창의 값으로 구함)
                        if(electInput.getText().toString().equals("")) {
                            inputNumber = 0;
                        } else {
                            inputNumber = Integer.parseInt(electInput.getText().toString());
                        }
                    }
                } else {
                    next.setBackgroundResource(R.drawable.next_button_radius);
                    next.setEnabled(true);

                    // 수기입력 창에 값이 들어온 경우, 무조건 수기입력 창을 우선시 한다. (사용량을 수기입력창의 값으로 구함)
                    if(electInput.getText().toString().equals("")) {
                        inputNumber = 0;
                    } else {
                        inputNumber = Integer.parseInt(electInput.getText().toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        goCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra("machine", machine);
                startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                elect = new Elect();

                if(inputNumber > 0) {
                    if(Integer.parseInt(user.getElectBefore()) > Integer.parseInt(electInput.getText().toString())) {
                        MeasureResultDialog dialog = new MeasureResultDialog(getContext(), new MeasureResultDialog.DialogListener() {
                            @Override
                            public void onReShootClicked() {
                                // 전월지침 변경 화면으로 이동
                                Intent intent = new Intent(getContext(), BeforeElectActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onManualClicked() {
                                electInput.requestFocus();
                            }
                        });
                        dialog.showDialog();
                    } else {
                        electUsage = Integer.parseInt(electInput.getText().toString()) - Integer.parseInt(user.getElectBefore());
                        elect.setElectMeasure("수기입력");
                        elect.setElectAmount(Integer.toString(electUsage));
                    }

                } else if(result.getResponseCode().equals("0000")) {
                    ocrResult = checkZero(ocrResult);
                    if(Integer.parseInt(user.getElectBefore()) > Integer.parseInt(ocrResult)) {
                        MeasureResultDialog dialog = new MeasureResultDialog(getContext(), new MeasureResultDialog.DialogListener() {
                            @Override
                            public void onReShootClicked() {
                                // 전월지침 변경 화면으로 이동
                                Intent intent = new Intent(getContext(), BeforeElectActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onManualClicked() {
                                electInput.requestFocus();
                            }
                        });
                        dialog.showDialog();
                    } else {
                        electUsage = Integer.parseInt(ocrResult) - Integer.parseInt(user.getElectBefore());
                        elect.setElectMeasure(ocrResult);
                        elect.setElectAmount(Integer.toString(electUsage));
                    }
                } else if(result.getResponseCode().equals("9999")) {
                    if(Integer.parseInt(user.getElectBefore()) > inputNumber) {
                        MeasureResultDialog dialog = new MeasureResultDialog(getContext(), new MeasureResultDialog.DialogListener() {
                            @Override
                            public void onReShootClicked() {
                                // 전월지침 변경 화면으로 이동
                                Intent intent = new Intent(getContext(), BeforeElectActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onManualClicked() {
                                electInput.requestFocus();
                            }
                        });
                        dialog.showDialog();
                    } else {
                        electUsage = inputNumber - Integer.parseInt(user.getElectBefore());
                        elect.setElectMeasure("수기입력");
                        elect.setElectAmount(Integer.toString(electUsage));
                    }
                }


                if(electUsage <= 0) {
                    // 전력사용량 계산 결과가 마이너스 또는 0 값이 나옴(재촬영 또는 수기입력 유도 다이얼로그 노출)
                    MeasureResultDialog dialog = new MeasureResultDialog(getContext(), new MeasureResultDialog.DialogListener() {
                        @Override
                        public void onReShootClicked() {
                            // 전월지침 변경 화면으로 이동
                            Intent intent = new Intent(getContext(), BeforeElectActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onManualClicked() {
                            electInput.requestFocus();
                        }
                    });
                    dialog.showDialog();
                } else {
                    mActivity.onPageChange(ResultFragment.newInstance(), elect);
                }
            }
        });

        return rootView;
    }

    private String checkZero(String data) {

        String []split = data.split(" ");
        String result = "";
        int noZeroStartIndex = 0;

        for(int i=0; i<split.length; i++) {
            if(split[i].equals("0")) {
                noZeroStartIndex = i;
            }
        }

        for(int i=noZeroStartIndex; i<split.length; i++) {
            result = result + split[i];
        }

        return result;
    }
}
