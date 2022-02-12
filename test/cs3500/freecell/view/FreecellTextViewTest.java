package cs3500.freecell.view;

import org.junit.Test;

import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.SimpleFreecellModel;

import static org.junit.Assert.assertEquals;

/**
 * Contains the tests for the FreecellTextView class.
 */
public class FreecellTextViewTest {

  @Test
  public void testClass() {
    FreecellModel basic = new SimpleFreecellModel();
    basic.startGame(basic.getDeck(), 6, 4, false);
    FreecellTextView good = new FreecellTextView(basic);

    String basicText = "F1:\n"
            + "F2:\n"
            + "F3:\n"
            + "F4:\n"
            + "O1:\n"
            + "O2:\n"
            + "O3:\n"
            + "O4:\n"
            + "C1: A♥, 7♥, K♥, 6♦, Q♦, 5♠, J♠, 4♣, 10♣\n"
            + "C2: 2♥, 8♥, A♦, 7♦, K♦, 6♠, Q♠, 5♣, J♣\n"
            + "C3: 3♥, 9♥, 2♦, 8♦, A♠, 7♠, K♠, 6♣, Q♣\n"
            + "C4: 4♥, 10♥, 3♦, 9♦, 2♠, 8♠, A♣, 7♣, K♣\n"
            + "C5: 5♥, J♥, 4♦, 10♦, 3♠, 9♠, 2♣, 8♣\n"
            + "C6: 6♥, Q♥, 5♦, J♦, 4♠, 10♠, 3♣, 9♣";

    assertEquals(basicText, good.toString());

    FreecellModel noStart = new SimpleFreecellModel();
    FreecellTextView ns = new FreecellTextView(noStart);
    assertEquals("", ns.toString());

    //Freecell model must not be null
    try {
      new FreecellTextView(null);
    }
    catch (Exception e) {
      new IllegalArgumentException("Model cannot be null.");
    }
  }

}