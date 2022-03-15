package cs3500.freecell.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a Playing Card. Keeps track of rank and suit.
 */
public class Card {
  private final Rank rank;
  private final Suit suit;

  /**
   * Constructs a card. Keeps track of rank and suit.
   *
   * @param rank rank of card (A-K)
   * @param suit type of card
   */
  public Card(Rank rank, Suit suit) {
    if (suit == null || rank == null) {
      throw new IllegalArgumentException("Suit can not be null!");
    }

    this.rank = rank;
    this.suit = suit;
  }

  /**
   * Get the rank of this card. A rank of '0' indicates a rank of 10.
   *
   * @return the rank of the card
   */
  public Rank getRank() {
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
    String r;

    switch (this.rank) {
      case ACE:
        r = "A";
        break;
      case TWO:
        r = "2";
        break;
      case THREE:
        r = "3";
        break;
      case FOUR:
        r = "4";
        break;
      case FIVE:
        r = "5";
        break;
      case SIX:
        r = "6";
        break;
      case SEVEN:
        r = "7";
        break;
      case EIGHT:
        r = "8";
        break;
      case NINE:
        r = "9";
        break;
      case TEN:
        r = "10";
        break;
      case JACK:
        r = "J";
        break;
      case QUEEN:
        r = "Q";
        break;
      case KING:
        r = "K";
        break;
      default:
        r = "";
        break;
    }
    return r + this.suit.toString();
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
  public boolean isBlackCard() {
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
