package tester;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import static org.junit.Assert.assertEquals;

public class TestArrayDequeEC {
    @Test
    public void testRandomizedTest() {
        ArrayDequeSolution<Integer> correctDeque = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> studentDeque = new StudentArrayDeque<>();
        StringBuilder operationHistory = new StringBuilder();

        int operations = 1000;  // 限制测试操作次数防止无限循环
        for (int i = 0; i < operations; i++) {
            int operation = StdRandom.uniform(0, 7); // 生成0-6的操作，跳过printDeque测试
            switch (operation) {
                case 0: {
                    // Test addFirst
                    int value = StdRandom.uniform(100);
                    operationHistory.append("addFirst(").append(value).append(")\n");
                    correctDeque.addFirst(value);
                    studentDeque.addFirst(value);
                    break;
                }
                case 1: {
                    // Test addLast
                    int value = StdRandom.uniform(100);
                    operationHistory.append("addLast(").append(value).append(")\n");
                    correctDeque.addLast(value);
                    studentDeque.addLast(value);
                    break;
                }
                case 2: {
                    // Test removeFirst
                    if (!correctDeque.isEmpty()) {
                        operationHistory.append("removeFirst()\n");
                        Integer expected = correctDeque.removeFirst();
                        Integer actual = studentDeque.removeFirst();
                        assertEquals(operationHistory.toString(), expected, actual);
                    }
                    break;
                }
                case 3: {
                    // Test removeLast
                    if (!correctDeque.isEmpty()) {
                        operationHistory.append("removeLast()\n");
                        Integer expected = correctDeque.removeLast();
                        Integer actual = studentDeque.removeLast();
                        assertEquals(operationHistory.toString(), expected, actual);
                    }
                    break;
                }
                case 4: {
                    // Test get
                    if (!correctDeque.isEmpty()) {
                        int index = StdRandom.uniform(0, correctDeque.size());
                        operationHistory.append("get(").append(index).append(")\n");
                        Integer expected = correctDeque.get(index);
                        Integer actual = studentDeque.get(index);
                        assertEquals(operationHistory.toString(), expected, actual);
                    }
                    break;
                }
                case 5: {
                    // Test isEmpty
                    operationHistory.append("isEmpty()\n");
                    boolean expected = correctDeque.isEmpty();
                    boolean actual = studentDeque.isEmpty();
                    assertEquals(operationHistory.toString(), expected, actual);
                    break;
                }
                case 6: {
                    // Test size
                    operationHistory.append("size()\n");
                    int expected = correctDeque.size();
                    int actual = studentDeque.size();
                    assertEquals(operationHistory.toString(), expected, actual);
                    break;
                }
            }

            // 每次操作后检查双端队列状态是否一致
            if (!correctDeque.isEmpty()) {
                assertEquals(operationHistory.toString(), correctDeque.size(), studentDeque.size());
                for (int j = 0; j < correctDeque.size(); j++) {
                    assertEquals(operationHistory.toString() + "Mismatch at index " + j,
                            correctDeque.get(j), studentDeque.get(j));
                }
            }
        }
    }
}
