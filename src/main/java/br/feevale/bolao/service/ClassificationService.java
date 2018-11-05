package br.feevale.bolao.service;

import br.feevale.bolao.model.Bet;
import br.feevale.bolao.model.GameMatch;
import br.feevale.bolao.model.User;
import br.feevale.bolao.repository.BetRepository;
import br.feevale.bolao.repository.GameMatchRepository;
import br.feevale.bolao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClassificationService {
    private static final long HOUR = 3600;
    private static ArrayList<HashMap<String, Object>> teams = null;
    private static long lastUpdate = 0;

    @Autowired
    private GameMatchRepository matchRepo;

    @Autowired
    private BetRepository betRepo;

    @Autowired
    private UserRepository userRepo;

    public ArrayList<HashMap<String, Object>> getTeamsClassification() {
        return teams;
    }

    public ArrayList<HashMap<String, Object>> getUsersClassification() {
        final List<GameMatch> gameMatches = matchRepo.findFinishedMatches();

        final HashMap<Long, ArrayList<Bet>> bets = new HashMap<>();

        for (GameMatch m : gameMatches) {
            bets.put(m.getId(), new ArrayList<>());
        }

        for (Bet bet : betRepo.findAll()) {
            bets.get(bet.getIdMatch()).add(bet);
        }

        final List<User> users = userRepo.findAll();

        final HashMap<Long, Integer> points = new HashMap<>();

        for (User user : users) {
            points.put(user.getId(), 0);
        }

        for (GameMatch match : gameMatches) {
            final ArrayList<Long> winners = new ArrayList<>();

            for (Bet bet : bets.get(match.getId())) {
                boolean gotHome = bet.getScoreHome().equals(match.getScoreHome());
                boolean gotVisistor = bet.getScoreVisitor().equals(match.getScoreVisitor());

                if (gotHome && gotVisistor) {
                    winners.add(bet.getIdUser());
                }
            }

            if (!winners.isEmpty()) {
                int p = 10 / winners.size();

                if (p < 1) {
                    p = 1;
                }

                for (Long userId : winners) {
                    points.put(userId, points.get(userId) + p);
                }
            }
        }

        ArrayList<HashMap<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            HashMap<String, Object> x = new HashMap<>();

            x.put("name", user.getName());
            x.put("points", points.get(user.getId()));

            result.add(x);
        }

        result.sort((a, b) -> ((Integer)a.get("points")).compareTo((Integer)b.get("points")));

        return result;
    }

    public ArrayList<HashMap<String, String>> getRound(int number) {
        if (number < 1 || number > 38) {
            throw new RuntimeException("Rodada inválida");
        }

        final ArrayList<HashMap<String, String>> round = new ArrayList<>();

        for (GameMatch m : matchRepo.findByRound(number)) {
            final HashMap<String, String> clubs = new HashMap<>();

            clubs.put("home", m.getNameHome());
            clubs.put("visitor", m.getNameVisitor());

            round.add(clubs);
        }

        return round;
    }

    public synchronized void update() {
        try {
            StringBuilder html = downloadPage("https://www.gazetaesportiva.com/campeonatos/brasileiro-serie-a/");

            if (html != null && html.length() > 0) {
                updateTeamsCache(html);
                updateMatchesTable(html);

                 lastUpdate = Instant.now().getEpochSecond();
            }
        } catch (IOException ex) {
            // TODO logar exception em algum lugar
            return;
        }
    }

    private void updateTeamsCache(StringBuilder html) {
        teams = new ArrayList<>();

        final Pattern classification_regexName = Pattern.compile("alt='([^']+)'\\s+title='\\1'>\\1<\\/a>");
        final Pattern classification_regexStats = Pattern.compile("<td\\s+class=\"table__stats[^\"]*\">(-?\\d+)<\\/td>");

        final Matcher matcherNames = classification_regexName.matcher(html);
        final Matcher matcherStats = classification_regexStats.matcher(html);

        while (matcherNames.find()) {
            final HashMap<String, Object> team = new HashMap<>();

            team.put("nome", matcherNames.group(1));

            matcherStats.find();
            team.put("P", Integer.parseInt(matcherStats.group(1)));

            matcherStats.find();
            team.put("J", Integer.parseInt(matcherStats.group(1)));

            matcherStats.find();
            team.put("V", Integer.parseInt(matcherStats.group(1)));

            matcherStats.find();
            team.put("E", Integer.parseInt(matcherStats.group(1)));

            matcherStats.find();
            team.put("D", Integer.parseInt(matcherStats.group(1)));

            matcherStats.find();
            team.put("GP", Integer.parseInt(matcherStats.group(1)));

            matcherStats.find();
            team.put("GC", Integer.parseInt(matcherStats.group(1)));

            matcherStats.find();
            team.put("S", Integer.parseInt(matcherStats.group(1)));

            teams.add(team);
        }
    }

    private void updateMatchesTable(StringBuilder html) {
        final Pattern regexNames = Pattern.compile("<a href=\"#\" title=\"([^\"]+)");
        final Pattern rounds_regexScoreHome = Pattern.compile("score__home\">(\\d*)<");
        final Pattern rounds_regexScoreVisitor = Pattern.compile("score__guest\">(\\d*)<");
        final Pattern rounds_regexDates = Pattern.compile("class=\"date\">\\s+(.{3}\\s-\\s.{10})?");


        final Matcher rounds_matcherDates = rounds_regexDates.matcher(new StringBuilder(html));
        final Matcher matcherNames = regexNames.matcher(html);
        final Matcher rounds_matcherScoreHome = rounds_regexScoreHome.matcher(new StringBuilder(html));
        final Matcher rounds_matcherScoreVisitor = rounds_regexScoreVisitor.matcher(new StringBuilder(html));

        for (int i = 1; i <= 380; i++) {
            rounds_matcherDates.find();
            rounds_matcherScoreHome.find();
            rounds_matcherScoreVisitor.find();

            GameMatch m1 = new GameMatch();

//            m1.setDate(rounds_matcherDates.group(1));
            m1.setDate("");

            matcherNames.find();
            m1.setNameHome(matcherNames.group(1));

            matcherNames.find();
            m1.setNameVisitor(matcherNames.group(1));

            m1.setScoreHome(rounds_matcherScoreHome.group(1).isEmpty() ? null : Integer.parseInt(rounds_matcherScoreHome.group(1)));
            m1.setScoreVisitor(rounds_matcherScoreVisitor.group(1).isEmpty() ? null : Integer.parseInt(rounds_matcherScoreVisitor.group(1)));

            m1.setRound(i % 10);

            if (m1.getDate().length() > 10) {
                m1.setDate(m1.getDate().substring(6));
            }

            GameMatch m2 = matchRepo.findByHomeAndVisitor(m1.getNameHome(), m1.getNameVisitor());

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

    private StringBuilder downloadPage(String url) throws IOException {
        HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();

        httpcon.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:63.0) Gecko/20100101 Firefox/63.0");

        try (InputStream inputStream = httpcon.getInputStream()) {
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
