package br.feevale.bolao.service;

import br.feevale.bolao.model.GameMatch;
import br.feevale.bolao.repository.GameMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Pattern regexScoreHome = Pattern.compile("mandante\">(\\d+)<\\/");
    private static final Pattern regexScoreVisitor = Pattern.compile("visitante\">(\\d+)<\\/");
    private static final Pattern regexnNames = Pattern.compile("placar-jogo-equipes-nome\">([^<]+)<\\/");
    private static final Pattern regexDates = Pattern.compile("placar-jogo-informacoes\">.{4}(\\d\\d\\/\\d\\d\\/\\d{4})");

    private static final long HOUR = 3600;
    private static ArrayList<HashMap<String, Object>> teams = null;
    private static long lastUpdate = 0;

    @Autowired
    private GameMatchRepository matchRepo;

//    private class GameMatch {
//        public final String date;
//        public final Tuple<String, String> clubs;
//        public final Tuple<Integer, Integer> score;
//
//        public GameMatch(String home, Integer homeScore, String visitor, Integer visitorScore, String date) {
//            clubs = new Tuple<>(home, visitor);
//            score = new Tuple<>(homeScore, visitorScore);
//            this.date = date;
//        }
//
//        @Override
//        public boolean equals(Object other) {
//            GameMatch m = (GameMatch)other;
//
//            return m.date.equals(date) && m.clubs.equals(clubs) && m.score.equals(score);
//        }
//
//        @Override
//        public int hashCode() {
//            return date.hashCode() ^ clubs.hashCode() ^ score.hashCode();
//        }
//    }

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
        final ArrayList<GameMatch> gameMatches = new ArrayList<>();

        for (int i = 1; i <= 38; i++) {
            gameMatches.addAll(fetchRound(i));
        }

        final HashMap<Tuple<String, String>, ArrayList<Tuple<String, Tuple<Integer, Integer>>>> bets = new HashMap<>();
        final HashMap<String, Integer> points = new HashMap<>();

        for (GameMatch gameMatch : gameMatches) {
            final ArrayList<String> winners = new ArrayList<>();
            final Tuple<String, String> clubs = new Tuple<>(gameMatch.getNameHome(), gameMatch.getNameVisitor());

            for (Tuple<String, Tuple<Integer, Integer>> bet : bets.get(clubs)) {
                String userName = bet.fst;
                Tuple<Integer, Integer> score = bet.snd;

                if (score.fst.equals(gameMatch.getScoreHome()) && score.snd.equals(gameMatch.getScoreVisitor())) {
                    winners.add(userName);
                }
            }

            if (!winners.isEmpty()) {
                int p = 10 / winners.size();

                if (p < 1) {
                    p = 1;
                }

                for (String winner : winners) {
                    points.put(winner, points.getOrDefault(winner, 0) + p);
                }
            }
        }

        return points;
    }

    public ArrayList<HashMap<String, String>> getRound(int number) {
        if (number < 1 || number > 38) {
            throw new RuntimeException("Rodada inválida");
        }

        updateMatchesTable();

        final ArrayList<HashMap<String, String>> round = new ArrayList<>();

        for (GameMatch m : matchRepo.findByRound(number)) {
            final HashMap<String, String> clubs = new HashMap<>();

            clubs.put("home", m.getNameHome());
            clubs.put("visitor", m.getNameVisitor());

            round.add(clubs);
        }

        return round;
    }

    private void updateMatchesTable() {
        for (int i = 1; i <= 38; i++) {
            for (GameMatch m1 : fetchRound(i)) {
                GameMatch m2 = matchRepo.findByRoundAndHomeAndVisitor(i, m1.getNameHome(), m1.getNameVisitor());

                if (m2 != null) {
                    if (!m2.isSameScore(m1)) {
                        m2.setScoreHome(m1.getScoreHome());
                        m2.setScoreVisitor(m1.getScoreVisitor());
                        matchRepo.save(m2);
                    }
                } else {
                    matchRepo.save(m1);
                }
            }
        }
    }

    private ArrayList<GameMatch> fetchRound(int number) {
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

        final ArrayList<GameMatch> round = new ArrayList<>();

        final Matcher matcherDates = regexDates.matcher(sb);
        final Matcher matcherScoreHome = regexScoreHome.matcher(sb);
        final Matcher matcherScoreVisitor = regexScoreVisitor.matcher(sb);
        final Matcher matcherNames = regexnNames.matcher(sb);

        while (matcherDates.find()) { // && matcherScoreHome.find() && matcherScoreVisitor.find()) {
            String date = matcherDates.group(1);
            Integer scoreHome = null;
            Integer scoreVisitor = null;
            if (matcherScoreHome.find() && matcherScoreVisitor.find()) {
                scoreHome = Integer.parseInt(matcherScoreHome.group(1));
                scoreVisitor = Integer.parseInt(matcherScoreVisitor.group(1));
            }

            matcherNames.find();
            String home = matcherNames.group(1); //.toLowerCase();

            matcherNames.find();
            String visitor = matcherNames.group(1); //.toLowerCase();

            GameMatch m = new GameMatch();

            m.setRound(number);
            m.setDate(date);
            m.setNameHome(home);
            m.setNameVisitor(visitor);
            m.setScoreHome(scoreHome);
            m.setScoreVisitor(scoreVisitor);

            round.add(m);
        }

        return round;
    }

    private void updateCacheTeamsClassification() {
        StringBuilder sb = null;

        try {
            sb = downloadPage("https://web.archive.org/web/20181028093445/https://globoesporte.globo.com/futebol/brasileirao-serie-a/");
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

    private class Tuple<T, U> {
        public final T fst;
        public final U snd;

        public Tuple(T fst, U snd) {
            this.fst = fst;
            this.snd = snd;
        }

        @Override
        public boolean equals(Object other) {
            Tuple<T, U> x = (Tuple<T, U>) other;

            return x.fst.equals(fst) && x.snd.equals(snd);
        }

        @Override
        public int hashCode() {
            return fst.hashCode() ^ snd.hashCode();
        }
    }

    private class Triplet<T, U, V> {
        public final T fst;
        public final U snd;
        public final V trd;

        public Triplet(T fst, U snd, V trd) {
            this.fst = fst;
            this.snd = snd;
            this.trd = trd;
        }

        @Override
        public boolean equals(Object other) {
            Triplet<T, U, V> x = (Triplet<T, U, V>) other;

            return x.fst.equals(fst) && x.snd.equals(snd) && x.trd.equals(trd);
        }

        @Override
        public int hashCode() {
            return fst.hashCode() ^ snd.hashCode() ^ trd.hashCode();
        }
    }
}
