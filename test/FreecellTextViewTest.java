import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import cs3500.freecell.controller.SimpleFreecellController;
import cs3500.freecell.model.Card;
import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.PileType;
import cs3500.freecell.model.SimpleFreecellModel;
import cs3500.freecell.model.multimove.ComplexFreecellModel;
import cs3500.freecell.view.FreecellTextView;

import static org.junit.Assert.assertEquals;

/**
 * Contains the tests for the FreecellTextView class.
 */
public class FreecellTextViewTest {
  private FreecellModel basic;
  private FreecellTextView good;
  private String basicText;
  private String basicTextMove;

  @Before
  public void setUp() {
    basic = new SimpleFreecellModel();
    good = new FreecellTextView(basic);

    basicText = "F1:\n"
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

    basicTextMove = "F1:\n"
            + "F2:\n"
            + "F3:\n"
            + "F4:\n"
            + "O1:\n"
            + "O2: K♣\n"
            + "O3:\n"
            + "O4:\n"
            + "C1: A♥, 7♥, K♥, 6♦, Q♦, 5♠, J♠, 4♣, 10♣\n"
            + "C2: 2♥, 8♥, A♦, 7♦, K♦, 6♠, Q♠, 5♣, J♣\n"
            + "C3: 3♥, 9♥, 2♦, 8♦, A♠, 7♠, K♠, 6♣, Q♣\n"
            + "C4: 4♥, 10♥, 3♦, 9♦, 2♠, 8♠, A♣, 7♣\n"
            + "C5: 5♥, J♥, 4♦, 10♦, 3♠, 9♠, 2♣, 8♣\n"
            + "C6: 6♥, Q♥, 5♦, J♦, 4♠, 10♠, 3♣, 9♣";

    //Freecell model must not be null
    try {
      new FreecellTextView(null);
    }
    catch (Exception e) {
      new IllegalArgumentException("Model cannot be null.");
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testConstructorNullAppendable() throws IOException {
    FreecellTextView view = new FreecellTextView(basic, null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testConstructorNullModel() throws IOException {
    FreecellTextView view = new FreecellTextView(null, new StringBuilder());
  }

  @Test
  public void testToStringSM() {
    //toString
    basic.startGame(basic.getDeck(), 6, 4, false);
    assertEquals(basicText, good.toString());

    //toString after moving
    basic.move(PileType.CASCADE,3, 8, PileType.OPEN, 1);
    assertEquals(basicTextMove, good.toString());

    //no game started
    FreecellModel noStart = new SimpleFreecellModel();
    FreecellTextView ns = new FreecellTextView(noStart);
    assertEquals("", ns.toString());

    //test toString when game can't start
    FreecellModel badStart = new SimpleFreecellModel();
    FreecellTextView bs = new FreecellTextView(badStart);

    try {
      badStart.startGame(badStart.getDeck(), 2, 5, false);
    }
    catch (Exception e) {
      new IllegalArgumentException("There must be at least 4 cascade piles.");
    }
    assertEquals("", bs.toString());
  }

  @Test
  public void testRenderBoardSM() throws IOException {
    String board = "F1:\n" +
            "F2:\n" +
            "F3:\n" +
            "F4:\n" +
            "O1:\n" +
            "O2:\n" +
            "O3:\n" +
            "O4:\n" +
            "C1: A♥, 5♥, 9♥, K♥, 4♦, 8♦, Q♦, 3♠, 7♠, J♠, 2♣, 6♣, 10♣\n" +
            "C2: 2♥, 6♥, 10♥, A♦, 5♦, 9♦, K♦, 4♠, 8♠, Q♠, 3♣, 7♣, J♣\n" +
            "C3: 3♥, 7♥, J♥, 2♦, 6♦, 10♦, A♠, 5♠, 9♠, K♠, 4♣, 8♣, Q♣\n" +
            "C4: 4♥, 8♥, Q♥, 3♦, 7♦, J♦, 2♠, 6♠, 10♠, A♣, 5♣, 9♣, K♣";

    StringBuilder a = new StringBuilder();
    FreecellModel<Card> m = new SimpleFreecellModel();
    FreecellTextView view = new FreecellTextView(m, a);
    SimpleFreecellController ctrl = new SimpleFreecellController(m, new StringReader("q"), a);

    ctrl.playGame(m.getDeck(), 4, 4, false);
    view.renderBoard();

    assertEquals(a.toString(), board + "\nGame quit prematurely.\n" + board + "\n");
  }

  @Test
  public void testRenderMessageSM() throws IOException {
    String board = "F1:\n" +
            "F2:\n" +
            "F3:\n" +
            "F4:\n" +
            "O1:\n" +
            "O2:\n" +
            "O3:\n" +
            "O4:\n" +
            "C1: A♥, 5♥, 9♥, K♥, 4♦, 8♦, Q♦, 3♠, 7♠, J♠, 2♣, 6♣, 10♣\n" +
            "C2: 2♥, 6♥, 10♥, A♦, 5♦, 9♦, K♦, 4♠, 8♠, Q♠, 3♣, 7♣, J♣\n" +
            "C3: 3♥, 7♥, J♥, 2♦, 6♦, 10♦, A♠, 5♠, 9♠, K♠, 4♣, 8♣, Q♣\n" +
            "C4: 4♥, 8♥, Q♥, 3♦, 7♦, J♦, 2♠, 6♠, 10♠, A♣, 5♣, 9♣, K♣";

    StringBuilder a = new StringBuilder();
    FreecellModel<Card> m = new SimpleFreecellModel();
    FreecellTextView view = new FreecellTextView(m, a);
    SimpleFreecellController ctrl = new SimpleFreecellController(m, new StringReader("q"), a);

    ctrl.playGame(m.getDeck(), 4, 4, false);
    view.renderMessage("test message");
    assertEquals(a.toString(), board + "\nGame quit prematurely." + "\ntest message\n");

    view.renderMessage("test message 2");
    assertEquals(a.toString(), board + "\nGame quit prematurely."
            + "\ntest message\ntest message 2\n");

  }

  @Test
  public void testToStringCM() {
    FreecellModel complex = new ComplexFreecellModel();
    FreecellTextView text = new FreecellTextView(complex);
    //toString
    complex.startGame(complex.getDeck(), 6, 4, false);
    assertEquals(basicText, text.toString());

    //toString after moving
    complex.move(PileType.CASCADE,3, 8, PileType.OPEN, 1);
    assertEquals(basicTextMove, text.toString());

    //no game started
    FreecellModel noStart = new SimpleFreecellModel();
    FreecellTextView ns = new FreecellTextView(noStart);
    assertEquals("", ns.toString());

    //test toString when game can't start
    FreecellModel bad = new ComplexFreecellModel();
    FreecellTextView bs = new FreecellTextView(bad);

    try {
      bad.startGame(bad.getDeck(), 2, 5, false);
    }
    catch (Exception e) {
      new IllegalArgumentException("There must be at least 4 cascade piles.");
    }
    assertEquals("", bs.toString());
  }

  @Test
  public void testRenderBoardCM() throws IOException {
    String board = "F1:\n" +
            "F2:\n" +
            "F3:\n" +
            "F4:\n" +
            "O1:\n" +
            "O2:\n" +
            "O3:\n" +
            "O4:\n" +
            "C1: A♥, 5♥, 9♥, K♥, 4♦, 8♦, Q♦, 3♠, 7♠, J♠, 2♣, 6♣, 10♣\n" +
            "C2: 2♥, 6♥, 10♥, A♦, 5♦, 9♦, K♦, 4♠, 8♠, Q♠, 3♣, 7♣, J♣\n" +
            "C3: 3♥, 7♥, J♥, 2♦, 6♦, 10♦, A♠, 5♠, 9♠, K♠, 4♣, 8♣, Q♣\n" +
            "C4: 4♥, 8♥, Q♥, 3♦, 7♦, J♦, 2♠, 6♠, 10♠, A♣, 5♣, 9♣, K♣";

    StringBuilder a = new StringBuilder();
    FreecellModel<Card> m = new ComplexFreecellModel();
    FreecellTextView view = new FreecellTextView(m, a);
    SimpleFreecellController ctrl = new SimpleFreecellController(m, new StringReader("q"), a);

    ctrl.playGame(m.getDeck(), 4, 4, false);
    view.renderBoard();

    assertEquals(a.toString(), board + "\nGame quit prematurely.\n" + board + "\n");
  }

  @Test
  public void testRenderMessageCM() throws IOException {
    String board = "F1:\n" +
            "F2:\n" +
            "F3:\n" +
            "F4:\n" +
            "O1:\n" +
            "O2:\n" +
            "O3:\n" +
            "O4:\n" +
            "C1: A♥, 5♥, 9♥, K♥, 4♦, 8♦, Q♦, 3♠, 7♠, J♠, 2♣, 6♣, 10♣\n" +
            "C2: 2♥, 6♥, 10♥, A♦, 5♦, 9♦, K♦, 4♠, 8♠, Q♠, 3♣, 7♣, J♣\n" +
            "C3: 3♥, 7♥, J♥, 2♦, 6♦, 10♦, A♠, 5♠, 9♠, K♠, 4♣, 8♣, Q♣\n" +
            "C4: 4♥, 8♥, Q♥, 3♦, 7♦, J♦, 2♠, 6♠, 10♠, A♣, 5♣, 9♣, K♣";

    StringBuilder a = new StringBuilder();
    FreecellModel<Card> m = new ComplexFreecellModel();
    FreecellTextView view = new FreecellTextView(m, a);
    SimpleFreecellController ctrl = new SimpleFreecellController(m, new StringReader("q"), a);

    ctrl.playGame(m.getDeck(), 4, 4, false);
    view.renderMessage("test message");
    assertEquals(a.toString(), board + "\n" + "Game quit prematurely.\n" + "test message\n");

    view.renderMessage("test message 2");
    assertEquals(a.toString(), board + "\n" + "Game quit prematurely."
            + "\ntest message\ntest message 2\n");

  }

}