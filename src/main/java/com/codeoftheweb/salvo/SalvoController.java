package com.codeoftheweb.salvo;

import com.sun.javafx.collections.MappingChange;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
public class SalvoController {

    @Autowired private GameRepository GameRep;
    @Autowired private PlayerRepository playerRep;
    @Autowired private GamePlayerRepository GPrep;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ScoreRepository scoreRep;
    @Autowired private ShipRepository shipRep;
    @Autowired private SalvoRepository salvoRep;

    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Object>cheat(@PathVariable long id, Authentication authentication){
        Player player = getLoggedPlayer(authentication);
        GamePlayer gamePlayer = GPrep.findById(id).orElse(null);
        if (gamePlayer == null){
            return new ResponseEntity<>(makeMap("error","Forbidden"),HttpStatus.FORBIDDEN);
        }
        if(player.getId() != gamePlayer.getJugador().getId()){
            return new ResponseEntity<>(makeMap("error","Unauthorized"),HttpStatus.UNAUTHORIZED);
        }
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("id",gamePlayer.getJuego().getId());
        dto.put("gamePlayers",gamePlayerList(gamePlayer.getJuego().getGamePlayers()));
        dto.put("ships", shipList(gamePlayer.getShips()));
        dto.put(("salvoes"),getSalvoList(gamePlayer.getJuego()));
        dto.put("hits",makeHitsDTO(gamePlayer,getOpponent(gamePlayer)));
        dto.put("gameState",gamePlayer.getState());
        dto.put("created",gamePlayer.getCreDate());
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }
    private Map<String,Object> makeHitsDTO(GamePlayer opponentGP, GamePlayer selfGP) {
        Map<String,Object> dto=new LinkedHashMap<>();
        dto.put("self",getHits(selfGP,opponentGP));
        dto.put("opponent",getHits(opponentGP,selfGP));
        return dto;
    }
    private GamePlayer getOpponent (GamePlayer gamePlayer){
        List<GamePlayer>gamePlayers = gamePlayer.getJuego().getGamePlayers();
        for(GamePlayer gp : gamePlayers){
            if(gamePlayer.getId() != gp.getId()){
                return gp;
            }
        }
        return null;
    }
    private Player getLoggedPlayer(Authentication authentication){
        return playerRep.findByUserName(authentication.getName());
    }
   /* public Map<String,Object>getGameView(@PathVariable Long id){
        GamePlayer gamePlayer = GPrep.findById(id).get();
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("id",gamePlayer.getJuego().getId());
        dto.put("gamePlayers",gamePlayerList(gamePlayer.getJuego().getGamePlayers()));
        dto.put("ships",shipList(gamePlayer.getShips()));
        dto.put(("salvoes"),getSalvoList(gamePlayer.getJuego()));
        return dto;*/

    private List<Map<String,Object>>salvoList(List<Salvo>salvos){
        return salvos
                .stream()
                .map(salvo -> SalvoDTO(salvo))
                .collect(Collectors.toList());
    }
    private Map<String,Object>SalvoDTO(Salvo salvo){
        Map<String,Object> dto = new LinkedHashMap<>();
            dto.put("Turno",salvo.getTurn());
            dto.put("Location",salvo.getLocations());
            dto.put("Player",salvo.getGamePlayer().getJugador().getId());
            return dto;
    }
    private List<Map<String,Object>> getSalvoList(Game game){
        List<Map<String,Object>> myList = new ArrayList<>();
        game.getGamePlayers().forEach(gamePlayer -> myList.addAll(salvoList(gamePlayer.getSalvos())));
        return myList;
    }

    private List<Object>shipList(List<Ship>ships){
        return ships
                .stream()
                .map(ship -> ShipDTO(ship))
                .collect(Collectors.toList());
    }
    private Map<String,Object>ShipDTO(Ship ship){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("Type",ship.getType());
        dto.put("Location",ship.getSlocation());
        return dto;
    }

