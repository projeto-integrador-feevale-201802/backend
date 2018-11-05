package br.feevale.bolao.repository;

import br.feevale.bolao.model.GameMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameMatchRepository extends JpaRepository<GameMatch, Long> {

    List<GameMatch> findByRound(Integer round);

    @Query(value = "SELECT * FROM game_match WHERE round = ?1 AND name_home = ?2 AND name_visitor = ?3", nativeQuery = true)
    GameMatch findByRoundAndHomeAndVisitor(int round, String home, String visitor);

    @Query(value = "SELECT name_home, name_visitor, date FROM game_match WHERE round = ?1 AND score_home IS NULL AND score_visitor IS NULL", nativeQuery = true)
    List<GameMatch> findNewGamesByRound(int round);

    @Query(value = "SELECT DISTINCT round FROM game_match WHERE score_home IS NULL AND score_visitor IS NULL", nativeQuery = true)
    List<Integer> findNewRounds();
}
