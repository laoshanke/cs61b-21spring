package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c){
        super();
        this.comparator = c;
    }
    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxElement = this.get(0);
        for (T element : this) { // 直接迭代元素，避免索引越界风险
            if (c.compare(element, maxElement) > 0) {
                maxElement = element;
            }
        }
        return maxElement;
    }

}
