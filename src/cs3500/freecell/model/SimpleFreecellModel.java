package cs3500.freecell.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Contains the data for a playable Freecell game.
 */
public class SimpleFreecellModel implements FreecellModel<Card> {
  private final int numFP;
  private int numCP;
  private int numOP;

  private ArrayList<Stack<Card>> foundationPiles;
  private ArrayList<ArrayList<Card>> cascadePiles;
  private Card [] openPiles;

  /**
   * Builds the Freecell game.
   */
  public SimpleFreecellModel() {
    this.numFP = 4;
    this.numCP = -1;
    this.numOP = -1;
    this.foundationPiles = new ArrayList<Stack<Card>>();
  }
  
  @Override
  public List<Card> getDeck() { //creates an unshuffled deck
    //lists of cards, suits, ranks
    ArrayList<Card> cards = new ArrayList<Card>();
    ArrayList<Suit> suits = new ArrayList<Suit>(
            Arrays.asList(new Suit(SuitType.HEART), new Suit(SuitType.DIAMOND),
                    new Suit(SuitType.SPADE), new Suit(SuitType.CLUB)));
    ArrayList<Character> ranks = new ArrayList<Character>(
            Arrays.asList('A', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'J', 'Q', 'K'));

    for (int i = 0; i < suits.size(); i = i + 1) { //executes 4 times, once for each suit
      for (int j = 0; j < ranks.size(); j = j + 1) { //executes 13 times, once for each rank
        cards.add(new Card(ranks.get(j), suits.get(i)));
      }
    }
    return cards;
  }

  @Override
  public void startGame(List<Card> deck, int numCascadePiles, int numOpenPiles, boolean shuffle) {
    //checks validity of deck and number of piles
    if (!validDeck(deck)) {
      throw new IllegalArgumentException("Provided deck is invalid.");
    }
    else if (numCascadePiles < 4) {
      throw new IllegalArgumentException("There must be at least 4 cascade piles.");
    }
    else if (numOpenPiles < 1) {
      throw new IllegalArgumentException("There must be at least 1 open piles.");
    }

    //creates an empty stack for each foundation pile
    ArrayList<Stack<Card>> fp = new ArrayList<Stack<Card>>();
    for (int i = 0; i < 4; i++) {
      fp.add(new Stack<Card>());
    }
    this.foundationPiles = fp;

    //sets Cascade/Open piles & info
    this.numCP = numCascadePiles;
    this.cascadePiles = new ArrayList<ArrayList<Card>>();
    this.numOP = numOpenPiles;
    this.openPiles = new Card[numOpenPiles];

    //shuffles the deck if necessary
    if (shuffle) {
      Collections.shuffle(deck);
    }
    dealCascade(deck);  //deals the cards to the cascade piles
  }

  /**
   * Determines if the given deck is valid. A deck is invalid if it has duplicate cards,
   * does not contain exactly 52 cards, has an invalid card(s).
   *
   * @param deck the deck to check its validity
   * @return true if deck is valid
   */
  private boolean validDeck(List<Card> deck) {
    //checks size of deck
    if (deck.size() != 52) {
      return false;
    }

    List<Card> seen = new ArrayList<Card>(); //worklist of cards

    //checks every single card in list
    for (int i = 0; i < deck.size(); i++) {
      Card c = deck.get(i); //current card
      if (c.isValidCard()) {  //checks card's validity
        if (i != 0 && c.inDeck(seen)) { //checks if card is repeated
          return false;
        }
        else {
          seen.add(c); //adds current card to worklist
        }
      }
    }
    return true;
  }

  /**
   * Deals the deck in round-robin fashion.
   * @param deck the deck to be dealt
   * @throws IllegalArgumentException if there is an invalid number of cascade piles
   */
  private void dealCascade(List<Card> deck) {
    if (validDeck(deck)) {
      int pileCount = getNumCascadePiles();

      /*
      if (pileCount < 4) { //checks that there are an appropriate number of cascade piles.
        throw new IllegalArgumentException(
                "There must be at least 4 cascade piles.");
      }
       */

      //creates an empty list for each pile
      for (int i = 0; i < pileCount; i++) {
        this.cascadePiles.add(new ArrayList<Card>());
      }

      int pileIndex = 0; //current pile
      for (Card c : deck) { //puts each card in 'deck' into a cascade pile
        if (pileIndex == pileCount) { //resets pileIndex when it exceeds # of piles
          pileIndex = 0;
        }
        cascadePiles.get(pileIndex).add(c);
        pileIndex++; //increases pile index
      }
    }
  }

