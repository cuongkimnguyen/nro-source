package com.ngocrong.lib;

public class KeyValue<K, V> {

    public KeyValue(K key, V value, Object... obj) {
        this.key = key;
        this.value = value;
        this.elements = obj;
    }

    public K key;
    public V value;
    public Object[] elements;
}
