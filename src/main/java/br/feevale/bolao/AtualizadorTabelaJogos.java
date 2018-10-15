package br.feevale.bolao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtualizadorTabelaJogos implements Runnable {
    private static final Pattern regexNome = Pattern.compile("itemprop=\"name\">([^<]+)<\\/strong>");
    private static final Pattern regexStats = Pattern.compile("<td class=\"tabela-pontos-ponto\">(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(\\d+)<\\/td>\\s*<td>(-?\\d+)<\\/td>");

    public class Time {
        private String nome;
        private int pontos;
        private int jogos;
        private int vitorias;
        private int derrotas;
        private int empates;
        private int golsPro;
        private int golsContra;
        private int saldo;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public int getPontos() {
            return pontos;
        }

        public void setPontos(int pontos) {
            this.pontos = pontos;
        }

        public int getVitorias() {
            return vitorias;
        }

        public void setVitorias(int vitorias) {
            this.vitorias = vitorias;
        }

        public int getDerrotas() {
            return derrotas;
        }

        public void setDerrotas(int derrotas) {
            this.derrotas = derrotas;
        }

        public int getEmpates() {
            return empates;
        }

        public void setEmpates(int empates) {
            this.empates = empates;
        }

        public int getGolsPro() {
            return golsPro;
        }

        public void setGolsPro(int golsPro) {
            this.golsPro = golsPro;
        }

        public int getGolsContra() {
            return golsContra;
        }

        public void setGolsContra(int golsContra) {
            this.golsContra = golsContra;
        }

        public int getSaldo() {
            return saldo;
        }

        public void setSaldo(int saldo) {
            this.saldo = saldo;
        }

        public int getJogos() {
            return jogos;
        }

        public void setJogos(int jogos) {
            this.jogos = jogos;
        }
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
        }

        ArrayList<Time> times = new ArrayList<>();

        Matcher matcherNomes = regexNome.matcher(sb);
        Matcher matcherStats = regexStats.matcher(sb);

        while (matcherNomes.find() && matcherStats.find()) {
            Time time = new Time();

            time.setNome(matcherNomes.group(1));
            time.setPontos(Integer.parseInt(matcherStats.group(1)));
            time.setJogos(Integer.parseInt(matcherStats.group(2)));
            time.setVitorias(Integer.parseInt(matcherStats.group(3)));
            time.setEmpates(Integer.parseInt(matcherStats.group(4)));
            time.setDerrotas(Integer.parseInt(matcherStats.group(5)));
            time.setGolsPro(Integer.parseInt(matcherStats.group(6)));
            time.setGolsContra(Integer.parseInt(matcherStats.group(7)));
            time.setSaldo(Integer.parseInt(matcherStats.group(8)));

            times.add(time);
        }

        matcherNomes = null;
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
