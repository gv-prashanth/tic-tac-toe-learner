package com.vadrin.tictactoe.models;

import lombok.Data;

@Data
public class Board {

  private Symbol[][] symbols;

  public Board(int size) {
    super();
    symbols = new Symbol[size][size];
  }
  
}
