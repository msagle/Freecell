package cs3500.freecell.controller;

import java.util.List;

/**
 * Interface for the freecell game controller. An implementation will work with
 * the IFreeCellModel interface to provide a game of freecell
 */
public interface FreecellController<Card> {

  /**
   * Start and play a new game of freecell with the provided deck. This deck
   * should be used as-is. This method returns only when the game is over
   * (either by winning or by quitting)
   *
   * @param deck        the deck to be used to play this game
   * @param numCascades the number of cascade piles
   * @param numOpens    the number of open piles
   * @param shuffle     shuffle the deck if true, false otherwise
   * @throws IllegalStateException if writing to the Appendable object used by it fails or reading
   *                               from the provided Readable fails
   * @throws IllegalArgumentException if the deck provided to it are null
   */
  void playGame(List<Card> deck, int numCascades, int numOpens, boolean shuffle);
}
