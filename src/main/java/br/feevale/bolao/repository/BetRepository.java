package br.feevale.bolao.repository;

import br.feevale.bolao.model.Bet;
import br.feevale.bolao.model.View_GoodBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    @Query(value = "select idBet from bet where idMatch = ?1 and idUser = ?2", nativeQuery = true)
    Long find(long idMatch, long idUser);

    @Query(value = "select b.* from bet b join game_match m on b.idMatch = m.id where b.idUser = ?1 and m.round = ?2", nativeQuery = true)
    List<Bet> find(long idUser, int round);

    @Query(value = "select * from vw_good_bets", nativeQuery = true)
    List<View_GoodBet> findGoodBets();
}
