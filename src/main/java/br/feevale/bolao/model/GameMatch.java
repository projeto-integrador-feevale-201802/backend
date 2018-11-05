package br.feevale.bolao.model;

import javax.persistence.*;

@Entity
public class GameMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameHome;
    private String nameVisitor;
    private Integer scoreHome;
    private Integer scoreVisitor;
    private String date;
    private Integer round;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameHome() {
        return nameHome;
    }

    public void setNameHome(String nameHome) {
        this.nameHome = nameHome;
    }

    public String getNameVisitor() {
        return nameVisitor;
    }

    public void setNameVisitor(String nameVisitor) {
        this.nameVisitor = nameVisitor;
    }

    public Integer getScoreHome() {
        return scoreHome;
    }

    public void setScoreHome(Integer scoreHome) {
        this.scoreHome = scoreHome;
    }

    public Integer getScoreVisitor() {
        return scoreVisitor;
    }

    public void setScoreVisitor(Integer scoreVisitor) {
        this.scoreVisitor = scoreVisitor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public boolean isSameScore(GameMatch m) {
        if (getScoreHome() == null && m.getScoreHome() != null) {
            return false;
        }

        if (getScoreVisitor() == null && m.getScoreVisitor() != null) {
            return false;
        }

        return getScoreHome().equals(m.getScoreHome()) && getScoreVisitor().equals(m.getScoreVisitor());
    }

    @Override
    public boolean equals(Object other) {
        GameMatch m = (GameMatch)other;

        return
            m.getRound().equals(getRound()) &&
            m.getDate().equals(getDate()) &&
            m.getNameHome().equals(getNameHome()) &&
            m.getScoreHome().equals(getScoreHome()) &&
            isSameScore(m);
    }

    @Override
    public int hashCode() {
        return getRound().hashCode() ^ getScoreHome().hashCode() ^ getNameVisitor().hashCode();
    }
}