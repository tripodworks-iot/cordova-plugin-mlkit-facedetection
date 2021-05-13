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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.google.android.gms.common.images.Size;
import com.google.common.base.Preconditions;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.HashMap;
import java.util.Map;

import jp.co.tripodw.iot.facedetection.common.CameraSource;
import jp.co.tripodw.iot.facedetection.common.CameraSource.SizePair;

/**
 * Utility class to retrieve shared preferences.
 */
public class PreferenceUtils {
    private static Map<String, Object> cameraParam = new HashMap<>();
    static {
        cameraParam.put("enableViewport", true);
        cameraParam.put("landmarkMode", FaceDetectorOptions.LANDMARK_MODE_NONE);
        cameraParam.put("contourMode", FaceDetectorOptions.CONTOUR_MODE_ALL);
        cameraParam.put("classificationMode", FaceDetectorOptions.CLASSIFICATION_MODE_NONE);
        cameraParam.put("performanceMode", FaceDetectorOptions.PERFORMANCE_MODE_FAST);
        cameraParam.put("faceTrackMode", false);
        cameraParam.put("minFaceSize", 0.1f);
    }

    public static void saveParam(@Nullable String prefKeyId, Object value) {
        cameraParam.put(prefKeyId, value);
    }

    @Nullable
    public static SizePair getCameraPreviewSizePair(Context context, int cameraId) {
        Preconditions.checkArgument(
                cameraId == CameraSource.CAMERA_FACING_BACK
                        || cameraId == CameraSource.CAMERA_FACING_FRONT);
        String previewSizePrefKey;
        String pictureSizePrefKey;
        if (cameraId == CameraSource.CAMERA_FACING_BACK) {
            previewSizePrefKey = "rcpvs";
            pictureSizePrefKey = "rcpts";
        } else {
            previewSizePrefKey = "fcpvs";
            pictureSizePrefKey = "fcpts";
        }

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return new SizePair(
                    Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)),
                    Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)));
        } catch (Exception e) {
            return null;
        }
    }

    public static FaceDetectorOptions getFaceDetectorOptionsForLivePreview() {

        int landmarkMode = (Integer) cameraParam.get("landmarkMode");

        int contourMode = (Integer) cameraParam.get("contourMode");

        int classificationMode = (Integer) cameraParam.get("classificationMode");

        int performanceMode = (Integer) cameraParam.get("performanceMode");

        boolean enableFaceTracking = (Boolean) cameraParam.get("faceTrackMode");
        float minFaceSize = (Float) cameraParam.get("minFaceSize");

        FaceDetectorOptions.Builder optionsBuilder =
                new FaceDetectorOptions.Builder()
                        .setLandmarkMode(landmarkMode)
                        .setContourMode(contourMode)
                        .setClassificationMode(classificationMode)
                        .setPerformanceMode(performanceMode)
                        .setMinFaceSize(minFaceSize);
        if (enableFaceTracking) {
            optionsBuilder.enableTracking();
        }
        return optionsBuilder.build();
    }

    public static boolean isCameraLiveViewportEnabled(Context context) {
        return (Boolean) cameraParam.get("enableViewport");
    }

    private PreferenceUtils() {
    }
}
