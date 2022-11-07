package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.ReaderProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

// For decrypting pdf.
suspend fun getPdfDecrypted(
    sourceFilePath: String,
    ownerPassword: String,
    context: Activity,
): String? {

    var result: String? = null

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uri = utils.getURI(sourceFilePath)

        val sourceFileInputStream: InputStream? = contentResolver.openInputStream(uri)

//        val pdfReaderFile: File =
//            File.createTempFile("readerTempFile", ".pdf")
//        utils.copyDataFromSourceToDestDocument(
//            sourceFileUri = uri,
//            destinationFileUri = pdfReaderFile.toUri(),
//            contentResolver = contentResolver
//        )
        val pdfReader: PdfReader
        val pdfDocument: PdfDocument

        val pdfWriterFile: File = File.createTempFile("writerTempFile", ".pdf")

        val pdfWriter = PdfWriter(pdfWriterFile)

        pdfWriter.setSmartMode(true)
        pdfWriter.compressionLevel = 9

        try {
            pdfReader = PdfReader(
                sourceFileInputStream, ReaderProperties().setPassword(ownerPassword.toByteArray())
            ).setMemorySavingMode(true).setUnethicalReading(true)
            pdfDocument = PdfDocument(pdfReader, pdfWriter)

            result = pdfWriterFile.path

            pdfDocument.close()
            pdfReader.close()
            pdfWriter.close()
        } finally {

            pdfWriter.close()
            sourceFileInputStream?.close()
        }

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")
    }

    return result
}