package br.feevale.bolao.service;

import br.feevale.bolao.model.Bet;
import br.feevale.bolao.repository.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BetService {

    @Autowired
    BetRepository repository;

    public void save(ArrayList<Bet> bets) {
        bets.forEach(bet -> {
            if (bet.getScoreHome() != null && bet.getScoreVisitor() != null) {
                bet.setDtCreated("29/10/2018");
                repository.save(bet);
            }
        });
    }

    public List<Bet> findAll() {
        return repository.findAll();
    }

}
