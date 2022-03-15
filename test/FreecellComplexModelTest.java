import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cs3500.freecell.model.Card;
import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.PileType;
import cs3500.freecell.model.Rank;
import cs3500.freecell.model.SimpleFreecellModel;
import cs3500.freecell.model.Suit;
import cs3500.freecell.model.SuitType;
import cs3500.freecell.model.multimove.ComplexFreecellModel;
import cs3500.freecell.view.FreecellTextView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * JUnit test cases for the multi-move freecell model.
 */
public class FreecellComplexModelTest {
  private FreecellModel basic;
  private FreecellModel seeded;
  private FreecellModel big;
  private FreecellModel gameOver;

  private Suit spade;
  private Suit heart;
  private Suit diamond;
  private Suit club;

  @Before
  public void setUp() {
    spade = new Suit(SuitType.SPADE);
    heart = new Suit(SuitType.HEART);
    diamond = new Suit(SuitType.DIAMOND);
    club = new Suit(SuitType.CLUB);

    //randomly shuffled game
    basic = new ComplexFreecellModel();

    //create a set random game
    Random r = new Random(10);
    seeded = new ComplexFreecellModel();
    List<Card> seed = new ArrayList<>();
    seed = seeded.getDeck();
    Collections.shuffle(seed, r);
    seeded.startGame(seed, 8, 4, false);


    //create a game with 52 cascade so have easy access to everything
    big = new ComplexFreecellModel();
    big.startGame(big.getDeck(), 52, 2, false);

    //finished game
    gameOver = new ComplexFreecellModel();
    gameOver.startGame(gameOver.getDeck(), 52, 2, false);
    for (int i = 0; i < 52; i++) {
      Suit curr = ((ComplexFreecellModel) gameOver).getCascadeCardAt(i, 0).getSuit();

      if (curr.equals(spade)) {
        gameOver.move(PileType.CASCADE, i, 0, PileType.FOUNDATION, 0);
      } else if (curr.equals(heart)) {
        gameOver.move(PileType.CASCADE, i, 0, PileType.FOUNDATION, 1);
      } else if (curr.equals(diamond)) {
        gameOver.move(PileType.CASCADE, i, 0, PileType.FOUNDATION, 2);
      } else {
        gameOver.move(PileType.CASCADE, i, 0, PileType.FOUNDATION, 3);
      }
    }

  }

  @Test
  public void testGetDeck() {
    List<Card> test = new ArrayList<Card>();

    assertEquals(0, test.size());

    test = basic.getDeck();
    assertEquals(52, test.size());
    assertEquals(new Card(Rank.ACE, heart), test.get(0));
    assertEquals(new Card(Rank.KING, club), test.get(51));
  }

  @Test
  public void testStartGame() {
    //before
    assertEquals(-1, basic.getNumCascadePiles());
    assertEquals(-1, basic.getNumOpenPiles());
    assertEquals(false, basic.isGameOver());

    basic.startGame(basic.getDeck(), 6, 2, true);

    //after
    assertEquals(6, basic.getNumCascadePiles());
    assertEquals(2, basic.getNumOpenPiles());
    assertEquals(false, basic.isGameOver());
    assertEquals(9, basic.getNumCardsInCascadePile(0));
    assertEquals(8, basic.getNumCardsInCascadePile(5));

    //restart midgame
    basic.startGame(basic.getDeck(), 8, 5, true);
    assertEquals(8, basic.getNumCascadePiles());
    assertEquals(5, basic.getNumOpenPiles());
    assertEquals(false, basic.isGameOver());

    //restarting a game that has ended
    assertEquals(true, gameOver.isGameOver());
    gameOver.startGame(gameOver.getDeck(), 6, 2, false); //restart
    assertEquals(false, gameOver.isGameOver());

    //making sure the deck was shuffled
    FreecellModel shuffleMe = new SimpleFreecellModel();
    shuffleMe.startGame(shuffleMe.getDeck(), 6, 4, true);
    assertNotEquals(shuffleMe.getCascadeCardAt(0, 0),
            new Card(Rank.ACE, heart));
    assertNotEquals(shuffleMe.getCascadeCardAt(3, 0),
            new Card(Rank.ACE, club));
  }

