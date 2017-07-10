/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
 */
/**
 * @namespace app
 */
var app = (function () {
  'use strict';
  var stateMap = {
    container : null
  }, initModule, _registerWebixStompProxy, _extendWebixRules;

  _registerWebixStompProxy = function () {
    webix.proxy.stomp = {
      $proxy : true,
      game_room : null,
      user_prefix : null,
      init : function () {
        this.clientId = this.clientId || webix.uid();
      },
      load : function (view) {
        var selfId = this.clientId, subscribeUrl = '/topic' + this.source,
          subscribeList = [], _responseHandler;

        _responseHandler = function (response) {
          var update = {};
          update = JSON.parse(response.body);

          if (update.clientId === selfId) {
            return;
          }

          if (update.objectOperation && update.objectOperation === 'anotherPlayerInputCount'
            && app.model.getPlayer().gameRole === app.const.gameRole.DEFENDER) {
            return;
          }

          webix.dp(view).ignore(function () {
            if (update.operation === 'delete') {
              view.remove(update.data.id);
            } else if (update.operation === 'insert') {
              view.add(update.data);
            } else if (update.operation === 'update') {
              var item = view.getItem(update.data.id);
              if (item) {
                webix.extend(item, update.data, true);
                view.refresh(item.id);
              }
            }
          });
        };

        subscribeList.push(
          app.v_shell.getStompClient().subscribe(subscribeUrl, _responseHandler, {id : 'sub-' + this.clientId})
        );

        if (this.user_prefix) {
          subscribeList.push(
            app.v_shell.getStompClient().subscribe(this.user_prefix + subscribeUrl,
              _responseHandler, {id : 'sub-' + this.clientId})
          );
        }

        view.attachEvent('onDestruct', function () {
          subscribeList.forEach(function (sub) {
            sub.unsubscribe();
          });
        });
      },
      save : function (view, update/*, dp, callback*/) {
        if (view === undefined || view === null) {
          return;
        }

        if (this.source.indexOf('chat') !== -1) {
          delete update.data.id;
        }

        // TODO 게임룸에서의 채팅이라는것 구분 및 데이터 전달이 목적인데 더 나은 방법 고민해보기...
        if (this.game_room) {
          update.data.gameRoom = this.game_room;
        }

        update.clientId = this.clientId;
        update.data.email = app.model.getPlayer().email;
        var header = {}, sendUrl = '/app' + this.source;
        app.v_shell.getStompClient().send(sendUrl, header, JSON.stringify(update));
      }
    };
  };

  _extendWebixRules = function () {
    webix.extend(webix.rules, {
      /**
       * 입력된 값이 비었는지 확인 한다
       * @param {String} value 사용자 입력 값
       * @returns {Boolean} 값이 비었으면 true
       */
      isEmpty : function (value) {
        return (value === undefined || value === null || value.replace(/\s/g, '') === '');
      },
      /**
       * 입력된 값이 비어있지 않은지 확인 한다
       *
       * @param {String} value 사용자 입력 값
       * @returns {Boolean} 입력값이 비었으면 false
       */
      isNotEmpty : function (value) {
        return (value !== undefined && value !== null && value.replace(/\s/g, '') !== '');
      },
      /**
       * 중복된 숫자가 있는지 확인 한다.
       *
       * @param {String} value 사용자 입력 값
       * @returns {Boolean} 중복된 숫자가 있으면 true, 없으면 false
       */
      containsSameNumber : function (value) {
        var valueList, result = false, temp = {};
        value = value.replace(/\s/g, '');
        valueList = value.split('');
        valueList.forEach(function (v) {
          temp[v] = v;
        });

        if (Object.keys(temp).length < value.length) {
          result = true;
        }

        return result;
      },
      /**
       * 입력된 값의 길이가 설정된 값이랑 동일한지 확인한다
       * @param {String} value 사용자 입력 값
       * @param {Number} correctCount 비교할 숫자
       * @return {Boolean} 사용자 입력값의 갯수와 비교대상의 수가 같으면 true
       */
      isInputCountCorrect : function (value, correctCount) {
        return value.length === correctCount;
      }
    }, true);
  };

  initModule = function (container) {
    stateMap.container = container;

    _registerWebixStompProxy();
    _extendWebixRules();

    app.v_shell.initModule(stateMap.container);
  };

  return {
    initModule : initModule
  };
}());