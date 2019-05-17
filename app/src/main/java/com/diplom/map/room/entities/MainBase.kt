package com.diplom.map.room.entities

import android.content.ContentValues
import android.content.Context
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.diplom.map.R
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader

@Entity(
    indices = [Index(value = ["numberObject"], unique = true)]
)
data class MainBase(
    @PrimaryKey
    var numberObject: Long = 0,
    var leshos: String = "",
    var lesnich: String = "",
    var kzas: String = "",
    var kv: String = "",
    var admr: String = "",
    var akt: String = "",
    var firma: String = "",
    var ex: String = "",
    var vydel: String = "",
    var pl: String = "",
    var kzm_m1: String = "",
    var ozu: String = "",
    var por_m3: String = "",
    var bon: String = "",
    var tl: String = "",
    var tum: String = "",
    var grv: String = "",
    var zapv: String = "",
    var xozs: String = "",
    var vozrr: String = "",
    var klvozr: String = "",
    var xmer1: String = "",
    var xmer1p: String = "",
    var sux: String = "",
    var xmer2: String = "",
    var por_m2: String = "",
    var god_r: String = "",
    var pni_w: String = "",
    var pni_c: String = "",
    var dm: String = "",
    var ne2a: String = "",
    var ptk2: String = "",
    var ptk1: String = "",
    var tip_m3: String = "",
    var zaxlo: String = "",
    var zaxll: String = "",
    var opt: String = "",
    var ptg: String = "",
    var mapinfo_id: String = "",
    var xmer3: String = "",
    var eks: String = "",
    var krut: String = "",
    var lflag: String = ""
) {
    companion object {
        fun getValues(context: Context): Single<List<ContentValues>> {
            val values = ArrayList<ContentValues>()
            val csvReader =
                BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.mainbase), "windows-1251"))
            csvReader.use {
                val lines = it.readLines()
                val header = lines[0].replace('"', ' ').toLowerCase().split(';')
                for (i in 1 until lines.size) {
                    val value = ContentValues()
                    val line = lines[i].replace('"', ' ').split(';')
                    for (j in 0 until line.size)
                        value.put(header[j].trim(), line[j].trim())
                    values.add(value)
                }
            }
            return Single.just(values)
        }
    }
}

