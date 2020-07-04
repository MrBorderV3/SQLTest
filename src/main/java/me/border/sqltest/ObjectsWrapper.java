package me.border.sqltest;

// PUT AS MANY OBJECTS AS YOU WANT HERE - Currently 4

public class ObjectsWrapper<T, V, R, D> {
    private NullPointerException exception = new NullPointerException("Wrapped object cannot be null");
    private T t;
    private V v;
    private R r;
    private D d;

    public ObjectsWrapper(){}

    public ObjectsWrapper(T t, V v, R r, D d){
        if (t == null || v == null || r == null || d == null){
            throw exception;
        }
        this.t = t;
        this.v = v;
        this.r = r;
        this.d = d;
    }

    public void setT(T t){
        if (t == null){
            throw exception;
        }
        this.t = t;
    }

    public T getT(){
        return this.t;
    }

    public Class<?> getTClass(){
        return this.t.getClass();
    }

    public void setV(V v){
        if (v == null){
            throw exception;
        }
        this.v = v;
    }

    public V getV(){
        return this.v;
    }

    public Class<?> getVClass(){
        return this.v.getClass();
    }

    public void setR(R r){
        if (r == null){
            throw exception;
        }
        this.r = r;
    }

    public R getR(){
        return this.r;
    }

    public Class<?> getRClass(){
        return this.r.getClass();
    }

    public void setD(D d){
        if (d == null){
            throw exception;
        }
        this.d = d;
    }

    public D getD(){
        return this.d;
    }

    public Class<?> getDClass(){
        return this.d.getClass();
    }
}
