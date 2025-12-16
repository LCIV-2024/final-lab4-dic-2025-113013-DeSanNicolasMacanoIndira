package com.example.demobase.service;

import com.example.demobase.dto.ScoreboardDTO;
import com.example.demobase.model.Game;
import com.example.demobase.model.Player;
import com.example.demobase.model.Word;
import com.example.demobase.repository.GameRepository;
import com.example.demobase.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreboardServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private ScoreboardService scoreboardService;

    private Player player1;
    private Player player2;
    private Word word;
    private Game game1;
    private Game game2;
    private Game game3;

    @BeforeEach
    void setUp() {
        player1 = new Player(1L, "Juan Pérez", LocalDate.of(2025, 1, 15));
        player2 = new Player(2L, "María García", LocalDate.of(2025, 1, 20));
        word = new Word(1L, "PROGRAMADOR", true);

        game1 = new Game();
        game1.setId(1L);
        game1.setJugador(player1);
        game1.setPalabra(word);
        game1.setResultado("GANADO");
        game1.setPuntaje(20);
        game1.setFechaPartida(LocalDateTime.now());

        game2 = new Game();
        game2.setId(2L);
        game2.setJugador(player1);
        game2.setPalabra(word);
        game2.setResultado("GANADO");
        game2.setPuntaje(20);
        game2.setFechaPartida(LocalDateTime.now());

        game3 = new Game();
        game3.setId(3L);
        game3.setJugador(player1);
        game3.setPalabra(word);
        game3.setResultado("PERDIDO");
        game3.setPuntaje(5);
        game3.setFechaPartida(LocalDateTime.now());
    }

    @Test
    void testGetScoreboard() {
        // Given
        List<Player> players = Arrays.asList(player1, player2);
        List<Game> player1Games = Arrays.asList(game1, game2, game3);
        List<Game> player2Games = Arrays.asList();

        when(playerRepository.findAll()).thenReturn(players);
        when(gameRepository.findByJugador(player1)).thenReturn(player1Games);
        when(gameRepository.findByJugador(player2)).thenReturn(player2Games);

        // When
        List<ScoreboardDTO> result = scoreboardService.getScoreboard();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        ScoreboardDTO player1Stats = result.get(0);
        assertEquals(1L, player1Stats.getIdJugador());
        assertEquals("Juan Pérez", player1Stats.getNombreJugador());
        assertEquals(45, player1Stats.getPuntajeTotal()); // 20 + 20 + 5
        assertEquals(3L, player1Stats.getPartidasJugadas());
        assertEquals(2L, player1Stats.getPartidasGanadas());
        assertEquals(1L, player1Stats.getPartidasPerdidas());

        ScoreboardDTO player2Stats = result.get(1);
        assertEquals(2L, player2Stats.getIdJugador());
        assertEquals(0, player2Stats.getPuntajeTotal());
        assertEquals(0L, player2Stats.getPartidasJugadas());

        verify(playerRepository, times(1)).findAll();
        verify(gameRepository, times(1)).findByJugador(player1);
        verify(gameRepository, times(1)).findByJugador(player2);
    }

    @Test
    void testGetScoreboardByPlayer_Success() {
        // TODO: Implementar el test para testGetScoreboardByPlayer_Success
        // Given
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player1));
        when(gameRepository.findByJugador(player1))
                .thenReturn(Arrays.asList(game1, game2, game3));

        // When
        ScoreboardDTO result = scoreboardService.getScoreboardByPlayer(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getIdJugador());
        assertEquals("Juan Pérez", result.getNombreJugador());
        assertEquals(45, result.getPuntajeTotal()); // 20 + 20 + 5
        assertEquals(3L, result.getPartidasJugadas());
        assertEquals(2L, result.getPartidasGanadas());
        assertEquals(1L, result.getPartidasPerdidas());

        verify(playerRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).findByJugador(player1);
    }

    @Test
    void testGetScoreboardByPlayer_NotFound() {
        // Given
        when(playerRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> scoreboardService.getScoreboardByPlayer(999L));
        verify(playerRepository, times(1)).findById(999L);
        verify(gameRepository, never()).findByJugador(any(Player.class));
    }

    @Test
    void testGetScoreboard_OrderedByScore() {
        // Given
        Player highScorePlayer = new Player(3L, "Alto Puntaje", LocalDate.now());
        Game highScoreGame = new Game();
        highScoreGame.setJugador(highScorePlayer);
        highScoreGame.setPuntaje(100);

        List<Player> players = Arrays.asList(player1, highScorePlayer);
        when(playerRepository.findAll()).thenReturn(players);
        when(gameRepository.findByJugador(player1)).thenReturn(Arrays.asList(game1));
        when(gameRepository.findByJugador(highScorePlayer)).thenReturn(Arrays.asList(highScoreGame));

        // When
        List<ScoreboardDTO> result = scoreboardService.getScoreboard();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        // El jugador con mayor puntaje debe estar primero
        assertTrue(result.get(0).getPuntajeTotal() >= result.get(1).getPuntajeTotal());
    }
}

