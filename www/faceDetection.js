/*
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
 */

var exec = require('cordova/exec');
var faceDetection = function () {};

faceDetection.prototype.start = function(param, success, error) {
  exec(success, error, "faceDetection", "start", [param]);
};

faceDetection.prototype.stop = function(success, error) {
  exec(success, error, "faceDetection", "stop", []);
};

faceDetection.prototype.takePicture = function(param, success, error) {
  exec(success, error, "faceDetection", "takePicture", [param]);
};

faceDetection.prototype.startX = function(param, success, error) {
  exec(success, error, "faceDetection", "startX", [param]);
};

faceDetection.prototype.stopX = function(success, error) {
  exec(success, error, "faceDetection", "stopX", []);
};

var FaceDetection = new faceDetection();
module.exports = FaceDetection;