  @Test
  public void testStartGameBadDeck() {
    List<Card> bad = basic.getDeck();
    bad.remove(51);
    bad.add(new Card(Rank.TEN, spade));

    assertEquals(52, bad.size());

    //invalid deck (repeat card)
    try {
      basic.startGame(bad, 8, 3, false);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Provided deck is invalid.");
    }

    //invalid deck (length)
    try {
      basic.startGame(new ArrayList<Card>(), 8, 3, false);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Provided deck is invalid.");
    }
  }

  @Test
  public void testNumCardsFP() {
    basic.startGame(basic.getDeck(), 8, 4, true);

    assertEquals(0, basic.getNumCardsInFoundationPile(0));
    assertEquals(0, basic.getNumCardsInFoundationPile(1));
    assertEquals(0, basic.getNumCardsInFoundationPile(2));
    assertEquals(0, basic.getNumCardsInFoundationPile(3));

    seeded.move(PileType.CASCADE, 4, 5, PileType.FOUNDATION, 1);
    assertEquals(1, seeded.getNumCardsInFoundationPile(1));
    assertEquals(5, seeded.getNumCardsInCascadePile(4));

    big.move(PileType.CASCADE, 39, 0, PileType.FOUNDATION, 0);
    assertEquals(1, big.getNumCardsInFoundationPile(0));
    big.move(PileType.CASCADE, 40, 0, PileType.FOUNDATION, 0);
    assertEquals(2, big.getNumCardsInFoundationPile(0));
    assertEquals(0, big.getNumCardsInCascadePile(39));
    assertEquals(0, big.getNumCardsInCascadePile(40));

    //get num of cards in pile that doesn't exist
    try {
      assertEquals(0, basic.getNumCardsInFoundationPile(4));
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }
  }

  @Test
  public void testNumCardsCP() {
    basic.startGame(basic.getDeck(), 8, 4, true);

    assertEquals(7, basic.getNumCardsInCascadePile(0));
    assertEquals(6, basic.getNumCardsInCascadePile(7));

    assertEquals(7, seeded.getNumCardsInCascadePile(1));
    assertEquals(6, seeded.getNumCardsInCascadePile(6));
    seeded.move(PileType.CASCADE, 1, 6, PileType.CASCADE, 6);
    assertEquals(6, seeded.getNumCardsInCascadePile(1));
    assertEquals(7, seeded.getNumCardsInCascadePile(6));

    //incorrect index number
    try {
      basic.getNumCardsInCascadePile(10);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }

    //game that hasn't started
    FreecellModel noStart = new SimpleFreecellModel();
    try {
      noStart.getNumCardsInCascadePile(0);
    } catch (IllegalStateException e) {
      new IllegalStateException("Game has not started yet!");
    }
  }

  @Test
  public void testNumCardsOP() {
    basic.startGame(basic.getDeck(), 8, 3, true);

    assertEquals(0, basic.getNumCardsInOpenPile(0));
    assertEquals(0, basic.getNumCardsInOpenPile(1));
    assertEquals(0, basic.getNumCardsInOpenPile(2));

    assertEquals(1, big.getNumCardsInCascadePile(3));
    assertEquals(0, big.getNumCardsInOpenPile(1));
    big.move(PileType.CASCADE, 3, 0, PileType.OPEN, 1);
    assertEquals(0, big.getNumCardsInCascadePile(3));
    assertEquals(1, big.getNumCardsInOpenPile(1));

    //incorrect index number
    try {
      basic.getNumCardsInOpenPile(5);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }

    //game that hasn't started
    FreecellModel noStart = new ComplexFreecellModel();
    try {
      noStart.getNumCardsInOpenPile(0);
    } catch (IllegalStateException e) {
      new IllegalStateException("Game has not started yet!");
    }
  }

