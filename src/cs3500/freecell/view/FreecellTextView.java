package cs3500.freecell.view;

import java.io.IOException;

import cs3500.freecell.model.FreecellModelState;

/**
 * Represents a Freecell game via text.
 */
public class FreecellTextView implements FreecellView {
  private FreecellModelState<?> m;
  private Appendable dest;

  /**
   * Builds an appendable text view of the given FreecellModel.
   * This version is used by the controller to display changes in the model state.
   * @param model the game to translate to text
   * @param dest place to add new information about the game
   * @throws IllegalArgumentException if given model is null
   */
  public FreecellTextView(FreecellModelState<?> model, Appendable dest) {
    if (model == null || dest == null) {
      throw new IllegalArgumentException("Neither the model nor appendable can be null");
    }

    this.m = model;
    this.dest = dest;
  }

  /**
   * Builds the text view of the given FreecellModel.
   * @param model the game to translate to text
   * @throws IllegalArgumentException if given model is null
   */
  public FreecellTextView(FreecellModelState<?> model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.m = model;
  }

  @Override
  public void renderBoard() throws IOException {
    if (m == null) {
      throw new IOException("Invalid model.");
    }
    dest.append(this.toString());
    dest.append("\n");
  }

  @Override
  public void renderMessage(String message) throws IOException {
    if (message == null) {
      throw new IOException("Invalid message.");
    }
    dest.append(message);
  }

  @Override
  public String toString() {
    if (m.getNumCascadePiles() == -1) {
      return "";
    }

    String fp = "";
    String op = "";
    String cp = "";

    String game = "";

    int numFP = 4;
    int numOP = m.getNumOpenPiles();
    int numCP = m.getNumCascadePiles();

    //foundation piles
    for (int i = 0; i < numFP; i++) {
      String fTemp = "F" + (i + 1) + ":";
      if (m.getNumCardsInFoundationPile(i) > 0) {
        fTemp = fTemp + " " + m.getFoundationCardAt(i, 0);

        int place = 1;
        while (place < m.getNumCardsInFoundationPile(i)) {
          fTemp = fTemp + ", " + m.getFoundationCardAt(i, place);
          place++;
        }
      }
      game += fTemp + "\n";
    }

    //open piles
    for (int i = 0; i < numOP; i++) {
      String oTemp = "O" + (i + 1) + ":";
      if (m.getNumCardsInOpenPile(i) == 1) {
        oTemp = oTemp + " " + m.getOpenCardAt(i);
      }
      game += oTemp + "\n";
    }

    //cascade piles
    for (int i = 0; i < numCP; i++) {
      String cTemp = "C" + (i + 1) + ":";
      if (m.getNumCardsInCascadePile(i) > 0) {
        cTemp = cTemp + " " + m.getCascadeCardAt(i, 0);

        int place = 1;
        while (place < m.getNumCardsInCascadePile(i)) {
          cTemp = cTemp + ", " + m.getCascadeCardAt(i, place);
          place++;
        }
      }

      if (i == numCP - 1) {
        game += cTemp;
      } else {
        game += cTemp + "\n";
      }
    }
    return game;
  }
}
