/**
 * @author Hana Lee
 * @since 2016-01-17 19:35
 */
app.v_game_room = (function () {
  'use strict';

  var
    ON_UPDATE_GAME_ROOM_INFO = 'onUpdateGameRoomInfo',
    ON_GAME_START = 'onGameStart',
    ON_GAME_END = 'onGameEnd',
    ON_ANOTHER_PLAYER_INPUT_RESULT_INFO = 'onAnotherPlayerInputResultInfo',
    stateMap = {
      container : null,
      webix_events : [],
      subscribeObj : [],
      input_count : 0
    }, webixMap = {},
    _createView, _onOwnerChangeHandler, _resetWebixMap, _resetStateMap,
    _getSettingTemplate, _getTitleTemplate, _updateGameRoomTitle,
    initModule, destructor;

  _resetWebixMap = function () {
    webixMap = {};
  };

  _resetStateMap = function () {
    stateMap.webix_events.forEach(function (eventItem) {
      webix.detachEvent(eventItem);
    });

    stateMap.subscribeObj.forEach(function (subscribe) {
      subscribe.unsubscribe();
    });

    stateMap = {};
    stateMap.webix_events = [];
    stateMap.subscribeObj = [];
    stateMap.container = null;
  };

  _getSettingTemplate = function () {
    return [
      '입력지연 : ', '25초', ', ',
      '입력 : ', app.model.getGameRoom().setting.limitGuessInputCount, ', ',
      '갯수 : ', app.model.getGameRoom().setting.generationNumberCount, ', ',
      '오류 : ', app.model.getGameRoom().setting.limitWrongInputCount
    ];
  };

  _getTitleTemplate = function () {
    return [
      '[', app.model.getGameRoom().roomNumber, '번방] ', app.model.getGameRoom().name
    ];
  };

  _createView = function () {
    var gameRoomTitle, menuContainer, centerContainer,
      gameBoardContainer, gamePadContainer, playerProfileContainer, chatContainer,
      mainView;

    gameRoomTitle = {
      id : 'game-room-title',
      template : _getTitleTemplate().join('') + ' - ' + _getSettingTemplate().join(''),
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
        {height : 5},
        menuContainer,
        {height : 5},
        centerContainer
      ],
      on : {
        onDestruct : function () {
          _resetStateMap();
          _resetWebixMap();
        }
      }
    }];

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('game-room');
    webixMap.menu_container = $$('game-room-menu-container');
    webixMap.board_container = $$('game-room-board-container');
    webixMap.pad_container = $$('game-room-pad-container');
    webixMap.profile_container = $$('game-room-player-profile-container');
    webixMap.chat_container = $$('game-room-chat-container');
    webixMap.title = $$('game-room-title');

    app.v_game_room_menu.initModule(webixMap.menu_container);
    app.v_game_board.initModule(webixMap.board_container);
    app.v_game_pad.initModule(webixMap.pad_container);

    app.v_player_profile.configModule({
      height : 200,
      avatar_width : 130,
      player_model : app.model.getPlayer()
    });
    app.v_player_profile.initModule(webixMap.profile_container);

    app.v_chat.configModule({
      chat_height : 450,
      data_url : '/chat/gameroom/' + app.model.getGameRoom().id,
      system_message_list : [
        '[' + app.model.getGameRoom().roomNumber + '번 ' + app.model.getGameRoom().name + '] 방에 입장 하셨습니다',
        '즐거운 게임 즐기시기 바랍니다 :-)'
      ]
    });
    app.v_chat.initModule(webixMap.chat_container);
  };

  _onOwnerChangeHandler = function (updatedGameRoom) {
    if (updatedGameRoom.owner.id === app.model.getPlayer().id) {
      webix.alert({
        title : '정보',
        ok : '확인',
        text : '지금부터 [<b>' + app.model.getPlayer().nickname + '</b>] 님이<br/>방장입니다'
      });

      $$('owner-change').show();
    } else {
      $$('owner-change').hide();
    }
  };

  _updateGameRoomTitle = function () {
    webixMap.title.define({
      template : _getTitleTemplate().join('') + ' - ' + _getSettingTemplate().join('')
    });
    webixMap.title.refresh();
  };

  initModule = function (container) {
    var subscribeUrl = '/topic/gameroom/' + app.model.getGameRoom().id + '/updated';

    stateMap.container = container;

    stateMap.subscribeObj.push(
      app.v_shell.getStompClient().subscribe(subscribeUrl, function (response) {
        var responseBody = JSON.parse(response.body),
          updatedGameRoom = responseBody.object,
          operation = responseBody.objectOperation;

        if (updatedGameRoom === undefined || updatedGameRoom === null
          || updatedGameRoom.roomNumber === undefined || updatedGameRoom.roomNumber === null) {
          updatedGameRoom = responseBody.data;
          operation = responseBody.operation;
        }

        if (app.model.getGameRoom().owner.email !== updatedGameRoom.owner.email) {
          _onOwnerChangeHandler(updatedGameRoom);
        }

        app.model.setGameRoom(updatedGameRoom);

        _updateGameRoomTitle();
        
        if (updatedGameRoom.status === app.const.status.GAME_END) {
          webixMap.profile_container.getChildViews()[0].destructor();

          app.v_shell.playerInfoUpdate(function () {
            app.v_player_profile.configModule({
              height : 200,
              avatar_width : 130,
              player_model : app.model.getPlayer()
            });
            app.v_player_profile.initModule(webixMap.profile_container);
          });
        }

        webix.callEvent(ON_UPDATE_GAME_ROOM_INFO, [operation]);
      }, {})
    );

    subscribeUrl = '/topic/gameroom/' + app.model.getGameRoom().id + '/player-ready-status-updated';
    stateMap.subscribeObj.push(
      app.v_shell.getStompClient().subscribe(subscribeUrl, function (response) {
        var responseBody = JSON.parse(response.body),
          operation = responseBody.operation;

        app.model.setGameRoom(responseBody.data);

        webix.callEvent(ON_UPDATE_GAME_ROOM_INFO, [operation]);
      }, {})
    );

    subscribeUrl = '/topic/gameroom/' + app.model.getGameRoom().id + '/another-player-input-result-info';
    stateMap.subscribeObj.push(
      app.v_shell.getStompClient().subscribe(subscribeUrl, function (response) {
        var responseBody = JSON.parse(response.body), operation, anotherPlayerInfo, message;

        if (app.model.getPlayer().gameRole === app.const.gameRole.DEFENDER) {
          anotherPlayerInfo = responseBody.object;
          operation = responseBody.objectOperation;
          message = responseBody.data;

          webix.callEvent(ON_ANOTHER_PLAYER_INPUT_RESULT_INFO, [anotherPlayerInfo, message, operation]);
        }
      }, {})
    );

    _createView();
  };

  destructor = function () {
    webixMap.main_view.destructor();
  };

  return {
    ON_UPDATE_GAME_ROOM_INFO : ON_UPDATE_GAME_ROOM_INFO,
    ON_GAME_START : ON_GAME_START,
    ON_GAME_END : ON_GAME_END,
    ON_ANOTHER_PLAYER_INPUT_RESULT_INFO : ON_ANOTHER_PLAYER_INPUT_RESULT_INFO,
    initModule : initModule,
    destructor : destructor
  };
}());