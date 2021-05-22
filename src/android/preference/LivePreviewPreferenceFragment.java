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

package jp.co.tripodw.iot.facedetection.preference;

import android.hardware.Camera;
import android.util.Log;

import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

import jp.co.tripodw.iot.facedetection.common.CameraSource;
import jp.co.tripodw.iot.facedetection.common.CameraSource.SizePair;

/**
 * Configures live preview demo settings.
 */
public class LivePreviewPreferenceFragment {
    private static final String TAG = "LivePreviewPreference";

    public static void setEnableViewport(boolean value) {
        PreferenceUtils.saveParam("enableViewport", value);
    }

    public static void setMinFaceSize(float value) {
        PreferenceUtils.saveParam("minFaceSize", value);
    }

    public static void setLandmarkMode(boolean value) {
        if (value) {
            PreferenceUtils.saveParam("landmarkMode", FaceDetectorOptions.LANDMARK_MODE_ALL);
        } else {
            PreferenceUtils.saveParam("landmarkMode", FaceDetectorOptions.LANDMARK_MODE_NONE);
        }
    }

    public static void setContourMode(boolean value) {
        if (value) {
            PreferenceUtils.saveParam("contourMode", FaceDetectorOptions.CONTOUR_MODE_ALL);
        } else {
            PreferenceUtils.saveParam("contourMode", FaceDetectorOptions.CONTOUR_MODE_NONE);
        }
    }

    public static void setClassificationMode(boolean value) {
        if (value) {
            PreferenceUtils.saveParam("classificationMode", FaceDetectorOptions.CLASSIFICATION_MODE_ALL);
        } else {
            PreferenceUtils.saveParam("classificationMode", FaceDetectorOptions.CLASSIFICATION_MODE_NONE);
        }
    }

    public static void setPerformanceMode(boolean value) {
        if (value) {
            PreferenceUtils.saveParam("performanceMode", FaceDetectorOptions.PERFORMANCE_MODE_FAST);
        } else {
            PreferenceUtils.saveParam("performanceMode", FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE);
        }
    }

    public static void setEnableFaceTracking(boolean value) {
        PreferenceUtils.saveParam("faceTrackMode", value);
    }

    public static void setUpCameraSize(String cameraSize) {

        Log.i(TAG, "previewSize start for CAMERA_FACING_BACK");
        setUpCameraPreviewSize(cameraSize, CameraSource.CAMERA_FACING_BACK);

        Log.i(TAG, "previewSize start for CAMERA_FACING_FRONT");
        setUpCameraPreviewSize(cameraSize, CameraSource.CAMERA_FACING_FRONT);
    }

    public static boolean getShowFrame() {
        return (Boolean) PreferenceUtils.getParam("showFrame");
    }

    private static void setUpCameraPreviewSize(String cameraSize, int cameraId) {
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);

            List<SizePair> previewSizeList = CameraSource.generateValidPreviewSizeList(camera);

            for (SizePair sizePair : previewSizeList) {
                String support = sizePair.preview.toString();
                Log.i(TAG, "previewSize=" + support);
                if (cameraSize.equals(support) && cameraId == CameraSource.CAMERA_FACING_BACK) {
                    PreferenceUtils.saveParam("backSize", support);
                    break;
                }

                if (cameraSize.equals(support) && cameraId == CameraSource.CAMERA_FACING_FRONT) {
                    PreferenceUtils.saveParam("frontSize", support);
                    break;
                }
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "pRuntimeException", e);
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
    }
}
