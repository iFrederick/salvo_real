package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.boot.autoconfigure.web.ResourceProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity

public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime dateTime;

    @OneToMany(mappedBy = "juego", fetch = FetchType.EAGER)
    private List<GamePlayer>gamePlayers;
    @OneToMany(mappedBy = "game")
    private Set<Score> score;

    public void addGamePlayer(GamePlayer gamePlayer){
      gamePlayer.setJuego(this);
      gamePlayers.add(gamePlayer);
    }

    public Game() {
        this.dateTime = LocalDateTime.now();
    }

    public Game(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public long getId() {
        return id;
    }

    public void setScores(Set<Score> scores) {
        this.score = scores;
    }

    public Set<Score> getScores() {
        return score;
    }

    public void setId(long id) {
        this.id = id;
    }
    @JsonIgnore
    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }
    public void setGamePlayers(List<GamePlayer>gamePlayers){
        this.gamePlayers = gamePlayers;
    }
}
