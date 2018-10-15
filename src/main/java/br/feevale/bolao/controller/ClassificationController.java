package br.feevale.bolao.controller;

import br.feevale.bolao.TeamsClassificationUpdater;
import br.feevale.bolao.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("classification")
public class ClassificationController {

    @Autowired
    private ClassificationService classificationService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/teams")
    public Object teams() {
        TeamsClassificationUpdater att = new TeamsClassificationUpdater(classificationService);
        Thread thread = new Thread(att);

        thread.start();

        HashMap<String, Object> resp = new HashMap<>();

        resp.put("success", true);

        return resp;
    }
}
