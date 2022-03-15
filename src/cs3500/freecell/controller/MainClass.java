package cs3500.freecell.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cs3500.freecell.model.Card;
import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.FreecellModelCreator;
import cs3500.freecell.model.multimove.ComplexFreecellModel;


/**
 * Class used to manually test the controller.
 */
public class MainClass {

  /**
   * Method to run the freecell game through the console.
   *
   * @param args input stream from the user
   * @throws IOException when I/O fails
   */
  public static void main(String[] args) throws IOException {

    //---->MULTI-MOVE
    FreecellModel complex = FreecellModelCreator.create(FreecellModelCreator.GameType.MULTIMOVE);
    //FreecellController controller = new SimpleFreecellController(complex,
    //      new InputStreamReader(System.in), System.out);

    //controller.playGame(complex.getDeck(), 8, 4, true);


    Random r = new Random(10);
    ComplexFreecellModel seeded = new ComplexFreecellModel();
    List<Card> seed = seeded.getDeck();
    Collections.shuffle(seed, r);
    FreecellController controller = new SimpleFreecellController(seeded,
                  new InputStreamReader(System.in), System.out);

    controller.playGame(seed, 8, 4, false);


    /*
    //---->SINGLE MOVE:
    FreecellModel model = new SimpleFreecellModel();
    FreecellController controller = new SimpleFreecellController(model,
            new InputStreamReader(System.in), System.out);

    List<Card> deck = model.getDeck();
    Collections.reverse(deck);
    controller.playGame(deck, 52, 4, false);
     */
  }
}
