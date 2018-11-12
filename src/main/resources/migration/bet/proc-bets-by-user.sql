--liquibase formatted SQL
--changeset includeAll:proc_bets_by_user

DROP PROCEDURE IF EXISTS bets_by_user;

CREATE PROCEDURE
  bets_by_user(p_idUser INT, p_round INT)
  BEGIN
    SELECT
      m.id            idMatch,
      m.name_home     nameHome,
      m.name_visitor  nameVisitor,
      m.score_home    actualScoreHome,
      m.score_visitor actualScoreVisitor,
      b.scoreHome     betScoreHome,
      b.scoreVisitor  betScoreVisitor
    FROM
      game_match m
      LEFT JOIN (SELECT * FROM bet WHERE idUser = p_idUser) b ON b.idMatch = m.id
    WHERE
      m.round = p_round;
  END;
