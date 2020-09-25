var exec = require('cordova/exec');

module.exports.initBike = function (arg0, success, error) {

    exec(success, error, 'BikeSensors', 'initBike', [arg0]);
    
};

module.exports.startBike = function (arg0, success, error) {

    exec(success, error, 'BikeSensors', 'startBike', [arg0]);

};