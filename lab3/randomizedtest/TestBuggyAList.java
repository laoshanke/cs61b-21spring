package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> lst = new BuggyAList<>();
        AListNoResizing<Integer> lst2 = new AListNoResizing<>();
        int []arr ={4, 5, 6};
        for (int i = 0; i < 3; i += 1) {
            lst.addLast(i);
            lst2.addLast(i);
        }
        for (int i = 0; i < 3; i += 1) {
            assertEquals(lst.removeLast(), lst2.removeLast());
        }
    }
    // YOUR TESTS HERE
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L2 = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size2 = L2.size();
                assertEquals(size2, size);
                System.out.println("size: " + size);
                System.out.println("size2: " + size2);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() > 0) {
                    int last = L.getLast();
                    int last2 = L2.getLast();
                    assertEquals(last2, last);
                    System.out.println("getLast: " + last);
                    System.out.println("getLast2: " + last2);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() > 0) {
                    int last = L.removeLast();
                    int last2 = L2.removeLast();
                    System.out.println("removeLast: " + last);
                    assertEquals(last2, last);
                }
            }
        }

    }
}
