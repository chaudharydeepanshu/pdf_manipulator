package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.core.net.toUri
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File


// For compressing pdf.
suspend fun getCompressedPDFPath(
    sourceFilePath: String,
    imageQuality: Int,
    imageScale: Double,
    unEmbedFonts: Boolean,
    context: Activity,
): String? {

    val resultPDFPath: String?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uri = Utils().getURI(sourceFilePath)

        val pdfReaderFile: File = File.createTempFile("readerTempFile", ".pdf")
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uri,
            destinationFileUri = pdfReaderFile.toUri(),
            contentResolver = contentResolver
        )

        val pdfReader = PdfReader(pdfReaderFile).setUnethicalReading(true)
        pdfReader.setMemorySavingMode(true)

        val pdfWriterFile: File = File.createTempFile("writerTempFile", ".pdf")

        val pdfWriter = PdfWriter(pdfWriterFile)

        pdfWriter.setSmartMode(true)
        pdfWriter.compressionLevel = 9

        val pdfDocument = PdfDocument(pdfReader, pdfWriter)

        suspend fun reduceImagesSize(scale: Double, quality: Int) {
            val factor = scale.toFloat()
            for (indRef in pdfDocument.listIndirectReferences()) {
                yield()

                // Get a direct object and try to resolve indirect chain.
                // Note: If chain of references has length of more than 32,
                // this method return 31st reference in chain.
                val pdfObject: PdfObject? = indRef.refersTo
                if ((pdfObject == null) || !pdfObject.isStream) {
                    continue
                }

                val stream: PdfStream = pdfObject as PdfStream

                if (PdfName.Image != stream.getAsName(PdfName.Subtype)) {
                    continue
                }
                if (PdfName.DCTDecode != stream.getAsName(PdfName.Filter)) {
                    continue
                }
                val image = PdfImageXObject(stream)
                val width = (image.width * factor).toInt()
                val height = (image.height * factor).toInt()
                if (width <= 0 || height <= 0) {
                    continue
                }

                val options: BitmapFactory.Options = BitmapFactory.Options()
                options.inMutable = true
                options.inPreferredConfig = Bitmap.Config.RGB_565
                options.outWidth = width
                options.outHeight = height

                val bmp = BitmapFactory.decodeByteArray(
                    image.imageBytes, 0, image.imageBytes.size, options
                )

                val matrix = Matrix()
                matrix.postTranslate((-0).toFloat(), (-0).toFloat())

                if (factor != 1.0f) matrix.postScale(factor, factor)

                val scaledBitmap = Bitmap.createBitmap(
                    bmp, 0, 0, bmp.width - 1, bmp.height - 1, matrix, true
                )
                bmp.recycle()
                val scaledBitmapStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, scaledBitmapStream)

                scaledBitmap.recycle()
                resetImageStream(
                    stream,
                    scaledBitmapStream.toByteArray(),
                    image.width.toInt(),
                    image.height.toInt()
                )
                scaledBitmapStream.close()
            }
        }

        reduceImagesSize(imageScale, imageQuality)

        suspend fun removeFont() {
            for (i in 0 until pdfDocument.numberOfPdfObjects) {
                yield()
                val obj: PdfObject? = pdfDocument.getPdfObject(i)

                // Skip all objects that aren't a dictionary
                if ((obj == null) || !obj.isDictionary) {
                    continue
                }

                // Process all dictionaries
                unEmbedTTF((obj as PdfDictionary))
            }
        }

        if (unEmbedFonts) {
            removeFont()
        }

        pdfDocument.close()

        pdfReader.close()
        pdfWriter.close()

        utils.deleteTempFiles(listOfTempFiles = listOf(pdfReaderFile))

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

        resultPDFPath = pdfWriterFile.path
    }

    return resultPDFPath
}

fun resetImageStream(
    stream: PdfStream, imgBytes: ByteArray, imgWidth: Int, imgHeight: Int
) {
//    stream.clear()
    if (stream.bytes.size > imgBytes.size) {
        stream.setData(imgBytes)
    } else {
        stream.setData(stream.bytes)
    }
    stream.put(PdfName.Type, PdfName.XObject)
    stream.put(PdfName.Subtype, PdfName.Image)
    stream.put(PdfName.Filter, PdfName.DCTDecode)
    stream.put(PdfName.Width, PdfNumber(imgWidth))
    stream.put(PdfName.Height, PdfNumber(imgHeight))
    stream.put(PdfName.BitsPerComponent, PdfNumber(8))
    stream.put(PdfName.ColorSpace, PdfName.DeviceRGB)
}

fun unEmbedTTF(dict: PdfDictionary) {

    // Ignore all dictionaries that aren't font dictionaries
    if (PdfName.Font != dict.getAsName(PdfName.Type)) {
        return
    }

    // Only TTF fonts should be removed
    if (dict.getAsDictionary(PdfName.FontFile2) != null) {
        return
    }

    // Check if a subset was used (in which case we remove the prefix)
    var baseFont = dict.getAsName(PdfName.BaseFont)

    if (baseFont.value.toByteArray().size >= 7 && baseFont.value.toByteArray()[6] == '+'.code.toByte()) {
        baseFont = PdfName(baseFont.value.substring(7))
        println(baseFont)
        dict.put(PdfName.BaseFont, baseFont)
    }

    // Check if there's a font descriptor
    val fontDescriptor = dict.getAsDictionary(PdfName.FontDescriptor) ?: return

    // Replace the font name and remove the font file
    fontDescriptor.put(PdfName.FontName, baseFont)
    fontDescriptor.remove(PdfName.FontFile2)
}