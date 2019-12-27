package com.github.rxchallenge.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Sebastian Schipor
 */
@Entity(tableName = "posts")
public class Post implements Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;
    @ColumnInfo(name = "userId")
    public int userId;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "body")
    public String body;
    @ColumnInfo(name = "isFavorite")
    public transient boolean isFavorite;

    public Post() {
    }

    protected Post(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        title = in.readString();
        body = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userId);
        parcel.writeString(title);
        parcel.writeString(body);
        parcel.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
