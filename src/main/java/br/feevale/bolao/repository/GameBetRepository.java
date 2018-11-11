//package br.feevale.bolao.repository;
//
//import br.feevale.bolao.model.GameBet;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//
//public interface GameBetRepository extends JpaRepository<GameBet, Long> {
//
//    String QUERY_FIND_USER_ROUND = "" +
//            "SELECT DISTINCT b.scoreHome AS betscorehome, b.scoreVisitor AS betscorevisitor, g.name_home, g.name_visitor, g.score_home, g.score_visitor FROM bet b " +
//            "INNER JOIN game_match g ON g.round = b.idMatch" +
//            "WHERE b.idUser = ?1 AND b.idMatch = ?2";
//
//    @Query(value = QUERY_FIND_USER_ROUND, nativeQuery = true)
//    List<GameBet> findByUserAndRound(Integer userId, Integer round);
//
//}
