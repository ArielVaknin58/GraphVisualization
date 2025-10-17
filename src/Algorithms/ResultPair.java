package Algorithms;

import javafx.beans.property.SimpleObjectProperty;

public class ResultPair<K, V> {
    private final SimpleObjectProperty<K> key;
    private final SimpleObjectProperty<V> value;

    public ResultPair(K key, V value) {
        this.key = new SimpleObjectProperty<>(key);
        this.value = new SimpleObjectProperty<>(value);
    }

    public K getKey() { return key.get(); }
    public V getValue() { return value.get(); }
}