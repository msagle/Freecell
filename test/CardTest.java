import org.junit.Before;
import org.junit.Test;

import cs3500.freecell.model.Card;
import cs3500.freecell.model.Rank;
import cs3500.freecell.model.Suit;
import cs3500.freecell.model.SuitType;

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

    s = new Card(Rank.ACE, spade);
    h = new Card(Rank.TEN, heart);
    c = new Card(Rank.FIVE, club);
    d = new Card(Rank.QUEEN, diamond);

    try {
      new Card(null, diamond);
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid card. Make sure rank and suit are valid;");
    }

    try {
      new Card(Rank.ACE, null);
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid card. Make sure rank and suit are valid;");
    }
  }

  @Test
  public void testGetRank() {
    assertEquals(Rank.ACE, s.getRank());
    assertEquals(Rank.TEN, h.getRank());
    assertEquals(Rank.FIVE, c.getRank());
    assertEquals(Rank.QUEEN, d.getRank());
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
  public void testEquals() {
    assertEquals(false, s.equals(1));
    assertEquals(false, d.equals("str"));
    assertEquals(false, s.equals(h));
    assertEquals(false, h.equals(s));
    assertEquals(true, c.equals(c));

    Card h2 = new Card(Rank.TEN, heart);
    assertEquals(true, h.equals(h2));
    assertEquals(true, h2.equals(h));
  }

  @Test
  public void testHashcode() {
    assertEquals(h.hashCode(), h.hashCode());
    assertEquals(h.hashCode(), (new Card(Rank.TEN, heart)).hashCode());
    assertEquals((new Card(Rank.TEN, heart)).hashCode(), h.hashCode());
  }

}