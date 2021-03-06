/**
 * @author Hana Lee
 * @since 2016-03-21 19:50
 */
/**
 * @namespace app.model
 *
 * @description 플레이어, 게임룸, 게임룸 리스트를 저장하여 어플리케이션에서 활용 한다
 */
app.model = (function () {
  'use strict';

  var
    /**
     * @description 현재 로그인한 플레이어
     *
     * @type {Player|null}
     * @private
     */
    player = null,
    /**
     * @description 입장한 게임룸
     *
     * @type {GameRoom|null}
     * @private
     */
    gameRoom = null,
    /**
     * @description 생성된 게임룸 리스트
     *
     * @type {GameRoom[]}
     * @private
     */
    gameRoomList = [],

    /**
     * @description 현재 로그인한 사용자의 정보를 설정 한다
     *
     * @param {Player|null} player_model
     * @public
     */
    setPlayer = function (player_model) {
      player = player_model;
    },

    /**
     * @description 현재 로그인 한 사용자의 정보를 반환 한다
     *
     * @returns {Player|null}
     * @public
     */
    getPlayer = function () {
      return player;
    },

    /**
     * @description 현재 입장한 게임룸의 정보를 설정 한다
     *
     * @param {GameRoom|null} gameRoom_model
     * @public
     */
    setGameRoom = function (gameRoom_model) {
      gameRoom = gameRoom_model;
    },

    /**
     * @description 현재 입장한 게임룸의 정보를 반환 한다
     *
     * @returns {GameRoom|null}
     * @public
     */
    getGameRoom = function () {
      return gameRoom;
    },

    /**
     * @description 현재 생성된 게임룸 리스트를 설정 한다
     *
     * @param {GameRoom[]|null} gameRoomList_model
     * @public
     */
    setGameRoomList = function (gameRoomList_model) {
      if (gameRoomList_model === null || gameRoomList_model === undefined) {
        gameRoomList_model = [];
      }
      gameRoomList = gameRoomList_model;
    },

    /**
     * @description 현재 설정된 게임룸 리스트를 반환 한다
     *
     * @returns {GameRoom[]|null}
     * @public
     */
    getGameRoomList = function () {
      return gameRoomList;
    },

    /**
     * @description 새로운 게임룸을 게임룸 리스트에 추가 한다
     *
     * @param {GameRoom} newGameRoom
     */
    addGameRoom = function (newGameRoom) {
      gameRoomList.push(newGameRoom);
    },

    /**
     * @description 게임룸 목록에서 id 를 이용해 찾아 삭제 한다
     *
     * @param {Number} gameRoomId
     */
    removeGameRoom = function (gameRoomId) {
      var gameRoomIdx;
      gameRoomList.forEach(function (gameRoom, idx) {
        if (gameRoom.id === gameRoomId) {
          gameRoomIdx = idx;
        }
      });

      if (gameRoomIdx !== undefined) {
        gameRoomList.splice(gameRoomIdx, 1);
      }
    },

    /**
     * @description 게임룸 목록을 완전히 삭제 한다
     */
    clearGameRoomList = function () {
      gameRoomList = [];
    };

  return {
    setPlayer : setPlayer,
    getPlayer : getPlayer,
    setGameRoom : setGameRoom,
    getGameRoom : getGameRoom,
    setGameRoomList : setGameRoomList,
    getGameRoomList : getGameRoomList,
    addGameRoom : addGameRoom,
    removeGameRoom : removeGameRoom,
    clearGameRoomList : clearGameRoomList
  };
}());