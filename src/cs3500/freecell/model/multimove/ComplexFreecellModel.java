package cs3500.freecell.model.multimove;

import java.util.ArrayList;
import java.util.List;

import cs3500.freecell.model.Card;
import cs3500.freecell.model.PileType;
import cs3500.freecell.model.SimpleFreecellModel;

/**
 * Contains the data for a playable Freecell game.
 * Keeps track of the model's state: all of the card piles, the number of each type of pile,
 * and allows for the movement of MULTIPLE cards between cascade piles.
 */
public class ComplexFreecellModel extends SimpleFreecellModel {

  /**
   * Builds the multi-move freecell game.
   */
  public ComplexFreecellModel() {
    super();
  }

  @Override
  public void move(PileType source, int pileNumber, int cardIndex,
                   PileType destination, int destPileNumber) {

    throwGameException(); //throws exception if the game hasn't started

    //call super move method if source is O/F or if just one card at the end of a cascade pile
    if (source == PileType.OPEN || source == PileType.FOUNDATION
            || (source == PileType.CASCADE && singleCascadeCard(pileNumber, cardIndex))) {
      super.move(source, pileNumber, cardIndex, destination, destPileNumber);
      return;
    }
    else { //when source is CASCADE and there is a stack of cards:
      if (!validCStart(pileNumber, cardIndex)) { //checks for a valid starting position
        throw new IllegalArgumentException(
                "Illegal move. Cannot move the card at the specified index.");
      }
      //cannot move a pile to open/foundation
      else if (destination == PileType.OPEN || destination == PileType.FOUNDATION ) {
        throw new IllegalArgumentException("Illegal move. "
                + "Cannot move cards to the specified index.");
      }

      List<Card> cPile = new ArrayList<Card>();
      cPile = getCascadePile(pileNumber);
      List<Card> moveMe = cPile.subList(cardIndex, cPile.size()); //stack of cards to move

      //adds moveMe to the desired destination if possible, otherwise throws an exception
      if (canMoveTo(pileNumber, cardIndex, destPileNumber)) {
        moveOne(pileNumber, cardIndex, destPileNumber); //moves cards to new pile/removes from old
      }
      else {
        throw new
                IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
      }
    }
  }

  /**
   * Ensures that there exists a card at the given index in the given cascade pile.
   * and determines if it can move. If the given card is in the middle of a cascade pile,
   * checks if the cards on top of it are arranged so that the entire stack can be moved.
   *
   * @param pileNumber index of cascade pile
   * @param cardIndex  index of card in pile
   * @return true if the card exists and is at the end of the pile
   * @throws IllegalArgumentException if the given card cannot be moved
   */
  private boolean validCStart(int pileNumber, int cardIndex) {
    throwIndexException(getNumCascadePiles(), pileNumber); //does starting index exist?
    throwIndexException(getNumCardsInCascadePile(pileNumber),
            cardIndex); //checks that the original card exists
    return validRun(pileNumber, cardIndex); //determines if stack can be moved
  }