  @Test
  public void testNumCP() {
    assertEquals(-1, basic.getNumCascadePiles());

    basic.startGame(basic.getDeck(), 8, 4, true);
    assertEquals(8, basic.getNumCascadePiles());

    basic.startGame(basic.getDeck(), 4, 4, true);
    assertEquals(4, basic.getNumCascadePiles());

    assertEquals(52, big.getNumCascadePiles());
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEnoughCascade() {
    basic.startGame(basic.getDeck(), 3, 4, true);
  }

  @Test
  public void testNumOP() {
    basic.startGame(basic.getDeck(), 8, 4, true);
    assertEquals(4, basic.getNumOpenPiles());

    basic.startGame(basic.getDeck(), 8, 2, true);
    assertEquals(2, basic.getNumOpenPiles());
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEnoughOpen() {
    basic.startGame(basic.getDeck(), 10, 0, true);
  }

  @Test
  public void testGetFC() {
    big.move(PileType.CASCADE, 39, 0, PileType.FOUNDATION, 0);
    assertEquals(new Card(Rank.ACE, club), big.getFoundationCardAt(0, 0));
    big.move(PileType.CASCADE, 40, 0, PileType.FOUNDATION, 0);
    assertEquals(new Card(Rank.TWO, club), big.getFoundationCardAt(0, 1));

    try { //pile doesn't exist
      big.getFoundationCardAt(5, 0);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }

    try { //card doesn't exist
      big.getFoundationCardAt(0, 9);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }
  }

  @Test
  public void testGetCC() {
    assertEquals(new Card(Rank.KING, club), seeded.getCascadeCardAt(0, 4));
    assertEquals(new Card(Rank.SIX, heart), seeded.getCascadeCardAt(6, 5));
    assertEquals(new Card(Rank.FOUR, diamond), seeded.getCascadeCardAt(2, 0));

    try { //pile doesn't exist
      seeded.getCascadeCardAt(9, 0);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }

    try { //card doesn't exist
      seeded.getCascadeCardAt(0, 10);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }
  }

  @Test
  public void testGetOC() {
    big.move(PileType.CASCADE, 3, 0, PileType.OPEN, 1);
    assertEquals(new Card(Rank.FOUR, heart), big.getOpenCardAt(1));

    big.move(PileType.CASCADE, 51, 0, PileType.OPEN, 0);
    assertEquals(new Card(Rank.KING, club), big.getOpenCardAt(0));

    try { //open pile doesn't exist
      big.getOpenCardAt(5);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }
  }

  @Test
  public void testGameOver() {
    //game never started
    assertEquals(false, basic.isGameOver());

    //game in progress
    basic.startGame(basic.getDeck(), 6, 2, true);
    assertEquals(false, basic.isGameOver());

    //finished game
    assertEquals(true, gameOver.isGameOver());
  }


  //--------->>>> SINGLE MOVING
  @Test
  public void testMoveSC() {
    //--> I would've split up this method but testing builds upon moving cards
    // around so it would be repetitive

    //VALID! moving cascade to cascade
    assertEquals(7, seeded.getNumCardsInCascadePile(1));
    assertEquals(6, seeded.getNumCardsInCascadePile(6));
    seeded.move(PileType.CASCADE, 1, 6, PileType.CASCADE, 6);
    assertEquals(6, seeded.getNumCardsInCascadePile(1));
    assertEquals(7, seeded.getNumCardsInCascadePile(6));

    //VALID! moving cascade to open
    seeded.move(PileType.CASCADE, 5, 5, PileType.OPEN, 0);
    seeded.move(PileType.CASCADE, 7, 5, PileType.OPEN, 1);
    assertEquals(1, seeded.getNumCardsInOpenPile(0));
    assertEquals(1, seeded.getNumCardsInOpenPile(1));
    assertEquals(5, seeded.getNumCardsInCascadePile(5));
    assertEquals(5, seeded.getNumCardsInCascadePile(7));

    //VALID! moving cascade to foundation
    assertEquals(6, seeded.getNumCardsInCascadePile(4));
    assertEquals(0, seeded.getNumCardsInFoundationPile(2));
    seeded.move(PileType.CASCADE, 4, 5, PileType.FOUNDATION, 2);
    assertEquals(5, seeded.getNumCardsInCascadePile(4));
    assertEquals(1, seeded.getNumCardsInFoundationPile(2));

    //INVALID! moving cascade to cascade (incorrect suit)
    try {
      seeded.move(PileType.CASCADE, 6, 6, PileType.CASCADE, 7);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! moving cascade to cascade (incorrect rank)
    try {
      seeded.move(PileType.CASCADE,5, 4, PileType.CASCADE, 3);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card tp specified index.");
    }

    //INVALID! moving cascade card that is not at the top of the pile
    try {
      seeded.move(PileType.CASCADE, 5, 2, PileType.CASCADE, 1);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot access the card at the specified index.");
    }

    //INVALID! moving cascade to foundation (not A)
    try {
      seeded.move(PileType.CASCADE, 2, 6, PileType.FOUNDATION,0);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! moving cascade to foundation (same suit, wrong rank)
    seeded.move(PileType.OPEN, 0, 0, PileType.FOUNDATION, 0);
    try {
      seeded.move(PileType.CASCADE, 3, 6, PileType.FOUNDATION, 0);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! moving cascade to foundation (right rank, wrong suit)
    seeded.move(PileType.CASCADE, 4, 4, PileType.OPEN, 2);
    try {
      seeded.move(PileType.CASCADE, 4, 3, PileType.FOUNDATION, 1);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! moving cascade to occupied open pile
    try {
      seeded.move(PileType.CASCADE, 0, 6, PileType.OPEN, 1);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! moving to a cascade pile that doesn't exist
    try {
      seeded.move(PileType.CASCADE, 0, 6, PileType.CASCADE, 10);
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid index.");
    }

    //INVALID! moving from a cascade pile that doesn't exist
    try {
      seeded.move(PileType.CASCADE, -1, 0, PileType.CASCADE, 3);
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid index.");
    }
  }


  @Test
  public void testMoveSO() {
    //VALID! move cascade to open
    assertEquals(1, big.getNumCardsInCascadePile(3));
    assertEquals(0, big.getNumCardsInOpenPile(1));
    big.move(PileType.CASCADE, 3, 0, PileType.OPEN, 1);
    assertEquals(0, big.getNumCardsInCascadePile(3));
    assertEquals(1, big.getNumCardsInOpenPile(1));

    //VALID! move open to open
    big.move(PileType.OPEN, 1, 0, PileType.OPEN,0);
    assertEquals(0, big.getNumCardsInOpenPile(1));
    assertEquals(1, big.getNumCardsInOpenPile(0));

    //VALID! move open to cascade
    big.move(PileType.OPEN, 0, 0, PileType.CASCADE,30);
    assertEquals(0, big.getNumCardsInOpenPile(0));
    assertEquals(2, big.getNumCardsInCascadePile(30));

    //VALID! move open to foundation
    big.move(PileType.CASCADE, 0, 0, PileType.OPEN, 0);
    assertEquals(0, big.getNumCardsInCascadePile(0));
    assertEquals(1, big.getNumCardsInOpenPile(0));
    big.move(PileType.OPEN, 0, 0, PileType.FOUNDATION, 0);
    assertEquals(1, big.getNumCardsInFoundationPile(0));
    assertEquals(0, big.getNumCardsInOpenPile(0));

  }

  @Test (expected = IllegalArgumentException.class)
  public void testMoveToInvalidOpenIndex() {
    big.move(PileType.OPEN, 1, 9, PileType.OPEN, 0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testMoveFromInvalidOpenIndex() {
    big.move(PileType.OPEN, 7, 9, PileType.OPEN, 0);
  }

  @Test
  public void testMoveSF() {
    //VALID! Ace from Cascade pile to foundation
    assertEquals(8, seeded.getNumCascadePiles());
    assertEquals(0, seeded.getNumCardsInFoundationPile(1));
    seeded.move(PileType.CASCADE, 4, 5, PileType.FOUNDATION, 1);
    assertEquals(1, seeded.getNumCardsInFoundationPile(1));
    assertEquals(new Card(Rank.ACE, diamond), seeded.getFoundationCardAt(1, 0));

    //VALID! multiple cards from cascade pile to foundation
    big.move(PileType.CASCADE, 39, 0, PileType.FOUNDATION, 0);
    assertEquals(1, big.getNumCardsInFoundationPile(0));
    assertEquals(new Card(Rank.ACE, club), big.getFoundationCardAt(0, 0));
    big.move(PileType.CASCADE, 40, 0, PileType.FOUNDATION, 0);
    assertEquals(2, big.getNumCardsInFoundationPile(0));
    assertEquals(new Card(Rank.TWO, club), big.getFoundationCardAt(0, 1));

    //VALID! from open pile
    seeded.move(PileType.CASCADE, 5, 5, PileType.OPEN, 1);
    assertEquals(new Card(Rank.ACE, club), seeded.getOpenCardAt(1));
    seeded.move(PileType.OPEN, 1, 0, PileType.FOUNDATION, 3);
    assertEquals(new Card(Rank.ACE, club), seeded.getFoundationCardAt(3, 0));

    FreecellTextView seededText = new FreecellTextView(seeded);
    System.out.println(seededText.toString());

  }

  @Test (expected = IllegalArgumentException.class)
  public void testMoveInvalidRemoveFromFP() {
    big.move(PileType.CASCADE, 39, 0, PileType.FOUNDATION, 0);
    big.move(PileType.FOUNDATION, 0, 0, PileType.OPEN, 0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testMoveInvalidCardToFP() {
    seeded.move(PileType.CASCADE, 4, 5, PileType.FOUNDATION, 1);
    seeded.move(PileType.CASCADE, 1, 6, PileType.FOUNDATION, 1);
  }

  //--------->>>> MULTIMOVING
  @Test
  public void testMultiMove() {
    //--> I would've split up this method but testing builds upon moving cards
    // around so it would be repetitive

    seeded.move(PileType.CASCADE, 5, 5, PileType.FOUNDATION, 0);
    seeded.move(PileType.CASCADE, 3, 6, PileType.CASCADE, 5);

    //moving multiple cards (2), all open no empty cascade
    assertEquals(7, seeded.getNumCardsInCascadePile(1));
    seeded.move(PileType.CASCADE, 5, 4, PileType.CASCADE, 1);
    assertEquals(9, seeded.getNumCardsInCascadePile(1));

    //moving multiple cards (3), all open no empty cascade
    assertEquals(6, seeded.getNumCardsInCascadePile(6));
    seeded.move(PileType.CASCADE, 1, 6, PileType.CASCADE, 6);
    assertEquals(9, seeded.getNumCardsInCascadePile(6));

    //making an empty cascade (C6)
    seeded.move(PileType.CASCADE, 5, 3, PileType.CASCADE, 3);
    seeded.move(PileType.CASCADE, 5, 2, PileType.CASCADE, 1);
    seeded.move(PileType.CASCADE, 5, 1, PileType.CASCADE, 3);
    seeded.move(PileType.CASCADE, 5, 0, PileType.CASCADE, 7);

    //multimove with 1 empty cascade, 1 open (3 cards)
    seeded.move(PileType.CASCADE, 0, 6, PileType.OPEN, 0);
    seeded.move(PileType.CASCADE, 0, 5, PileType.OPEN, 1);
    seeded.move(PileType.CASCADE, 0, 4, PileType.OPEN, 2);
    assertEquals(7, seeded.getNumCardsInCascadePile(1));
    seeded.move(PileType.CASCADE, 3, 5, PileType.CASCADE, 1);
    assertEquals(10, seeded.getNumCardsInCascadePile(1));

    //moving pile to pile with incorrect rank
    try {
      seeded.move(PileType.CASCADE, 1, 7, PileType.CASCADE, 2);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    seeded.move(PileType.CASCADE, 4, 5, PileType.FOUNDATION, 1);

    //moving pile to pile with incorrect suit
    try {
      seeded.move(PileType.CASCADE, 7, 5, PileType.CASCADE, 4);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //multimove with 1 empty cascade, 1 open (5 cards)
    try {
      seeded.move(PileType.CASCADE, 1, 5, PileType.CASCADE, 5);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //multimove with 1 open, cascade as destination (5 cards)
    try {
      seeded.move(PileType.CASCADE, 1, 5, PileType.CASCADE, 5);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //multimove with 1 open, cascade as destination (2 cards)
    assertEquals(0, seeded.getNumCardsInCascadePile(5));
    seeded.move(PileType.CASCADE, 1, 8, PileType.CASCADE, 5);
    assertEquals(2, seeded.getNumCardsInCascadePile(5));

    //fill up open piles
    seeded.move(PileType.CASCADE, 0, 3, PileType.OPEN, 3);

    //multimove with 0 open, 0 cascade (2 cards)
    try {
      seeded.move(PileType.CASCADE, 5, 0, PileType.CASCADE, 1);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //move with 0 open, 0 cascade (1 card)
    assertEquals(3, seeded.getNumCardsInCascadePile(0));
    seeded.move(PileType.CASCADE, 0, 2, PileType.CASCADE, 2);
    assertEquals(8, seeded.getNumCardsInCascadePile(2));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testMultiMoveToOpen() { //moving multiple to open pile
    seeded.move(PileType.CASCADE, 5, 5, PileType.FOUNDATION, 0);
    seeded.move(PileType.CASCADE, 3, 6, PileType.CASCADE, 5);

    seeded.move(PileType.CASCADE, 5, 4, PileType.OPEN, 1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testMultiMoveToFoundation() {
    //not possible bc can't make a valid run with the same suit
    seeded.move(PileType.CASCADE, 4, 3, PileType.FOUNDATION, 1);
  }

}