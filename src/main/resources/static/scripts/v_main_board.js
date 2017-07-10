/**
 * @author Hana Lee
 * @since 2016-01-17 19:35
 */
app.v_main_board = (function () {
  'use strict';

  var
    stateMap = {
      container : null
    }, webixMap = {},
    _resetWebixMap, _resetStateMap,
    _createView,
    initModule, destructor;

  _resetWebixMap = function () {
    webixMap = {};
  };

  _resetStateMap = function () {
    stateMap = {};
    stateMap.container = null;
  };

  _createView = function () {
    var mainTitle, playerListContainer, mainMenuContainer, playerProfileContainer,
      gameRoomListContainer, mainChatContainer,
      mainContentLeftContainer, mainContentRightContainer, mainContentContainer, mainView;

    mainTitle = {id : 'main-title', template : '야구게임 v0.1', type : 'header'};

    mainMenuContainer = {
      id : 'main-menu-container',
      view : 'layout',
      rows : []
    };

    playerProfileContainer = {
      id : 'player-profile-container',
      view : 'layout',
      rows : []
    };

    playerListContainer = {
      id : 'player-list-container',
      view : 'layout',
      width : 300,
      margin : 10,
      rows : []
    };

    gameRoomListContainer = {
      id : 'game-room-list-container',
      view : 'layout',
      css : 'game_room_list_container',
      rows : []
    };

    mainChatContainer = {
      id : 'main-chat-container',
      view : 'layout',
      rows : []
    };

    mainContentLeftContainer = {
      template : '게임룸 컨테이너',
      id : 'main-content-left-container',
      margin : 10,
      rows : [
        gameRoomListContainer,
        {view : 'resizer'},
        mainChatContainer
      ]
    };

    mainContentRightContainer = {
      id : 'main-content-right-container',
      view : 'layout',
      width : 300,
      rows : [
        playerProfileContainer,
        {height : 10},
        playerListContainer
      ]
    };

    mainContentContainer = {
      cols : [
        mainContentLeftContainer,
        {width : 10},
        mainContentRightContainer
      ]
    };

    mainView = [{
      id : 'main-board',
      view : 'layout',
      rows : [
        mainTitle,
        {height : 5},
        mainMenuContainer,
        {height : 5},
        mainContentContainer
      ],
      on : {
        onDestruct : function () {
          _resetWebixMap();
          _resetStateMap();
        }
      }
    }];
    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.main_view = $$('main-board');
    webixMap.main_title = $$('main-title');
    webixMap.main_content = $$('main-content-container');
    webixMap.player_profile_container = $$('player-profile-container');
    webixMap.game_room_list_container = $$('game-room-list-container');
    webixMap.main_chat_container = $$('main-chat-container');
    webixMap.main_menu_container = $$('main-menu-container');
    webixMap.player_list_container = $$('player-list-container');

    app.v_game_list.initModule(webixMap.game_room_list_container);

    app.v_chat.configModule({
      chat_height : 240,
      chat_list_height : 'auto',
      proxy_name : 'stomp',
      data_url : '/chat',
      system_message_list : [
        '숫자 야구 게임에 오신걸 환영합니다 :-)',
        '건전한 채팅 문화는 우리의 미래 입니다 ㅋㅋㅋ'
      ]
    });
    app.v_chat.initModule(webixMap.main_chat_container);
    app.v_main_menu.initModule(webixMap.main_menu_container);

    app.v_player_profile.configModule({
      player_model : app.model.getPlayer()
    });
    app.v_player_profile.initModule(webixMap.player_profile_container);
    app.v_player_list.initModule(webixMap.player_list_container);
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
    destructor : destructor
  };
}());