package com.massky.sraum.Utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

/**
 * Created by zhu on 2018/8/16.
 */

public class ParceUtil {

    public static String object2String(Parcelable stu) {
        // 1.序列化
        Parcel p = Parcel.obtain();
        stu.writeToParcel(p, 0);
        String mac = p.readString();
        byte[] bytes = p.marshall();
        p.recycle();

        // 2.编码
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        return str;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // this is extremely important!
        return parcel;
    }

    public static <T> T unmarshall(String str, Parcelable.Creator<T> creator) {
        // 1.解码
        byte[] bytes = Base64.decode(str, Base64.DEFAULT);
        // 2.反序列化
        Parcel parcel = unmarshall(bytes);
        return creator.createFromParcel(parcel);
    }
}
