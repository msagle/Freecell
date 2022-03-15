import org.junit.Before;
import org.junit.Test;

import cs3500.freecell.model.Suit;
import cs3500.freecell.model.SuitType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Contains tests for the Suit class.
 */
public class SuitTest {
  private Suit spade;
  private Suit heart;
  private Suit club;
  private Suit diamond;

  @Before
  public void setUp() {
    spade = new Suit(SuitType.SPADE);
    heart = new Suit(SuitType.HEART);
    club = new Suit(SuitType.CLUB);
    diamond = new Suit(SuitType.DIAMOND);
  }

  @Test
  public void testGet() {
    assertEquals(SuitType.SPADE, spade.getSuit());
    assertEquals(SuitType.HEART, heart.getSuit());
    assertEquals(SuitType.CLUB, club.getSuit());
    assertEquals(SuitType.DIAMOND, diamond.getSuit());
  }

  @Test
  public void testBlackCard() {
    assertEquals(true, spade.isBlackCard());
    assertEquals(true, club.isBlackCard());
    assertEquals(false, heart.isBlackCard());
    assertEquals(false, diamond.isBlackCard());
  }

  @Test
  public void testToString() {
    assertEquals("♠", spade.toString());
    assertEquals("♥", heart.toString());
    assertEquals("♣", club.toString());
    assertEquals("♦", diamond.toString());
  }

  @Test
  public void testEquals() {
    assertEquals(true, spade.equals(spade));
    assertEquals(false, heart.equals(spade));
    assertEquals(false, spade.equals(heart));
    assertEquals(true, diamond.equals(diamond));
    assertNotEquals("1", heart);
    assertNotEquals(1, diamond);
  }

  @Test
  public void testHashcode() {
    assertEquals(spade.hashCode(), spade.hashCode());
    assertEquals(spade.hashCode(), (new Suit(SuitType.SPADE)).hashCode());
    assertEquals((new Suit(SuitType.SPADE)).hashCode(), spade.hashCode());
  }

}