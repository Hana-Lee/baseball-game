/**
 * @author Hana Lee
 * @since 2016-01-17 21:08
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
/*global $, app, webix */

app.v_player_profile = (function () {
  'use strict';

  var
    configMap = {
      settable_map : {
        height : null,
        avatar_width : null,
        avatar_height : null,
        player_model : null,
        show_email : null,
        custom_text : null
      },
      height : 200,
      avatar_width : 130,
      avatar_height : 173,
      player_model : null,
      show_email : true,
      custom_text : null
    },
    stateMap = {
      container : null
    }, webixMap = {},
    _createView, _calculateWinRate, _resetStateMap, _resetConfigMap, _resetWebixMap,
    configModule, initModule;

  _resetWebixMap = function () {
    webixMap = {};
  };

  _resetStateMap = function () {
    stateMap = {};
    stateMap.container = null;
  };

  _resetConfigMap = function () {
    configMap.height = 200;
    configMap.avatar_height = 173;
    configMap.avatar_width = 130;
    configMap.player_model = null;
    configMap.show_email = true;
    configMap.custom_text = null;
  };

  _createView = function () {
    var mainView, makeTemplate,
      email, nickname, avatarImagePath, level, totalScore, totalGameCnt, winCnt, loseCnt, winRate, totalRank, status;

    email = configMap.player_model.email;
    nickname = configMap.player_model.nickname;
    avatarImagePath = configMap.player_model.avatar.imagePath;
    level = configMap.player_model.level.value;
    totalScore = configMap.player_model.totalScore.value;
    totalGameCnt = configMap.player_model.matchRecord.totalGame.count;
    winCnt = configMap.player_model.matchRecord.win.count;
    loseCnt = configMap.player_model.matchRecord.lose.count;
    winRate = _calculateWinRate(totalGameCnt, winCnt);
    totalRank = configMap.player_model.totalRank.value;
    status = configMap.player_model.status;

    if (!status) {
      status = '';
    }

    makeTemplate = function () {
      var result = nickname + '님';
      if (configMap.show_email) {
        result += ' ( ' + email + ' )';
      }

      if (configMap.custom_text && configMap.custom_text !== '') {
        result += ' ' + configMap.custom_text;
      }

      return '<span data-email="' + email + '">' + result + '</span>';
    };

    mainView = [{
      //id : 'player-profile-' + configMap.player_model.id,
      css : 'player_profile',
      height : configMap.height,
      rows : [{
        css : 'webix_header player_profile_header ' + status.toLowerCase(),
        template : makeTemplate,
        type : 'header'
      }, {
        cols : [{
          css : 'player_profile_avatar',
          template : '<img src="' + avatarImagePath + '" height="100%" width="100%">',
          width : configMap.avatar_width
        }, {
          css : 'player_profile_info',
          template : '<ul style="list-style:none;padding:0;margin:0;">' +
          '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">레벨: ' + level + '</li>' +
          '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">총점: ' + totalScore + '점</li>' +
          '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">전적: ' + totalGameCnt + '전 ' + winCnt + '승 ' + loseCnt + '패</li>' +
          '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">승률: ' + winRate + '%</li>' +
          '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">등수: ' + totalRank + '등</li></ul>'
        }]
      }],
      on : {
        onDestruct : function () {
          _resetConfigMap();
          _resetStateMap();
          _resetWebixMap();
        }
      }
    }];

    webixMap.top = webix.ui(mainView, stateMap.container);
  };

  _calculateWinRate = function (totalGameCnt, winCnt) {
    var winRate = 0;
    if (totalGameCnt > 0) {
      winRate = winCnt / totalGameCnt * 100;
    }
    return winRate.toFixed(2);
  };

  configModule = function (input_map) {
    if (!input_map.height) {
      input_map.height = 200;
    }
    if (!input_map.custom_text) {
      input_map.custom_text = null;
    }
    if (input_map.show_email === undefined || input_map.show_email === null) {
      input_map.show_email = true;
    }
    app.utils.setConfigMap({
      input_map : input_map,
      settable_map : configMap.settable_map,
      config_map : configMap
    });
  };

  initModule = function (container) {
    stateMap.container = container;

    _createView();
  };

  return {
    initModule : initModule,
    configModule : configModule
  };
}());