package br.feevale.bolao.repository;

import br.feevale.bolao.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetRepository extends JpaRepository<Bet, Long> {


}
