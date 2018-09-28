package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

//conecta con la BD
@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime CreDate;
    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private List<Ship> ships;
    @OneToMany(mappedBy = "gamePlayer")
    private List<Salvo>salvos;
    private String state;

    //Conexión de la BD y con la clase Player
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "player_id")
    private Player jugador;

    //Conexión de la BD y con la clase Game
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "game_id")
    private Game juego;

    public GamePlayer(){}

    public GamePlayer(Player jugador, Game juego) {
        this.jugador = jugador;
        this.juego = juego;
    }

    public Score getScore(){
        return this.jugador.getScore(this.juego);
    }

    public Player getJugador() {
        return jugador;
    }

    public void setJugador(Player jugador) {
        this.jugador = jugador;
    }

    public Game getJuego() {
        return juego;
    }

    public void setJuego(Game juego) {
        this.juego = juego;

    }

     public LocalDateTime getCreDate()   {
        return CreDate;
      }

    public void setCreDate(LocalDateTime creDate) {
        CreDate = creDate;
    }
    public void addShip(Ship ship){
        ships.add(ship);
    }
    @JsonIgnore
    public List<Ship> getShips() {
        return ships;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public List<Salvo> getSalvos() {
        return salvos;
    }

    public void setSalvos(List<Salvo>salvos) {
        this.salvos = salvos;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
