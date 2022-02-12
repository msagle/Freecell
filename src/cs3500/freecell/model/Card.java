package cs3500.freecell.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a Playing Card. Keeps track of rank and suit.
 */
public class Card {
  private final char rank;
  private final Suit suit;

  /**
   * Constructs a card. Keeps track of rank and suit.
   *
   * @param rank rank of card (A-K)
   * @param suit type of card
   */
  public Card(char rank, Suit suit) {
    this.rank = rank;
    this.suit = suit;

    if (!this.isValidCard()) {
      throw new IllegalArgumentException("Invalid card. Make sure rank and suit are valid;");
    }
  }

  /**
   * Get the rank of this card. A rank of '0' indicates a rank of 10.
   *
   * @return the rank of the card
   */
  public char getRank() {
    return this.rank;
  }

  /**
   * Get the suit of this card.
   *
   * @return the suit of this card
   */
  public Suit getSuit() {
    return this.suit;
  }

  /**
   * Represents the card as a string.
   *
   * @return rank and suit of the card
   */
  public String toString() {
    String ans = this.rank + this.suit.toString();

    if (this.rank == '0') {
      return "1" + ans;
    }
    else {
      return ans;
    }
  }

  /**
   * Determines if this card is valid. Card is invalid if its rank doesn't
   * fall within the traditional A-K specification.
   *
   * @return true if the card is valid, false otherwise
   */
  protected boolean isValidCard() {
    return (rank >= '2' && rank <= '9') || rank == '0'
              || rank == 'J' || rank == 'Q' || rank == 'K' || rank == 'A';
  }

  /**
   * Determines if this card is in the given deck of cards.
   *
   * @param seen deck of cards
   * @return true if the card is contained in the deck, false otherwise
   */
  protected boolean inDeck(List<Card> seen) {
    for (int i = 0; i < seen.size(); i += 1) {
      if (this.equals(seen.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if this card is black.
   * @return true if card is black, false if card is red
   */
  protected boolean isBlackCard() {
    return suit.isBlackCard();
  }

  /**
   * Compares this card with a given object for equality in terms of rank and suit.
   *
   * @param o object being compared
   * @return true if the objects are equal, false otherwise
   */
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    else if (! (o instanceof Card)) {
      return false;
    }
    else {
      Card cast = (Card)o;
      return this.rank == cast.rank && this.suit.equals(cast.suit);
    }
  }

  /**
   * Creates a unique hashcode for unique cards.
   * @return the hashcode
   */
  public int hashCode() {
    return Objects.hash(rank, suit);
  }

}
