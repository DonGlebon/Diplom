package com.diplom.map.esri.model

import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

internal class DbfStreamReader(stream: InputStream) : DataInputStream(stream) {

    private val stream: DataInputStream = DataInputStream(stream)

    @Throws(IOException::class)
    fun readString(length: Int): String {
        val array = ByteArray(length)
        this.readFully(array)
        val s = String(array, StandardCharsets.UTF_8)
        return s.trim { it <= ' ' }
    }

    @Throws(IOException::class)
    fun readLEShort(): Short {
        val byte1 = this.stream.read()
        val byte2 = this.stream.read()
        return if (byte2 == -1) {
            throw EOFException()
        } else {
            ((byte2 shl 8) + byte1).toShort()
        }
    }

    @Throws(IOException::class)
    fun readLEInt(): Int {
        val byte1: Int
        val byte2: Int
        val byte3: Int
        val byte4: Int
        synchronized(this) {
            byte1 = this.stream.read()
            byte2 = this.stream.read()
            byte3 = this.stream.read()
            byte4 = this.stream.read()
        }

        return if (byte4 == -1) {
            throw EOFException()
        } else {
            (byte4 shl 24) + (byte3 shl 16) + (byte2 shl 8) + byte1
        }
    }

}
