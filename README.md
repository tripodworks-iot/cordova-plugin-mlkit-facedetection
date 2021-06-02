<!--
 * Copyright 2021 TripodWorks
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
-->
# cordova plugin for mlkit face detection
<a href="https://badge.fury.io/js/cordova-plugin-mlkit-facedetection" target="_blank"><img height="21" style='border:0px;height:21px;' border='0' src="https://badge.fury.io/js/cordova-plugin-mlkit-facedetection.svg" alt="NPM Version"></a>
<a href='https://www.npmjs.org/package/cordova-plugin-mlkit-facedetection' target='_blank'><img height='21' style='border:0px;height:21px;' src='https://img.shields.io/npm/dt/cordova-plugin-mlkit-facedetection.svg?label=NPM+Downloads' border='0' alt='NPM Downloads' /></a>

Cordova plugin that allows face detection from Javascript and HTML
Preferences

**Currently available for Android only. Will support iOS in the future.<br> This plugin is under constant development. Releases are being kept up to date when appropriate.**

## Requirements
- Cordova 9.0.0 or higher
- Android Cordova library 8.0.0 or higher

## Features
- Start the camera preview for face detection from HTML code
- Stop the camera preview
- Take Photos

## Installation

Use any one of the installation methods listed below depending on which framework you use.

To install the master version with latest fixes and features

```
$ cordova plugin add https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection.git
$ ionic cordova plugin add https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection.git
```
or if you want to use the last released version on npm
```
$ cordova plugin add cordova-plugin-mlkit-facedetection
$ ionic cordova plugin add cordova-plugin-mlkit-facedetection
```

## Methods

### start(options, [successCallback, errorCallback])
Start the camera preview instance for face detection.

<strong>Options: </strong>
All options stated are optional and will default to values here.

| Item | Type | Default | Note |
| --- | --- | --- | --- |
| x | int | 0 | start x position for camera  |
| y | int | 0 | start y position for camera |
| width | int | 0 | Camera screen width |
| height | int | 0 | Camera screen height |
| front | boolean | true | Defaults to front camera |
| cameraPixel | string | '480x640' | Picture pixel |
| minFaceSize | float | 0.1 | Recognize the proportion of the face |
| landmark | boolean | true | Whether to attempt to identify facial "landmarks": eyes, ears, nose, cheeks, mouth, and so on |
| classification | boolean | true | Whether or not to classify faces into categories such as "smiling", and "eyes open" |
| faceTrack | boolean | false | Whether or not to assign faces an ID, which can be used to track faces across images <br>Note that when contour detection is enabled, only one face is detected, so face tracking doesn't produce useful results. For this reason, and to improve detection speed, don't enable both contour detection and face tracking. |
| contour | boolean | false | Whether to detect the contours of facial features. Contours are detected for only the most prominent face in an image. <br>Note that when face contour detection or classification and landmark detection, but not both |

<br>
<strong>successCallback(result): </strong>Result will default to values here.

| Item | Type | Note |
| --- | --- | --- |
| type | string | Type of live frame information (image/face) |
| data | Json/List  | if result.type is 'image', data type is Json, otherwise，data type is List |

<br>
If result.type is 'image', data type is Json, values as follows.

| Item | Type | Note |
| --- | --- | --- |
| imageSize | string | Input Picture pixel  |
| framesPerSecond | int | Image frames Per Second  |
| frameLatency | int | Image frames Latency(ms)  |
| detectorLatency | int | Image detector Latency(ms)  |

<br>

If result.type is 'face', data type is List. The type of the list element is Json, element values as follow.

| Item | Type | Note |
| --- | --- | --- |
| id | int | the tracking ID if the tracking is enabled.  Only when start option faceTrack is true  |
| smiling | float | The probability that the face is smiling(0~1). Only when start option classification is true  |
| leftEyeOpen | float | The probability that the face's left eye is open(0~1). Only when start option classification is true  |
| rightEyeOpen | float | The probability that the face's right eye is open(0~1). Only when start option classification is true  |
| eulerX | float | Rotation of the face about the horizontal axis of the image  |
| eulerY | float | Rotation of the face about the vertical axis of the image  |
| eulerZ | float | Rotation of the face about the axis pointing out of the image  |

<br>

```
  let options = {
    x: 30,
    y: 10,
    width: 200,
    height: 400,
    front: false,
    cameraPixel: '480x640',
    minFaceSize: 0.5,
    landmark:false,
    classification:false,
    faceTrack:true,
    contour:false,
    liveFrame:false,
  };

  faceDetection.start(options, function(result) {
    const data = result.data;
    if(result.type == 'image') {
      // get live frame information
       console.log(JSON.stringify(data));
    }else {
      // get face frame information
      data.forEach(function(face)){
        console.log(JSON.stringify(face));
      });
    }
  });
```

### stop([successCallback, errorCallback])
Stop the camera preview instance.

```
faceDetection.stop();
```

### takePicture(options, successCallback, [errorCallback])
Take the picture. It will choose a supported photo size that is closest to width and height specified and has closest aspect ratio to the preview.

<strong>Options:</strong>
All options stated are optional and will default to values here.

| Item | Type | Default | Note |
| --- | --- | --- | --- |
| width | int | 480 | Taken image width. If width are not specified or are 0 it will use the defaults |
| height | int | 640 | Taken Image height. If height are not specified or are 0 it will use the defaults |
| quality | int | 85 | Specifies the quality/compression value: 0=min compression, 100=max quality

```
  let options = {
    width: 200,
    height: 400,
    quality: 90,
  };

  faceDetection.takePicture(options, function(base64Data) {
    /*
      base64Data is base64 encoded jpeg image. Use this data to store to a file or upload.
      Its up to the you to figure out the best way to save it to disk or whatever for your application.
    */

    // One simple example is if you are going to use it inside an HTML img src attribute
    // then you would do the following:
    imageSrcData = 'data:image/jpeg;base64,' + base64Data;
    $('img#my-img').attr('src', imageSrcData);
  });

  // OR if you want to use the default options.
  faceDetection.takePicture(function(base64Data){
    /* code here */
  });
```

## Sample App
Sample cordova application <a href="https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection/tree/main/examples/mlkit.facedeteciton.cordova">mlkit.facedeteciton.cordova</a>


### Screenshots
<img width="200" src="https://user-images.githubusercontent.com/50347751/119295757-91a9bf00-bc92-11eb-8b78-7e0f56322900.png"/><img hspace="30" width="200" src="https://user-images.githubusercontent.com/50347751/119295761-93738280-bc92-11eb-8940-aa3c80987121.png"/>
