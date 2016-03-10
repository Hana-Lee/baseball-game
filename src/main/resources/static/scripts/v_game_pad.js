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
    stateMap = {
      container : null,
      isReady : false,
      selected_num : []
    }, webixMap = {}, _createView,
    _resetWebixMap, _resetStateMap, _sendReadyDataToServer,
    initModule, showProgressBar;

  _sendReadyDataToServer = function (callback) {
    var sendData = {
      status : stateMap.isReady ? 'READY_DONE' : 'READY_BEFORE'
    };
    webix.ajax().headers({
      'Content-Type' : 'application/json'
    }).patch('gameroom/ready/' + app.v_game_room.getGameRoomModel().id, JSON.stringify(sendData), {
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
        webixMap.ready_button.refresh();
        if (stateMap.isReady) {
          webixMap.ready_button.config.label = '취소!!';
          callback();
        } else {
          webixMap.ready_button.config.label = '준비!!';
        }
      }
    });
  };

  _resetStateMap = function () {
    stateMap.selected_num = [];
    stateMap.isReady = false;
  };

  _resetWebixMap = function () {
    webixMap = {};
  };

  _createView = function () {
    var mainView = {
      id : 'game-pad-container', css : 'game_pad_container',
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
              _sendReadyDataToServer(showProgressBar);
            }
          }
        }]
      }, {
        rows : [{
          id : 'game-pad',
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
                if ($$('game-pad').isSelected(id)) {
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
                  $$('game-pad').select(stateMap.selected_num);
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
          label : '제출',
          height : 58,
          disabled : true,
          on : {
            onItemClick : function (/*id, evt*/) {
              stateMap.selected_num = [];
              $$('game-pad').unselectAll();
              this.disable();
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
  };

  showProgressBar = function () {
    $$('game-pad').showProgress({
      type : 'top',
      delay : 25000,
      hide : false,
      position : 0
    });
  };

  initModule = function (container) {
    stateMap.container = container;
    if (app.m_player.getInfo().status === 'READY_DONE') {
      stateMap.isReady = true;
    }
    _createView();
  };

  return {
    initModule : initModule
  };
}());