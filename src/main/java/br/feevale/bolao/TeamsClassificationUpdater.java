package br.feevale.bolao;

import br.feevale.bolao.service.ClassificationService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeamsClassificationUpdater implements Runnable {
    private static final Pattern regexNome = Pattern.compile("itemprop=\"name\">([^<]+)<\\/strong>");
    private static final Pattern regexStats = Pattern.compile("<td class=\"tabela-pontos-ponto\">(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(-?\\d+)<\\/td>");

    private final ClassificationService service;

    public TeamsClassificationUpdater(ClassificationService service) {
        this.service = service;
    }

    @Override
    public void run() {
        StringBuilder sb = null;

        try {
            sb = downloadGloboesportePage();
        } catch (IOException ex) {
            // TODO logar exception em algum lugar
            return;
        }

        if (sb == null || sb.length() == 0) {
            // TODO logar em algum lugar
            return;
        }

        final ArrayList<HashMap<String, Object>> times = new ArrayList<>();

        final Matcher matcherNomes = regexNome.matcher(sb);
        final Matcher matcherStats = regexStats.matcher(sb);

        while (matcherNomes.find() && matcherStats.find()) {
            final HashMap<String, Object> time = new HashMap<>();

            time.put("nome", matcherNomes.group(1));
            time.put("pontos", Integer.parseInt(matcherStats.group(1)));
            time.put("jogos", Integer.parseInt(matcherStats.group(2)));
            time.put("vitorias", Integer.parseInt(matcherStats.group(3)));
            time.put("empates", Integer.parseInt(matcherStats.group(4)));
            time.put("derrotas", Integer.parseInt(matcherStats.group(5)));
            time.put("golsPro", Integer.parseInt(matcherStats.group(6)));
            time.put("golsContra", Integer.parseInt(matcherStats.group(7)));
            time.put("saldo", Integer.parseInt(matcherStats.group(8)));

            times.add(time);
        }

        try {
            service.updateTeams(times);
        } catch (JsonProcessingException e) {
            // TODO logar exception em algum lugar
        }
    }

    private StringBuilder downloadGloboesportePage() throws IOException {
        URL url = new URL("https://globoesporte.globo.com/futebol/brasileirao-serie-a/");

        try (InputStream inputStream = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String chunk;

            while ((chunk = reader.readLine()) != null) {
                sb.append(chunk);
            }

            return sb;
        }
    }
}
