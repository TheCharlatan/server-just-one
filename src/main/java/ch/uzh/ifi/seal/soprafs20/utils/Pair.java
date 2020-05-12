package ch.uzh.ifi.seal.soprafs20.utils;

public class Pair<X, Y> {
  public final X x;
  public Y y;
  public Pair(X x, Y y) {
    this.x = x;
    this.y = y;
  }

  public Y getY() {
      return this.y;
    }
}
