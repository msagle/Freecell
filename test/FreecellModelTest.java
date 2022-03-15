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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * JUnit test cases for the freecell model.
 */
public class FreecellModelTest {
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
    basic = new SimpleFreecellModel();

    //create a set random game
    Random r = new Random(10);
    seeded = new SimpleFreecellModel();
    List<Card> seed = new ArrayList<>();
    seed = seeded.getDeck();
    Collections.shuffle(seed, r);
    seeded.startGame(seed, 8, 4, false);

    //create a game with 52 cascade so have easy access to everything
    big = new SimpleFreecellModel();
    big.startGame(big.getDeck(), 52, 2, false);

    //finished game
    gameOver = new SimpleFreecellModel();
    gameOver.startGame(gameOver.getDeck(), 52, 2, false);
    for (int i = 0; i < 52; i++) {
      Suit curr = ((SimpleFreecellModel) gameOver).getCascadeCardAt(i, 0).getSuit();

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

    //restarting a game that has ended
    assertEquals(true, gameOver.isGameOver());
    gameOver.startGame(gameOver.getDeck(), 6, 2, false); //restart
    assertEquals(false, gameOver.isGameOver());

    //making sure the deck was shuffled
    FreecellModel shuffleMe = new SimpleFreecellModel();
    shuffleMe.startGame(shuffleMe.getDeck(), 6, 4, true);
    assertNotEquals(shuffleMe.getCascadeCardAt(0,0),
            new Card(Rank.ACE, heart));
    assertNotEquals(shuffleMe.getCascadeCardAt(3,0),
            new Card(Rank.ACE, club));

    //not enough cascade piles
    try {
      basic.startGame(basic.getDeck(), 3, 4, true);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("There must be at least 4 cascade piles.");
    }

    //not enough open piles
    try {
      basic.startGame(basic.getDeck(), 10, 0, true);
    } catch (IllegalArgumentException e) {
      new IllegalArgumentException("There must be at least 1 open pile.");
    }
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
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Provided deck is invalid.");
    }

    //invalid deck (length)
    try {
      basic.startGame(new ArrayList<Card>(), 8, 3, false);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Provided deck is invalid.");
    }
  }

  @Test
  public void testMove() {
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
  public void testMoveOpen() {
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

    //INVALID! move open to already occupied open
    big.move(PileType.CASCADE, 4, 0, PileType.OPEN, 1);
    try {
      big.move(PileType.CASCADE, 8, 0, PileType.OPEN, 1);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! move open to invalid foundation
    big.move(PileType.CASCADE, 39, 0, PileType.OPEN, 0);
    try {
      big.move(PileType.OPEN, 0, 0, PileType.FOUNDATION, 0);
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid index.");
    }

    //INVALID! move open card from an invalid index
    try {
      big.move(PileType.OPEN, 1, 9, PileType.OPEN, 0);
    }
    catch (Exception e) {
      new IllegalArgumentException("Invalid index.");
    }

  }

  @Test
  public void testMoveFoundation() {

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

    //INVALID! removing card from FP
    try {
      big.move(PileType.FOUNDATION, 0, 0, PileType.OPEN, 0);
    }
    catch (Exception e) {
      new IllegalArgumentException("Cannot move a foundation card.");
    }

    //INVALID! adding Ace to occupied FP
    try {
      big.move(PileType.CASCADE, 0, 0, PileType.FOUNDATION, 0);
    } catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! adding invalid card to index
    try {
      seeded.move(PileType.CASCADE, 1, 6, PileType.FOUNDATION, 3);
    } catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! adding card from middle of list
    try {
      seeded.move(PileType.CASCADE, 4, 3, PileType.FOUNDATION, 3);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot access card at the specified index.");
    }

    //INVALID! adding invalid card (incorrect rank, same suit)
    try {
      big.move(PileType.CASCADE, 44, 0, PileType.FOUNDATION, 0);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
    }

    //INVALID! adding invalid card (incorrect suit, correct rank)
    try {
      big.move(PileType.CASCADE, 2, 0, PileType.FOUNDATION, 0);
    }
    catch (Exception e) {
      new IllegalArgumentException("Illegal move. Cannot move card to the specified index.");
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
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
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
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }

    //game that hasn't started
    FreecellModel noStart = new SimpleFreecellModel();
    try {
      noStart.getNumCardsInCascadePile(0);
    }
    catch (IllegalStateException e) {
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
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }

    //game that hasn't started
    FreecellModel noStart = new SimpleFreecellModel();
    try {
      noStart.getNumCardsInOpenPile(0);
    }
    catch (IllegalStateException e) {
      new IllegalStateException("Game has not started yet!");
    }

  }

  @Test
  public void testNumOP() {
    basic.startGame(basic.getDeck(), 8, 4, true);
    assertEquals(4, basic.getNumOpenPiles());

    basic.startGame(basic.getDeck(), 8, 2, true);
    assertEquals(2, basic.getNumOpenPiles());

    //not enough open piles
    try {
      basic.startGame(basic.getDeck(), 8, 0, true);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("There must be between 1 and 4 open piles");
    }
  }

  @Test
  public void testGetFC() {
    big.move(PileType.CASCADE, 39, 0, PileType.FOUNDATION, 0);
    assertEquals(new Card(Rank.ACE, club), big.getFoundationCardAt(0, 0));
    big.move(PileType.CASCADE, 40, 0, PileType.FOUNDATION, 0);
    assertEquals(new Card(Rank.TWO, club), big.getFoundationCardAt(0, 1));

    //pile doesn't exist
    try {
      big.getFoundationCardAt(5, 0);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }

    //card doesn't exist
    try {
      big.getFoundationCardAt(0, 9);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }
  }

  @Test
  public void testGetCC() {
    assertEquals(new Card(Rank.KING, club), seeded.getCascadeCardAt(0,4));
    assertEquals(new Card(Rank.SIX, heart), seeded.getCascadeCardAt(6,5));
    assertEquals(new Card(Rank.FOUR, diamond), seeded.getCascadeCardAt(2,0));

    //pile doesn't exist
    try {
      seeded.getCascadeCardAt(9, 0);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }

    //card index doesn't exist
    try {
      seeded.getCascadeCardAt(0, 10);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Invalid index.");
    }
  }

  @Test
  public void testGetOC() {
    big.move(PileType.CASCADE, 3, 0, PileType.OPEN, 1);
    assertEquals(new Card(Rank.FOUR, heart), big.getOpenCardAt(1));

    big.move(PileType.CASCADE, 51, 0, PileType.OPEN, 0);
    assertEquals(new Card(Rank.KING, club), big.getOpenCardAt(0));

    //open pile doesn't exist
    try {
      big.getOpenCardAt(5);
    }
    catch (IllegalArgumentException e) {
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
}
