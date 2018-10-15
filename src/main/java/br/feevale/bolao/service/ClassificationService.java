package br.feevale.bolao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ClassificationService {
    public void updateTeams(ArrayList<HashMap<String, Object>> teams) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(teams);

        Logger.getGlobal().log(Level.ALL, result);
    }
}
