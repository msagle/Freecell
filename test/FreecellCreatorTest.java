import org.junit.Test;

import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.FreecellModelCreator;
import cs3500.freecell.model.SimpleFreecellModel;
import cs3500.freecell.model.multimove.ComplexFreecellModel;

import static org.junit.Assert.assertTrue;

/**
 * JUnit test cases for the freecell creator.
 */
public class FreecellCreatorTest {

  @Test
  public void testCreate() {
    FreecellModel simple = FreecellModelCreator.create(FreecellModelCreator.GameType.SINGLEMOVE);
    assertTrue(simple instanceof SimpleFreecellModel);

    FreecellModel complex = FreecellModelCreator.create(FreecellModelCreator.GameType.MULTIMOVE);
    assertTrue(complex instanceof ComplexFreecellModel);
  }
}
