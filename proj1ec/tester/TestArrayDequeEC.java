package tester;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class TestArrayDequeEC {
    @Test
    public void testRandomizedTest() {
        ArrayDequeSolution<Integer> correctDeque = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> studentDeque = new StudentArrayDeque<>();
        StringBuilder operationHistory = new StringBuilder();

        while (true) {
            int operation = StdRandom.uniform(0, 8);
            switch (operation) {
                case 0: {
                    // Test addFirst
                    operationHistory.append("addFirst(1)\n");
                    correctDeque.addFirst(1);
                    studentDeque.addFirst(1);
                    break;
                }
                case 1: {
                    // Test addLast
                    operationHistory.append("addLast(2)\n");
                    correctDeque.addLast(2);
                    studentDeque.addLast(2);
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
                case 7: {
                    // Test printDeque
                    operationHistory.append("printDeque()\n");
                    String expectedOutput = capturePrintDeque(correctDeque);
                    String actualOutput = capturePrintDeque(studentDeque);
                    assertEquals(operationHistory.toString(), expectedOutput, actualOutput);
                    break;
                }
            }

            // 每次操作后检查双端队列状态是否一致
            if (!correctDeque.isEmpty()) {
                assertEquals(operationHistory.toString(), correctDeque.size(), studentDeque.size());
                for (int i = 0; i < correctDeque.size(); i++) {
                    assertEquals(operationHistory.toString() + "Mismatch at index " + i,
                            correctDeque.get(i), studentDeque.get(i));
                }
            }
        }
    }

    // 捕获 printDeque 的输出并返回字符串
    private String capturePrintDeque(Object deque) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            if (deque instanceof ArrayDequeSolution) {
                ((ArrayDequeSolution<?>) deque).printDeque();
            } else if (deque instanceof StudentArrayDeque) {
                ((StudentArrayDeque<?>) deque).printDeque();
            }
        } finally {
            System.setOut(originalOut);
        }

        return outputStream.toString().trim();
    }
}