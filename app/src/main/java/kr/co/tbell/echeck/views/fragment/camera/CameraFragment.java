package kr.co.tbell.echeck.views.fragment.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.dto.EcheckApiResult;
import kr.co.tbell.echeck.model.dto.EcheckRequest;
import kr.co.tbell.echeck.network.EcheckClient;
import kr.co.tbell.echeck.views.activity.RealtimeChargeActivity;
import kr.co.tbell.echeck.views.dialog.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    View scannerLayout;
    String encodedImage;
    byte[] encodedBytes;
    String machine;

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "CameraFragment";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1280;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 720;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        // SurfaceTextrue 화면이 카메라를 Rendering 할 준비가 되었을 경우 호출
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        // SurfaceTextrue 화면의 크기가 변경되었을 경우 호출
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        // 카메라 모듈이 정상 동작할 경우 호출되는 Callback
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // 카메라가 Open 되었을 때, 세마포어 Lock을 UnLock 함
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            // 카메라 Preview의 세션 생성을 시작함
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        // 카메라 모듈이 비정상 동작시 호출되는 Callback
        // 카메라 동작이 비정상일 경우, 카메라를 Close함
        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File mFile;

    /**
     * 센서로부터 영상이 {@link ImageReader}를 통해 전달되고 "onImageAvailable"되어 촬영 준비가 되었을 때 Callback이 호출되어 프레임을 저장하는 부분
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {

            /**
             * 현재 캡쳐된 이미지를 읽어옴
             * 캡쳐된 이미지는 각도가 좌로 90도 틀어져있기 때문에, 우로 90도 틀어주는 작업 진행
             * 작업 완료후 API 서버의 인코딩에 맞게 Base64 형식으로 변경
             */

            Image image = null;

            // 1. Image to Bytearray
            image = reader.acquireLatestImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);

            // 2. Bytearray to Bitmap
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap resultBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, option);

            // 3. Bitmap rotate 90F
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(90F);
            Bitmap rotateBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight(), rotateMatrix, false);

            // 4. Bitmap to
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            encodedBytes = Base64.encode(stream.toByteArray(), Base64.NO_WRAP);
            encodedImage = new String(encodedBytes);

            final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.startLoadingDialog();

            Handler handler = new Handler();

            // 전력량계별 모델 통신 분기 처리
            if(machine.equals("기계식 전력량계")) {
                machine = "1";
            } else if(machine.equals("전자식 전력량계(E-type)")) {
                machine = "2";
            }

            // 1. 디바이스 ID 구함(유저 구분을 위함)
            String deviceId = Build.MODEL + "_ANDROID_" + Build.VERSION.RELEASE;

            // 2. 현재 날짜 구함(측정 로그 수집을 위함)
            SimpleDateFormat format = new SimpleDateFormat ( "yyyyMMddHHmmss");
            Date date = new Date();
            String time = format.format(date);

            // 3. Base64형식의 ByteArray(String) 준비
            EcheckRequest apiRequest = new EcheckRequest();
            apiRequest.setDeviceId(deviceId);
            apiRequest.setRequestTime(time);
            apiRequest.setMeterId(machine);
            apiRequest.setImBytearray(encodedImage);

            // Retrofit 인터페이스 인스턴스화
            Call<EcheckApiResult> call = EcheckClient.getOcrService().getOcrResult(apiRequest);

            // 서버 통신 시작
            call.enqueue(new Callback<EcheckApiResult>() {
                @Override
                public void onResponse(Call<EcheckApiResult> call, Response<EcheckApiResult> response) {
//                            System.out.println("status code : " + response.code());
//                    System.out.println("status body : " + response.body());
//                    System.out.println("status message : " + response.message());
                    if(!response.isSuccessful()) {
                        Toast.makeText(getContext(), "연결 상태가 비정상입니다!", Toast.LENGTH_LONG).show();

                        String noResponse = null;
                        Intent intent = new Intent(getContext(), RealtimeChargeActivity.class);
                        intent.putExtra("ocrResponse", noResponse);
                        intent.putExtra("machine", machine);
                        startActivity(intent);

                        loadingDialog.dismissDialog();

                        return;
                    }

                    EcheckApiResult result2 = response.body();
//                            System.out.println("result : " + result2.toString());
                    Toast.makeText(getContext(), "OCR 통신 성공!", Toast.LENGTH_SHORT).show();

                    // 연결 성공하여 response json data RealtimeChargeActivity 전달
                    Intent intent = new Intent(getContext(), RealtimeChargeActivity.class);
                    intent.putExtra("ocrResponse", response.body());
                    intent.putExtra("machine", machine);
                    startActivity(intent);

                    loadingDialog.dismissDialog();
                }

                @Override
                public void onFailure(Call<EcheckApiResult> call, Throwable t) {
//                    System.out.println("dddd  :  " + t.getMessage());
                    Toast.makeText(getContext(), "OCR 동작이 비정상입니다!", Toast.LENGTH_LONG).show();

                    String noResponse = null;
                    Intent intent = new Intent(getContext(), RealtimeChargeActivity.class);
                    intent.putExtra("ocrResponse", noResponse);
                    intent.putExtra("machine", machine);
                    startActivity(intent);

                    loadingDialog.dismissDialog();
                }
            });

        }
    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        // afState 값이 없어도 캡처를 계속 진행함
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AF_STATE_FOCUSED_LOCKED - Af가 성공적으로 포커스를 잡았고 Lock 된 경우
                        // CONTROL_AF_STATE_NOT_FOCUSED_LOCKED - Af가 성공적으로 포커스를 못했고 Lock 된 경우

                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            // AF 동작에 상관없이 captureStillPicture 함수로 진입
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    // OnAttach(), OnCreate()는 필요가없어 구현 생략
    // fragment_camera2_basic을 inflate하여 화면에 표시
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 넘어온 전력량계 종류 값 받음
        if (getArguments() != null) {
            machine = getArguments().getString("machine");
        }
