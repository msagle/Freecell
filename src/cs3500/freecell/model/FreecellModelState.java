package cs3500.freecell.model;

/**
 * This interface represents different operations that a freecell model must support to return
 * various aspects of its state. This interface does not provide any operations to mutate the state
 * of a freecell model.
 *
 * @param <Card> the Card type
 */
public interface FreecellModelState<Card> {

  /**
   * Get the number of cards in a given foundation pile.
   *
   * @param index the index of the foundation pile, starting at 0
   * @return the number of cards in the given foundation pile
   * @throws IllegalArgumentException if the provided index is invalid
   * @throws IllegalStateException    if the game has not started
   */
  int getNumCardsInFoundationPile(int index);

  /**
   * Get the number of cascade piles in this game of freecell.
   *
   * @return the number of cascade piles, as an integer, or -1 if the game has not started yet
   */
  int getNumCascadePiles();

  /**
   * Get the number of cards in a given cascade pile.
   *
   * @param index the index of the cascade pile, starting at 0
   * @return the number of cards in the given cascade pile
   * @throws IllegalArgumentException if the provided index is invalid
   * @throws IllegalStateException    if the game has not started
   */
  int getNumCardsInCascadePile(int index);

  /**
   * Get the number of cards in a given open pile.
   *
   * @param index the index of the open pile, starting at 0
   * @return the number of cards in the given open pile
   * @throws IllegalArgumentException if the provided index is invalid
   * @throws IllegalStateException    if the game has not started
   */
  int getNumCardsInOpenPile(int index);


  /**
   * Get the number of open piles in this game of freecell.
   *
   * @return the number of open piles, as an integer, or -1 if the game has not started yet
   */
  int getNumOpenPiles();


  /**
   * Get the card at the provided index in the provided foundation pile.
   *
   * @param pileIndex the index of the foundation pile, starting at 0
   * @param cardIndex the index of the card in the above foundation pile, starting at 0
   * @return the card at the provided indices
   * @throws IllegalArgumentException if the pileIndex or cardIndex is invalid
   * @throws IllegalStateException    if the game has not started
   */
  Card getFoundationCardAt(int pileIndex, int cardIndex);

  /**
   * Get the card at the provided index in the provided cascade pile.
   *
   * @param pileIndex the index of the cascade pile, starting at 0
   * @param cardIndex the index of the card in the above cascade pile, starting at 0
   * @return the card at the provided indices
   * @throws IllegalArgumentException if the pileIndex or cardIndex is invalid
   * @throws IllegalStateException    if the game has not started
   */
  Card getCascadeCardAt(int pileIndex, int cardIndex);

  /**
   * Get the card in the given open pile.
   *
   * @param pileIndex the index of the open pile, starting at 0
   * @return the card at the provided index, or null if there is no card there
   * @throws IllegalArgumentException if the pileIndex is invalid
   * @throws IllegalStateException    if the game has not started
   */
  Card getOpenCardAt(int pileIndex);
}
