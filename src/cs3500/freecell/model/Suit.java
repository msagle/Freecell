package cs3500.freecell.model;

import java.util.Objects;

/**
 * Represents a card's suit and can determine its color;
 * must be of the type spade, heart, club, or diamond.
 */
public class Suit {
  private final SuitType suit;

  /**
   * Constructs the suit. Either ♠, ♥, ♣, or ♦.
   *
   * @param suit type of card
   */
  public Suit(SuitType suit) {
    this.suit = suit;
  }

  /**
   * Returns the value of this Suit's type.
   * @return the type of suit, either S/H/C/D
   */
  public SuitType getSuit() {
    return this.suit;
  }

  /**
   * Determines whether the card is black or red.
   *
   * @return true if card is black, false if red
   */
  public boolean isBlackCard() {
    return getSuit() == SuitType.SPADE  || getSuit() == SuitType.CLUB;
  }

  /**
   * Represents the suit as a string.
   *
   * @return suit
   */
  public String toString() {
    switch (suit) {
      case SPADE:
        return "♠";
      case HEART:
        return "♥";
      case CLUB:
        return "♣";
      case DIAMOND:
        return "♦";
      default:
        return "";
    }
  }

  /**
   * Determines if the given object is equal to this suit.
   * @param o object to be compared
   * @return true if the object is the same suit
   */
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    else if (! (o instanceof Suit)) {
      return false;
    }
    else {
      Suit cast = (Suit)o;
      return this.suit == cast.suit;
    }
  }

  /**
   * Creates a unique hashcode for unique suits.
   * @return the hashcode
   */
  public int hashCode() {
    return Objects.hash(suit);
  }

}