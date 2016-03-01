/**
 * @author Hana Lee
 * @since 2016-02-19 21:33
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
/*global $, app, webix, $$ */

app.m_player = (function () {
  'use strict';

  var info, initModule, getInfo, reset;

  getInfo = function () {
    return info;
  };

  reset = function () {
    info = null;
  };

  initModule = function (playerObject) {
    info = playerObject;
  };

  return {
    initModule : initModule,
    getInfo : getInfo,
    reset : reset
  };
}());