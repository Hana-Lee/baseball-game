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

  var
    ON_UPDATE_GAME_ROOM_INFO = 'onUpdateGameRoomInfo',
    ON_GAME_START = 'onGameStart',
    ON_GAME_END = 'onGameEnd',
    configMap = {
      settable_map : {
        game_room_model : null
      },
      game_room_model : null
    },
    stateMap = {
      container : null,
      webix_events : [],
      subscribeObj : [],
      input_count : 0
    }, webixMap = {},
    _createView, _onOwnerChangeHandler, _resetWebixMap, _resetConfigMap, _resetStateMap,
    _getSettingTemplate, _getTitleTemplate, _updateGameRoomTitle,
    configModule, initModule, destructor, getGameRoomModel;

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

  _resetConfigMap = function () {
    configMap.game_room_model = null;
  };

  _getSettingTemplate = function () {
    return [
      '입력지연 : ', '25초', ', ',
      '입력 : ', configMap.game_room_model.setting.limitGuessInputCount, ', ',
      '갯수 : ', configMap.game_room_model.setting.generationNumberCount, ', ',
      '오류 : ', configMap.game_room_model.setting.limitWrongInputCount
    ];
  };

  _getTitleTemplate = function () {
    return [
      '[', configMap.game_room_model.roomNumber, '번방] ', configMap.game_room_model.name
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
          _resetConfigMap();
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
      player_model : app.m_player.getInfo()
    });
    app.v_player_profile.initModule(webixMap.profile_container);

    app.v_chat.configModule({
      chat_height : 450,
      data_url : '/chat/gameroom/' + configMap.game_room_model.id,
      system_message_list : [
        '[' + configMap.game_room_model.roomNumber + '번 ' + configMap.game_room_model.name + '] 방에 입장 하셨습니다',
        '즐거운 게임 즐기시기 바랍니다 :-)'
      ]
    });
    app.v_chat.initModule(webixMap.chat_container);
  };

  configModule = function (input_map) {
    app.utils.setConfigMap({
      input_map : input_map,
      settable_map : configMap.settable_map,
      config_map : configMap
    });
  };

  _onOwnerChangeHandler = function (updatedGameRoom) {
    if (updatedGameRoom.owner.id === app.m_player.getInfo().id) {
      webix.alert({
        title : '정보',
        ok : '확인',
        text : '지금부터 [<b>' + app.m_player.getInfo().nickname + '</b>] 님이<br/>방장입니다'
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
    var subscribeUrl = '/topic/gameroom/' + configMap.game_room_model.id + '/updated';

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

        if (configMap.game_room_model.owner.email !== updatedGameRoom.owner.email) {
          _onOwnerChangeHandler(updatedGameRoom);
        }

        configMap.game_room_model = updatedGameRoom;

        _updateGameRoomTitle();

        webix.callEvent(ON_UPDATE_GAME_ROOM_INFO, [operation]);
      }, {})
    );

    subscribeUrl = '/topic/gameroom/' + configMap.game_room_model.id + '/player-ready-status-updated';
    stateMap.subscribeObj.push(
      app.v_shell.getStompClient().subscribe(subscribeUrl, function (response) {
        var responseBody = JSON.parse(response.body),
          operation = responseBody.operation;

        configMap.game_room_model = responseBody.data;

        webix.callEvent(ON_UPDATE_GAME_ROOM_INFO, [operation]);
      }, {})
    );
    _createView();
  };

  destructor = function () {
    webixMap.main_view.destructor();
  };

  getGameRoomModel = function () {
    return configMap.game_room_model;
  };

  return {
    ON_UPDATE_GAME_ROOM_INFO : ON_UPDATE_GAME_ROOM_INFO,
    ON_GAME_START : ON_GAME_START,
    ON_GAME_END : ON_GAME_END,
    initModule : initModule,
    destructor : destructor,
    configModule : configModule,
    getGameRoomModel : getGameRoomModel
  };
}());