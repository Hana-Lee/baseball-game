/**
 * @author Hana Lee
 * @since 2016-01-17 19:35
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

app.v_game_room = (function () {
  'use strict';

  var stateMap = {
      container : null,
      webix_events : []
    }, webixMap = {}, _createView,
    setGameRoom, initModule, destructor;

  _createView = function () {
    var gameRoomTitle, menuContainer, centerContainer,
      gameBoardContainer, gamePadContainer, playerProfileContainer, chatContainer,
      mainView;
    gameRoomTitle = {
      template : '[' + stateMap.game_room_info.id + '번방] ' + stateMap.game_room_info.name,
      type : 'header'
    };

    menuContainer = {
      id : 'game-room-menu-container',
      view : 'layout',
      rows : []
    };

    gameBoardContainer = {
      id : 'game-room-board-container',
      view : 'layout',
      rows : []
    };

    gamePadContainer = {
      id : 'game-room-pad-container',
      view : 'layout',
      rows : []
    };

    playerProfileContainer = {
      id : 'game-room-player-profile-container',
      view : 'layout',
      rows : []
    };

    chatContainer = {
      id : 'game-room-chat-container',
      view : 'layout',
      rows : []
    };

    centerContainer = {
      view : 'layout',
      cols : [
        {
          view : 'layout',
          margin : 10,
          rows : [
            gameBoardContainer,
            gamePadContainer
          ]
        },
        {width : 10},
        {
          view : 'layout',
          width : 300,
          margin : 10,
          rows : [
            playerProfileContainer,
            chatContainer
          ]
        }
      ]
    };

    mainView = [{
      id : 'game-room',
      view : 'layout',
      rows : [
        gameRoomTitle,
        menuContainer,
        centerContainer
      ]
    }];

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('game-room');
    webixMap.menu_container = $$('game-room-menu-container');
    webixMap.board_container = $$('game-room-board-container');
    webixMap.pad_container = $$('game-room-pad-container');
    webixMap.profile_container = $$('game-room-player-profile-container');
    webixMap.chat_container = $$('game-room-chat-container');

    webixMap.main_view.attachEvent('onDestruct', function () {
      stateMap.webix_events.forEach(function (eventItem) {
        webix.detachEvent(eventItem);
      });
      stateMap.webix_events = [];
    });
    stateMap.webix_events.push(webix.attachEvent('onLeaveGameRoom', function () {
      webix.ajax().headers({
        'Content-Type' : 'application/json'
      }).post('gameroom/leave/' + stateMap.game_room_info.id, {}, {
        error : function (text) {
          console.log(text);
        },
        success : function (text) {
          console.log(text);
          var from = 'game-room';
          app.v_shell.showMainBoard(from);
        }
      });
    }));

    app.v_game_room_menu.initModule(webixMap.menu_container);
    app.v_game_board.initModule(webixMap.board_container);
    app.v_game_pad.initModule(webixMap.pad_container);
    app.v_player_profile.initModule(webixMap.profile_container);

    app.v_chat.configModule({
      chat_height : 450,
      data_url : '/chat/gameroom',
      system_message_list : [
        '[' + stateMap.game_room_info.id + '번 ' + stateMap.game_room_info.name + '] 방에 입장 하셨습니다',
        '즐거운 게임 즐기시기 바랍니다 :-)'
      ],
      player_model : app.m_player.getInfo()
    });
    app.v_chat.initModule(webixMap.chat_container);
  };

  setGameRoom = function (gameRoomInfo) {
    stateMap.game_room_info = gameRoomInfo;
  };

  initModule = function (container) {
    stateMap.container = container;
    _createView();
  };

  destructor = function () {
    webixMap.main_view.destructor();
  };

  return {
    initModule : initModule,
    destructor : destructor,
    setGameRoom : setGameRoom
  };
}());