//        System.out.println("선택한 머신명 @@@@@@@@@@@@ " + machine);

        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    // Fragment의 View가 다 그려진 상태에서 호출
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        // 카메라 캡쳐 버튼에 클릭 리스너 등록
        view.findViewById(R.id.picture).setOnClickListener(this);
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        scannerLayout = view.findViewById(R.id.scannerLayout);
    }

    // onCreateView 함수가 동작이 완료된 다음 호출되는 함수
    // Fragment와 연결된 Activity가 OnCreate() 함수 작업을 완료했을 때 호출됨됨
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 계속해서 사용할 파일 경로를 지정하여 객체 생성
    }

    // onActivityCreated 함수가 동작이 완료된 다음 호출되는 함수
    // Background로 작업을 돌리기 위해서 Thread를 Start하고 실제 카메라를 Open 함
    @Override
    public void onResume() {
        super.onResume();
        // Background로 작업을 돌리기 위해 startBackgroundThread함수를 이용하여 비동기적 처리를 합니다.
        startBackgroundThread();

        // 화면이 꺼지고 다시 켜질 때, SurfaceTexture는 이미 사용 가능하고 "onSurfaceTextureAvailable"은 호출되지 않는다.
        // 위와 같은 경우에, onResume 함수에서 카메라를 열고 프리뷰를 시작할 수 있다.
        // 그렇지 않으면, SurfaceTextureListener에서 surface가 준비될 때까지 대기한다.
        if (mTextureView.isAvailable()) {
            // 화면이 카메라를 Rendering 할 수 있는 준비가 되어있다면 카메라 오픈
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            // 화면이 카메라를 Rendering 할 준비가 안되어 있다면 Listener 호출
            // setSurfaceTextureListener는 SurfaceTexture와 관련된 이벤트를 받기 위해 설정하는 함수
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        // 카메라를 종료하기 위해 Clear 버튼을 누르면 Close Camera가 호출되어 카메라가 종료됨
        // 카메라 모듈 안에 하드웨어를 Off한다는 말
        closeCamera();
        // Background에서 실행되던 Thread를 종료함
        stopBackgroundThread();
        super.onPause();
    }

    // 카메라 사용 권한을 물어보는 함수
    // 사용자가 허용하지 않으면, 카메라 사용 불가
    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 카메라를 보여주기 전 초기 설정 구성을 위한 함수
     * 카메라 사용 권한을 획득한 이후 호출됨
     *
     * @param width  카메라 프리뷰의 가로 사이즈
     * @param height 카메라 프리뷰의 세로 사이즈
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();

        // CameraManager 카메라 서비스를 관리하는 클래스, Camera HAL(Hardware Abstraction Layout)을 통해서 카메라 기능을 query(쿼리)할 수 있음
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            // getCameraIdList 함수를 통해서 디바이스의 카메라 개수를 얻음 (전면, LENS_FACING_FRONT, value 0 / 후면, LENS_FACING_BACK, value 1)
            // 카메라의 전체에 동일한 설정을 하기 위해 for Each 문으로 설정을 반복함
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // 전면 카메라 사용 하지 않음 (LENS_FACING_FRONT, 전면 카메라)
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                // 영상의 사이즈별 포맷을 구성하기 위핸 영상 스트림 설정 객체를 얻음
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // 스틸 이미지 캡쳐(한 프레임)를 가장 큰 이미지로 사용한다.
                Size []size = map.getOutputSizes(ImageFormat.JPEG);
                Size targetSize = null;
                for (int i=0; i<size.length; i++) {
//                    System.out.println("size  :  " + size[i]);
                    if((size[i].getHeight() > 608 && size[i].getHeight() < 1000) && (size[i].getWidth() > 608 && size[i].getWidth() < 1000)) {
                        targetSize = size[i];
                        System.out.println("size  :  " + targetSize.toString());
                        break;
                    }
                }
//                Size largest = Collections.max(
//                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
//                        new CompareSizesByArea());

                // ImageReader로 스트림 영상을 읽어옴
                mImageReader = ImageReader.newInstance(targetSize.getWidth(), targetSize.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
//                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);

                // ImageReader
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                // 센서의 상대적인 프리뷰 크기를 얻기 위해 치수(차원)를 변경할 필요가있는지 확인
                // display를 회전할 경우 영상의 비율을 화면에 맞게 맞춰주는 부분
                // 카메라 센서는 주로 4:3 비율로 영상을 출력하는데 Display를 세로로 전환할 경우 그에 맞는 비율로 변경
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Preview 크기를 너무 크게 잡으면, 카메라가 지원하는 크기를 초과할 수 있어 위험함
                // 현재 Display에 맞는 최적의 Preview size를 획득
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, targetSize);
//                maxPreviewHeight, largest);

                // 지정된 Preview 크기에 TextureView의 세로/가로 비율을 맞춘다.
                // 획득한 Preview 크기로 Width와 Height의 비율이 4:3인지, 16:9인지를 TextureView에 알려줌
                // TextureView에도 Camera Preview 비율대로 설정
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // 플래쉬 지원 여부를 확인합니다.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

            // Camera2 API를 지원하지 않는 디바이스에서는 NullPointException을 던지는데 해당 부분 예외처리
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }

    /**
     * {@link CameraFragment#mCameraId} 에서 확인한 CameraId를 통해 Camera Open
     */
    private void openCamera(int width, int height) {
        // 카메라 사용 권한 확인
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }
        // 카메라 초기 설정
        setUpCameraOutputs(width, height);
        // 카메라 화면 회전 설정
        configureTransform(width, height);
        Activity activity = getActivity();
        // 시스템 서비스의 카메라 서비스를 통해 카메라매니저 객체 초기화
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            // 카메라가 정상적으로 오픈되었을 때, 하위계층에 onOpend Callback 보내어 HAL 이전에 설정된 세마포어 LOCK을 해제해 주는 함수를 호출
            // Lock과 UnLock 중간에 다른 동작이 못 들어오게 하는 작업임, 주어진 시간에 따라 Lock을 한 후 UnLock을 하지 않아도 2500ms 이후에 Lock의 효력을 없앰
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * {@link CameraDevice} 카메라 종료
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Camera Preview 영상을 TextrueView에 보내는 마지막 단계
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // 카메라 Preview의 Width, Height 크기만큼 버퍼를 설정
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // 카메라 영상을 Surface에 전달하여 surface 객체를 만듬
            Surface surface = new Surface(texture);

            // 카메라의 영상과 연결된 CaptureRequest.Builder를 Surface로 Target 추가해주는 부분
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // 카메라 Preview에 CameraCaptureSession을 생성하고 Callback이 onConfigured로 오면,
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // 카메라 Preview에 Auto focus 을 어떤 것으로 사용할 건지 설정
                                // Continuous_Picture 모드용 AF를 사용
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                // 플래쉬 자동 설정
                                setAutoFlash(mPreviewRequestBuilder);

                                // 카메라 영상을 Maximum Rate(FPS, 초당 카메라 영상을 보내는 속도)로 계속 보내줍니다.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * SetUpCameraOutputs에서 결정된 Preview 사이즈를 가지고 Preview을 Display할 TextrueView에 크기를 정함
     * `mTextureView`에 영상을 Transform(x, y 위치로 이동)한다. 설정을 위해 Maxrix가 필요
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        // Maxrix를 이용해 Scale(이미지 축소, 확대), Rotate(회전)을 한 결과물을 mTextureView에 위치시킨다.
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    private void takePicture() {
        lockFocus();
    }

    /**
     *
     */
    private void lockFocus() {
        try {
            // 카메라에 Lock Focus 하는 방법을 알림, Autofocus를 Start 시킴
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // mState를 STATE_WAITING_LOCK 설정하고 #mCaptureCallback에게 lock을 기다리라고 알림
            mState = STATE_WAITING_LOCK;
            //
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // TEMPLATE_STILL_CAPTURE로 Capture을 위한 영상을 요청
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Preview도 동일한 AE와 AF 모드를 사용
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);

            // Orientation를 구하여 captureBuilder에 설정
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            // CaptureCallback을 통하여 실제 촬영을 하고 난 다음, 촬영이 완료되었다는 Callback을 받음
            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                // 촬영 완료 Callback을 받으면 호출되는 함수
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {


                    // onCaptureCompleted 카메라 캡처가 완료된 후에 unlockFocus를 호출함
                    // 다시 Preview로 돌아감, AutoFocus Trigger를 Reset해주고, 계속해서 Preview 영상을 보냄
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture: {
                takePicture();
                break;
            }
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "카메라 오류입니다. 앱을 재구동해주세요.";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }
    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }
}
