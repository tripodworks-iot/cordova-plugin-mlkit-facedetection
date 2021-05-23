/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.tripodw.iot.facedetection;

import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;

import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONObject;

import jp.co.tripodw.iot.facedetection.common.CameraXViewModel;
import jp.co.tripodw.iot.facedetection.common.GraphicOverlay;
import jp.co.tripodw.iot.facedetection.common.VisionImageProcessor;
import jp.co.tripodw.iot.facedetection.facedetector.FaceDetectorProcessor;
import jp.co.tripodw.iot.facedetection.preference.LivePreviewPreferenceFragment;
import jp.co.tripodw.iot.facedetection.preference.PreferenceUtils;

/**
 * Live preview for ML Kit APIs using CameraX.
 */
@RequiresApi(VERSION_CODES.LOLLIPOP)
public final class CameraXLivePreviewActivity extends Fragment {
    private static final String TAG = "CameraXLivePreview";

    private PreviewView preview;
    private GraphicOverlay graphicOverlay;

    @Nullable
    private ProcessCameraProvider cameraProvider;
    @Nullable
    private Preview previewUseCase;
    @Nullable
    private ImageAnalysis analysisUseCase;
    private VisionImageProcessor imageProcessor;
    private boolean needUpdateGraphicOverlayImageSourceInfo;

    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private CameraSelector cameraSelector;

    private String appResourcesPackage;
    private View view;
    private FrameLayout frameContainerLayout;

    private int width;
    private int height;
    private int x;
    private int y;

    public void stopCamera() {
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

//  @Override
//  public void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//
//    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//    int idLayoutActivityMain = getApplication().getResources().getIdentifier("live_preview_x_activity", "layout", getApplication().getPackageName());
//    View rootView = inflater.inflate(idLayoutActivityMain, null);
//    setContentView(rootView);
//
//    preview = rootView.findViewWithTag("preview_view" );
//    graphicOverlay = rootView.findViewWithTag("graphic_overlay" );
//
//    cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
//
//    new ViewModelProvider(this, AndroidViewModelFactory.getInstance(getApplication()))
//            .get(CameraXViewModel.class)
//            .getProcessCameraProvider()
//            .observe(
//                    this,
//                    provider -> {
//                      cameraProvider = provider;
//                      bindAllCameraUseCases();
//            });
//  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appResourcesPackage = getActivity().getPackageName();

        // Inflate the layout for this fragment

        view = inflater.inflate(getResources().getIdentifier("live_preview_x_activity", "layout", appResourcesPackage), container, false);

        //set box position and size
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.setMargins(x, y, 0, 0);
        frameContainerLayout = view.findViewWithTag("frame_container");
        frameContainerLayout.setLayoutParams(layoutParams);

        preview = view.findViewWithTag("preview_view");
        graphicOverlay = view.findViewWithTag("graphic_overlay");

        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        new ViewModelProvider(this, AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        this,
                        provider -> {
                            cameraProvider = provider;
                            bindAllCameraUseCases();
                        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindAllCameraUseCases();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
    }

    private void bindPreviewUseCase() {
        if (!PreferenceUtils.isCameraLiveViewportEnabled(getContext())) {
            return;
        }
        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        Size targetResolution = PreferenceUtils.getCameraXTargetResolution(lensFacing);
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution);
        }
        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(preview.getSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase);
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }
        if (imageProcessor != null) {
            imageProcessor.stop();
        }

        try {
            Log.i(TAG, "Using Face Detector Processor");
            FaceDetectorOptions faceDetectorOptions =
                    PreferenceUtils.getFaceDetectorOptionsForLivePreview();
            imageProcessor = new FaceDetectorProcessor(getContext(), faceDetectorOptions);
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: ", e);
            return;
        }

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        Size targetResolution = PreferenceUtils.getCameraXTargetResolution(lensFacing);
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution);
        }
        analysisUseCase = builder.build();

        needUpdateGraphicOverlayImageSourceInfo = true;
        analysisUseCase.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(getContext()),
                imageProxy -> {
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            graphicOverlay.setImageSourceInfo(
                                    imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                        } else {
                            graphicOverlay.setImageSourceInfo(
                                    imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false;
                    }
                    try {
                        imageProcessor.processImageProxy(imageProxy, graphicOverlay);
                    } catch (MlKitException e) {
                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                    }
                });

        cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
    }

    public void setCameraParams(JSONObject params, DisplayMetrics metrics) {

        this.setCameraType(params);
        this.setRect(params, metrics);

        // カメラサイズ設定
        String cameraSize = params.optString("cameraSize", "640x480");
        LivePreviewPreferenceFragment.setUpCameraSize(cameraSize);

        boolean enableViewport = params.optBoolean("viewport", true);
        Log.i(TAG, "viewportEnable:" + enableViewport);
        LivePreviewPreferenceFragment.setEnableViewport(enableViewport);

        float minFaceSize = (float) params.optDouble("minFaceSize", 0.1);
        Log.i(TAG, "minFaceSize:" + minFaceSize);
        if (minFaceSize < 0.0f || minFaceSize > 1.0f) {
            minFaceSize = 0.1f;
        }
        LivePreviewPreferenceFragment.setMinFaceSize(minFaceSize);

        boolean performanceMode = params.optBoolean("performance", true);
        Log.i(TAG, "performanceMode:" + performanceMode);
        LivePreviewPreferenceFragment.setPerformanceMode(performanceMode);

        boolean landmarkMode = params.optBoolean("landmark", true);
        Log.i(TAG, "landmarkMode:" + landmarkMode);
        LivePreviewPreferenceFragment.setLandmarkMode(landmarkMode);

        boolean classificationMode = params.optBoolean("classification", true);
        Log.i(TAG, "classificationMode:" + classificationMode);
        LivePreviewPreferenceFragment.setClassificationMode(classificationMode);

        boolean enableFaceTracking = params.optBoolean("faceTrack", false);
        Log.i(TAG, "faceTrack:" + enableFaceTracking);
        LivePreviewPreferenceFragment.setEnableFaceTracking(enableFaceTracking);

        boolean contourMode = params.optBoolean("contour", false);
        Log.i(TAG, "contourMode:" + contourMode);
        LivePreviewPreferenceFragment.setContourMode(contourMode);
        if (contourMode) {
            LivePreviewPreferenceFragment.setLandmarkMode(false);
            LivePreviewPreferenceFragment.setClassificationMode(false);
            LivePreviewPreferenceFragment.setEnableFaceTracking(false);
        }
    }

    private void setRect(JSONObject params, DisplayMetrics metrics) {
        // offset
        int x = params.optInt("x", 0);
        int y = params.optInt("y", 0);
        this.x = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, metrics);
        this.y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y, metrics);

        // size
        int width = params.optInt("width", 0);
        int height = params.optInt("height", 0);
        this.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, metrics);
        this.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, metrics);
    }

    private void setCameraType(JSONObject params) {
        boolean front = params.optBoolean("front", false);
        Log.i(TAG, "lensFacing:" + front);

        if (front) {
            this.lensFacing = CameraSelector.LENS_FACING_FRONT;
        } else {
            this.lensFacing = CameraSelector.LENS_FACING_BACK;
        }
    }

}
