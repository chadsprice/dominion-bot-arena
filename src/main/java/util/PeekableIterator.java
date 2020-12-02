package util;

import java.util.Iterator;

// an iterator that can return its next value without consuming it
public class PeekableIterator<T> {
    private final Iterator<? extends T> iter;
    private boolean hasNext;
    private T next;

    public PeekableIterator(Iterable<? extends T> iterable) {
        iter = iterable.iterator();
        advance();
    }

    private void advance() {
        hasNext = iter.hasNext();
        if (hasNext) {
            next = iter.next();
        }
    }

    public boolean hasNext() {
        return hasNext;
    }

    public T next() {
        if (!hasNext) {
            throw new IllegalStateException();
        }
        T ret = next;
        advance();
        return ret;
    }

    public T peek() {
        if (!hasNext) {
            throw new IllegalStateException();
        }
        return next;
    }
}