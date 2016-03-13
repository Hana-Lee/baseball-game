/**
 * @author Hana Lee
 * @since 2016-01-19 20:16
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

app.v_game_board = (function () {
  'use strict';

  var
    configMap = {
      welcome_messages : [
        {message : '환영합니다.', type : 'normal'},
        {message : '시작하려면 준비를 눌러주세요', type : 'focus'}
      ]
    },
    stateMap = {
      container : null,
      events : [],
      player_container_list : [],
      proxy : null
    }, webixMap = {},
    _createView, _makePlayersProfile, _resetJoinedPlayersProfile,
    _resetWebixMap, _resetStateMap,
    _updateGameRoomInfoHandler, _playerReadyHandler,
    initModule;

  _playerReadyHandler = function (operation, readyPlayer) {
    if (operation === 'ready') {
      if (readyPlayer.status === 'READY_DONE') {
        webixMap.game_progress_board.add();
      }
    }
  };

  _resetWebixMap = function () {
    webixMap = {};
  };

  _resetStateMap = function () {
    stateMap.events.forEach(function (event_item) {
      webix.detachEvent(event_item);
    });

    stateMap = {};
    stateMap.container = null;
    stateMap.events = [];
    stateMap.player_container_list = [];
    stateMap.proxy = null;
  };

  _updateGameRoomInfoHandler = function () {
    if (app.v_game_room.getGameRoomModel().status === 'RUNNING') {
      webixMap.game_progress_board.add({
        message : '게임이 시작 되었습니다', type : 'alert'
      });
    }
    _resetJoinedPlayersProfile();
    _makePlayersProfile();
  };

  _createView = function () {
    var leftTopPlayer, rightTopPlayer, leftBottomPlayer, rightBottomPlayer, mainView;

    leftTopPlayer = {
      id : 'left-top-player',
      css : 'player_profile',
      rows : [{template : '플레이어', type : 'header'}, {template : '대기중..'}]
    };

    rightTopPlayer = {
      id : 'right-top-player',
      css : 'player_profile',
      rows : [{template : '플레이어', type : 'header'}, {template : '대기중..'}]
    };

    leftBottomPlayer = {
      id : 'left-bottom-player',
      css : 'player_profile',
      rows : [{template : '플레이어', type : 'header'}, {template : '대기중..'}]
    };

    rightBottomPlayer = {
      id : 'right-bottom-player',
      css : 'player_profile',
      rows : [{template : '플레이어', type : 'header'}, {template : '대기중..'}]
    };

    mainView = {
      id : 'board-container',
      css : 'board_container',
      cols : [{
        id : 'left-player-container', width : 220,
        rows : [{
          rows : [leftTopPlayer]
        }, {
          height : 5
        }, {
          rows : [leftBottomPlayer]
        }]
      }, {
        width : 5
      }, {
        id : 'game-progress-container',
        rows : [{
          template : '진행상황', type : 'header'
        }, {
          id : 'game-progress-board',
          css : 'game_progress_board',
          view : 'list',
          select : false,
          template : function (obj) {
            var className = obj.type || 'normal';
            return '<span class="' + className + '">' + obj.message + '</span>';
          },
          data : [],
          url : stateMap.proxy,
          on : {
            onAfterAdd : function (id) {
              webix.delay(function () {
                this.showItem(id);
              }, this);
            }
          }
        }]
      }, {
        width : 5
      }, {
        id : 'right-player-container', width : 220,
        rows : [{
          rows : [rightTopPlayer]
        }, {
          height : 5
        }, {
          rows : [rightBottomPlayer]
        }]
      }],
      on : {
        onDestruct : function () {
          _resetStateMap();
          _resetWebixMap();
        }
      }
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.left_player_container = $$('left-player-container');
    webixMap.right_player_container = $$('right-player-container');
    webixMap.left_top_player = $$('left-top-player');
    webixMap.left_bottom_player = $$('left-bottom-player');
    webixMap.right_top_player = $$('right-top-player');
    webixMap.right_bottom_player = $$('right-bottom-player');
    webixMap.game_progress_board = $$('game-progress-board');

    webix.dp(webixMap.game_progress_board).ignore(function () {
      configMap.welcome_messages.forEach(function (msgObj) {
        webixMap.game_progress_board.add(msgObj);
      });
    });

    stateMap.player_container_list.push(webixMap.left_top_player);
    stateMap.player_container_list.push(webixMap.right_top_player);
    stateMap.player_container_list.push(webixMap.left_bottom_player);
    stateMap.player_container_list.push(webixMap.right_bottom_player);

    _makePlayersProfile();
  };

  _resetJoinedPlayersProfile = function () {
    var template;
    stateMap.player_container_list.forEach(function (profileContainer) {
      profileContainer.getChildViews().forEach(function (childView) {
        childView.destructor();
      });
      template = [{template : '플레이어', type : 'header'}, {template : '대기중..'}];
      webix.ui(template, profileContainer);
    });
  };

  _makePlayersProfile = function () {
    var playerList, playerListWithoutCurrentPlayer = [], owner, playerListLength, isOwner, i;
    playerList = app.v_game_room.getGameRoomModel().players;
    playerList.forEach(function (player) {
      if (player.id !== app.m_player.getInfo().id) {
        playerListWithoutCurrentPlayer.push(player);
      }
    });
    owner = app.v_game_room.getGameRoomModel().owner;
    playerListLength = playerListWithoutCurrentPlayer.length;
    for (i = 0; i < playerListLength; i++) {
      isOwner = playerListWithoutCurrentPlayer[i].id === owner.id;
      app.v_player_profile.configModule({
        height : 207,
        avatar_width : 80,
        player_model : playerListWithoutCurrentPlayer[i],
        show_email : false,
        custom_text : isOwner ? '_방장_' : null
      });
      app.v_player_profile.initModule(stateMap.player_container_list[i]);
    }
  };

  initModule = function (container) {
    stateMap.container = container;
    stateMap.proxy = webix.proxy('stomp', '/gameroom/' + app.v_game_room.getGameRoomModel().id + '/progress/updated');
    stateMap.proxy.clientId = app.utils.guid();

    stateMap.events.push(webix.attachEvent(app.v_game_room.ON_UPDATE_GAME_ROOM_INFO, _updateGameRoomInfoHandler));

    _createView();
  };

  return {
    initModule : initModule
  };
}());