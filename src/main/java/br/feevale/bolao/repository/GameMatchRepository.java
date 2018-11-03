package br.feevale.bolao.repository;

import br.feevale.bolao.model.GameMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameMatchRepository extends JpaRepository<GameMatch, Long> {
    List<GameMatch> findByRound(Integer round);

    @Query(value = "select * from game_match where round = ?1 and name_home = ?2 and name_visitor = ?3", nativeQuery = true)
    GameMatch findByRoundAndHomeAndVisitor(int round, String home, String visitor);
}
