package com.zybooks.weighttracker;

import java.io.Serializable;

public class Weight implements Serializable {

    private long mId;
    private long mTime;
    private float mWeight;

    // Default constructor
    public Weight() {}

    // Constructor with time and weight
    public Weight(Long time, float weight) {
        mTime = time;
        mWeight = weight;
    }

    // Getter methods
    public long getId() { return mId; }
    public Long getTime() {
        return mTime;
    }
    public float getWeight() { return mWeight; }

    // Setter methods
    public void setId(long id) { mId = id; }
    public void setTime(Long time) {
        mTime = time;
    }
    public void setWeight(float weight) { mWeight = weight; }

}