  @Override
  public void move(PileType source, int pileNumber, int cardIndex,
                   PileType destination, int destPileNumber) {

    throwGameException(); //throws exception if the game hasn't started

    //checks for a valid starting position
    if (!validStart(source, pileNumber, cardIndex)) {
      throw new IllegalArgumentException(
              "Illegal move. Cannot access the card at the specified index.");
    }

    Card moveMe;
    switch (source) {  //stores initial card in moveMe
      case OPEN:
        moveMe = openPiles[pileNumber];
        break;
      case CASCADE:
        moveMe = cascadePiles.get(pileNumber).remove(cardIndex);
        break;
      case FOUNDATION:
        throw new IllegalArgumentException(
                "Illegal move. Cannot move a foundation card!");
      default:
        moveMe = null;
        break;
    }

    //adds moveMe to the desired destination if possible, otherwise throws an exception
    if (canMoveTo(destination, destPileNumber, moveMe)) {
      switch (destination) {
        case OPEN:
          openPiles[destPileNumber] = moveMe;
          break;
        case CASCADE:
          cascadePiles.get(destPileNumber).add(moveMe);
          break;
        case FOUNDATION:
          foundationPiles.get(destPileNumber).push(moveMe);
          break;
        default:
          break;
      }

      switch (source) {  //removes moveMe from initial position
        case OPEN:
          openPiles[pileNumber] = null; //cardIndex?
          break;
        case CASCADE:
          cascadePiles.get(pileNumber).remove(moveMe);
          break;
        case FOUNDATION:
          break;
        default:
          break;
      }
    }
    else {
      throw new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }
  }

  /**
   * Ensures that there exists a card at the given index.
   * @param source type of card pile
   * @param pileNumber index of crad pile
   * @param cardIndex index of card in pile
   * @return true if the card exists and is at the end of the pile
   * @throws IllegalArgumentException if the given card cannot be moved
   */
  private boolean validStart(PileType source, int pileNumber, int cardIndex) {
    switch (source) {
      case OPEN:
        //does starting index exist?
        throwIndexException(numOP, pileNumber); //checks that the original pile exists
        throwIndexException(1, cardIndex);  //checks that the index exists
        return (openPiles[pileNumber] != null);
      case CASCADE:
        throwIndexException(numCP, pileNumber); //does starting index exist?
        throwIndexException(cascadePiles.get(pileNumber).size(),
                cardIndex); //checks that the original card exists
        return cardIndex == cascadePiles.get(pileNumber).size() - 1;
      case FOUNDATION:
        throw new IllegalArgumentException("Cannot move a foundation card.");
      default:
        return false;
    }
  }

  /**
   * Determines if the given card can be moved to the given destination.
   * @param destination card type of destination pile
   * @param destPileNumber index of destination pile
   * @param moveMe card to be moved
   * @return returns true if the card can be moved
   */
  private boolean canMoveTo(PileType destination, int destPileNumber, Card moveMe) {
    switch (destination) {
      case OPEN:
        throwIndexException(numOP, destPileNumber); //checks that the final pile exists
        return (openPiles[destPileNumber] == null);
      case CASCADE:
        throwIndexException(numCP, destPileNumber); //checks that the final pile exists
        if (cascadePiles.get(destPileNumber) == null) {
          return true;
        }
        else {
          int sizePile = cascadePiles.get(destPileNumber).size(); //size of dest. pile
          Card last = cascadePiles.get(destPileNumber).get(sizePile - 1); //top card in dest. pile

          return (last.isBlackCard() != moveMe.isBlackCard())
                  && (checkOrder(moveMe, last));
        }
      case FOUNDATION:
        throwIndexException(numFP, destPileNumber); //checks that the final pile exists
        return checkAddToFP(moveMe, destPileNumber);
      default:
        return false;
    }
  }

