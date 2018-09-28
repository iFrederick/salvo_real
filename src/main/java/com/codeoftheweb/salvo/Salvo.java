package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int turn;

    @ElementCollection
    @JoinColumn (name = "Location")
    private List<String>locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo(){}

    public Salvo(int Turno,List<String>lista,GamePlayer gamePlayer){
        this.turn = Turno;
        this.locations = lista;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
    @JsonIgnore
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
