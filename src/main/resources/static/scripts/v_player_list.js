/**
 * @author Hana Lee
 * @since 2016-01-17 22:07
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_player_list = (function () {
  'use strict';

  var configMap = {
      height: 62
    }, stateMap = {
      container: null
    }, webixMap = {},
    _getLoggedInPlayers, _createView,
    _loggedInPlayers, initModule;

  _getLoggedInPlayers = function (callback) {
    var serverResponse = '';
    webix.ajax().sync().get('player/login/true', {
      error: function (text) {
        serverResponse = JSON.parse(text);
        _loggedInPlayers = [];
      },
      success: function (text) {
        serverResponse = JSON.parse(text);
        _loggedInPlayers = serverResponse;
      }
    });

    callback();
  };

  _createView = function () {
    var mainView = {
      id: 'player-list',
      view: 'list',
      select: true,
      type: {
        height: configMap.height,
        template: '<img src="images/blank_character_2.gif" width="50" height="55" style="float:left;padding-right:10px;">' +
        '<div>#nickname#(#email#)</div><div style="padding-left:18px;">점수:#totalScore.value#, 등수:#totalRank.value#</div>'
      },
      data: _loggedInPlayers
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
  };

  initModule = function (container) {
    stateMap.container = container;
    _getLoggedInPlayers(_createView);
  };

  return {
    initModule: initModule
  };
}());