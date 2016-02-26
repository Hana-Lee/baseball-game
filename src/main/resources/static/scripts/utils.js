/**
 * @author Hana Lee
 * @since 2016-02-26 22:22
 */
/*jslint
 browser  : true,
 continue : true,
 devel    : true,
 indent   : 2,
 maxerr   : 100,
 nomen    : true,
 plusplus : true,
 regexp   : true,
 vars     : false,
 white    : true,
 todo     : true
 */
/*global app */
app.utils = (function () {
  'use strict';
  var _s4,
    guid;
  
  _s4 = function () {
    return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
  };

  guid = function () {
    return _s4() + _s4() + '-' + _s4() + '-' + _s4() + '-' + _s4() + '-' + _s4() + _s4() + _s4();
  };
  
  return {
    guid: guid
  };
}());