package cs3500.freecell.controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import cs3500.freecell.model.Card;
import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.PileType;
import cs3500.freecell.view.FreecellTextView;

/**
 * Represents the physical controller for a game of freecell.
 * Reads in user input and passes commands to the model accordingly.
 */
public class SimpleFreecellController implements FreecellController<Card> {
  private final FreecellModel<Card> model;
  private final FreecellTextView view;
  private Readable in;
  private Appendable out;

  /**
   * Builds the controller for a given freecell model.
   *
   * @param model game to be played
   * @param rd    input stream
   * @param ap    output stream
   * @throws IllegalArgumentException if a parameter is null
   */
  public SimpleFreecellController(FreecellModel<Card> model, Readable rd, Appendable ap) {
    if (model == null || rd == null || ap == null) {
      throw new IllegalArgumentException("Parameters cannot be null.");
    }
    this.model = model;
    this.in = rd;
    this.out = ap;
    this.view = new FreecellTextView(model, out);
  }

  @Override
  public void playGame(List<Card> deck, int numCascades, int numOpens, boolean shuffle) {

    try { //catches any IOExceptions if they occur
      if (deck == null) { //throws exception if deck is null
        throw new IllegalArgumentException("Deck cannot be null");
      }

      try { //checks if game can be started, ends program if it cannot
        model.startGame(deck, numCascades, numOpens, shuffle);
        view.renderBoard();
      } catch (IllegalArgumentException e) {
        view.renderMessage("Could not start game.");
        return;
      }

      //creating the scanner to read in the input
      Scanner scan = new Scanner(in);
      String input;

      //holds the source pile data
      String source = "";
      String sPileType;
      int sPileNum;
      boolean sGood = false;

      //holds the card data
      String card = "";
      int cardNum = 0;
      boolean cGood = false;

      //holds the destination pile data
      String dest = "";
      String dPileType;
      int dPileNum;
      boolean dGood = false;

      if (scan.hasNext()) { //makes sure the input stream is open
        //repeat whilst game is still in play
        while (!model.isGameOver()) {
          while (!sGood || !cGood || !dGood) {
            input = scan.next();
            if (input.equalsIgnoreCase("Q")) {
              view.renderMessage("Game quit prematurely.");
              return;
            }

            //checking inputs
            if (!sGood && checkSourceDest(input, "Source")) { //gets source
              source = input;
              sGood = true;
            } else if (!cGood && sGood && checkCard(input)) {  //gets card
              card = input;
              cGood = true;
            } else if (!dGood && sGood && cGood
                    && checkSourceDest(input, "Destination")) {  //gets dest
              dest = input;
              dGood = true;
            } else {
              continue;
            }
          }

          sPileType = source.substring(0, 1);
          sPileNum = Integer.parseInt(source.substring(1));

          cardNum = Integer.parseInt(card);

          dPileType = dest.substring(0, 1);
          dPileNum = Integer.parseInt(dest.substring(1));

          try { //executing move
            model.move(getPT(sPileType), sPileNum - 1, cardNum - 1,
                    getPT(dPileType), dPileNum - 1);
          } catch (IllegalArgumentException e) {
            view.renderMessage("Invalid move. Try again.");
            out.append("\n");
          }

          //move failed/succeeded so need to reset for next move
          sGood = false;
          cGood = false;
          dGood = false;

          view.renderBoard();
          out.append("\n");
        }
        view.renderMessage("Game over.");
        out.append("\n");
        scan.close();
      } else {
        throw new IllegalStateException("Readable has run out!");
      }
    } catch (IOException e) {
      throw new IllegalStateException("You should never encounter an IOException.");
    }
  }

  /**
   * Determines if the given input is a valid input for a source or destination pile.
   * Checks that the input begins with C/c/O/o/F/f and is followed by a number.
   *
   * @param input    user input to be validated
   * @param pileType source or destination pile
   * @return true if the input is valid, false otherwise
   * @throws IOException           if IO fails
   * @throws NumberFormatException if input does not contain a valid integer
   */
  private boolean checkSourceDest(String input, String pileType) throws IOException {
    //checks that the length of the input is valid
    if (input.length() <= 1) {
      view.renderMessage(pileType + " pile cannot be one character. Try again.");
      out.append("\n");
      return false;
    }
    //checks if piletype was valid
    if (input.charAt(0) != 'C' && input.charAt(0) != 'F' && input.charAt(0) != 'O'
            && input.charAt(0) != 'c' && input.charAt(0) != 'f' && input.charAt(0) != 'o') {
      view.renderMessage(pileType + " pile input invalid, try again.");
      out.append("\n");
      return false;
    }

    try {
      int pileNum = Integer.parseInt(input.substring(1));
    } catch (NumberFormatException e) {
      view.renderMessage(pileType + " index cannot be parsed to a valid integer. Try again.");
      out.append("\n");
      return false;
    }
    return true;
  }

  /**
   * Determines if the given input is a valid input for a card index.
   *
   * @param card the card index to be checked
   * @return true if input can be parsed to an integer
   * @throws IOException           if IO fails
   * @throws NumberFormatException if input does not contain a valid integer
   */
  private boolean checkCard(String card) throws IOException {
    int cardNum = 0;

    try {
      cardNum = Integer.parseInt(card);
    } catch (NumberFormatException e) {
      view.renderMessage("Cannot parse card to a valid integer. Try again.");
      out.append("\n");
      return false;
    }
    return true;
  }

  /**
   * Returns the corresponding PileType of the given string.
   *
   * @param pile type of pile to be returned; Either C/O/F
   * @return PileType of the given strinh
   * @throws IllegalArgumentException if the given string does not correspond to a pile type
   */
  private PileType getPT(String pile) {
    if (pile.equals("C") || pile.equals("c")) {
      return PileType.CASCADE;
    } else if (pile.equals("O") || pile.equals("o")) {
      return PileType.OPEN;
    } else if (pile.equals("F") || pile.equals("f")) {
      return PileType.FOUNDATION;
    } else {
      throw new IllegalArgumentException("Invalid pile type.");
    }
  }
}