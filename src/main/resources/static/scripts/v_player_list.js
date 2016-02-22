/**
 * @author Hana Lee
 * @since 2016-01-17 22:07
 */
/*jslint        browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix, $$ */

app.v_player_list = (function () {
  'use strict';

  var configMap = {
      height: 62
    }, stateMap = {
      container: null
    }, webixMap = {},
    _getLoggedInPlayers, _createView, _createTemplate, _createContextMenu, _showPlayerInfoWindow, _calculateWinRate,
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
    var mainView;

    mainView = {
      id: 'player-list',
      view: 'list',
      select: true,
      type: {
        height: configMap.height,
        template: _createTemplate
      },
      data: _loggedInPlayers,
      onContext: {},
      on: {
        onAfterContextMenu: function (id) {
          this.select(id);
        },
        onDestruct: function () {
          webixMap.context_menu.destructor();
          if (webixMap.profile_window) {
            webixMap.profile_window.destructor();
          }
        }
      }
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('player-list');
    webixMap.context_menu = _createContextMenu();

    webixMap.context_menu.attachTo(webixMap.main_view);
  };

  _createTemplate = function (obj) {
    console.log('obj', obj);
    return '<img src="images/blank_character_2.gif" width="50" height="55" style="float:left;padding-right:10px;">' +
      '<div>' + obj.nickname + '(' + obj.email + ')</div>' +
      '<div style="padding-left:18px;">점수: ' + obj.totalScore.value + ', 등수:' + obj.totalRank.value + '</div>';
  };

  _createContextMenu = function () {
    return webix.ui({
      view: 'contextmenu',
      id: 'player-context-menu',
      data: [
        '상세보기'
      ],
      on: {
        onItemClick: function () {
          var selectedPlayerInfo = webixMap.main_view.getSelectedItem();
          console.log(selectedPlayerInfo);
          _showPlayerInfoWindow(selectedPlayerInfo);
        }
      }
    });
  };

  _showPlayerInfoWindow = function (playerInfo) {
    var profileView,
      email, nickname, avatarImagePath, level, totalScore, totalGameCnt, winCnt, loseCnt, winRate, totalRank;

    email = playerInfo.email;
    nickname = playerInfo.nickname;
    avatarImagePath = playerInfo.avatar.imagePath;
    level = playerInfo.level.value;
    totalScore = playerInfo.totalScore.value;
    totalGameCnt = playerInfo.matchRecord.totalGame.count;
    winCnt = playerInfo.matchRecord.win.count;
    loseCnt = playerInfo.matchRecord.lose.count;
    winRate = _calculateWinRate(totalGameCnt, winCnt);
    totalRank = playerInfo.totalRank.value;

    profileView = {
      id: 'player-profile-' + playerInfo.id,
      height: 260,
      type: 'space',
      rows: [
        {template: nickname + '님 ( ' + email + ' )', type: 'header'},
        {
          cols: [
            {template: '<img src="' + avatarImagePath + '" height="100%" width="100%">', width: 130},
            {
              template: '<ul style="list-style:none;padding:0;margin:0;">' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">레벨: ' + level + '</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">총점: ' + totalScore + '점</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">전적: ' + totalGameCnt + '전 ' + winCnt + '승 ' + loseCnt + '패</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">승률: ' + winRate + '%</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">등수: ' + totalRank + '등</li></ul>'
            }
          ]
        },
        {
          cols: [
            {
              view: 'button', type: 'form', label: '닫기', hotkey: 'esc',
              click: function () {
                webixMap.profile_window.close();
              }
            }
          ]
        }
      ]
    };

    webix.ui({
      view: 'window',
      id: 'player-info-window',
      head: '플레이어 정보',
      position: 'center',
      modal: true,
      width: 400,
      body: profileView
    }).show();

    webixMap.profile_window = $$('player-info-window');
  };

  _calculateWinRate = function (totalGameCnt, winCnt) {
    var winRate = 0;
    if (totalGameCnt > 0) {
      winRate = winCnt / totalGameCnt * 100;
    }
    return winRate.toFixed(2);
  };

  initModule = function (container) {
    stateMap.container = container;
    _getLoggedInPlayers(_createView);
  };

  return {
    initModule: initModule
  };
}());