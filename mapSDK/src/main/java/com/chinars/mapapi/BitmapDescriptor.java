//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.chinars.mapapi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class BitmapDescriptor {
    Bitmap a;
    private Bundle b;

    BitmapDescriptor(Bitmap var1) {
        if(var1 != null) {
            int var2 = var1.getWidth();
            int var3 = var1.getHeight();
            this.a = this.a(var1, var2, var3);
        }

    }

    public Bitmap getBitmap() {
        return this.a;
    }

    private Bitmap a(Bitmap var1, int var2, int var3) {
        Bitmap var4 = Bitmap.createBitmap(var2, var3, Config.ARGB_8888);
        Canvas var5 = new Canvas(var4);
        Paint var6 = new Paint();
        var6.setAntiAlias(true);
        var6.setFilterBitmap(true);
        var5.drawBitmap(var1, 0.0F, 0.0F, var6);
        return var4;
    }

    byte[] a() {
        ByteBuffer var1 = ByteBuffer.allocate(this.a.getWidth() * this.a.getHeight() * 4);
        this.a.copyPixelsToBuffer(var1);
        byte[] var2 = var1.array();
        return var2;
    }

    Bundle b() {
        if(this.a == null) {
            throw new IllegalStateException("the bitmap has been recycled! you can not use it again");
        } else {
            if(this.b == null) {
                Bundle var1 = new Bundle();
                var1.putInt("image_width", this.a.getWidth());
                var1.putInt("image_height", this.a.getHeight());
                byte[] var2 = this.a();
                var1.putByteArray("image_data", var2);
                MessageDigest var3 = null;

                try {
                    var3 = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException var7) {
                    var7.printStackTrace();
                }

                var3.update(var2, 0, var2.length);
                byte[] var4 = var3.digest();
                StringBuilder var5 = new StringBuilder("");

                for(int var6 = 0; var6 < var4.length; ++var6) {
                    var5.append(Integer.toString((var4[var6] & 255) + 256, 16).substring(1));
                }

                String var8 = var5.toString();
                var1.putString("image_hashcode", var8);
                this.b = var1;
            }

            return this.b;
        }
    }

    public void recycle() {
        if(this.a != null && !this.a.isRecycled()) {
            this.a.recycle();
            this.a = null;
        }

    }
}