  /**
   * Ensures that the given card can be added to the given foundation pile.
   * @param moveMe card to be moved
   * @param destPileNumber index of foundation pile
   * @return true if card can be placed at given pile
   * @throws IllegalArgumentException if the card cannot be moved to the intended foundation pile
   */
  private boolean checkAddToFP(Card moveMe, int destPileNumber) {
    //checks card is Ace and destination pile is empty
    if (moveMe.getRank() == 'A' && foundationPiles.get(destPileNumber).size() == 0) {
      return true;
    }
    //checks if card is not Ace and destination pile is empty
    else if (foundationPiles.get(destPileNumber).size() == 0) {
      throw new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }
    //checks that card can be placed on destination pile
    else {
      Card top = foundationPiles.get(destPileNumber).peek(); //top FP card
      if (checkOrder(top, moveMe) && moveMe.getSuit().equals(top.getSuit())) {
        return true;
      }
      else {
        throw new IllegalArgumentException("Illegal move. Cannot move card"
                + " to the specified index.");
      }
    }
  }

  /**
   * Determines if the cards can be placed on top of eachother numerically.
   * @param lowRank card that's supposed to have a lower rank
   * @param highRank card that's supposed to have a higher rank
   * @return true if cards can be stacked
   */
  private boolean checkOrder(Card lowRank, Card highRank) {
    ArrayList<Character> rankOrder = new ArrayList<Character>(
            Arrays.asList('A', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'J', 'Q', 'K'));

    int lowRankNum = rankOrder.indexOf(lowRank.getRank());
    int highRankNum = rankOrder.indexOf(highRank.getRank());

    return (lowRankNum == highRankNum - 1);
  }

  @Override
  public boolean isGameOver() {
    //game can't be over if it never started
    if (getNumCascadePiles() == -1) {
      return false;
    }

    return (foundationPiles.get(0).size() == 13
            && foundationPiles.get(1).size() == 13
            && foundationPiles.get(2).size() == 13
            && foundationPiles.get(3).size() == 13);
  }

  /**
   * Throws an exception if the game is not in play.
   * @throws IllegalStateException when the game hasn't started or is over
   */
  private void throwGameException() {
    if (getNumCascadePiles() == -1) {
      throw new IllegalStateException("Game has not started yet!");
    }
  }

  /**
   * Throws an exception if the given index doesn't exist.
   * @param numIndices number of possible indices
   * @param index index to test
   * @throws IllegalArgumentException when given an invalid index
   */
  private void throwIndexException(int numIndices, int index) {
    if (index < 0 || index >= numIndices) {
      throw new IllegalArgumentException("Invalid index.");
    }
  }

  //---------FREECELL MODEL STATE----------

  @Override
  public int getNumCardsInFoundationPile(int index) {
    throwGameException();
    throwIndexException(numFP, index);

    return foundationPiles.get(index).size();
  }

  @Override
  public int getNumCascadePiles() {
    return numCP;
  }

  @Override
  public int getNumCardsInCascadePile(int index) {
    throwGameException();
    throwIndexException(numCP, index);

    return cascadePiles.get(index).size();
  }

  @Override
  public int getNumCardsInOpenPile(int index) {
    throwGameException();
    throwIndexException(numOP, index);

    if (openPiles[index] == null) {
      return 0;
    }
    else {
      return 1;
    }
  }

  @Override
  public int getNumOpenPiles() {
    return numOP;
  }

  @Override
  public Card getFoundationCardAt(int pileIndex, int cardIndex) {
    throwGameException();
    throwIndexException(numFP, pileIndex);
    throwIndexException(foundationPiles.get(pileIndex).size(), cardIndex);

    return foundationPiles.get(pileIndex).get(cardIndex);
  }

  @Override
  public Card getCascadeCardAt(int pileIndex, int cardIndex) {
    throwGameException();
    throwIndexException(numCP, pileIndex);
    throwIndexException(cascadePiles.get(pileIndex).size(), cardIndex);

    return cascadePiles.get(pileIndex).get(cardIndex);
  }

  @Override
  public Card getOpenCardAt(int pileIndex) {
    throwGameException();
    throwIndexException(numOP, pileIndex);

    return openPiles[pileIndex];
  }

}