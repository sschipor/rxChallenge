package com.github.rxchallenge.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Sebastian Schipor
 */
@Entity(tableName = "comments")
public class Comment implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;
    @ColumnInfo(name = "postId")
    public String postId;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "email")
    public String email;
    @ColumnInfo(name = "body")
    public String body;

    public Comment() {
    }

    protected Comment(Parcel in) {
        id = in.readInt();
        postId = in.readString();
        name = in.readString();
        email = in.readString();
        body = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(postId);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(body);
    }
}
