package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PageRange
import com.itextpdf.kernel.utils.PdfSplitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


// For splitting pdf into multiple files.
suspend fun getSplitPDFPathsByPageCount(
    sourceFileUri: String,
    pageCount: Int,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Uri.parse(sourceFileUri)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        // https://kb.itextpdf.com/home/it7kb/faq/volume-counter-faqs example converted from java to kotlin
        @Throws(IOException::class)
        fun splitPDF(SRC: File, PageCount: Int) {
            object : PdfSplitter(PdfDocument(PdfReader(SRC).setMemorySavingMode(true))) {
                var partNumber = 1
                override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter {
                    return try {
                        val splitTempFile: File =
                            File.createTempFile("splitTempFile_" + partNumber++ + "_", ".pdf")
                        splitTempFilesList.add(splitTempFile)
                        PdfWriter(splitTempFile)
                    } catch (ignored: FileNotFoundException) {
                        throw RuntimeException()
                    }
                }
            }.splitByPageCount(
                PageCount
            ) { pdfDocument, _ -> pdfDocument.close() }
        }

        val sourceTempFile: File =
            File.createTempFile("readerTempFile", ".pdf")

        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uriForFileToSplit,
            destinationFileUri = sourceTempFile.toUri(),
            contentResolver = contentResolver
        )

        splitPDF(sourceTempFile, pageCount)

        val splitTempFilesPathsList: MutableList<String> = mutableListOf()

        0.until(splitTempFilesList.size).map { index ->
            yield()
            splitTempFilesPathsList.add(splitTempFilesList.elementAt(index).path)
        }

        println(splitTempFilesPathsList)

        utils.deleteTempFiles(listOfTempFiles = listOf(sourceTempFile))

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

        splitPDFPaths = splitTempFilesPathsList
    }

    return splitPDFPaths
}

// For splitting pdf into multiple files.
suspend fun getSplitPDFPathsByByteSize(
    sourceFileUri: String,
    byteSize: Long,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Uri.parse(sourceFileUri)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        // https://kb.itextpdf.com/home/it7kb/examples/splitting-a-pdf-file example converted from java to kotlin
        @Throws(IOException::class)
        fun splitPDF(SRC: File, byteSize: Long) {
            val pdfDoc = PdfDocument(PdfReader(SRC).setMemorySavingMode(true))
            val splitDocuments: List<PdfDocument> = object : PdfSplitter(pdfDoc) {
                var partNumber = 1
                override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter {
                    return try {
                        val splitTempFile: File =
                            File.createTempFile("splitTempFile_" + partNumber++ + "_", ".pdf")
                        splitTempFilesList.add(splitTempFile)
                        PdfWriter(splitTempFile)
                    } catch (e: FileNotFoundException) {
                        throw RuntimeException(e)
                    }
                }
            }.splitBySize(byteSize)
            for (doc in splitDocuments) {
                doc.close()
            }
            pdfDoc.close()
        }

        val sourceTempFile: File =
            File.createTempFile("readerTempFile", ".pdf")

        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uriForFileToSplit,
            destinationFileUri = sourceTempFile.toUri(),
            contentResolver = contentResolver
        )

        splitPDF(sourceTempFile, byteSize)

        val splitTempFilesPathsList: MutableList<String> = mutableListOf()

        0.until(splitTempFilesList.size).map { index ->
            yield()
            splitTempFilesPathsList.add(splitTempFilesList.elementAt(index).path)
        }

        println(splitTempFilesPathsList)

        utils.deleteTempFiles(listOfTempFiles = listOf(sourceTempFile))

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

        splitPDFPaths = splitTempFilesPathsList
    }

    return splitPDFPaths
}