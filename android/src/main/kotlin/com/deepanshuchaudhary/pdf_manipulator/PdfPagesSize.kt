package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import androidx.core.net.toUri
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.PdfReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// For pdf pages size.
suspend fun getPDFPagesSize(
    sourceFilePath: String,
    context: Activity,
): List<List<Double>> {

    val result: MutableList<List<Double>> = mutableListOf()

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uri = utils.getURI(sourceFilePath)


// Important note: As the files are huge, they are probably located on disk, so it is very important to use PdfReader(String) or PdfReader(File) constructors.
// Those take advantage of random read possibility.
// Otherwise, if you simply pass an InputStream, the stream will be first read fully into memory and then document will be constructed.
// This of course still saves some memory for the data structures but keeps the source document in memory which I believe is undesired.

//        val sourceFileInputStream: InputStream? = contentResolver.openInputStream(uri)

        val pdfReaderFile: File =
            File.createTempFile("readerTempFile", ".pdf")
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uri,
            destinationFileUri = pdfReaderFile.toUri(),
            contentResolver = contentResolver
        )

        val pdfReader = PdfReader(pdfReaderFile)
        pdfReader.setMemorySavingMode(true)

        val pdfDocument =
            PdfDocument(pdfReader)

        // Getting pdf all pages size info.
        for (i in 1..pdfDocument.numberOfPages) {

            val pdfPage: PdfPage = pdfDocument.getPage(i)
            val pageSize: Rectangle = pdfPage.pageSizeWithRotation

            result.add(
                listOf(
                    i.toDouble(),
                    pageSize.width.toDouble(),
                    pageSize.height.toDouble()
                )
            )

        }

        pdfDocument.close()

        utils.deleteTempFiles(listOfTempFiles = listOf(pdfReaderFile))

//        sourceFileInputStream?.close()

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")
    }

    return result
}