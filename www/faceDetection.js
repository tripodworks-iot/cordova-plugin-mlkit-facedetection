var exec = require('cordova/exec');
var faceDetection = function(){};

faceDetection.prototype.start = function(param, success, error) {
    exec(success, error, "faceDetection", "start", [param]);
};

faceDetection.prototype.CAMERA_DIRECTION = {
    BACK: 'back',
    FRONT: 'front'
  };

var FaceDetection = new faceDetection();
module.exports = FaceDetection;
