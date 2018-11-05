package br.feevale.bolao.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idbet")
    private Long id;

    @Column(name = "idmatch")
    private Long idMatch;

    @Column(name = "iduser")
    private Long idUser;

    @Column(name = "scorehome")
    private Integer scoreHome;

    @Column(name = "scorevisitor")
    private Integer scoreVisitor;

    @Column(name = "dtcreated")
    private String dtCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
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

    public String getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(String dtCreated) {
        this.dtCreated = dtCreated;
    }
}
