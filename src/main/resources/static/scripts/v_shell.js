/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
 */
/**
 * @namespace app.v_shell
 *
 * @description 게임 전반을 다루는 컨트롤러
 */
app.v_shell = (function () {
  'use strict';

  var
    ON_PLAYER_INFO_UPDATED = 'onPlayerInfoUpdated',
    ON_WEB_SOCKET_ERROR = 'onWebSocketError',
    configMap = {
      width : 1024,
      height : 750
    }, stateMap = {
      loggedIn : false,
      container : null,
      stomp_client : null
    }, webixMap = {},
    showSignUp, showLogin, createGameRoom, showMainBoard, logout, getStompClient, joinGameRoom, joinRandomGameRoom,
    leaveGameRoom, leaveAndGameRoomDelete, playerInfoUpdate, playerGameOverNotification,
    _getLoggedInPlayerInfo, _initStompClient, _createView, _loginNotification, _logoutNotification, _showGameRoom,
    _updatePlayerHandler, _socketErrorHandler,
    gameEndNotification, showWaitDialog, hideWaitDialog, initModule;

  showSignUp = function () {
    $$('login-container').destructor();
    app.v_sign_up.initModule(stateMap.container);
  };

  showLogin = function () {
    $$('sign-up-container').destructor();
    app.v_login.initModule(stateMap.container);
  };

  joinGameRoom = function (selectedRoom, selectedGameRole) {
    app.v_main_board.destructor();

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).patch('gameroom/join/' + selectedRoom.id, JSON.stringify({gameRole : selectedGameRole}), {
      error : function (text) {
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (text) {
        var joinedGameRoom = JSON.parse(text);
        app.model.getPlayer().gameRole = selectedGameRole;
        app.model.getPlayer().gameRoomId = joinedGameRoom.id;
        app.model.setGameRoom(joinedGameRoom);
        _showGameRoom();
      }
    });
  };

  joinRandomGameRoom = function (gameRoomModelList) {
    var url, data, dataString, randomGameRoomIndex, selectedGameRoom;

    app.v_main_board.destructor();

    data = {
      gameRole : app.const.gameRole.ATTACKER
    };
    dataString = JSON.stringify(data);

    randomGameRoomIndex = Math.floor(Math.random() * gameRoomModelList.length);
    selectedGameRoom = gameRoomModelList[randomGameRoomIndex];

    url = 'gameroom/join/' + selectedGameRoom.id;

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).patch(url, dataString, {
      error : function (text) {
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (text) {
        var joinedGameRoom = JSON.parse(text);
        app.model.getPlayer().gameRole = data.gameRole;
        app.model.getPlayer().gameRoomId = joinedGameRoom.id;
        app.model.setGameRoom(joinedGameRoom);
        _showGameRoom();
      }
    });
  };

  createGameRoom = function (data) {
    app.v_main_board.destructor();

    var sendData = {
      name : data.name,
      gameRole : data.gameRole,
      setting : {
        generationNumberCount : data.generationNumberCount,
        limitGuessInputCount : data.limitGuessInputCount,
        limitWrongInputCount : data.limitWrongInputCount
      }
    };

    sendData = JSON.stringify(sendData);

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).post('gameroom', sendData, {
      error : function (text) {
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (text) {
        var gameRoomJson = JSON.parse(text);
        app.model.getPlayer().gameRole = gameRoomJson.owner.gameRole;
        app.model.getPlayer().gameRoomId = gameRoomJson.id;
        app.model.setGameRoom(gameRoomJson);
        _showGameRoom();
      }
    });
  };

  _showGameRoom = function () {
    location.hash = '#gr_' + app.model.getGameRoom().id;
    app.v_game_room.initModule(webixMap.top);
  };

  showMainBoard = function (removeContainer, email) {
    app.model.getPlayer().gameRole = null;
    app.model.getPlayer().status = null;
    app.model.getPlayer().gameRoomId = null;
    app.model.getPlayer().wrongCount = 0;
    app.model.setGameRoom(null);
    location.hash = '';

    if (removeContainer === 'login-container') {
      $$('login-container').destructor();
      $('#main-container').html('');
      stateMap.loggedIn = true;
      _initStompClient(function () {
        _loginNotification(email);
        initModule(stateMap.container);
      });
    } else {
      //app.v_game_room.destructor();
      app.v_main_board.initModule(webixMap.top);
    }
  };

  leaveGameRoom = function (gameRoomModel) {
    app.v_game_room.destructor();

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).patch('gameroom/leave/' + gameRoomModel.id, {}, {
      error : function (text) {
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (/*text*/) {
        showMainBoard();
      }
    });
  };

  leaveAndGameRoomDelete = function (gameRoomModel) {
    app.v_game_room.destructor();

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).del('gameroom/leave/' + gameRoomModel.id, {}, {
      error : function (text) {
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (/*text*/) {
        showMainBoard();
      }
    });
  };

  logout = function () {
    stateMap.loggedIn = false;
    
    _logoutNotification(app.model.getPlayer().email);

    webix.ajax().post('logout', {
      error : function (text) {
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function () {
        webixMap.main_view.destructor();

        if (stateMap.stomp_client) {
          stateMap.stomp_client.disconnect();
          stateMap.stomp_client = null;
        }
        $('#main-container').html('');

        app.model.setPlayer(null);
        app.model.setGameRoom(null);
        app.model.clearGameRoomList();

        app.initModule(stateMap.container);
      }
    });
  };

  _getLoggedInPlayerInfo = function (callback) {
    var serverResponse;
    webix.ajax().get('player', {
      error : function () {
        app.model.setPlayer(null);
        stateMap.loggedIn = false;
        if (stateMap.stomp_client) {
          stateMap.stomp_client.disconnect();
        }
        app.v_login.initModule(stateMap.container);
      },
      success : function (text) {
        serverResponse = JSON.parse(text);
        app.model.setPlayer(serverResponse);
        stateMap.loggedIn = true;

        if (!stateMap.stomp_client) {
          _initStompClient(callback);
        } else {
          callback();
        }
      }
    });
  };

  _initStompClient = function (callback) {
    var api_socket = new SockJS('/bbg/sock'), login = '', passcode = '';
    stateMap.stomp_client = Stomp.over(api_socket);
    stateMap.stomp_client.connect(login, passcode,
      function () {
        // connect 완료 시 error subscribe, global 에러 처리.
        if (callback) {
          callback();
        }
        stateMap.stomp_client.subscribe('/topic/player/updated', _updatePlayerHandler, {});
        stateMap.stomp_client.subscribe('/user/topic/player/updated', _updatePlayerHandler, {});
        stateMap.stomp_client.subscribe('/user/topic/errors', _socketErrorHandler, {});
      },
      function (error) {
        if (error && error.toLowerCase().indexOf('lost connection') !== -1 && stateMap.loggedIn) {
          logout();
        }
      }
    );
  };

  _socketErrorHandler = function (response) {
    var responseJson = JSON.parse(response.body);
    if (responseJson.errorMessage) {
      webix.alert({
        title : '오류',
        ok : '확인',
        text : responseJson.errorMessage
      });
    }

    webix.callEvent(ON_WEB_SOCKET_ERROR, [responseJson]);
  };

  _updatePlayerHandler = function (response) {
    var responseJson = JSON.parse(response.body),
      player = responseJson.object, operation = responseJson.objectOperation;

    if (player === undefined || player === null
      || player.email === undefined || player.email === null) {
      player = responseJson.data;
      operation = responseJson.operation;
    }

    if (player.id === app.model.getPlayer().id) {
      app.model.setPlayer(player);
    }

    webix.callEvent(ON_PLAYER_INFO_UPDATED, [operation, player]);
  };

  _createView = function () {
    if (stateMap.loggedIn) {
      var mainView = {
        id : 'main-layout',
        type : 'space',
        css : 'main-layout',
        view : 'layout',
        height : configMap.height,
        width : configMap.width,
        rows : [{template : 'main-content'}]
      };

      webixMap.top = webix.ui(mainView, stateMap.container);
      webixMap.main_view = $$('main-layout');
      webix.extend(webixMap.top, webix.ProgressBar);

      app.v_main_board.initModule(webixMap.top);
      //app.v_game_room.initModule(webixMap.top);
    }
  };

  _loginNotification = function (email) {
    var header = {}, data = {email : email, operation : 'insert'};
    stateMap.stomp_client.send('/app/player/login', header, JSON.stringify(data));
  };

  _logoutNotification = function (email) {
    var header = {}, data = {email : email, operation : 'delete'};
    stateMap.stomp_client.send('/app/player/logout', header, JSON.stringify(data));
  };

  playerInfoUpdate = function (callback) {
    webix.ajax().get('player', {}, {
      error : function (text) {
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson
        });
      },
      success : function (text) {
        var textJson = JSON.parse(text);
        if (textJson) {
          app.model.setPlayer(textJson);
        }
        if (callback && typeof callback === 'function') {
          callback();
        }
      }
    });
  };

  playerGameOverNotification = function (data) {
    var sendUrl, header = {};
    sendUrl = '/app/gameroom/' + app.model.getGameRoom().id + '/player-game-over-notification';
    stateMap.stomp_client.send(sendUrl, header, JSON.stringify(data));
  };

  gameEndNotification = function () {
    var sendUrl, header = {}, data = {};
    sendUrl = '/app/gameroom/' + app.model.getGameRoom().id + '/game-end-notification';
    stateMap.stomp_client.send(sendUrl, header, JSON.stringify(data));
  };

  getStompClient = function () {
    return stateMap.stomp_client;
  };

  showWaitDialog = function () {
    webixMap.top.disable();
    webixMap.top.showProgress();
  };

  hideWaitDialog = function () {
    webixMap.top.enable();
    webixMap.top.hideProgress();
  };

  initModule = function (container) {
    stateMap.container = container;
    _getLoggedInPlayerInfo(_createView);
  };

  return {
    initModule : initModule,
    showSignUp : showSignUp,
    showLogin : showLogin,
    showMainBoard : showMainBoard,
    logout : logout,
    getStompClient : getStompClient,
    joinGameRoom : joinGameRoom,
    joinRandomGameRoom : joinRandomGameRoom,
    createGameRoom : createGameRoom,
    leaveGameRoom : leaveGameRoom,
    leaveAndGameRoomDelete : leaveAndGameRoomDelete,
    playerInfoUpdate : playerInfoUpdate,
    playerGameOverNotification : playerGameOverNotification,
    gameEndNotification : gameEndNotification,
    showWaitDialog : showWaitDialog,
    hideWaitDialog : hideWaitDialog,
    ON_PLAYER_INFO_UPDATED : ON_PLAYER_INFO_UPDATED,
    ON_WEB_SOCKET_ERROR : ON_WEB_SOCKET_ERROR
  };
}());