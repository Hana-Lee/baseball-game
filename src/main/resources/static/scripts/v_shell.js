/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
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
/*global $, app, webix, $$, SockJS, Stomp */

app.v_shell = (function () {
  'use strict';

  var configMap = {
      width : 1024,
      height : 750
    }, stateMap = {
      loggedIn : false,
      container : null,
      stomp_client : null
    }, webixMap = {},
    showSignUp, showLogin, createGameRoom, showMainBoard, logout, getStompClient, joinGameRoom, joinRandomGameRoom,
    leaveGameRoom, leaveAndGameRoomDelete,
    _getLoggedInPlayerInfo, _initStompClient, _createView, _loginNotification, _logoutNotification, _showGameRoom,
    initModule;

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
        app.m_player.getInfo().gameRole = selectedGameRole;
        _showGameRoom(joinedGameRoom);
      }
    });
  };

  joinRandomGameRoom = function (gameRoomModelList) {
    var url, data, dataString, randomGameRoomIndex, selectedGameRoom;

    app.v_main_board.destructor();

    data = {
      gameRole : 'ATTACKER'
    };
    dataString = JSON.stringify(data);

    randomGameRoomIndex = Math.floor(Math.random() * gameRoomModelList.length);
    selectedGameRoom = gameRoomModelList[randomGameRoomIndex];

    url = 'gameroom/join/' + selectedGameRoom.id;

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).patch(url, dataString, {
      error : function (text) {
        console.log(text);
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (text) {
        var joinedGameRoom = JSON.parse(text);
        app.m_player.getInfo().gameRole = data.gameRole;

        _showGameRoom(joinedGameRoom);
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
        console.log(text);
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (text) {
        var gameRoomJson = JSON.parse(text);
        app.m_player.getInfo().gameRole = gameRoomJson.owner.gameRole;
        _showGameRoom(gameRoomJson);
      }
    });
  };

  _showGameRoom = function (gameRoomModel) {
    app.v_game_room.configModule({
      game_room_model : gameRoomModel
    });
    app.v_game_room.initModule(webixMap.top);
  };

  showMainBoard = function (removeContainer, email) {
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
        console.log(text);
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (/*text*/) {
        app.m_player.getInfo().gameRole = null;
        app.v_main_board.initModule(webixMap.top);
      }
    });
  };

  leaveAndGameRoomDelete = function (gameRoomModel) {
    app.v_game_room.destructor();

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).del('gameroom/leave/' + gameRoomModel.id, {}, {
      error : function (text) {
        console.log(text);
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });
      },
      success : function (/*text*/) {
        app.m_player.getInfo().gameRole = null;
        app.v_main_board.initModule(webixMap.top);
      }
    });
  };

  logout = function () {
    _logoutNotification(app.m_player.getInfo().email);

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
        $$('main-layout').destructor();

        //_logoutNotification(app.m_player.getInfo().email);

        stateMap.loggedIn = false;
        stateMap.stomp_client.disconnect();
        stateMap.stomp_client = null;
        $('#main-container').html('');

        app.m_player.reset();

        app.initModule(stateMap.container);
      }
    });
  };

  _getLoggedInPlayerInfo = function (callback) {
    var serverResponse = '';
    webix.ajax().sync().get('player', {
      error : function (text) {
        serverResponse = JSON.parse(text);
        app.m_player.initModule(null);
        stateMap.loggedIn = false;
        if (stateMap.stomp_client) {
          stateMap.stomp_client.disconnect();
        }
        app.v_login.initModule(stateMap.container);
      },
      success : function (text) {
        serverResponse = JSON.parse(text);
        app.m_player.initModule(serverResponse);
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
      function (frame) {
        // connect 완료 시 error subscribe, global 에러 처리.
        console.log(frame);

        if (callback) {
          callback();
        }
        stateMap.stomp_client.subscribe('/topic/player/updated', function (response) {
          console.log('player updated', response);
          var player = JSON.parse(response).data;
          app.m_player.initModule(player);
        }, {});
      },
      function (error) {
        console.log(error);
      }
    );
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
    //stateMap.stomp_client.send('/app/player/logout', header, {});
  };

  getStompClient = function () {
    return stateMap.stomp_client;
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
    leaveAndGameRoomDelete : leaveAndGameRoomDelete
  };
}());