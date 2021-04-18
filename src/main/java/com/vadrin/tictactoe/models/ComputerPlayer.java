package com.vadrin.tictactoe.models;

import com.vadrin.neuroevolution.models.Genome;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ComputerPlayer extends Player {

  private Genome genome;

  public ComputerPlayer(Symbol symbol, Genome genome) {
    super(symbol);
    this.genome = genome;
  }

}
