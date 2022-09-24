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


// For splitting pdf by page count.
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
        suspend fun splitPDF(src: File, pageCount: Int) {
            yield()
            object : PdfSplitter(PdfDocument(PdfReader(src).setMemorySavingMode(true))) {
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
                pageCount
            ) { pdfDocument, _ ->
                pdfDocument.close()
            }
        }

        val sourceTempFile: File = File.createTempFile("readerTempFile", ".pdf")

        yield()
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

// For splitting pdf by byte size.
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
        suspend fun splitPDF(src: File, byteSize: Long) {
            yield()
            val pdfDoc = PdfDocument(PdfReader(src).setMemorySavingMode(true))
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

        val sourceTempFile: File = File.createTempFile("readerTempFile", ".pdf")

        yield()
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

// For splitting pdf by page numbers.
suspend fun getSplitPDFPathsByPageNumbers(
    sourceFileUri: String,
    pageNumbers: List<Int>,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Uri.parse(sourceFileUri)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        @Throws(IOException::class)
        suspend fun splitPDF(src: File, pageNumbers: List<Int>) {
            yield()
            object : PdfSplitter(PdfDocument(PdfReader(src).setMemorySavingMode(true))) {
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
            }.splitByPageNumbers(
                pageNumbers
            ) { pdfDocument, _ -> pdfDocument.close() }
        }

        val sourceTempFile: File = File.createTempFile("readerTempFile", ".pdf")

        yield()
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uriForFileToSplit,
            destinationFileUri = sourceTempFile.toUri(),
            contentResolver = contentResolver
        )

        splitPDF(sourceTempFile, pageNumbers)

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

// For splitting pdf by list of page range.
suspend fun getSplitPDFPathsByPageRanges(
    sourceFileUri: String,
    pageRanges: List<String>,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Uri.parse(sourceFileUri)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        @Throws(IOException::class)
        suspend fun splitPDF(src: File, pageRanges: List<PageRange>) {
            yield()
            val pdfDoc = PdfDocument(PdfReader(src).setMemorySavingMode(true))
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
            }.extractPageRanges(pageRanges)
            for (doc in splitDocuments) {
                doc.close()
            }
            pdfDoc.close()
        }

        val sourceTempFile: File = File.createTempFile("readerTempFile", ".pdf")

        yield()
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uriForFileToSplit,
            destinationFileUri = sourceTempFile.toUri(),
            contentResolver = contentResolver
        )

        val ranges: MutableList<PageRange> = mutableListOf()
        pageRanges.indices.map { index ->
            yield()
            ranges.add(PageRange(pageRanges.elementAt(index)))
        }

        splitPDF(sourceTempFile, ranges)

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


// For splitting pdf by page range.
suspend fun getSplitPDFPathsByPageRange(
    sourceFileUri: String,
    pageRange: String,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Uri.parse(sourceFileUri)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        @Throws(IOException::class)
        suspend fun splitPDF(src: File, pageRange: PageRange) {
            yield()
            val pdfDoc = PdfDocument(PdfReader(src).setMemorySavingMode(true))
            val splitDocument: PdfDocument = object : PdfSplitter(pdfDoc) {
                override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter {
                    return try {
                        val splitTempFile: File =
                            File.createTempFile("splitTempFile_" + 1 + "_", ".pdf")
                        splitTempFilesList.add(splitTempFile)
                        PdfWriter(splitTempFile)
                    } catch (e: FileNotFoundException) {
                        throw RuntimeException(e)
                    }
                }
            }.extractPageRange(pageRange)
            splitDocument.close()
            pdfDoc.close()
        }

        val sourceTempFile: File = File.createTempFile("readerTempFile", ".pdf")

        yield()
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uriForFileToSplit,
            destinationFileUri = sourceTempFile.toUri(),
            contentResolver = contentResolver
        )

        val range = PageRange(pageRange)

        splitPDF(sourceTempFile, range)

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