/**
 * @author Hana Lee
 * @since 2016-03-21 19:51
 */
app.vo = (function () {
  'use strict';

  var
    /**
     * @typedef {Object} Avatar
     * @type {Avatar}
     *
     * @property {Number} id
     * @property {String} imagePath
     * @property {String} imageExt
     * @property {String} mimeType
     */
    avatar = {
      id : null,
      imagePath : null,
      imageExt : null,
      mimeType : null
    },
    /**
     * @typedef {Object} Level
     * @type {Level}
     *
     * @property {Number} id
     * @property {Number} value
     */
    level = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} TotalScore
     * @type {TotalScore}
     *
     * @property {Number} id
     * @property {Number} value
     */
    totalScore = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} TotalRank
     * @type {TotalRank}
     *
     * @property {Number} id
     * @property {Number} value
     */
    totalRank = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} TotalGame
     * @type {TotalGame}
     *
     * @property {Number} id
     * @property {Number} count
     */
    totalGame = {
      id : null,
      count : null
    },
    /**
     * @typedef {Object} Win
     * @type {Win}
     *
     * @property {Number} id
     * @property {Number} count
     */
    win = {
      id : null,
      count : null
    },
    /**
     * @typedef {Object} Lose
     * @type {Lose}
     *
     * @property {Number} id
     * @property {Number} count
     */
    lose = {
      id : null,
      count : null
    },
    /**
     * @typedef {Object} MatchRecord
     * @type {MatchRecord}
     *
     * @property {Number} id
     * @property {TotalGame} totalGame
     * @property {Win} win
     * @property {Lose} lose
     */
    matchRecord = {
      id : null,
      totalGame : totalGame,
      win : win,
      lose : lose
    },
    /**
     * @typedef {Object} AttackerRoleCount
     * @type {AttackerRoleCount}
     *
     * @property {Number} id
     * @property {Number} value
     */
    attackerRoleCount = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} DefenderRoleCount
     * @type {DefenderRoleCount}
     *
     * @property {Number} id
     * @property {Number} value
     */
    defenderRoleCount = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} Settlement
     * @type {Settlement}
     *
     * @property {Number} id
     * @property {Boolean} solved
     */
    settlement = {
      id : null,
      solved : null
    },
    /**
     * @typedef {Object} Strike
     * @type {Strike}
     *
     * @property {Number} id
     * @property {Number} value
     */
    strike = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} Ball
     * @type {Ball}
     *
     * @property {Number} id
     * @property {Number} value
     */
    ball = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} Result
     * @type {Result}
     *
     * @property {Number} id
     * @property {Settlement} settlement
     * @property {Strike} strike
     * @property {Ball} ball
     */
    result = {
      id : null,
      settlement : settlement,
      strike : strike,
      ball : ball
    },
    /**
     * @typedef {Object} Rank
     * @type {Rank}
     *
     * @property {Number} id
     * @property {Number} value
     */
    rank = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} Score
     * @type {Score}
     *
     * @property {Number} id
     * @property {Number} value
     */
    score = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} Player
     * @type {Player}
     *
     * @property {Number} id
     * @property {String} email
     * @property {String} nickname
     * @property {Avatar} avatar
     * @property {Level} level
     * @property {TotalScore} totalScore
     * @property {TotalRank} totalRank
     * @property {MatchRecord} matchRecord
     * @property {AttackerRoleCount} attackerRoleCount
     * @property {DefenderRoleCount} defenderRoleCount
     * @property {String} gameRole
     * @property {String} status
     * @property {Number} created
     * @property {Number} updated
     * @property {Number} deleted
     * @property {Boolean} admin
     * @property {String} enabled
     * @property {Number} inputCount
     * @property {Number} wrongCount
     * @property {String} guessNumber
     * @property {Result} result
     * @property {Rank} rank
     * @property {Score} score
     * @property {Number} gameOverTime
     */
    player = {
      id : null,
      email : null,
      nickname : null,
      avatar : avatar,
      level : level,
      totalScore : totalScore,
      totalRank : totalRank,
      matchRecord : matchRecord,
      attackerRoleCount : attackerRoleCount,
      defenderRoleCount : defenderRoleCount,
      gameRole : null,
      status : null,
      created : null,
      updated : null,
      deleted : null,
      admin : null,
      enabled : null,
      inputCount : null,
      wrongCount : null,
      guessNumber : null,
      result : result,
      rank : rank,
      score : score,
      gameOverTime : null
    },
    /**
     * @typedef {Object} Setting
     * @type {Setting}
     * 
     * @property {Number} id
     * @property {Number} limitWrongInputCount
     * @property {Number} limitGuessInputCount
     * @property {Number} generationNumberCount
     */
    setting = {
      id : null,
      limitWrongInputCount : null,
      limitGuessInputCount : null,
      generationNumberCount : null
    },
    /**
     * @typedef {Object} GameNumber
     * @type {GameNumber}
     *
     * @property {Number} id
     * @property {Number} value
     */
    gameNumber = {
      id : null,
      value : null
    },
    /**
     * @typedef {Object} GameRoom
     * @type {GameRoom}
     *
     * @property {Number} id
     * @property {String} name
     * @property {Number} roomNumber
     * @property {Number} limitPlayerCount
     * @property {String} status
     * @property {Player} owner
     * @property {Player[]} players
     * @property {Setting} setting
     * @property {Number} gameCount
     * @property {GameNumber} gameNumber
     * @property {Number} created
     * @property {Number} updated
     * @property {Number} deleted
     * @property {String} enabled
     */
    gameRoom = {
      id : null,
      name : null,
      roomNumber : null,
      limitPlayerCount : null,
      status : null,
      owner : player,
      players : [],
      setting : setting,
      gameCount : null,
      gameNumber : gameNumber,
      created : null,
      updated : null,
      deleted : null,
      enabled : null
    };

  return {
    player : player,
    gameRoom : gameRoom
  };
}());