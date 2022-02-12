package cs3500.freecell.view;

import cs3500.freecell.model.FreecellModelState;

/**
 * Represents a Freecell game via text.
 */
public class FreecellTextView implements FreecellView {
  private FreecellModelState<?> m;

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
