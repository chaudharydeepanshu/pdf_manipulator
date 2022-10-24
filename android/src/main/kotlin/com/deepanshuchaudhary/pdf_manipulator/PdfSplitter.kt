package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
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
    sourceFilePath: String,
    pageCount: Int,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Utils().getURI(sourceFilePath)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        val pdfWritersList: MutableList<PdfWriter> = mutableListOf()

        // https://kb.itextpdf.com/home/it7kb/faq/volume-counter-faqs example converted from java to kotlin
        @Throws(IOException::class)
        suspend fun splitPDF(src: File, pageCount: Int) {
            yield()
            val readerPdfDocument =
                PdfDocument(PdfReader(src).setMemorySavingMode(true).setUnethicalReading(true))
            object : PdfSplitter(readerPdfDocument) {
                var partNumber = 1
                override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter {
                    return try {
                        val splitTempFile: File =
                            File.createTempFile("splitTempFile_" + partNumber++ + "_", ".pdf")
                        splitTempFilesList.add(splitTempFile)
                        val pdfWriter = PdfWriter(splitTempFile)
                        pdfWriter.setSmartMode(true)
                        pdfWriter.compressionLevel = 9
                        pdfWritersList.add(pdfWriter)
                        pdfWritersList.forEach { pdfWriter ->
                            pdfWriter.flush()
                        }
                        pdfWriter
                    } catch (ignored: FileNotFoundException) {
                        throw RuntimeException()
                    }
                }
            }.splitByPageCount(
                pageCount
            ) { pdfDocument, _ ->
                pdfDocument.close()
            }
            readerPdfDocument.close()
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
    sourceFilePath: String,
    byteSize: Long,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Utils().getURI(sourceFilePath)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        val pdfWritersList: MutableList<PdfWriter> = mutableListOf()

        // https://kb.itextpdf.com/home/it7kb/examples/splitting-a-pdf-file example converted from java to kotlin
        @Throws(IOException::class)
        suspend fun splitPDF(src: File, byteSize: Long) {
            yield()
            val readerPdfDocument =
                PdfDocument(PdfReader(src).setMemorySavingMode(true).setUnethicalReading(true))
            val splitDocuments: List<PdfDocument> = object : PdfSplitter(readerPdfDocument) {
                var partNumber = 1
                override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter {
                    return try {
                        val splitTempFile: File =
                            File.createTempFile("splitTempFile_" + partNumber++ + "_", ".pdf")
                        splitTempFilesList.add(splitTempFile)
                        val pdfWriter = PdfWriter(splitTempFile)
                        pdfWriter.setSmartMode(true)
                        pdfWriter.compressionLevel = 9
                        pdfWritersList.add(pdfWriter)
                        pdfWritersList.forEach { pdfWriter ->
                            pdfWriter.flush()
                        }
                        pdfWriter
                    } catch (e: FileNotFoundException) {
                        throw RuntimeException(e)
                    }
                }
            }.splitBySize(byteSize)
            for (doc in splitDocuments) {
                doc.close()
            }
            readerPdfDocument.close()
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
    sourceFilePath: String,
    pageNumbers: List<Int>,
    context: Activity,
): List<String>? {

    var splitPDFPaths: List<String>? = null

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Utils().getURI(sourceFilePath)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        val pdfWritersList: MutableList<PdfWriter> = mutableListOf()

        @Throws(IOException::class)
        suspend fun splitPDF(src: File, pageNumbers: List<Int>) {
            yield()
            val readerPdfDocument =
                PdfDocument(PdfReader(src).setMemorySavingMode(true).setUnethicalReading(true))
            object : PdfSplitter(readerPdfDocument) {
                var partNumber = 1
                override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter {
                    return try {
                        val splitTempFile: File =
                            File.createTempFile("splitTempFile_" + partNumber++ + "_", ".pdf")
                        splitTempFilesList.add(splitTempFile)
                        val pdfWriter = PdfWriter(splitTempFile)
                        pdfWriter.setSmartMode(true)
                        pdfWriter.compressionLevel = 9
                        pdfWritersList.add(pdfWriter)
                        pdfWritersList.forEach { pdfWriter ->
                            pdfWriter.flush()
                        }
                        pdfWriter
                    } catch (ignored: FileNotFoundException) {
                        throw RuntimeException()
                    }
                }
            }.splitByPageNumbers(
                pageNumbers
            ) { pdfDocument, _ ->
                pdfDocument.close()
            }
            readerPdfDocument.close()
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
    sourceFilePath: String,
    pageRanges: List<String>,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Utils().getURI(sourceFilePath)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        val pdfWritersList: MutableList<PdfWriter> = mutableListOf()

        @Throws(IOException::class)
        suspend fun splitPDF(src: File, pageRanges: List<PageRange>) {
            yield()
            val readerPdfDocument =
                PdfDocument(PdfReader(src).setMemorySavingMode(true).setUnethicalReading(true))
            val splitDocuments: List<PdfDocument> = object : PdfSplitter(readerPdfDocument) {
                var partNumber = 1
                override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter {
                    return try {
                        val splitTempFile: File =
                            File.createTempFile("splitTempFile_" + partNumber++ + "_", ".pdf")
                        splitTempFilesList.add(splitTempFile)
                        val pdfWriter = PdfWriter(splitTempFile)
                        pdfWriter.setSmartMode(true)
                        pdfWriter.compressionLevel = 9
                        pdfWritersList.add(pdfWriter)
                        pdfWritersList.forEach { pdfWriter ->
                            pdfWriter.flush()
                        }
                        pdfWriter
                    } catch (e: FileNotFoundException) {
                        throw RuntimeException(e)
                    }
                }
            }.extractPageRanges(pageRanges)
            for (doc in splitDocuments) {
                doc.close()
            }
            readerPdfDocument.close()
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
    sourceFilePath: String,
    pageRange: String,
    context: Activity,
): List<String>? {

    val splitPDFPaths: List<String>?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uriForFileToSplit = Utils().getURI(sourceFilePath)

        val splitTempFilesList: MutableList<File> = mutableListOf()

        val pdfWritersList: MutableList<PdfWriter> = mutableListOf()

        @Throws(IOException::class)
        suspend fun splitPDF(src: File, pageRange: PageRange) {
            yield()
            val readerPdfDocument =
                PdfDocument(PdfReader(src).setMemorySavingMode(true).setUnethicalReading(true))
            val splitDocument: PdfDocument = object : PdfSplitter(readerPdfDocument) {
                override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter {
                    return try {
                        val splitTempFile: File =
                            File.createTempFile("splitTempFile_" + 1 + "_", ".pdf")
                        splitTempFilesList.add(splitTempFile)
                        val pdfWriter = PdfWriter(splitTempFile)
                        pdfWriter.setSmartMode(true)
                        pdfWriter.compressionLevel = 9
                        pdfWritersList.add(pdfWriter)
                        pdfWritersList.forEach { pdfWriter ->
                            pdfWriter.flush()
                        }
                        pdfWriter
                    } catch (e: FileNotFoundException) {
                        throw RuntimeException(e)
                    }
                }
            }.extractPageRange(pageRange)
            splitDocument.close()
            readerPdfDocument.close()
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