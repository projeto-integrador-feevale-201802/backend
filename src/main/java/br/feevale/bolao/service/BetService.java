package br.feevale.bolao.service;

import br.feevale.bolao.model.Bet;
import br.feevale.bolao.repository.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class BetService {

    @Autowired
    BetRepository repository;

    public void save(ArrayList<Bet> bets) {
        for (Bet bet : bets) {
            if (bet.getScoreHome() != null && bet.getScoreVisitor() != null) {
                bet.setCreated(Date.from(Instant.now()));

                Long id = repository.find(bet.getIdMatch(), bet.getIdUser());

                if (id != null) {
                    bet.setId(id);
                }

                repository.save(bet);
            }
        }
    }

    public List<Bet> find(long user, int round) {
        return repository.find(user, round);
    }

}
