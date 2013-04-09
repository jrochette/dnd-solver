package arrayUtilities;

import java.util.Arrays;

public class ArrayCopyOfTest {
  public static final int NEW_ARRAY_SIZE = 4;

  private static Integer[] aArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

  /**
   * @param args
   */
  public static void main(String[] args) {
    Integer[] tempIntegerArray = Arrays.copyOf(aArray, NEW_ARRAY_SIZE);
    System.out.println(tempIntegerArray.length);
    for (int i = 0; i < NEW_ARRAY_SIZE; i++) {
      System.out.println(tempIntegerArray[i]);
    }
  }
}
