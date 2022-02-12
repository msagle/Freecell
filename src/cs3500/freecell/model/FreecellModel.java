package cs3500.freecell.model;

import java.util.List;

/**
 * This is the interface of the Freecell model. It is parameterized over the
 * card type, i.e. when you implement it, you can substitute K with your
 * implementation of a card.
 */
public interface FreecellModel<Card> extends FreecellModelState<Card> {

  /**
   * Return a valid and complete deck of cards for a game of Freecell. There is
   * no restriction imposed on the ordering of these cards in the deck. An
   * invalid deck is defined as a deck that has one or more of these flaws: <ul>
   * <li>It does not have 52 cards</li> <li>It has duplicate cards</li> <li>It
   * has at least one invalid card (invalid suit or invalid number) </li> </ul>
   *
   * @return the deck of cards as a list
   */
  List<Card> getDeck();

  /**
   * Deal a new game of freecell with the given deck, with or without shuffling
   * it first. This method first verifies that the deck is valid. It deals the
   * deck among the cascade piles in roundrobin fashion. Thus if there are 4
   * cascade piles, the 1st pile will get cards 0, 4, 8, ..., the 2nd pile will
   * get cards 1, 5, 9, ..., the 3rd pile will get cards 2, 6, 10, ... and the
   * 4th pile will get cards 3, 7, 11, .... Depending on the number of cascade
   * piles, they may have a different number of cards
   *
   * @param numCascadePiles number of cascade piles
   * @param numOpenPiles    number of open piles
   * @param deck            the deck to be dealt
   * @param shuffle         if true, shuffle the deck else deal the deck as-is
   * @throws IllegalArgumentException if the deck is invalid
   */
  void startGame(List<Card> deck, int numCascadePiles, int numOpenPiles, boolean shuffle);

  /**
   * Move a card from the given source pile to the given destination pile, if
   * the move is valid.
   *
   * @param source         the type of the source pile see @link{PileType}
   * @param pileNumber     the pile number of the given type, starting at 0
   * @param cardIndex      the index of the card to be moved from the source
   *                       pile, starting at 0
   * @param destination    the type of the destination pile (see
   * @param destPileNumber the pile number of the given type, starting at 0
   * @throws IllegalArgumentException if the move is not possible {@link
   *                                  PileType})
   * @throws IllegalStateException    if a move is attempted before the game has
   *                                  starts
   */
  void move(PileType source,
            int pileNumber,
            int cardIndex,
            PileType destination,
            int destPileNumber);

  /**
   * Signal if the game is over or not.
   *
   * @return true if game is over, false otherwise
   */
  boolean isGameOver();


}
