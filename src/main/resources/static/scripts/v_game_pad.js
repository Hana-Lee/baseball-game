/**
 * @author Hana Lee
 * @since 2016-01-20 21:19
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

app.v_game_pad = (function () {
  'use strict';

  var
    configMap = {
      settable_map : {
        input_delay : null
      },
      input_delay : 25000
    },
    stateMap = {
      container : null,
      isReady : false,
      selected_num : [],
      events : [],
      input_count : 0,
      game_status : null
    }, webixMap = {},
    _createView, _showMakeNumberWindow,
    _resetWebixMap, _resetStateMap, _sendReadyDataToServer, _gameStartHandler, _sendCreatedNumber,
    _showProgressBar, _hideProgressBar, _playerReadyNotification, _gameTerminatedHandler, _sendGuessNumbers,
    _guessNumberValidate, _playerInfoUpdatedHandler, _playerInputCountNotification,
    initModule;

  _sendCreatedNumber = function (send_data) {
    var gameRoomId = app.v_game_room.getGameRoomModel().id,
      sendData = {
        gameRoomId : gameRoomId
      };

    if (send_data && (send_data.number !== undefined || send_data.number !== null)) {
      sendData.number = send_data.number;
    }

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).patch('gameroom/set-game-number/' + gameRoomId, JSON.stringify(sendData), {
      error : function (text) {
        console.log(text);
        var textJson = JSON.parse(text);
        webix.alert({
          title : '오류',
          ok : '확인',
          text : textJson.message
        });

        if (webixMap.game_number_field) {
          webixMap.game_number_field.focus();
        }
      },
      success : function (/*text*/) {
        _sendReadyDataToServer();
      }
    });
  };

  _sendReadyDataToServer = function () {
    var sendData = {
      status : stateMap.isReady ? 'READY_DONE' : 'READY_BEFORE'
    };

    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).patch('player/ready', JSON.stringify(sendData), {
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
        if (stateMap.isReady) {
          webixMap.ready_button.config.label = '취소!!';
          webixMap.ready_button.refresh();
          webixMap.number_pad.enable();
          //_showProgressBar();
        } else {
          webixMap.ready_button.config.label = '준비!!';
          webixMap.ready_button.refresh();
          webixMap.number_pad.disable();
          _hideProgressBar();
        }

        if (webixMap.make_number_window) {
          webixMap.make_number_window.close();
        }

        _playerReadyNotification();
      }
    });
  };

  _sendGuessNumbers = function (guessNumber) {
    var sendUrl, header = {}, data;
    app.m_player.getInfo().guessNumber = guessNumber;
    app.m_player.getInfo().gameRoomId = app.v_game_room.getGameRoomModel().id;
    data = app.m_player.getInfo();
    sendUrl = '/app/gameroom/' + app.v_game_room.getGameRoomModel().id + '/player-guess-number';

    app.v_shell.getStompClient().send(sendUrl, header, JSON.stringify(data));
  };

  _playerInputCountNotification = function () {
    var sendUrl, progressBoardProxyClientId, header = {}, data;
    sendUrl = '/app/gameroom/' + app.v_game_room.getGameRoomModel().id + '/player-input-count-notification';
    progressBoardProxyClientId = app.v_game_board.getProgressBoardProxyClientId();
    data = {clientId : progressBoardProxyClientId};

    app.v_shell.getStompClient().send(sendUrl, header, JSON.stringify(data));
  };

  _playerReadyNotification = function () {
    var sendUrl, header = {}, data = {};
    sendUrl = '/app/player/ready/' + app.v_game_room.getGameRoomModel().id;
    app.v_shell.getStompClient().send(sendUrl, header, JSON.stringify(data));

    sendUrl = '/app/player/ready/' + app.v_game_room.getGameRoomModel().id + '/gameroom/notification';
    app.v_shell.getStompClient().send(sendUrl, header, JSON.stringify(data));
  };

  _resetStateMap = function () {
    stateMap.events.forEach(function (event) {
      webix.detachEvent(event);
    });

    stateMap.events = [];

    stateMap.container = null;
    stateMap.input_count = 0;
    stateMap.selected_num = [];
    stateMap.isReady = false;
    stateMap.game_status = null;
  };

  _resetWebixMap = function () {
    webixMap = {};
  };

  _showMakeNumberWindow = function () {
    var numberCount = app.v_game_room.getGameRoomModel().setting.generationNumberCount;
    webix.ui({
      id : 'make-number-window',
      view : 'window',
      head : '게임 숫자 생성 (' + numberCount + '자리)',
      modal : true,
      height : 300,
      width : 200,
      position : 'center',
      body : {
        id : 'make-number-form',
        view : 'form',
        rules : {
          number : function (value) {
            var valid = false, invalidMessage;

            if (webix.rules.isEmpty(value) === true) {
              invalidMessage = '입력이 없습니다';
            } else if (webix.rules.isNumber(value) === false) {
              invalidMessage = '숫자만 입력가능';
            } else if (webix.rules.isInputCountCorrect(value, numberCount) === false) {
              invalidMessage = '숫자 갯수 확인';
            } else if (webix.rules.containsSameNumber(value) === true) {
              invalidMessage = '중복 숫자는 불가';
            } else {
              valid = true;
            }

            if (invalidMessage && !valid) {
              webixMap.game_number_field.config.invalidMessage = invalidMessage;
              webix.message(invalidMessage, 'error', 2000);
            }

            return valid;
          }
        },
        elements : [{
          rows : [{
            id : 'game-number-field',
            view : 'text',
            name : 'number',
            label : '게임숫자',
            invalidMessage : '입력을 확인해주세요',
            required : true,
            attributes : {
              maxlength : numberCount,
              required : true
            }
          }, {
            cols : [{
              view : 'button',
              value : '생성',
              type : 'form',
              hotkey : 'enter',
              on : {
                onItemClick : function () {
                  if (webixMap.form.validate()) {
                    _sendCreatedNumber(webixMap.form.getValues());
                  } else {
                    webixMap.game_number_field.focus();
                  }
                }
              }
            }, {
              view : 'button',
              value : '취소',
              type : 'danger',
              hotkey : 'esc',
              on : {
                onItemClick : function () {
                  stateMap.isReady = false;
                  webixMap.make_number_window.close();
                }
              }
            }]
          }]
        }]
      }
    }).show();

    webixMap.make_number_window = $$('make-number-window');
    webixMap.form = $$('make-number-form');
    webixMap.game_number_field = $$('game-number-field');
    webixMap.game_number_field.focus();
  };

  _createView = function () {
    var mainView = {
      id : 'number-pad-container', css : 'game_pad_container',
      cols : [{
        width : 120,
        rows : [{
          id : 'ready-button',
          view : 'button',
          type : 'danger',
          height : 229,
          label : stateMap.isReady ? '취소!!' : '준비!!',
          css : 'ready_btn',
          on : {
            onItemClick : function () {
              stateMap.isReady = !stateMap.isReady;

              if (stateMap.isReady && app.m_player.getInfo().gameRole === 'DEFENDER') {
                _showMakeNumberWindow();
              } else {
                _sendReadyDataToServer();
              }
            }
          }
        }]
      }, {
        rows : [{
          id : 'number-pad',
          view : 'dataview',
          css : 'game_pad',
          type : {
            width : 112,
            height : 70,
            template : '<div class="overall">#number#</div>'
          },
          select : false,
          multiselect : true,
          scroll : false,
          disabled : !stateMap.isReady,
          data : [
            {id : '0', number : 0},
            {id : '1', number : 1},
            {id : '2', number : 2},
            {id : '3', number : 3},
            {id : '4', number : 4},
            {id : '5', number : 5},
            {id : '6', number : 6},
            {id : '7', number : 7},
            {id : '8', number : 8},
            {id : '9', number : 9}
          ],
          ready : function () {
            webix.extend(this, webix.ProgressBar);
          },
          on : {
            'onItemClick' : function (id/*, evt, el*/) {
              var idIndex, genNumberCount = app.v_game_room.getGameRoomModel().setting.generationNumberCount;
              if (stateMap.isReady) {
                if ($$('number-pad').isSelected(id)) {
                  idIndex = stateMap.selected_num.indexOf(id);
                  stateMap.selected_num.splice(idIndex, 1);
                } else {
                  if (stateMap.selected_num.length === genNumberCount) {
                    webix.alert({
                      title : '경고',
                      ok : '확인',
                      text : genNumberCount + '개 이상 선택 할 수 없습니다'
                    });
                    return false;
                  }

                  stateMap.selected_num.push(id);
                }

                setTimeout(function () {
                  $$('number-pad').select(stateMap.selected_num);
                }, 0);

                if (stateMap.selected_num.length === genNumberCount) {
                  $$('number-submit').enable();
                }
              }
            }
          }
        }, {
          id : 'number-submit',
          view : 'button',
          css : 'submit_btn',
          label : '제출',
          height : 58,
          disabled : true,
          on : {
            onItemClick : function (/*id, evt*/) {
              var guessNumber = stateMap.selected_num.join('');
              if (_guessNumberValidate(guessNumber)) {
                _sendGuessNumbers(guessNumber);

                stateMap.selected_num = [];
                $$('number-pad').unselectAll();
                this.disable();
                _hideProgressBar(true);
              }
            }
          }
        }]
      }],
      on : {
        onDestruct : function () {
          _resetWebixMap();
          _resetStateMap();
        }
      }
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
    webixMap.ready_button = $$('ready-button');
    webixMap.number_pad = $$('number-pad');
  };

  _guessNumberValidate = function (guessNumber) {
    var result = false, message = '';
    if (webix.rules.isEmpty(guessNumber)) {
      message = '빈값은 입력할 수 없습니다';
    } else if (webix.rules.isInputCountCorrect(guessNumber, app.v_game_room.getGameRoomModel().setting.limitGuessInputCount)) {
      message = '입력 갯수를 확인 해주세요';
    } else if (webix.rules.containsSameNumber(guessNumber)) {
      message = '중복된 숫자는 입력할 수 없습니다';
    } else {
      result = true;
    }
    if (result === false && message !== '') {
      webix.alert({
        title : '오류',
        ok : '확인',
        text : message
      });
      webix.message(message, 'error');
    }

    return result;
  };

  _showProgressBar = function () {
    stateMap.input_count++;
    // TODO stateMap.input_count 번째 입력이라는 알림 보내기

    $$('number-pad').showProgress({
      type : 'top',
      delay : configMap.input_delay,
      hide : true,
      position : 0
    });

    if (stateMap.input_count === app.v_game_room.getGameRoomModel().setting.limitGuessInputCount) {
      // TODO 게임 입력 횟수 초과로 게임 종료 알림 보내기
      console.log('입력 횟수 초과!!');
    }
  };

  _hideProgressBar = function (now) {
    $$('number-pad').hideProgress(now);
  };

  _gameStartHandler = function () {
    var gameRoomStatus = app.v_game_room.getGameRoomModel().status;
    if (gameRoomStatus === app.const.status.RUNNING) {
      stateMap.game_status = app.v_game_room.getGameRoomModel().status;
      _showProgressBar();
      webixMap.ready_button.disable();
    }
  };

  _gameTerminatedHandler = function () {
    var gameRoomStatus = app.v_game_room.getGameRoomModel().status;
    if (gameRoomStatus === app.const.status.NORMAL || gameRoomStatus === app.const.status.GAME_END) {
      if (stateMap.game_status) {
        _hideProgressBar();
        stateMap.isReady = false;
        stateMap.game_status = null;

        webixMap.ready_button.config.label = '준비!!';
        webixMap.ready_button.refresh();

        webixMap.number_pad.disable();
      }

      webixMap.ready_button.enable();
    }
  };

  _playerInfoUpdatedHandler = function (operation) {
    if (app.m_player.getInfo().status === app.const.status.GAME_OVER) {
      _hideProgressBar();
    } else if (app.m_player.getInfo().status === app.const.status.INPUT && operation === 'playerGuessNumber') {
      _playerInputCountNotification();
      _showProgressBar();
    }
  };

  initModule = function (container) {
    stateMap.container = container;
    if (app.m_player.getInfo().status === app.const.status.READY_DONE) {
      stateMap.isReady = true;
    }
    _createView();

    stateMap.events.push(webix.attachEvent(app.v_game_room.ON_GAME_START, _gameStartHandler));
    stateMap.events.push(webix.attachEvent(app.v_game_room.ON_GAME_END, _gameTerminatedHandler));
    stateMap.events.push(webix.attachEvent(app.v_shell.ON_PLAYER_INFO_UPDATED, _playerInfoUpdatedHandler));
  };

  return {
    initModule : initModule
  };
}());