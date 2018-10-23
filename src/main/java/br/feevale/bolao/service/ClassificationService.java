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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClassificationService {
    private static final Pattern regexNome = Pattern.compile("itemprop=\"name\">([^<]+)<\\/strong>");
    private static final Pattern regexStats = Pattern.compile("<td class=\"tabela-pontos-ponto\">(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(-?\\d+)<\\/td>");

    private static final Pattern regexScoreHome = Pattern.compile("mandante\">(\\d+)<\\/");
    private static final Pattern regexScoreVisitor = Pattern.compile("visitante\">(\\d+)<\\/");
    private static final Pattern regexnNames = Pattern.compile("placar-jogo-equipes-nome\">([^<]+)<\\/");
    private static final Pattern regexDates = Pattern.compile("placar-jogo-informacoes\">.{4}(\\d\\d\\/\\d\\d\\/\\d{4})");

    private static final long HOUR = 3600;
    private static ArrayList<HashMap<String, Object>> teams = null;
    private static long lastUpdate = 0;

    private class Match {
        private String date;
        private String home;
        private String visitor;
        private int scoreHome;
        private int scoreVisitor;

        private int hash = 0;

        public Match()
        { }

        public Match(String home, int homeScore, String visitor, int visitorScore) {
            setHome(home);
            setVisitor(visitor);
            setScoreHome(homeScore);
            setScoreVisitor(visitorScore);
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public String getVisitor() {
            return visitor;
        }

        public void setVisitor(String visitor) {
            this.visitor = visitor;
        }

        public int getScoreHome() {
            return scoreHome;
        }

        public void setScoreHome(int scoreHome) {
            this.scoreHome = scoreHome;
        }

        public int getScoreVisitor() {
            return scoreVisitor;
        }

        public void setScoreVisitor(int scoreVisitor) {
            this.scoreVisitor = scoreVisitor;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || !Match.class.isInstance(other)) {
                return false;
            }

            return hashCode() == other.hashCode();
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = (
                    getHome() + "_" +
                    getScoreHome() + "_" +
                    getVisitor() + "_" +
                    getScoreVisitor()
                ).hashCode();
            }

            return hash;
        }
    }

    public synchronized ArrayList<HashMap<String, Object>> getTeamsClassificationJson() {
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

    public synchronized HashMap<String, Integer> getUsersClassification() {
        final ArrayList<Match> allMatches = new ArrayList<>();

        for (int i = 1; i <= 38; i++) {
            allMatches.addAll(fetchRound(i));
        }

        // TODO: pegar as apostas do banco de dados
        final HashMap<String, ArrayList<Match>> bets = new HashMap<>();
        final HashMap<Match, ArrayList<String>> hits = new HashMap<>();

        for (Match match : allMatches) {
            if (!hits.containsKey(match)) {
                hits.put(match, new ArrayList<>());
            }

            for (Map.Entry<String, ArrayList<Match>> pair : bets.entrySet()) {
                for (Match bet : pair.getValue()) {
                    if (bet.equals(match)) {
                        hits.get(match).add(pair.getKey());
                    }
                }
            }
        }

        final HashMap<String, Integer> points = new HashMap<>();

        for (String user : bets.keySet()) {
            points.put(user, 0);
        }

        for (ArrayList<String> winners : hits.values()) {
            if (!winners.isEmpty()) {
                Integer p = 10 / winners.size();

                for (String user : winners) {
                    points.put(user, points.get(user) + p);
                }
            }
        }

        return points;
    }

    private ArrayList<Match> fetchRound(int number) {
        StringBuilder sb = null;

        try {
            sb = downloadPage("https://globoesporte.globo.com/servico/backstage/esportes_campeonato/esporte/futebol/modalidade/futebol_de_campo/categoria/profissional/campeonato/campeonato-brasileiro/edicao/campeonato-brasileiro-2018/fases/fase-unica-seriea-2018/rodada/" + number + "/jogos.html");
        } catch (IOException e) {
            // TODO logar exception em algum lugar
            return null;
        }

        if (sb == null || sb.length() == 0) {
            // TODO logar em algum lugar
            return null;
        }

        final ArrayList<Match> round = new ArrayList<>();

        final Matcher matcherDates = regexDates.matcher(sb);
        final Matcher matcherScoreHome = regexScoreHome.matcher(sb);
        final Matcher matcherScoreVisitor = regexScoreVisitor.matcher(sb);

        while (matcherDates.find() && matcherScoreHome.find() && matcherScoreVisitor.find()) {
            Match match = new Match();

            match.setDate(matcherDates.group(1));
            match.setScoreHome(Integer.parseInt(matcherScoreHome.group(1)));
            match.setScoreVisitor(Integer.parseInt(matcherScoreVisitor.group(1)));

            round.add(match);
        }

        final Matcher matcherNames = regexnNames.matcher(sb);

        for (Match match : round) {
            matcherNames.find();
            match.setHome(matcherNames.group(1).toLowerCase());
            matcherNames.find();
            match.setVisitor(matcherNames.group(1).toLowerCase());
        }

        return round;
    }

    private void updateCacheTeamsClassification() {
        StringBuilder sb = null;

        try {
            sb = downloadPage("https://globoesporte.globo.com/futebol/brasileirao-serie-a/");
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

            team.put("nome", matcherNames.group(1));
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


    private StringBuilder downloadPage(String url) throws IOException {
        try (InputStream inputStream = new URL(url).openStream()) {
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
