package com.example.elaundryproject;

import com.google.zxing.LuminanceSource;

public class CustomLuminanceSource extends LuminanceSource {

    private final byte[] luminances;

    public CustomLuminanceSource(int width, int height, int[] pixels) {
        super(width, height);

        luminances = new byte[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                luminances[y * width + x] = (byte) ((r + g + b) / 3);
            }
        }
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image");
        }
        if (row == null || row.length < getWidth()) {
            row = new byte[getWidth()];
        }
        System.arraycopy(luminances, y * getWidth(), row, 0, getWidth());
        return row;
    }

    @Override
    public byte[] getMatrix() {
        return luminances;
    }
}
