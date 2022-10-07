package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// For reordering pages of pdf.
suspend fun getPDFPageReorder(
    sourceFileUri: String,
    pageNumbers: List<Int>,
    context: Activity,
): String? {

    val resultPDFPath: String?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uri = Uri.parse(sourceFileUri)

        val pdfReaderFile: File =
            File.createTempFile("readerTempFile", ".pdf")
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uri,
            destinationFileUri = pdfReaderFile.toUri(),
            contentResolver = contentResolver
        )

        val pdfReader = PdfReader(pdfReaderFile)
        pdfReader.setMemorySavingMode(true)

        val pdfWriterFile: File =
            File.createTempFile("writerTempFile", ".pdf")

        val pdfWriter = PdfWriter(pdfWriterFile)

        pdfWriter.setSmartMode(true)
        pdfWriter.compressionLevel = 9

        val srcDoc = PdfDocument(pdfReader)
        val resultDoc = PdfDocument(pdfWriter)

        // One should call this method to preserve the outlines of the source pdf file, otherwise they
        // will be absent in the resultant document to which we copy pages. In this particular sample,
        resultDoc.initializeOutlines()

        srcDoc.copyPagesTo(pageNumbers, resultDoc)
        resultDoc.close()
        srcDoc.close()

        utils.deleteTempFiles(listOfTempFiles = listOf(pdfReaderFile))

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

        resultPDFPath = pdfWriterFile.path
    }

    return resultPDFPath
}