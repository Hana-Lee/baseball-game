/**
 * @author Hana Lee
 * @since 2016-03-14 20:34
 */
app.const = (function () {
  'use strict';

  var
    status = {
      NORMAL : 'NORMAL',
      RUNNING : 'RUNNING',
      GAME_END : 'GAME_END',
      GAME_OVER : 'GAME_OVER',
      READY_BEFORE : 'READY_BEFORE',
      READY_DONE : 'READY_DONE',
      INPUT : 'INPUT'
    },
    gameRole = {
      ATTACKER : 'ATTACKER',
      DEFENDER : 'DEFENDER'
    };

  return {
    status : status,
    gameRole : gameRole
  };
}());