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
    stateMap = {
      container : null,
      events : [],
      player_container_list : []
    }, webixMap = {},
    _createView, _makeJoinedPlayersProfile, _resetJoinedPlayersProfile,
    _resetWebixMap, _resetStateMap,
    _onUpdateGameRoomInfoHandler,
    initModule;

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
  };

  _onUpdateGameRoomInfoHandler = function () {
    _resetJoinedPlayersProfile();
    _makeJoinedPlayersProfile();
  };

  _createView = function () {
    var leftTopPlayer, rightTopPlayer, leftBottomPlayer, rightBottomPlayer,  mainView;

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
        rows : [{template : '진행상황', type : 'header'}, {
          view : 'list',
          template : '#message#',
          data : [
            {message : '환영합니다.'},
            {message : '2/5 대기중입니다'},
            {message : '준비를 눌러주세요'},
            {message : '1번째 턴!!!'},
            {message : '숫자를 입력해주세요...'},
            {message : '1S 0B'},
            {message : '다른유저의 입력을 기다립니다'}
          ],
          ready : function () {
            this.attachEvent('onAfterAdd', function (id) {
              webix.delay(function () {
                this.showItem(id);
              }, this);
            });
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

    stateMap.player_container_list.push(webixMap.left_top_player);
    stateMap.player_container_list.push(webixMap.right_top_player);
    stateMap.player_container_list.push(webixMap.left_bottom_player);
    stateMap.player_container_list.push(webixMap.right_bottom_player);

    _makeJoinedPlayersProfile();
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

  _makeJoinedPlayersProfile = function () {
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

    stateMap.events.push(webix.attachEvent(app.v_game_room.EVENT_UPDATE_GAME_ROOM_INFO, _onUpdateGameRoomInfoHandler));

    _createView();
  };

  return {
    initModule : initModule
  };
}());