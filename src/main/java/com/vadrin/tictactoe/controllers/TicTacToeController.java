package com.vadrin.tictactoe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vadrin.neuroevolution.models.Genome;
import com.vadrin.neuroevolution.models.NodeGeneType;
import com.vadrin.neuroevolution.models.Pool;
import com.vadrin.neuroevolution.models.exceptions.InvalidInputException;
import com.vadrin.neuroevolution.services.NEAT;
import com.vadrin.tictactoe.models.Board;
import com.vadrin.tictactoe.models.ComputerPlayer;
import com.vadrin.tictactoe.models.Player;
import com.vadrin.tictactoe.models.Symbol;
import com.vadrin.tictactoe.models.TicTacToe;
import com.vadrin.tictactoe.models.exceptions.GameOverException;
import com.vadrin.tictactoe.models.exceptions.IllegalMoveException;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TicTacToeController {

  @Autowired
  private NEAT neat;

  private Pool pool;

  @PostMapping("/pool")
  public Pool instantiate(@RequestParam int size) {
    this.pool = new Pool(150, size * size, size * size);
    return this.pool;
  }

  @PutMapping("/pool")
  public Pool stepOneGeneration() {
    pool.getGenomes().parallelStream().forEach(this::loadFitness);
    neat.stepOneGeneration(pool);
    return pool;
  }

  @GetMapping("/pool")
  public Pool getPool() {
    return pool;
  }

  private void loadFitness(Genome genome) {
    double fitnessToSetWhenPlayedAsCross = pool.getGenomes().parallelStream()
        .filter(g -> !g.getId().equals(genome.getId())).reduce(0d, (partial, current) -> {
          ComputerPlayer asCross = new ComputerPlayer(Symbol.X, genome);
          ComputerPlayer opponent = new ComputerPlayer(Symbol.O, current);
          TicTacToe tictactoe = new TicTacToe(
              (int) Math.sqrt(pool.getGenomes().get(0).getNodeGenes(NodeGeneType.INPUT).size()), asCross, opponent);
          while (true) {
            try {
              autoPlay(tictactoe);
            } catch (GameOverException e) {
              break;
            } catch (InvalidInputException e) {
              e.printStackTrace();
            }
          }
          return partial + 0d;
        }, Double::sum);
    
    double fitnessToSetWhenPlayedAsCircle = pool.getGenomes().parallelStream()
        .filter(g -> !g.getId().equals(genome.getId())).reduce(0d, (partial, current) -> {
          ComputerPlayer asCircle = new ComputerPlayer(Symbol.O, genome);
          ComputerPlayer opponent = new ComputerPlayer(Symbol.X, current);
          TicTacToe tictactoe = new TicTacToe(
              (int) Math.sqrt(pool.getGenomes().get(0).getNodeGenes(NodeGeneType.INPUT).size()), asCircle, opponent);
          while (true) {
            try {
              autoPlay(tictactoe);
            } catch (GameOverException e) {
              break;
            } catch (InvalidInputException e) {
              e.printStackTrace();
            }
          }
          return partial + 0d;
        }, Double::sum);
    
    genome.setFitnessScore(fitnessToSetWhenPlayedAsCross+fitnessToSetWhenPlayedAsCircle);
  }

  @PostMapping("/tictactoe")
  public TicTacToe startGame(@RequestParam int size, @RequestParam Symbol userSymbol)
      throws InvalidInputException, IllegalMoveException, GameOverException {
    Player user = new Player(userSymbol);
    ComputerPlayer computer = new ComputerPlayer(userSymbol == Symbol.O ? Symbol.X : Symbol.O,
        pool.getGenomes().get(0));
    TicTacToe tictactoe = new TicTacToe(size, user, computer);
    if (userSymbol == Symbol.O)
      autoPlay(tictactoe);
    return tictactoe;
  }

  @PutMapping("/tictactoe")
  public TicTacToe play(@RequestBody TicTacToe tictactoe, int xPos, int yPos)
      throws IllegalMoveException, GameOverException, InvalidInputException {
    tictactoe.play(xPos, yPos);
    autoPlay(tictactoe);
    return tictactoe;
  }

  private void autoPlay(TicTacToe tictactoe) throws InvalidInputException, GameOverException {
    if (tictactoe.getNextMovePlayer().getClass().isAssignableFrom(ComputerPlayer.class)) {
      Genome thisGenome = ((ComputerPlayer) tictactoe.getNextMovePlayer()).getGenome();
      double[] result = neat.process(thisGenome, convertBoardToArray(tictactoe.getBoard()));
    } else {
      log.error("Something wrong in design!");
    }
  }

  private double[] convertBoardToArray(Board board) {
    Symbol[] symbols = getSymbolsLinear(board.getSymbols());
    double[] toReturn = new double[symbols.length];
    for(int i=0; i<symbols.length;i++) {
      if(symbols[i]==Symbol.X)
        toReturn[i] = 1d;
      if(symbols[i]==Symbol.O)
        toReturn[i] = 0d;
      else
        toReturn[i] = 0.5d;
    }
    return toReturn;
  }
  
  
  private Symbol[] getSymbolsLinear(Symbol[][] symbols) {
    Symbol[] toReturn = new Symbol[symbols.length*symbols.length];
    int next = 0;
    for(int i=0; i<symbols.length;i++) {
      for(int j=0; j<symbols[i].length;j++) {
        toReturn[next] = symbols[i][j];
        next++;
      }
    }
    return toReturn;
  }

}
