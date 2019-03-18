package com.diplom.map.layers.utils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class DbfStreamReader extends DataInputStream {

    DataInputStream in;

    DbfStreamReader(InputStream in) {
        super(in);
        this.in = new DataInputStream(in);
    }

    String readString(int length) throws IOException {
        byte[] array = new byte[length];
        this.readFully(array);
        String s = new String(array,"windows-1251");
        return s.trim();
    }

    short readLEShort() throws IOException {
        int byte1 = this.in.read();
        int byte2 = this.in.read();
        if (byte2 == -1) {
            throw new EOFException();
        } else {
            return (short) ((byte2 << 8) + byte1);
        }
    }

    int readLEInt() throws IOException {
        int byte1;
        int byte2;
        int byte3;
        int byte4;
        synchronized (this) {
            byte1 = this.in.read();
            byte2 = this.in.read();
            byte3 = this.in.read();
            byte4 = this.in.read();
        }

        if (byte4 == -1) {
            throw new EOFException();
        } else {
            return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
        }
    }

}
