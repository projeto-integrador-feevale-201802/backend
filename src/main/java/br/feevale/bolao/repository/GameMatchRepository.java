package br.feevale.bolao.repository;

import br.feevale.bolao.model.GameMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameMatchRepository extends JpaRepository<GameMatch, Long> {
    List<GameMatch> findByRound(Integer round);

    @Query(value = "select * from game_match where name_home = ?1 and name_visitor = ?2", nativeQuery = true)
    GameMatch findByHomeAndVisitor(String home, String visitor);

    @Query(value = "select * from game_match where score_home is not null and score_visitor is not null", nativeQuery = true)
    List<GameMatch> findFinishedMatches();
}
