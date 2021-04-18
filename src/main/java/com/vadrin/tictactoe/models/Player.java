package com.vadrin.tictactoe.models;

import lombok.Data;

@Data
public class Player {
  private Symbol symbol;
  private double moves;

  public Player(Symbol symbol) {
    super();
    this.symbol = symbol;
  }

  public void incrementMoves() {
    moves++;
  }
}
