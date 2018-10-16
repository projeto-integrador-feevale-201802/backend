package br.feevale.bolao.service;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClassificationService {
    private static final Pattern regexNome = Pattern.compile("itemprop=\"name\">([^<]+)<\\/strong>");
    private static final Pattern regexStats = Pattern.compile("<td class=\"tabela-pontos-ponto\">(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(-?\\d+)<\\/td>");
    private static final long HOUR = 3600;
    private static ArrayList<HashMap<String, Object>> teams = null;
    private static long lastUpdate = 0;

    public ArrayList<HashMap<String, Object>> getTeamsClassificationJson() {
        long now = Instant.now().getEpochSecond();

        if (teams == null || now - lastUpdate > 3 * HOUR) {
            Thread thread = new Thread(this::updateCacheTeamsClassification);
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException ex) {
                // TODO never gonna happen! but log it just in case...
            }
        }

        return teams;
    }

    private void updateCacheTeamsClassification() {
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

        teams = new ArrayList<>();

        final Matcher matcherNames = regexNome.matcher(sb);
        final Matcher matcherStats = regexStats.matcher(sb);

        while (matcherNames.find() && matcherStats.find()) {
            final HashMap<String, Object> team = new HashMap<>();

            team.put("clube", matcherNames.group(1));
            team.put("P", Integer.parseInt(matcherStats.group(1)));
            team.put("J", Integer.parseInt(matcherStats.group(2)));
            team.put("V", Integer.parseInt(matcherStats.group(3)));
            team.put("E", Integer.parseInt(matcherStats.group(4)));
            team.put("D", Integer.parseInt(matcherStats.group(5)));
            team.put("GP", Integer.parseInt(matcherStats.group(6)));
            team.put("GC", Integer.parseInt(matcherStats.group(7)));
            team.put("S", Integer.parseInt(matcherStats.group(8)));

            teams.add(team);
        }

        lastUpdate = Instant.now().getEpochSecond();
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
