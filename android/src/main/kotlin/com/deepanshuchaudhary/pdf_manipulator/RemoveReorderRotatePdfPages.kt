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

// For reordering, deleting, rotating pages of pdf.
suspend fun getPDFPageRotatorDeleterReorder(
    sourceFilePath: String,
    pageNumbersForReorder: List<Int>,
    pageNumbersForDeleter: List<Int>,
    pagesRotationInfo: List<PageRotationInfo>,
    context: Activity,
): String? {

    val resultPDFPath: String?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uri = Utils().getURI(sourceFilePath)

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

        val newPagesRotationInfo: List<PageRotationInfo> = pagesRotationInfo.filter {
            !pageNumbersForDeleter.contains(it.pageNumber)
        }

        val newPageNumbersForReorder: List<Int> = pageNumbersForReorder.filter {
            !pageNumbersForDeleter.contains(it)
        }

        //---------------------For rotating pages---------------------
        val pdfDoc = PdfDocument(pdfReader, pdfWriter)

        newPagesRotationInfo.forEach { pageRotationInfo ->
            val page = pdfDoc.getPage(pageRotationInfo.pageNumber)
            val rotate = page.rotation
            if (rotate == 0) {
                page.rotation = pageRotationInfo.rotationAngle
            } else {
                page.rotation = (rotate + pageRotationInfo.rotationAngle) % 360
            }
        }

        pdfDoc.close()
        //---------------------For rotating pages---------------------

        if (newPageNumbersForReorder.isNotEmpty()) {

            //---------------------Using the previous written file as source---------------------
            val updatedPdfReader = PdfReader(pdfWriterFile)
            pdfReader.setMemorySavingMode(true)

            val newPdfWriterFile: File =
                File.createTempFile("writerTempFile", ".pdf")

            val newPfWriter = PdfWriter(newPdfWriterFile)

            newPfWriter.setSmartMode(true)
            newPfWriter.compressionLevel = 9
            //---------------------Using the previous written file as source---------------------

            //---------------------For reordering pages---------------------

            val srcDoc = PdfDocument(updatedPdfReader)
            val resultDoc = PdfDocument(newPfWriter)

            // One should call this method to preserve the outlines of the source pdf file, otherwise they
            // will be absent in the resultant document to which we copy pages. In this particular sample,
            resultDoc.initializeOutlines()

            srcDoc.copyPagesTo(newPageNumbersForReorder, resultDoc)
            resultDoc.close()
            srcDoc.close()
            //---------------------For reordering pages---------------------

            utils.deleteTempFiles(listOfTempFiles = listOf(pdfReaderFile, pdfWriterFile))

            val end = System.nanoTime()
            println("Elapsed time in nanoseconds: ${end - begin}")

            resultPDFPath = newPdfWriterFile.path
        } else if (pageNumbersForDeleter.isNotEmpty()) {

            //---------------------Using the previous written file as source---------------------
            val updatedPdfReader = PdfReader(pdfWriterFile)
            pdfReader.setMemorySavingMode(true)

            val newPdfWriterFile: File =
                File.createTempFile("writerTempFile", ".pdf")

            val newPfWriter = PdfWriter(newPdfWriterFile)

            newPfWriter.setSmartMode(true)
            newPfWriter.compressionLevel = 9
            //---------------------Using the previous written file as source---------------------

            //---------------------For deleting pages---------------------

            val pdfDocument =
                PdfDocument(updatedPdfReader, newPfWriter)

            //Important to use descending as we remove a page the number of pages in your PDF will change
            val descendingListOfPageNumbers = pageNumbersForDeleter.sortedDescending()

            for (pageNumber in descendingListOfPageNumbers) {
                pdfDocument.removePage(pageNumber)
            }

            pdfDocument.close()
            //---------------------For deleting pages---------------------

            utils.deleteTempFiles(listOfTempFiles = listOf(pdfReaderFile, pdfWriterFile))

            val end = System.nanoTime()
            println("Elapsed time in nanoseconds: ${end - begin}")

            resultPDFPath = newPdfWriterFile.path

        } else {

            utils.deleteTempFiles(listOfTempFiles = listOf(pdfReaderFile))

            val end = System.nanoTime()
            println("Elapsed time in nanoseconds: ${end - begin}")

            resultPDFPath = pdfWriterFile.path
        }
    }

    return resultPDFPath
}