package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String type;

    @ElementCollection
    @JoinColumn (name ="locations")
    private List<String>slocation;

    @ManyToOne(fetch = FetchType.EAGER)         //conexion de la clase ship y gameplayer
    @JoinColumn (name = "gamePlayer_id")        //usando el "gameplayer_id" que se encuentra en Gameplayer
    private GamePlayer gamePlayer;

    public Ship (){}
    //Constructor que pasa los parametros a SalvoApp pidiendo el nombre y la lista.
    public Ship (String barco, List<String> lista,GamePlayer gamePlayer){
        this.type = barco;
        this.slocation = lista;
        this.gamePlayer = gamePlayer;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSlocation() {
        return slocation;
    }

    public void setSlocation(List<String> slocation) {
        this.slocation = slocation;
    }
}
