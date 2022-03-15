import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cs3500.freecell.controller.FailingAppendable;
import cs3500.freecell.controller.FreecellController;
import cs3500.freecell.controller.SimpleFreecellController;
import cs3500.freecell.model.Card;
import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.SimpleFreecellModel;
import cs3500.freecell.model.multimove.ComplexFreecellModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit test cases for the freecell controller when the model is complex.
 */
public class ComplexModelControllerTest {
  private FreecellModel model;
  private FreecellController controller;
  private StringReader r;
  private StringBuilder a;
  private String initialBoard;

  @Before
  public void setUp() {
    model = new ComplexFreecellModel();
    r = new StringReader("placeholder");
    a = new StringBuilder();
    controller = new SimpleFreecellController(model, r, a);
    initialBoard = "F1:\n" +
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

  }


  @Test (expected = IllegalStateException.class)
  public void testBadReadable() {
    StringReader empty = new StringReader("full");
    FreecellController badRead = new SimpleFreecellController(model, empty, a);
    empty.close();
    badRead.playGame(model.getDeck(), 4, 4, false);

    //tests the IOException because when the exception is caught it throws an IllegalState instead
  }

  @Test (expected = IllegalStateException.class)
  public void testBadAppendable() {
    Appendable ap = new FailingAppendable();

    FreecellController badApp = new
            SimpleFreecellController(model, new StringReader("test"), ap);
    badApp.playGame(model.getDeck(), 4, 4, true);

    //tests the IOException because when the exception is caught it throws an IllegalState instead
  }

