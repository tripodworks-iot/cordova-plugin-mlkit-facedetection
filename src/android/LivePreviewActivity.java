/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
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

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONObject;

import java.io.IOException;

import jp.co.tripodw.iot.facedetection.common.CameraSource;
import jp.co.tripodw.iot.facedetection.common.CameraSourcePreview;
import jp.co.tripodw.iot.facedetection.common.GraphicOverlay;
import jp.co.tripodw.iot.facedetection.facedetector.FaceDetectorProcessor;
import jp.co.tripodw.iot.facedetection.preference.LivePreviewPreferenceFragment;
import jp.co.tripodw.iot.facedetection.preference.PreferenceUtils;


/**
 * Live preview demo for ML Kit APIs.
 */
public class LivePreviewActivity extends Fragment {
    private static final String TAG = "LivePreviewActivity";

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private int width;
    private int height;
    private int x;
    private int y;

    private String appResourcesPackage;
    private View view;
    private FrameLayout frameContainerLayout;
    private Boolean front;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appResourcesPackage = getActivity().getPackageName();

        // Inflate the layout for this fragment
        view = inflater.inflate(getResources().getIdentifier("live_preview_activity", "layout", appResourcesPackage), container, false);
        createCameraPreview();
        return view;
    }

    public void stopCamera() {
        if (cameraSource == null) {
            return;
        }
        cameraSource.release();
        cameraSource = null;
    }

    public CameraSource getCameraSource() {
        return cameraSource;
    }

    private void createCameraPreview() {
        if (preview != null) {
            return;
        }

        //set box position and size
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.setMargins(x, y, 0, 0);
        frameContainerLayout = (FrameLayout) view.findViewById(getResources().getIdentifier("frame_container", "id", appResourcesPackage));
        frameContainerLayout.setLayoutParams(layoutParams);

        preview = (CameraSourcePreview) view.findViewById(getResources().getIdentifier("preview_view", "id", appResourcesPackage));

        graphicOverlay = (GraphicOverlay) view.findViewById(getResources().getIdentifier("graphic_overlay", "id", appResourcesPackage));

        createCameraSource();
    }

    public void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(getActivity(), graphicOverlay);
        }

        if (front) {
            cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
        } else {
            cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }

        try {
            Log.i(TAG, "Using Face Detector Processor");
            FaceDetectorOptions faceDetectorOptions =
                    PreferenceUtils.getFaceDetectorOptionsForLivePreview();
            cameraSource.setMachineLearningFrameProcessor(
                    new FaceDetectorProcessor(getActivity(), faceDetectorOptions));
        } catch (RuntimeException e) {
            Log.e(TAG, "Can not create image processor: ", e);
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    public void startCameraSource() {
        if (cameraSource == null) {
            Log.e(TAG, "resume: cameraSource is null");
            return;
        }

        if (preview == null) {
            Log.e(TAG, "resume: Preview is null");
            return;
        }
        if (graphicOverlay == null) {
            Log.e(TAG, "resume: graphOverlay is null");
            return;
        }

        try {
            preview.start(cameraSource, graphicOverlay);
        } catch (IOException e) {
            Log.e(TAG, "Unable to start camera source.", e);
            cameraSource.release();
            cameraSource = null;
        }
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
        Log.i(TAG, "front:" + front);
        this.front = front;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        createCameraSource();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}
