package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import androidx.core.net.toUri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class PageRotationInfo(var pageNumber: Int, val rotationAngle: Int)

// For rotating pages of pdf.
suspend fun getPDFPageRotator(
    sourceFilePath: String,
    pagesRotationInfo: List<PageRotationInfo>,
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

        val pdfDoc = PdfDocument(pdfReader, pdfWriter)

        pagesRotationInfo.forEach { pageRotationInfo ->
            val page = pdfDoc.getPage(pageRotationInfo.pageNumber)
            val rotate = page.rotation
            if (rotate == 0) {
                page.rotation = pageRotationInfo.rotationAngle
            } else {
                page.rotation = (rotate + pageRotationInfo.rotationAngle) % 360
            }
        }

        pdfDoc.close()
        pdfReader.close()
        pdfWriter.close()

        utils.deleteTempFiles(listOfTempFiles = listOf(pdfReaderFile))

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

        resultPDFPath = pdfWriterFile.path
    }

    return resultPDFPath
}