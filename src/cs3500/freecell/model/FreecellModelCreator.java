package cs3500.freecell.model;

import cs3500.freecell.model.multimove.ComplexFreecellModel;

/**
 * Factory class that creates the desired Freecell game.
 * Can create a single-card move or multi-card move game.
 */
public class FreecellModelCreator {

  /**
   * Represents the type of Freecell game that can be cretaed and played.
   * SINGLEMOVE only allows one cascade card to be moved at a time.
   * MULTIMOVE allows proper stacks of cascade cards to be moved to other cascade piles.
   */
  public enum GameType {
    SINGLEMOVE, MULTIMOVE;
  }

  /**
   * Builds the desired freecell game type.
   *
   * @param type type of game to be creted, single or multi move
   * @return the created freecell object
   */
  public static FreecellModel create(GameType type) {
    switch (type) {
      case SINGLEMOVE:
        return new SimpleFreecellModel();
      case MULTIMOVE:
        return new ComplexFreecellModel();
      default:
        throw new IllegalArgumentException(("Invalid game type!"));
    }
  }
}
