package hashtable;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HashTable<K, V> implements Iterable<HashTable.Entry<K, V>> {

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new HashTableIterator();
    }

    public static class Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Entry<?, ?> other = (Entry<?, ?>) obj;
            if (!key.equals(other.key)) {
                return false;
            }
            if (value == null) {
                return other.value == null;
            } else {
                return value.equals(other.value);
            }
        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private class HashTableIterator implements Iterator<Entry<K, V>> {
        private final int expectedModCount;
        private int currentIndex;
        private Iterator<Entry<K,V>> iterator;

        public HashTableIterator() {
            this.expectedModCount = modCount;
            this.currentIndex = 0;
            this.iterator = table[0].iterator();

            findNextEntry();
        }

        private void findNextEntry() {
            if (iterator.hasNext()) {
                return;
            }
            while (++currentIndex < table.length) {
                iterator = table[currentIndex].iterator();
                if (iterator.hasNext()) {
                    return;
                }
            }
            iterator = null;
        }

        @Override
        public boolean hasNext() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return iterator != null;
        }

        @Override
        public Entry<K, V> next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Entry<K, V> entry = iterator.next();
            if (!iterator.hasNext()) {
                findNextEntry();
            }
            return entry;
        }
    }

    private int size;
    private LinkedList<Entry<K, V>>[] table;
    private int modCount = 0;

    public HashTable() {
        this.table = new LinkedList[16];
        for (int i = 0; i < 16; i++) {
            table[i] = new LinkedList<>();
        }
        this.size = 0;
    }

    private int getIndex(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    public void put(K key, V value) {
        int hash = getIndex(key);
        LinkedList<Entry<K, V>> chain = table[hash];
        for (Entry<K, V> entry : chain) {
            if (entry.getKey().equals(key)) {
                entry.setValue(value);
                modCount++;
                return;
            }
        }
        chain.add(new Entry<>(key, value));
        size++;
        modCount++;
    }

    public V get(K key) {
        int hash = getIndex(key);
        LinkedList<Entry<K, V>> chain = table[hash];
        for (Entry<K, V> entry : chain) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void remove(K key) {
        int hash = getIndex(key);
        LinkedList<Entry<K, V>> chain = table[hash];
        Iterator<Entry<K, V>> iterator = chain.iterator();
        while (iterator.hasNext()) {
            Entry<K, V> entry = iterator.next();
            if (entry.getKey().equals(key)) {
                iterator.remove();
                size--;
                modCount++;
                return;
            }
        }
    }

    public void update(K key, V value) {
        put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof HashTable)) {
            return false;
        }
        HashTable<?,?> other = (HashTable<?, ?>) obj;

        if (this.size != other.size) {
            return false;
        }

        java.util.Set<Entry<K, V>> thisSet = new java.util.HashSet<>();
        for (Entry<K, V> entry : this) {
            thisSet.add(entry);
        }
        java.util.Set<Entry<?, ?>> otherSet = new java.util.HashSet<>();
        for (Entry<?, ?> otherEntry : other) {
            otherSet.add(otherEntry);
        }
        return thisSet.equals(otherSet);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("{");
        Iterator<Entry<K, V>> iter = this.iterator();
        while (iter.hasNext()) {
            output.append(iter.next().toString());
            if (iter.hasNext()) {
                output.append(", ");
            }
        }
        output.append("}");
        return output.toString();
    }
}
