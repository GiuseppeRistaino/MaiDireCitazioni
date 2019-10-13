package com.secondopianorcost.theoffice.maidirecitazioni.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by giuse on 23/12/2017.
 */

public class Character implements Parcelable {

    private String name;
    private int flag;

    public Character(String name, int flag) {
        this.name = name;
        this.flag = flag;
    }

    protected Character(Parcel in) {
        name = in.readString();
        flag = in.readInt();
    }

    public static final Creator<Character> CREATOR = new Creator<Character>() {
        @Override
        public Character createFromParcel(Parcel in) {
            return new Character(in);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getFlag() {
        return flag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(flag);
    }
}
