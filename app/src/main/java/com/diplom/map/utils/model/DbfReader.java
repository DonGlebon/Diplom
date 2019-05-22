package com.diplom.map.utils.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DbfReader {

    private DbfStreamReader _leis;

    private String[] _columnNames = null;

    private byte[] _lengths = null;

    private byte[] _decimalCounts = null;

    private byte[] _types = null;

    private int _columnCount = -1;

    private int _rowCount = -1;

    private short _headerLength = -1;

    private short _recordLength = -1;

    private ArrayList _records = null;

    public DbfReader(InputStream stream) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(stream);
        _leis = new DbfStreamReader(bis);
        readHeader();
        readFieldDescriptors();
        readData();
    }

    public String[] getColumnNames() {
        return _columnNames;
    }

    public byte[] getLengths() {
        return _lengths;
    }

    public byte[] getDecimalCounts() {
        return _decimalCounts;
    }

    public byte[] getTypes() {
        return _types;
    }

    public ArrayList getRecords() {
        return _records;
    }

    public int getColumnCount() {
        return _columnCount;
    }

    public int getRowCount() {
        return _rowCount;

    }

    private void readHeader() throws IOException {
        byte description = _leis.readByte();
        byte year = _leis.readByte();
        byte month = _leis.readByte();
        byte day = _leis.readByte();
        _rowCount = _leis.readLEInt();
        _headerLength = _leis.readLEShort();
        _recordLength = _leis.readLEShort();
        _columnCount = (_headerLength - 32 - 1) / 32;
        _leis.skipBytes(20);
    }

    private void readFieldDescriptors() throws IOException {
        _columnNames = new String[_columnCount];
        _types = new byte[_columnCount];
        _lengths = new byte[_columnCount];
        _decimalCounts = new byte[_columnCount];

        for (int n = 0; n <= _columnCount - 1; n++) {
            _columnNames[n] = _leis.readString(11);
            _types[n] = (byte) _leis.readByte();
            _leis.skipBytes(4);
            _lengths[n] = _leis.readByte();
            _decimalCounts[n] = _leis.readByte();
            _leis.skipBytes(14);
        }
    }

    private void readData() throws IOException {
        _leis.skipBytes(2);
        _records = new ArrayList();
        for (int r = 0; r <= _rowCount - 1; r++) {
            ArrayList record = new ArrayList();
            for (int c = 0; c <= _columnCount - 1; c++) {
                int length = _lengths[c];
                int type = _types[c];
                String cell = _leis.readString(length);
                if (cell.contains("*")) {
                    record.add(c, "");
                } else {
                    record.add(c, cell);
                }
            }
            _records.add(record);
            _leis.skipBytes(1);
        }
    }
}

