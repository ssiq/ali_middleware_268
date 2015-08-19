package com.alibaba.middleware.race.mom.service;

/**
 * Created by wlw on 15-8-4.
 */
public class Pair<T1,T2> {
    private T1 o1;
    private T2 o2;

    public Pair(T1 o1, T2 o2) {
        this.o1 = o1;
        this.o2 = o2;
    }

    public Pair() {
    }

    public T1 getO1() {
        return o1;
    }

    public T2 getO2() {
        return o2;
    }

    public void setO1(T1 o1) {
        this.o1 = o1;
    }

    public void setO2(T2 o2) {
        this.o2 = o2;
    }

    @Override
    public boolean equals(Object obj) {
        Pair another=(Pair)obj;
        return this.getO1().equals(another.getO1())&&this.getO2().equals(another.getO2());
    }

    @Override
    public int hashCode() {
        return o1.hashCode()*37+o2.hashCode();
    }

    @Override
    public String toString() {
        return "Pair{" +
                "o1=" + o1 +
                ", o2=" + o2 +
                '}';
    }
}
