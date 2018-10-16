package br.feevale.bolao.controller;

import br.feevale.bolao.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("classification")
public class ClassificationController {
    @Autowired
    private ClassificationService classificationService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/teams")
    public Object teams() {
        return classificationService.getTeamsClassificationJson();
    }
}
