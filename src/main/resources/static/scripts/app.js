/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app:true, webix */

var app = (function () {
  'use strict';
  var stateMap = {
    container: null
  }, initModule;

  initModule = function (container) {
    stateMap.container = container;
    app.v_shell.initModule(stateMap.container);
  };

  return {
    initModule: initModule
  };
}());