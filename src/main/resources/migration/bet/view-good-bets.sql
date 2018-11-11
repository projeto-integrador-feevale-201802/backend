delete from bet;
alter table bet drop dtCreated;
alter table bet add created timestamp not null;
delete from game_match;
alter table game_match drop date;
alter table game_match add played timestamp null;

create view vw_good_bets as
select
  m.id match_id,
  u.name user_name,
  u.idUser user_id
from
  bet b
  join game_match m on b.idMatch = m.id
  join user u on b.idUser = u.idUser
where
  b.scoreHome = m.score_home
  and b.scoreVisitor = m.score_visitor
  and b.created < m.date;
