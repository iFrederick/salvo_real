package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {
    @Autowired PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository PlayerRep, GameRepository GameRep, GamePlayerRepository GpRep, ShipRepository ShipRep,SalvoRepository SalvoRep,ScoreRepository ScoreRep){
		return (args) ->{
		    //Player repository = PlayerRep donde usamos el .save para guardar en la DB
			Player player1 = new Player("Aquaman",passwordEncoder.encode("123a"));
			PlayerRep.save(player1);
			Player player2 = new Player("Humanity","123b");
			PlayerRep.save(player2);
			Player player3 = new Player ("Goku@super.z",passwordEncoder.encode("123c"));
			PlayerRep.save(player3);
			Player player4 = new Player("PatrullaRoja@Malos.z",passwordEncoder.encode("123d"));
			PlayerRep.save(player4);

		};

			/*/Game repository
			Game game1 = new Game(LocalDateTime.now());
			GameRep.save(game1);
			Game game2 = new Game(LocalDateTime.now());
			GameRep.save(game2);

			//GamePlayer Repository
			GamePlayer gamePlayer1 = new GamePlayer(player1,game1);
			GpRep.save(gamePlayer1);
			GamePlayer gamePlayer2 = new GamePlayer(player2,game1);
			GpRep.save(gamePlayer2);
			GamePlayer gamePlayer3 = new GamePlayer(player3,game2);
			GpRep.save(gamePlayer3);
			GamePlayer gamePlayer4 = new GamePlayer(player4,game2);
			GpRep.save(gamePlayer4);

			//ShipRepository ShipRep   SHIPS DEL PLAYER 1
            Ship ship1 = new Ship ("Destroyer",Arrays.asList(new String[]{"F1","F2"}),gamePlayer1);
            ShipRep.save(ship1);
            Ship ship3 = new Ship ("Destroyer",Arrays.asList(new String[]{"C4","C3"}),gamePlayer1);
            ShipRep.save(ship3);
            //Ships del PLayer 2
            Ship ship2 = new Ship("Submarine", Arrays.asList(new String[]{"A1","A2","A3",}),gamePlayer2);
            ShipRep.save(ship2);

            //Salvo Repository (SalvoRep)
			Salvo salvo1 = new Salvo(1,Arrays.asList(new String[]{"F2","F3","A1"}),gamePlayer1);
			SalvoRep.save(salvo1);
			Salvo salvo2 = new Salvo(1,Arrays.asList(new String[]{"C3"}),gamePlayer2);
			SalvoRep.save(salvo2);

			//Scoore Repository
            Score score1 = new Score(0.5f,player1,game1);
            ScoreRep.save(score1);
            Score score2 = new Score (0.5f,player2,game1);
            ScoreRep.save(score2);
            Score score3 = new Score(1,player3,game2);
            ScoreRep.save(score3);
            Score score4 = new Score(0,player4,game2);
            ScoreRep.save(score4);*/

    }

	@Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired PlayerRepository PlayerRep;

    @Override
    public void init(AuthenticationManagerBuilder check) throws Exception {
        check.userDetailsService(inputName -> {
            Player player = PlayerRep.findByUserName(inputName);
            if (player != null){
                return new User(player.getUserName(),player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            }else {
                throw new UsernameNotFoundException("Unknown Jugatorr: "+ inputName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/**").permitAll()
                .antMatchers("/api/game_view").hasAuthority("USER")
                .antMatchers("/api/**").permitAll()
                .and()
                .formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }
    private void clearAuthenticationAttributes(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
