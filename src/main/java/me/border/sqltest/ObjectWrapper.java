package me.border.sqltest;

import org.apache.commons.lang.ObjectUtils;

public class ObjectWrapper<T> {
    private T t;

    public ObjectWrapper(){
    }

    public ObjectWrapper(T t){
        this.t = t;
    }

    public void set(T t){
        if (t == null){
            throw new NullPointerException("WrappedObject cannot be null");
        }
        this.t = t;
    }

    public T get(){
        if (t == null){
            throw new NullPointerException("WrappedObject cannot be null");
        }
        return this.t;
    }

    public Class<?> getTClass(){
        return this.t.getClass();
    }

    public boolean isWrapperFor(Class<?> type) {
        return this.t.getClass() == type;
    }
}
