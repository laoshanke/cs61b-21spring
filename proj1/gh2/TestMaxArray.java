package gh2;
import deque.*;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMaxArray {
    @Test
    public void testMaxArray() {
        MaxArrayDeque<Integer> a = new MaxArrayDeque<>(Integer::compare);
        MaxArrayDeque<Integer> b = new MaxArrayDeque<>(Integer::compare);
        initial(a,b);
        int max = a.max();
        int max2 = b.max();
        assertEquals("different!a,b:" + max+ max2, max, max2);


    }
    public  void initial(Deque<Integer> a , Deque<Integer> b){
        for (int i = 0; i < 10; i += 1) {
            int num = StdRandom.uniform(10);
            a.addFirst(num);
            b.addFirst(num);
    }

    }
}