    @RequestMapping("/games")
    public Map<String,Object>getPlayer(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            dto.put("player", "Guest");
        } else {
            Player player = playerRep.findByUserName(authentication.getName());
            dto.put("player", playerDTO(player));
        }
        dto.put("games",getGames());
        return dto;
    }
    public List<Map<String,Object>>getGames(){
        return GameRep
                .findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(Collectors.toList());
    }
    private Map<String,Object>makeGameDTO(Game game){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("id",game.getId());
        dto.put("created",game.getDateTime());
        dto.put("gamePlayers",gamePlayerList(game.getGamePlayers()));
        dto.put("scores",scoreList(game.getScores()));
        return dto;
    }
    private List<Map<String,Object>>scoreList(Set<Score> score){
        return score
                .stream()
                .map(scores -> scoreDTO (scores))
                .collect(Collectors.toList());

    }
    private Map<String,Object> scoreDTO(Score score){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("playerID",score.getPlayer().getId());
        dto.put("score",score.getScore());
        dto.put("finishDate",score.getFinish());
        return dto;

    }
    private List<Map<String,Object>>gamePlayerList(List<GamePlayer> gamePlayers){
        return gamePlayers
                .stream()
                .map(gamePlayer -> gamePlayerDTO(gamePlayer))
                .collect(Collectors.toList());
    }
    private Map<String,Object>gamePlayerDTO(GamePlayer gamePlayer){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("id",gamePlayer.getId());
        dto.put("joinDate",gamePlayer.getCreDate());
        dto.put("player",playerDTO(gamePlayer.getJugador()));
     //   if(gamePlayer.getScore() != null)
      //      dto.put("score", gamePlayer.getScore().getScore());

        return dto;
    }
    private Map<String,Object>playerDTO(Player player){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("id",player.getId());
        dto.put("email",player.getUserName());
      //  dto.put("score", scoreDTO(player));

        return dto;
    }
    @RequestMapping("/leaderBoard")
    private List<Map<String, Object>> getplayers(){
        return playerRep.findAll()
                .stream()
                .map( player -> playerDTO(player))
                .collect(Collectors.toList());
    }
    private Map<String,Object> leaderBoarddto(Player player) {
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("name",player.getUserName());
        dto.put("score",player.getTotalScore());
        dto.put("wins",player.getWins());
        dto.put("loses",player.getLoses());
        dto.put("ties",player.getTies());
        return dto;
    }
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    // Codigo para Registrar a un Jugador
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(@RequestParam String email, @RequestParam String pwd) {
        if (email.isEmpty() || pwd.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        if (playerRep.findByUserName(email) !=  null) {
            return new ResponseEntity<>(makeMap("error", "User already exist!"), HttpStatus.FORBIDDEN);
        }
        playerRep.save(new Player(email, passwordEncoder.encode(pwd)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    private Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }
    
    @RequestMapping(path ="/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>>createGame (Authentication authentication){
        if(authentication == null)
            return new ResponseEntity<>(makeMap("error", "Naur, Logeate o Naur, puedes Registrarte too, Guten luck."), HttpStatus.UNAUTHORIZED);
        Game newGame = (new Game());
        GameRep.save(newGame);
        Player player = playerRep.findByUserName(authentication.getName());
        GamePlayer newGameplayer = (new GamePlayer(player,newGame));
        newGameplayer.setState("WAITINGFOROPP");
        GPrep.save(newGameplayer);
        return new ResponseEntity<>(makeMap("gpid", newGameplayer.getId()), HttpStatus.CREATED);                          //cambio de clave de gpid por gamePlayers
       }
    }

    @RequestMapping(path = "/game/{gameId}/players",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>>joinGame (@PathVariable long gameId,Authentication authentication){
        if(isGuest(authentication))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        Player player = getLoggedPlayer(authentication);
        Game game = GameRep.findById(gameId).orElse(null);
        if (game == null)
            return new ResponseEntity<>(makeMap("error","No such game noobster"),HttpStatus.FORBIDDEN);
        if (game.getGamePlayers().size()>2)
            return new ResponseEntity<>(makeMap("error","Esta Full"),HttpStatus.FORBIDDEN);
        // ERRASE 236
        GameRep.save(game);
        GamePlayer gamePlayer = new GamePlayer(player,game);
        // 239 & 240 could be the same as 241 & 242
        gamePlayer.setState("PLACESHIPS");
        GPrep.save(gamePlayer);
        game.getGamePlayers().get(0).setState("PLACESHIPS");
        GPrep.save(game.getGamePlayers().get(0));
        // if u delete some of them, delete the last one 241 & 242
        return new ResponseEntity<>(makeMap("gpid",gamePlayer.getId()),HttpStatus.CREATED);
    }

    @RequestMapping(path ="games/players/{gpId}/ships",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>>addShips(@PathVariable long gpId,@RequestBody  List<Map<String,Object>> shipObjects, Authentication authentication){
        if (isGuest(authentication))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Player player = getLoggedPlayer(authentication);
        GamePlayer gamePlayer = GPrep.findById(gpId).orElse(null);

        if (gamePlayer == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (player.getId() != gamePlayer.getJugador().getId())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (gamePlayer.getShips().size()>= 5)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        for (Map<String,Object> ship : shipObjects){
            Ship barco = new Ship((String) ship.get("type"),(List<String>) ship.get("slocation"),gamePlayer);
            gamePlayer.addShip(barco);
            shipRep.save(barco);
        }

        return new ResponseEntity<>(makeMap("OK","Ships Summoneados"),HttpStatus.CREATED);
    }
    @RequestMapping(path ="game/players/{gpId/salvos",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>>addSalvos(@PathVariable long gpId,@RequestBody List<Salvo>salvos, Authentication authentication) {
        if (isGuest(authentication))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Player player = getLoggedPlayer(authentication);
        GamePlayer gamePlayer = GPrep.findById(gpId).orElse(null);

        if (gamePlayer == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (player.getId() != gamePlayer.getJugador().getId())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (gamePlayer.getSalvos().size() >= 2)
            return new ResponseEntity<>(makeMap("error", "Ya tiraste salvos"), HttpStatus.FORBIDDEN);
        for (Salvo salvo : salvos) {
            salvo.setGamePlayer(gamePlayer);
            salvoRep.save(salvo);
        }
        return new ResponseEntity<>(makeMap("OK", "Boom shacalaca,salvos fired"), HttpStatus.CREATED);
    }
    //Calcular Hits
    private List<Map> getHits(GamePlayer gamePlayer, GamePlayer opponentGameplayer) {
        List<Map> hits = new ArrayList<>();
        Integer carrierDamage = 0;
        Integer battleshipDamage = 0;
        Integer submarineDamage = 0;
        Integer destroyerDamage = 0;
        Integer patrolboatDamage = 0;
        List <String> carrierLocation = new ArrayList<>();
        List <String> battleshipLocation = new ArrayList<>();
        List <String> submarineLocation = new ArrayList<>();
        List <String> destroyerLocation = new ArrayList<>();
        List <String> patrolboatLocation = new ArrayList<>();
        if (gamePlayer != null) {
            gamePlayer.getShips().forEach(ship -> {
                switch (ship.getType()) {
                    case "carrier":
                        carrierLocation.addAll(ship.getSlocation());
                        break;
                    case "battleship":
                        battleshipLocation.addAll(ship.getSlocation());
                        break;
                    case "submarine":
                        submarineLocation.addAll(ship.getSlocation());
                        break;
                    case "destroyer":
                        destroyerLocation.addAll(ship.getSlocation());
                        break;
                    case "patrolboat":
                        patrolboatLocation.addAll(ship.getSlocation());
                        break;
                }
            });
        }
        if (opponentGameplayer != null){
            for (Salvo salvo : opponentGameplayer.getSalvos()) {
                Integer carrierHitsInTurn = 0;
                Integer battleshipHitsInTurn = 0;
                Integer submarineHitsInTurn = 0;
                Integer destroyerHitsInTurn = 0;
                Integer patrolboatHitsInTurn = 0;
                Integer missedShots = salvo.getLocations().size();
                Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();
                Map<String, Object> damagesPerTurn = new LinkedHashMap<>();
                List<String> salvoLocationsList = new ArrayList<>();
                List<String> hitCellsList = new ArrayList<>();
                salvoLocationsList.addAll(salvo.getLocations());
                for (String salvoShot : salvoLocationsList) {
                    if (carrierLocation.contains(salvoShot)) {
                        carrierDamage++;
                        carrierHitsInTurn++;
                        hitCellsList.add(salvoShot);
                        missedShots--;
                    }
                    if (battleshipLocation.contains(salvoShot)) {
                        battleshipDamage++;
                        battleshipHitsInTurn++;
                        hitCellsList.add(salvoShot);
                        missedShots--;
                    }
                    if (submarineLocation.contains(salvoShot)) {
                        submarineDamage++;
                        submarineHitsInTurn++;
                        hitCellsList.add(salvoShot);
                        missedShots--;
                    }
                    if (destroyerLocation.contains(salvoShot)) {
                        destroyerDamage++;
                        destroyerHitsInTurn++;
                        hitCellsList.add(salvoShot);
                        missedShots--;
                    }
                    if (patrolboatLocation.contains(salvoShot)) {
                        patrolboatDamage++;
                        patrolboatHitsInTurn++;
                        hitCellsList.add(salvoShot);
                        missedShots--;
                    }
                }
                damagesPerTurn.put("carrierHits", carrierHitsInTurn);
                damagesPerTurn.put("battleshipHits", battleshipHitsInTurn);
                damagesPerTurn.put("submarineHits", submarineHitsInTurn);
                damagesPerTurn.put("destroyerHits", destroyerHitsInTurn);
                damagesPerTurn.put("patrolboatHits", patrolboatHitsInTurn);
                damagesPerTurn.put("carrier", carrierDamage);
                damagesPerTurn.put("battleship", battleshipDamage);
                damagesPerTurn.put("submarine", submarineDamage);
                damagesPerTurn.put("destroyer", destroyerDamage);
                damagesPerTurn.put("patrolboat", patrolboatDamage);
                hitsMapPerTurn.put("turn", salvo.getTurn());
                hitsMapPerTurn.put("hitLocations", hitCellsList);
                hitsMapPerTurn.put("damages", damagesPerTurn);
                hitsMapPerTurn.put("missed", missedShots);
                hits.add(hitsMapPerTurn);
            }
        }
        return hits;
    }
}
