# cordova-plugin-mlkit-facedetection
cordova plugin for mlkit face detection

## Android Preferences

Preferences available for Android only.

## Install

```
$ cordova plugin add https://github.com/tripodworks-iot/cordova-plugin-mlkit-facedetection
```

## Usage

### Starts the camera preview instance.

```
faceDetection.start(options, [successCallback, errorCallback]);
```
<br>

<strong>Options:</strong>
All options stated are optional and will default to values here

* `x` - Defaults to 0
* `y` - Defaults to 0
* `width` - Defaults to window.screen.width
* `height` - Defaults to window.screen.height
* `front` - Defaults to front camera
* `landmark` - Whether to attempt to identify facial "landmarks": eyes, ears, nose, cheeks, mouth, and so on.(Default true)
* `classification` - Whether or not to classify faces into categories such as "smiling", and "eyes open".(Default true)
* `faceTrack` - Whether or not to assign faces an ID, which can be used to track faces across images.(Default false)
* `contour` - Whether to detect the contours of facial features. Contours are detected for only the most prominent face in an image.(Default true)
* `liveFrame` - Show live frame information.(Default true)

```
  let options = {
    x: x,
    y: y,
    width: width,
    height: height,
    front: false,
    landmark:false,
    classification:false,
    faceTrack:true,
    contour:false,
    liveFrame:false,
  };
```

### Stop the camera preview instance.

```
faceDetection.stop([successCallback, errorCallback]);
```

## Sample app
Execute the following command to start app.

```
$ cd /path/cordova-plugin-mlkit-facedetection/
$ cd ./examples\mlkit.facedeteciton.cordova
$ npm install
$ cd ./src-cordova
$ npm install
$ cordova platform add android
$ cd ..
$ npm run cordova-serve-android
```
