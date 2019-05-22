package com.diplom.map.utils

import android.content.Context
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import okhttp3.ResponseBody
import java.io.*

class FileUtils {

    companion object {
        fun writeResponseBodyToDisk(filename: String, body: ResponseBody, context: Context): File? {
            try {
                val futureStudioIconFile =
                    File(context.getExternalFilesDir(null)?.path + File.separator + "$filename.zip")
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    val fileReader = ByteArray(4096)
                    var fileSizeDownloaded: Long = 0

                    inputStream = body.byteStream()
                    outputStream = FileOutputStream(futureStudioIconFile)

                    while (true) {
                        val read = inputStream!!.read(fileReader)

                        if (read == -1) {
                            break
                        }

                        outputStream.write(fileReader, 0, read)

                        fileSizeDownloaded += read.toLong()
                    }
                    outputStream.flush()

                    return futureStudioIconFile
                } catch (e: IOException) {
                    return null
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            } catch (e: IOException) {
                return null
            }
        }


        @Throws(IOException::class)
        fun unzip(archive: File, targetDirectory: File) {
            val source = archive.path
            val destination = targetDirectory.path
            try {
                val zipFile = ZipFile(source)
                zipFile.extractAll(destination)
            } catch (e: ZipException) {
                e.printStackTrace()
            }
        }
    }
}