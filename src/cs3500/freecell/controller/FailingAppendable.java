package cs3500.freecell.controller;

import java.io.IOException;

/**
 * Class used to test a failing Appendable.
 * Written by Clark Freifeld for a CS3500 lecture explaining controllers.
 */
public class FailingAppendable implements Appendable {

  @Override
  public Appendable append(CharSequence csq) throws IOException {
    throw new IOException("Fail!");
  }

  @Override
  public Appendable append(CharSequence csq, int start, int end) throws IOException {
    throw new IOException("Fail!");
  }

  @Override
  public Appendable append(char c) throws IOException {
    throw new IOException("Fail!");
  }
}