'use strict';

var RestCall = {};

//Custom indexOf for IE
if (!Array.prototype.indexOf) {
    Array.prototype.indexOf = function(obj, start) {
        for (var i = (start || 0), j = this.length; i < j; i++) {
            if (this[i] === obj) { return i; }
        }
        return -1;
    }
}
var initializeRestCall = function($http) {
  RestCall.http = $http;
};

RestCall.get = function(address, success, error, opts) {

    var preventCache =  Math.floor(Math.random() * 100000000);

    if(!success) {
        success = function(){};
    }

    if(!error) {
        error = function(){};
    }

    if(address.indexOf('?')>-1) {
        address+="&r="+preventCache;
    } else {
        address+="?r="+preventCache;
    }

    if(opts) {
        RestCall.http.get(address,opts).success(success).error(error);
    } else {
        RestCall.http.get(address).success(success).error(error);
    }
};

RestCall.post = function(address, success, error, formData, opts) {
    var preventCache =  Math.floor(Math.random() * 100000000);

    if(!success) {
        success = function(){};
    }

    if(!error) {
        error = function(){};
    }

    if(address.indexOf('?')>-1) {
        address+="&r="+preventCache;
    } else {
        address+="?r="+preventCache;
    }
    if(formData) {
        if(opts) {
            RestCall.http.post(address, formData, opts).success(success).error(error);
        } else {
            RestCall.http.post(address, formData).success(success).error(error);
        }
    } else {
        RestCall.http.post(address).success(success).error(error);
    }
};











