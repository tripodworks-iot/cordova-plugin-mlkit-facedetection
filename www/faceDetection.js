var exec = require('cordova/exec');
var faceDetection = function(){};

faceDetection.prototype.start = function(arg0, success, error) {
    exec(success, error, "faceDetection", "start", arg0);
};

var FaceDetection = new faceDetection();
module.exports = FaceDetection;