  /**
   * Determines if there is a valid run of cards from the given card index.
   * A valid run consists of cards in descending rank and alternating color.
   *
   * @param pileNumber cascade pile number
   * @param cardIndex deepest card in the run to move
   * @return true if the run is valid, false otherwise
   */
  private boolean validRun(int pileNumber, int cardIndex) {
    List<Card> pile = getCascadePile(pileNumber);
    int pileSize = pile.size();
    //card index doesn't exist in pile
    if (cardIndex >= pileSize || cardIndex < 0 ) {
      return false;
    }
    else if (cardIndex == pileSize - 1) { //topmost card can be moved
      return true;
    }
    else {
      //loops through making sure that the card is a valid run
      for (int i = cardIndex + 1; i < pileSize ; i++) {
        Card low = pile.get(i); //card deeper in the pile
        Card high = pile.get(i - 1);  //card closer to top
        if (!checkOrder(low, high)) { //checks numerical run
          return false;
        }
        if (low.getSuit().isBlackCard() == high.getSuit().isBlackCard()) { //checks color switching
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Determines whether a card (and its remaining stack) can be moved to the desired location.
   * A stack of cascade cards can only be moved to another cascade pile. A stack can be moved
   * if it is the proper size, a valid run, and if the destination card is a compatible match.
   * @param pileNumber source cascade pile number
   * @param cardIndex beginning card in cascade pile
   * @param destPileNumber destination cascade pile number
   * @return true if the stack of cards can be moved
   */
  private boolean canMoveTo(int pileNumber, int cardIndex, int destPileNumber) {
    List<Card> pile = getCascadePile(pileNumber);
    List<Card> moveMe = pile.subList(cardIndex, pile.size());
    int moveSize = moveMe.size();

    throwIndexException(getNumCascadePiles(), destPileNumber); //checks that the final pile exists

    if (getCascadePile(destPileNumber) == null) {
      return true;
    }
    else {
      int sizePile = getNumCardsInCascadePile(destPileNumber); //size of dest. pile

      if (sizePile == 0) {
        return validRun(pileNumber, cardIndex) && properSize(moveMe, destPileNumber);
      }

      Card last = getCascadePile(destPileNumber).get(sizePile - 1); //top card in dest. pile
      //checks color, if the stack is a valid run, and if the size of the stack is acceptable
      return (last.isBlackCard() != moveMe.get(0).isBlackCard())
              && checkOrder(moveMe.get(0), last)
              && validRun(pileNumber, cardIndex) && properSize(moveMe, destPileNumber);
    }
  }

  /**
   * Determines if the given stack of cards is small enough to be moved.
   * A stack of cascade cards can be moved to another cascade pile if its size is less
   * than (n + 1)*2^k, where n is the number of empty open piles,
   * and k is the number of empty cascade piles. If moving to an empty cascade pile, that
   * pile does not count as empty.
   * @param moveMe list to check
   * @param destPileNumber pile to move to
   * @return true if the stack is small enough to move
   */
  private boolean properSize(List<Card> moveMe, int destPileNumber) {
    int n = numEmptyOpen();
    int k = numEmptyCascade();

    if (getNumCardsInCascadePile(destPileNumber) == 0) {
      return moveMe.size() <= ((n + 1) * (Math.pow(2, k - 1)));
    }
    else {
      return moveMe.size() <= ((n + 1) * (Math.pow(2, k)));
    }
  }

  /**
   * Removes a stack of cards from a given cascade pile and adds them to the given
   * cascade destination pile.
   * @param pileNumber source cascade pile number
   * @param cardIndex beginning card in cascade pile
   * @param destPileNumber destination cascade pile number
   */
  private void moveOne(int pileNumber, int cardIndex, int destPileNumber) {
    while (getNumCardsInCascadePile(pileNumber) > cardIndex) {
      getCascadePile(destPileNumber).add(getCascadePile(pileNumber).remove(cardIndex));
    }
  }

  /**
   * Determines if the given card is in the middle of a cascade pile or at the end.
   * @param pileNumber cascade pile number
   * @param cardIndex card in cascade pile
   * @return true if the given card is at the top (end) of the cascade pile
   */
  private boolean singleCascadeCard(int pileNumber, int cardIndex) {
    return cardIndex == getNumCardsInCascadePile(pileNumber) - 1;
  }

  /**
   * Returns the number of empty open piles. Used to determine if a stack of cards
   * can be moved or if it is too large.
   * @return number of empty open piles
   */
  private int numEmptyOpen() {
    int count = 0;

    for (int i = 0; i < getNumOpenPiles(); i++ ) {
      if (getNumCardsInOpenPile(i) == 0 ) {
        count++;
      }
    }
    return count;
  }

  /**
   * Returns the number of empty cascade piles. Used to determine if a stack of cards
   * can be moved or if it is too large.
   * @return number of empty cascade piles
   */
  private int numEmptyCascade() {
    int count = 0;

    for (int i = 0; i < getNumCascadePiles(); i++ ) {
      if (getNumCardsInCascadePile(i) == 0) {
        count++;
      }
    }
    return count;
  }
}
