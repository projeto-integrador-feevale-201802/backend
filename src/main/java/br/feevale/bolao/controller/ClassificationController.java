package br.feevale.bolao.controller;

import br.feevale.bolao.AtualizadorTabelaJogos;
import br.feevale.bolao.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("classification")
public class ClassificationController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/teams")
    public Object teams() {
        AtualizadorTabelaJogos att = new AtualizadorTabelaJogos();

        att.run();

        HashMap<String, Object> resp = new HashMap<>();

        resp.put("success", true);

        return resp;
    }
}
