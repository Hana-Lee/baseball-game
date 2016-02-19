/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
 */
/*jslint        browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true, todo    : true
 */
/*global $, app, webix, $$ */

app.v_shell = (function () {
  'use strict';

  var configMap = {
      width: 1024,
      height: 750
    }, stateMap = {
      loggedIn: false,
      container: null
    }, webixMap = {}, player = null, playerList = [],
    showSignUp, showLogin, showGameRoom, showMainBoard, logout,
    _getLoggedInPlayerInfo, _createView,
    initModule;

  showSignUp = function () {
    $$('login-container').destructor();
    app.v_sign_up.initModule(stateMap.container);
  };

  showLogin = function () {
    $$('sign-up-container').destructor();
    app.v_login.initModule(stateMap.container);
  };

  showGameRoom = function (/*roomId*/) {
    var mainLayout = $$('main-layout');
    mainLayout.removeView('main-board');
    mainLayout.addView(app.v_game_room.getView());
  };

  showMainBoard = function (removeContainer) {
    var mainLayout;
    if (removeContainer === 'login-container') {
      $$('login-container').destructor();
      $('#main-container').html('');
      stateMap.loggedIn = true;
      initModule(stateMap.container);
    } else {
      mainLayout = $$('main-layout');
      mainLayout.removeView('game-room');
      mainLayout.addView(app.v_main_board.getView());
    }
  };

  logout = function () {
    webix.ajax().post('logout', {
      error: function (text) {
        var textJson = JSON.parse(text);
        webix.alert({
          title: '오류',
          ok: '확인',
          text: textJson.message
        });

      },
      success: function () {
        $$('main-layout').destructor();
        stateMap.loggedIn = false;
        $('#main-container').html('');
        //initModule(stateMap.container);
        app.initModule(stateMap.container);
      }
    });
  };

  _getLoggedInPlayerInfo = function (callback) {
    var serverResponse = '';
    webix.ajax().sync().get('player', {
      error: function (text) {
        serverResponse = JSON.parse(text);
        app.m_player.initModule(null);
        stateMap.loggedIn = false;
      },
      success: function (text) {
        serverResponse = JSON.parse(text);
        app.m_player.initModule(serverResponse);
        stateMap.loggedIn = true;
      }
    });

    callback();
  };

  _createView = function () {
    if (stateMap.loggedIn) {
      var mainView = {
        id: 'main-layout',
        type: 'space',
        css: 'main-layout',
        view: 'layout',
        height: configMap.height,
        width: configMap.width,
        rows: [{template: 'main-content'}]
      };

      webixMap.top = webix.ui(mainView, stateMap.container);

      app.v_main_board.initModule(webixMap.top);
    } else {
      app.v_login.initModule(stateMap.container);
    }
  };

  initModule = function (container) {
    stateMap.container = container;
    _getLoggedInPlayerInfo(_createView);
  };

  return {
    initModule: initModule,
    showSignUp: showSignUp,
    showLogin: showLogin,
    showGameRoom: showGameRoom,
    showMainBoard: showMainBoard,
    logout: logout,
    player: player,
    playerList: playerList
  };
}());