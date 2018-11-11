package br.feevale.bolao.service;

import br.feevale.bolao.model.Bet;
import br.feevale.bolao.repository.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class BetService {

    @Autowired
    BetRepository repository;

//    GameBetRepository gameBetRepository;

    public void save(ArrayList<Bet> bets) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dateNow = format.format(new Date());

        bets.forEach(bet -> {
            if (bet.getScoreHome() != null && bet.getScoreVisitor() != null) {
                bet.setDtCreated(dateNow);
                repository.save(bet);
            }
        });
    }

    public List<Bet> findAll() {
        return repository.findAll();
    }

//    public List<GameBet> findByUserAndRound(Integer userId, Integer round) {
//        return gameBetRepository.findByUserAndRound(userId, round);
//    }

}
