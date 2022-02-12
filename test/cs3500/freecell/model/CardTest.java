package cs3500.freecell.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Contains the tests for the Card class.
 */
public class CardTest {
  private Suit spade;
  private Suit heart;
  private Suit club;
  private Suit diamond;

  private Card s;
  private Card h;
  private Card c;
  private Card d;

  @Before
  public void setUp() {
    spade = new Suit(SuitType.SPADE);
    heart = new Suit(SuitType.HEART);
    club = new Suit(SuitType.CLUB);
    diamond = new Suit(SuitType.DIAMOND);

    s = new Card('A', spade);
    h = new Card('0', heart);
    c = new Card('5', club);
    d = new Card('Q', diamond);

    try {
      new Card('1', diamond);
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid card. Make sure rank and suit are valid;");
    }

    try {
      new Card('Z', heart);
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid card. Make sure rank and suit are valid;");
    }
  }

  @Test
  public void testGetRank() {
    assertEquals('A', s.getRank());
    assertEquals('0', h.getRank());
    assertEquals('5', c.getRank());
    assertEquals('Q', d.getRank());
  }

  @Test
  public void testGetSuit() {
    assertEquals(spade, s.getSuit());
    assertEquals(heart, h.getSuit());
    assertEquals(club, c.getSuit());
    assertEquals(diamond, d.getSuit());
  }

  @Test
  public void testToString() {
    assertEquals("A♠", s.toString());
    assertEquals("10♥", h.toString());
    assertEquals("5♣", c.toString());
    assertEquals("Q♦", d.toString());
  }

  @Test
  public void testValid() {
    assertEquals(true, h.isValidCard());
    assertEquals(true, s.isValidCard());
    assertEquals(true, d.isValidCard());
    assertEquals(true, c.isValidCard());

    try {
      (new Card('H', club)).isValidCard();
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid card. Make sure rank and suit are valid;");
    }

  }

  @Test
  public void testInDeck() {
    List<Card> list = new ArrayList<Card>(Arrays.asList(s, h, d));

    assertEquals(true, s.inDeck(list));
    assertEquals(true, h.inDeck(list));
    assertEquals(true, d.inDeck(list));
    assertEquals(false, c.inDeck(list));

  }

  @Test
  public void testBlackCard() {
    assertEquals(true, s.isBlackCard());
    assertEquals(false, h.isBlackCard());
    assertEquals(true, c.isBlackCard());
    assertEquals(false, d.isBlackCard());
  }

  @Test
  public void testEquals() {
    assertEquals(false, s.equals(1));
    assertEquals(false, d.equals("str"));
    assertEquals(false, s.equals(h));
    assertEquals(false, h.equals(s));
    assertEquals(true, c.equals(c));

    Card h2 = new Card('0', heart);
    assertEquals(true, h.equals(h2));
    assertEquals(true, h2.equals(h));
  }

  @Test
  public void testHashcode() {
    assertEquals(h.hashCode(), h.hashCode());
    assertEquals(h.hashCode(), (new Card('0', heart)).hashCode());
    assertEquals((new Card('0', heart)).hashCode(), h.hashCode());
  }

}