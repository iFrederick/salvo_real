package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String userName;
    private String password;


    @OneToMany(mappedBy = "jugador", fetch = FetchType.EAGER)
    Set<GamePlayer>gamePlayers;
    @OneToMany(mappedBy = "player")
    Set<Score>score;


    public Player() { }

    public Player(String user,String pass) {
        this.setUserName(user);
        this.setPassword(pass);
    }
    public void addGamePlayer(GamePlayer gameplayer){
        gameplayer.setJugador(this);
        gamePlayers.add(gameplayer);
    }

    public Score getScore(Game game){
        return game.getScores()
                .stream()
                .filter(score -> score.getPlayer().equals(this)).findFirst().orElse(null);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    @JsonIgnore
    public List<Game>getJuegos(){
    return gamePlayers
            .stream()
            .map (sub -> sub.getJuego())
            .collect(Collectors.toList());
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Score> getScore() {
        return score;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void setScore(Set<Score> score) {
        this.score = score;
    }
    public double getTotalScore (){
        return getWins() + getLoses()* 0 + getTies()*0.5;
    }
    public long getWins(){
        return this.getScore().stream().filter(score -> score.getScore() == 1).count();
    }
    public long getLoses (){
        return this.getScore().stream().filter(score -> score.getScore() == 0).count();
    }
    public long getTies(){
        return this.getScore().stream().filter(score -> score.getScore() == 0.5).count();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


