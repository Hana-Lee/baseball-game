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

  var stateMap = {
      container : null,
      player : null
    }, webixMap = {},
    _createView, _calculateWinRate, initModule;

  _createView = function () {
    var mainView, email, nickname, avatarImagePath, level, totalScore, totalGameCnt, winCnt, loseCnt, winRate, totalRank;

    email = stateMap.player.email;
    nickname = stateMap.player.nickname;
    avatarImagePath = stateMap.player.avatar.imagePath;
    level = stateMap.player.level.value;
    totalScore = stateMap.player.totalScore.value;
    totalGameCnt = stateMap.player.matchRecord.totalGame.count;
    winCnt = stateMap.player.matchRecord.win.count;
    loseCnt = stateMap.player.matchRecord.lose.count;
    winRate = _calculateWinRate(totalGameCnt, winCnt);
    totalRank = stateMap.player.totalRank.value;

    mainView = {
      id : 'player-profile-' + stateMap.player.id,
      height : 200,
      rows : [
        {template : nickname + '님 ( ' + email + ' )', type : 'header'},
        {
          cols : [
            {template : '<img src="' + avatarImagePath + '" height="100%" width="100%">', width : 130},
            {
              template : '<ul style="list-style:none;padding:0;margin:0;">' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">레벨: ' + level + '</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">총점: ' + totalScore + '점</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">전적: ' + totalGameCnt + '전 ' + winCnt + '승 ' + loseCnt + '패</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">승률: ' + winRate + '%</li>' +
              '<li style="border-bottom: 1px solid lightgray;margin-bottom: 10px;">등수: ' + totalRank + '등</li></ul>'
            }
          ]
        }
      ]
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
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
    stateMap.player = app.m_player.getInfo();
    _createView();
  };

  return {
    initModule : initModule
  };
}());