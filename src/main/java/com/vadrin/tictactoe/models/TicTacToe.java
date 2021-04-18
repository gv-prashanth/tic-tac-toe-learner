package com.vadrin.tictactoe.models;

import com.vadrin.tictactoe.models.exceptions.GameOverException;
import com.vadrin.tictactoe.models.exceptions.IllegalMoveException;

import lombok.Data;
import lombok.Setter;

@Data
public class TicTacToe {

  private Player crossPlayer;
  private Player circlePlayer;
  private Board board;

  @Setter(lombok.AccessLevel.NONE)
  private Player nextMovePlayer;

  public void play(int xPos, int yPos) throws IllegalMoveException, GameOverException {
    if (isGameOver())
      throw new GameOverException();
    if (board.getSymbols()[xPos][yPos] != null)
      throw new IllegalMoveException();
    Symbol[][] temp = board.getSymbols();
    temp[xPos][yPos] = nextMovePlayer.getSymbol();
    nextMovePlayer.incrementMoves();
    board.setSymbols(temp);
    nextMovePlayer = nextMovePlayer == crossPlayer ? circlePlayer : crossPlayer;
  }
  
  private boolean isGameOver() {
    return false;
  }

  public TicTacToe(int size, Player player1, Player player2) {
    super();
    this.board = new Board(size);
    this.crossPlayer = player1.getSymbol() == Symbol.X ? player1 : player2;
    this.circlePlayer = player1.getSymbol() == Symbol.O ? player1 : player2;
    this.nextMovePlayer = crossPlayer;
  }

}
