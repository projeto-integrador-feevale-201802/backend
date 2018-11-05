package br.feevale.bolao.controller;

import br.feevale.bolao.model.GameMatch;
import br.feevale.bolao.service.GameMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("game")
public class GameMatchController {

    @Autowired
    private GameMatchService service;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{round}")
    public List<GameMatch> findByRound(@PathVariable("round") Integer round) {
        return service.findNewGamesByRound(round);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/rounds")
    public List<GameMatch> findNewRounds() {
        return service.findNewRounds();
    }
}