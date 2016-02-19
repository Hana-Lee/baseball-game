/**
 * @author Hana Lee
 * @since 2016-01-17 21:08
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_player_profile = (function () {
  'use strict';

  var stateMap = {
      container: null
    }, webixMap = {},
    _createView, initModule;

  _createView = function () {
    var mainView = {
      id: 'player-profile-container',
      height: 200,
      rows: [
        {template: '이하나님', type: 'header'},
        {
          cols: [
            {template: '<img src="images/character.gif" height="100%" width="100%">', width: 130},
            {
              template: '<ul style="list-style:none;padding:0;margin:0;">' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">Lv. 10</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">총점: 1004점</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">전적: 10전 8승 2패</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">승률: 80%</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">전체등수: 1등</li></ul>'
            }
          ]
        }
      ]
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
  };

  initModule = function (container) {
    stateMap.container = container;
    _createView();
  };

  return {
    initModule: initModule
  };
}());