/**
 * @author Hana Lee
 * @since 2016-01-19 20:16
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_game_board = (function () {
  'use strict';

  var stateMap = {
      container : null
    }, webixMap = {}, _createView,
    initModule;

  _createView = function () {
    var mainView = {
      id : 'board-container',
      cols : [
        {
          id : 'left-user-container', width : 220, rows : [{
          rows : [{template : '유저1', type : 'header'}, {template : '유저1 정보'}]
        }, {
          height : 5
        }, {
          rows : [{template : '유저2', type : 'header'}, {template : '유저2 정보'}]
        }]
        },
        {
          width : 5
        },
        {
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
        },
        {
          width : 5
        },
        {
          id : 'right-user-container', width : 220,
          rows : [
            {
              rows : [{template : '유저3', type : 'header'}, {template : '유저3 정보'}]
            },
            {
              height : 5
            },
            {
              rows : [{template : '유저4', type : 'header'}, {template : '유저4 정보'}]
            }
          ]
        }
      ]
    };

    webixMap.top = webix.ui(mainView, stateMap.container);
  };

  initModule = function (container) {
    stateMap.container = container;
    _createView();
  };

  return {
    initModule : initModule
  };
}());