  @Test (expected = IllegalArgumentException.class)
  public void testControllerBuildNullModel() {
    FreecellController badController = new SimpleFreecellController(null, r, a);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testControllerBuildNullReadable() {
    FreecellController badController = new SimpleFreecellController(model, null, a);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testControllerBuildNullAppendable() {
    FreecellController badController = new SimpleFreecellController(model, r, null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNullDeck() {
    controller.playGame(null, 5, 7, true);
  }

  @Test
  public void checkShuffle() {
    StringBuilder app = new StringBuilder();
    FreecellController ctrl = new SimpleFreecellController(model,
            new StringReader("C1 5 O1 q"), app);

    StringBuilder a = new StringBuilder();
    FreecellController ctrl1 = new SimpleFreecellController(model,
            new StringReader("C1 5 O1 q"), app);


    ctrl1.playGame(model.getDeck(),4, 4, false);
    ctrl.playGame(model.getDeck(),4, 4, true);

    //same exact model & readable.... must be different b/c of shuffling
    assertNotEquals(a.toString(), app.toString());

  }

  @Test
  public void testStartGameBad() {
    //bad number for cascade pile
    controller.playGame(model.getDeck(), -1, 7, true);
    assertEquals("Could not start game.\n", a.toString());

    //bad number for open pile
    a.setLength(0);
    controller.playGame(model.getDeck(), 5, -1, true);
    assertEquals("Could not start game.\n", a.toString());

    //bad deck
    a.setLength(0);
    assertEquals("", a.toString());
    List<Card> bad = model.getDeck();
    bad.remove(51);
    controller.playGame(bad, 5, 1, true);
    assertEquals("Could not start game.\n", a.toString());
  }

  @Test
  public void testQuitQ() {
    //just Q
    StringReader read = new StringReader("Q");
    FreecellController controller0 = new SimpleFreecellController(model, read, a);
    controller0.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void testQuitq() {
    //just q
    StringReader read = new StringReader(" q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller1 = new SimpleFreecellController(model, read, a);
    controller1.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void testQuitStuffQ() {
    //Q with stuff before
    StringReader read = new StringReader("C4 hahss 48 F1 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller2 = new SimpleFreecellController(model, read, a);
    controller2.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void testQuitQInMiddle() {
    //Q in the middle
    StringReader read = new StringReader("C4 hahss Q 48 F1 ");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller3 = new SimpleFreecellController(model, read, a);
    controller3.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void testQuitQStuff() {
    //Q with stuff after
    StringReader read = new StringReader("Q C4 hahss 48 F1 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller4 = new SimpleFreecellController(model, read, a);
    controller4.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void testQuitNewLineQ() {
    //Q after new line
    StringReader read = new StringReader("\nQ");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller5 = new SimpleFreecellController(model, read, a);
    controller5.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void InvalidInputSourceLength() {
    //invalid length
    StringReader read = new StringReader("w q");
    a.setLength(0);
    FreecellController controller0 = new SimpleFreecellController(model, read, a);
    controller0.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Source pile cannot be one character. Try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void InvalidInputSourcePT() {
    //invalid pile type
    StringReader read = new StringReader("H1 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller1 = new SimpleFreecellController(model, read, a);
    controller1.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Source pile input invalid, try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void InvalidInputSourceIndex() {
    //invalid pile index
    StringReader read = new StringReader("C10O q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller2 = new SimpleFreecellController(model, read, a);
    controller2.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Source index cannot be parsed to a valid integer. "
            + "Try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void InvalidInputSourceJunk() {
    //junk before/after valid source pile, but source pile is still accepted
    StringReader read = new StringReader("agsfet 8 C1 fhur q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller3 = new SimpleFreecellController(model, read, a);
    controller3.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Source pile input invalid, try again."));
    assertTrue(a.toString().contains("Source pile cannot be one character. Try again."));
    assertTrue(a.toString().contains("Cannot parse card to a valid integer. Try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }


  @Test
  public void testInvalidInputsCard() {
    //no card index present
    StringReader read = new StringReader("C1 ogdey hsud q"); //grabs source no card
    a.setLength(0);
    FreecellController controller0 = new SimpleFreecellController(model, read, a);
    controller0.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Cannot parse card to a valid integer. Try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));

    //junk before/after card index
    read = new StringReader("C1 ogdey 8 sws q"); //grabs source, rejects, grabs card
    a.setLength(0);
    FreecellController controller1 = new SimpleFreecellController(model, read, a);
    controller1.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Cannot parse card to a valid integer. Try again."));
    assertTrue(a.toString().contains("Destination pile input invalid, try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void InvalidInputDestLength() {
    //invalid length
    StringReader read = new StringReader("C1 9 d q");
    a.setLength(0);
    FreecellController controller0 = new SimpleFreecellController(model, read, a);
    controller0.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Destination pile cannot be one character. Try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void InvalidInputDestPT() {
    //invalid pile type
    StringReader read = new StringReader("C1 16 Q1 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller1 = new SimpleFreecellController(model, read, a);
    controller1.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Destination pile input invalid, try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void InvalidInputDestIndex() {
    //invalid pile index
    StringReader read = new StringReader("C1 7 C1m00 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller2 = new SimpleFreecellController(model, read, a);
    controller2.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Destination index cannot be parsed to a valid integer. "
            + "Try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void InvalidInputDestJunk() {
    //junk before/after valid destination pile, but destination pile is still accepted
    StringReader read = new StringReader("C1 8 hduehdenc fhur F2 jneif q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller3 = new SimpleFreecellController(model, read, a);
    controller3.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("Destination pile input invalid, try again."));
    assertTrue(a.toString().contains("Destination index cannot be parsed to a valid integer. "
            + "Try again."));
    assertTrue(a.toString().contains("Invalid move. Try again."));
    assertTrue(a.toString().contains("Source pile input invalid, try again."));
    assertTrue(a.toString().contains("Game quit prematurely."));
  }

  @Test
  public void testValidInputsInvalidMove1() {
    //these inputs are valid by the controller's standards
    //they are not valid by the model's standards

    //invalid source index (doesn't exist)
    StringReader read = new StringReader("C18 13 O4 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller0 = new SimpleFreecellController(model, read, a);
    controller0.playGame(model.getDeck(), 4, 4, false);
    assertEquals(a.toString(), initialBoard + "\nInvalid move. Try again.\n" +
            initialBoard + "\nGame quit prematurely.\n");

    //invalid card index (negative)
    read = new StringReader("C1 -7 O4 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller1 = new SimpleFreecellController(model, read, a);
    controller1.playGame(model.getDeck(), 4, 4, false);
    assertEquals(a.toString(), initialBoard + "\nInvalid move. Try again.\n" +
            initialBoard + "\nGame quit prematurely.\n");

    //invalid card index (doesn't exist)
    read = new StringReader("C1 17 O3 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller2 = new SimpleFreecellController(model, read, a);
    controller2.playGame(model.getDeck(), 4, 4, false);
    assertEquals(a.toString(), initialBoard + "\nInvalid move. Try again.\n" +
            initialBoard + "\nGame quit prematurely.\n");

    //invalid card index (not at end)
    read = new StringReader("C1 2 O3 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller3 = new SimpleFreecellController(model, read, a);
    controller3.playGame(model.getDeck(), 4, 4, false);
    assertEquals(a.toString(), initialBoard + "\nInvalid move. Try again.\n" +
            initialBoard + "\nGame quit prematurely.\n");

    //invalid destination index (incorrect foundation pile)
    read = new StringReader("C1 13 F1 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller4 = new SimpleFreecellController(model, read, a);
    controller4.playGame(model.getDeck(), 4, 4, false);
    assertEquals(a.toString(), initialBoard + "\nInvalid move. Try again.\n" +
            initialBoard + "\nGame quit prematurely.\n");

    //invalid destination index (doesn't exist)
    read = new StringReader("C1 13 O5 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller5 = new SimpleFreecellController(model, read, a);
    controller5.playGame(model.getDeck(), 4, 4, false);
    assertEquals(a.toString(), initialBoard + "\nInvalid move. Try again.\n" +
            initialBoard + "\nGame quit prematurely.\n");

  }

  @Test
  public void testValidInputsInvalidMove2() {
    //these inputs are valid by the controller's standards
    //they are also valid by the model's standard, however the move cannot be completed

    //invalid move to cascade
    StringReader read = new StringReader("C4 13 C2 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller0 = new SimpleFreecellController(model, read, a);
    controller0.playGame(model.getDeck(), 4, 4, false);
    assertEquals(a.toString(), initialBoard + "\nInvalid move. Try again.\n"
            + initialBoard + "\nGame quit prematurely.\n");

    //invalid move to foundation
    read = new StringReader("C4 13 F2 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller1 = new SimpleFreecellController(model, read, a);
    controller1.playGame(model.getDeck(), 4, 4, false);
    assertEquals(a.toString(), initialBoard + "\nInvalid move. Try again.\n"
            + initialBoard + "\nGame quit prematurely.\n");

    //invalid move to foundation with card inside
    read = new StringReader("C4 13 O1 C4 12 O2 C4 11 O3 C4 10 F3 C2 12 F3 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller2 = new SimpleFreecellController(model, read, a);
    controller2.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("F3: A♣"));
    assertTrue(a.toString().contains("Game quit prematurely.\n"));

    //card already in open pile
    read = new StringReader("C4 13 O2 C1 13 O2 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller3 = new SimpleFreecellController(model, read, a);
    controller3.playGame(model.getDeck(), 4, 4, false);
    assertTrue(a.toString().contains("O2: K♣"));
    assertTrue(a.toString().contains("Game quit prematurely.\n"));
  }

  @Test
  public void testValidInputValidMove() {
    //valid move to open
    StringReader read = new StringReader("c3 13 O1 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller0 = new SimpleFreecellController(model, read, a);
    controller0.playGame(model.getDeck(), 4, 4, false);
    assertFalse(a.toString().contains("Invalid move. Try again.\n"));

    //valid move to foundation
    read = new StringReader("C4 13 o1 c4 12 O2 C4 11 O3 C4 10 f3 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller1 = new SimpleFreecellController(model, read, a);
    controller1.playGame(model.getDeck(), 4, 4, false);
    assertFalse(a.toString().contains("Invalid move. Try again.\n"));

    //valid move to foundation w/card inside
    read = new StringReader("C4 13 o1 c4 12 O2 C4 11 O3 C4 10 f3 c1 13 o5 c1 12 o6 c1 11 f3 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller2 = new SimpleFreecellController(model, read, a);
    controller2.playGame(model.getDeck(), 4, 6, false);
    assertFalse(a.toString().contains("Invalid move. Try again.\n"));

    //valid move to cascade
    SimpleFreecellModel seeded = new SimpleFreecellModel();
    List<Card> seed = seeded.getDeck();
    Collections.shuffle(seed, new Random(22));

    read = new StringReader("C4 13 c2 q");
    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController controller3 = new SimpleFreecellController(seeded, read, a);
    controller3.playGame(seed, 4, 6, false);
    assertFalse(a.toString().contains("Invalid move. Try again.\n"));
  }

  @Test
  public void testGameOver() {
    String firstSuit = "c1 1 f1 c2 1 f1 c3 1 f1 c4 1 f1 c5 1 f1 c6 1 f1 c7 1 f1 c8 1 f1 "
            + "c9 1 f1 c10 1 f1 c11 1 f1 c12 1 f1 c13 1 f1";
    String secondSuit = " c14 1 f2 c15 1 f2 c16 1 f2 c17 1 f2 c18 1 f2 c19 1 f2 c20 1 f2 "
            + "c21 1 f2 c22 1 f2 c23 1 f2 c24 1 f2 c25 1 f2 c26 1 f2";
    String thirdSuit = " c27 1 f3 c28 1 f3 c29 1 f3 c30 1 f3 c31 1 f3 c32 1 f3 c33 1 f3 "
            + "c34 1 f3 c35 1 f3 c36 1 f3 c37 1 f3 c38 1 f3 c39 1 f3";
    String fourthSuit = " c40 1 f4 c41 1 f4 c42 1 f4 c43 1 f4 c44 1 f4 c45 1 f4 c46 1 f4"
            + " c47 1 f4 c48 1 f4 c49 1 f4 c50 1 f4 c51 1 f4 c52 1 f4";

    String complete = firstSuit + secondSuit + thirdSuit + fourthSuit;

    StringReader read = new StringReader(complete);
    FreecellController ctrl = new SimpleFreecellController(model, read, a);
    ctrl.playGame(model.getDeck(), 52, 6, false);
    assertTrue(a.toString().contains("Game over.\n"));

  }

  @Test
  public void testGameOverWithInvalid() {
    String firstSuit = "c1 1 f1 c2 1 f1 c3 1 f1 c4 1 f1 c5 1 f1 c6 1 f1 c7 1 f1 c8 1 f1 "
            + "c9 1 f1 c10 1 f1 c11 1 f1 c12 1 f1 c13 1 f1";
    String secondSuit = " c14 1 f2 h37 c15 1 f2 c16 1 f2 c17 1 f2 c18 1 f2 c19 1 f2 c20 1 f2 "
            + "c21 1 f2 c22 1 f2 c23 1 baby2 f2 c24 1 f2 c25 1 f2 c26 1 f2";
    String thirdSuit = " 12 -1 c27 1 f3 c28 1 f3 c29 1 f3 c30 1 f3 c31 1 f3 c32 1 f3 c33 1 f3 "
            + "c34 1 f3 c35 1 f3 c36 1 f3 c37 1 f3 c38 1 f3 c39 1 f3";
    String fourthSuit = " c40 1 f4 c41 1 f4 c42 1 f4 c43 1 f4 c44 1 f4 c45 1 f4 c46 1 f4"
            + " c47 1 f4 c48 1 f4 wrong75 c49 1 f4 c50 bad 1 f4 c51 1 f4 c52 1 f4";

    String complete = firstSuit + secondSuit + thirdSuit + fourthSuit;

    StringReader read = new StringReader(complete);
    FreecellController ctrl = new SimpleFreecellController(model, read, a);
    ctrl.playGame(model.getDeck(), 52, 6, false);
    assertTrue(a.toString().contains("Game over.\n"));

  }

  //----->>> MULTIMOVE

  @Test
  public void testValidInputsInvalidMoveMulti() {
    //these inputs are valid by the controller's standards
    //they are also valid by the model's standard, however the move cannot be completed

    //invalid move to casacde
    ComplexFreecellModel seeded = new ComplexFreecellModel();
    List<Card> seed = seeded.getDeck();
    Collections.shuffle(seed, new Random(10));

    //can't move stack to open or intended cascade
    StringReader read = new StringReader("c6 6 f1 c4 7 c6 c6 5 c2 c2 7 o4 c2 7 c5 q");
    String boardAfterInput = "Invalid move. Try again.\n" +
            "F1: A♣\n" +
            "F2:\n" +
            "F3:\n" +
            "F4:\n" +
            "O1:\n" +
            "O2:\n" +
            "O3:\n" +
            "O4:\n" +
            "C1: 5♠, 7♣, J♣, K♠, K♣, 8♦, 8♠\n" +
            "C2: J♦, 3♥, 8♥, 6♦, 5♦, 6♣, 5♣, 4♥, 3♣\n" +
            "C3: 4♦, 9♣, 4♣, 2♥, 3♠, 10♠, Q♦\n" +
            "C4: 7♥, 7♠, 10♦, Q♥, J♥, 4♠\n" +
            "C5: 9♦, 9♥, 10♣, 2♣, 9♠, A♦\n" +
            "C6: 7♦, 2♠, 5♥, 3♦\n" +
            "C7: A♠, K♦, K♥, 2♦, J♠, 6♥\n" +
            "C8: 10♥, Q♠, Q♣, A♥, 6♠, 8♣";

    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController ctrl = new SimpleFreecellController(seeded, read, a);
    ctrl.playGame(seed, 8, 4, false);
    assertTrue(a.toString().contains(boardAfterInput));
  }

  @Test
  public void testValidInputValidMoveMulti() {
    //valid move to cascade, 1/2/3 cards
    ComplexFreecellModel seeded = new ComplexFreecellModel();
    List<Card> seed = seeded.getDeck();
    Collections.shuffle(seed, new Random(10));

    StringReader read = new StringReader("c6 6 f1 c4 7 c6 c6 5 c2 c2 7 c7 q");
    String boardAfterInput = "F1: A♣\n" +
            "F2:\n" +
            "F3:\n" +
            "F4:\n" +
            "O1:\n" +
            "O2:\n" +
            "O3:\n" +
            "O4:\n" +
            "C1: 5♠, 7♣, J♣, K♠, K♣, 8♦, 8♠\n" +
            "C2: J♦, 3♥, 8♥, 6♦, 5♦, 6♣\n" +
            "C3: 4♦, 9♣, 4♣, 2♥, 3♠, 10♠, Q♦\n" +
            "C4: 7♥, 7♠, 10♦, Q♥, J♥, 4♠\n" +
            "C5: 9♦, 9♥, 10♣, 2♣, 9♠, A♦\n" +
            "C6: 7♦, 2♠, 5♥, 3♦\n" +
            "C7: A♠, K♦, K♥, 2♦, J♠, 6♥, 5♣, 4♥, 3♣\n" +
            "C8: 10♥, Q♠, Q♣, A♥, 6♠, 8♣";

    a.setLength(0);
    assertEquals("", a.toString());
    FreecellController ctrl = new SimpleFreecellController(seeded, read, a);
    ctrl.playGame(seed, 8, 4, false);
    assertTrue(a.toString().contains(boardAfterInput));
  }

}
