package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PdfMerger
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File

// For merging multiple pdf files.
suspend fun getMergedPDFPath(
    sourceFilesPaths: List<String>,
    context: Activity,
): String? {

    val mergedPDFPath: String?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val mergeResultFile: File = File.createTempFile("mergeResultFile", ".pdf")

        val contentResolver: ContentResolver = context.contentResolver

        val tempListOfUrisForFilesToMerge: MutableList<Uri> = mutableListOf()
        sourceFilesPaths.indices.map { index ->
            yield()
            val uri = Utils().getURI(sourceFilesPaths.elementAt(index))
            tempListOfUrisForFilesToMerge.add(uri)
        }

        suspend fun checkForPDFTagging(): MutableList<Boolean> {
            val listOfTaggingStatus: MutableList<Boolean> = mutableListOf()
            tempListOfUrisForFilesToMerge.forEachIndexed { index, element ->
                yield()
                val tempFile: File =
                    File.createTempFile("readerTempFile$index", ".pdf")
                utils.copyDataFromSourceToDestDocument(
                    sourceFileUri = element,
                    destinationFileUri = tempFile.toUri(),
                    contentResolver = contentResolver
                )
                val pdfReader = PdfReader(tempFile)
                pdfReader.setMemorySavingMode(true)
                val pdfDoc = PdfDocument(pdfReader)
                listOfTaggingStatus.add(pdfDoc.isTagged)
                pdfDoc.close()
                tempFile.delete()

            }
            return listOfTaggingStatus
        }

        val listOfTaggingStatusForFilesToMerge: MutableList<Boolean> =
            checkForPDFTagging()

        fun tempTaggedPdfCreator(): File {
            val tempTaggedPDFFile: File = File.createTempFile("taggedPDFFile", ".pdf")
            //Initialize PDF writer
            val writer = PdfWriter(tempTaggedPDFFile)

            //Initialize PDF document
            val pdf = PdfDocument(writer)

            // Initialize document
            val doc = Document(pdf)

            //Set tagged
            pdf.setTagged()
            //add empty line
            doc.add(Paragraph(" "))

            //Close document
            doc.close()
            return tempTaggedPDFFile
        }

        if (!(listOfTaggingStatusForFilesToMerge[0] || !listOfTaggingStatusForFilesToMerge.contains(
                true
            ))
        ) {
            val taggedPDFFile: File = tempTaggedPdfCreator()
            tempListOfUrisForFilesToMerge.add(0, taggedPDFFile.toUri())
            println(tempListOfUrisForFilesToMerge[0])
        }


        val sourceTempFilesList: MutableList<File> = mutableListOf()

        //This is the file in which all
        val parentTempFile: File = File.createTempFile("readerTempFile0", ".pdf")

        yield()
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = tempListOfUrisForFilesToMerge[0],
            destinationFileUri = parentTempFile.toUri(),
            contentResolver = contentResolver
        )

        sourceTempFilesList.add(parentTempFile)

        val pdfReader = PdfReader(parentTempFile)

        pdfReader.setMemorySavingMode(true)

        val pdfWriter = PdfWriter(mergeResultFile)

        pdfWriter.setSmartMode(true)
        pdfWriter.compressionLevel = 9

        val pdfDocument =
            PdfDocument(pdfReader, pdfWriter)

        val merger = PdfMerger(pdfDocument)


        tempListOfUrisForFilesToMerge.forEachIndexed { index, element ->
            if (index > 0) {
                yield()
                val tempFile: File =
                    File.createTempFile("readerTempFile$index", ".pdf")

                utils.copyDataFromSourceToDestDocument(
                    sourceFileUri = element,
                    destinationFileUri = tempFile.toUri(),
                    contentResolver = contentResolver
                )

                sourceTempFilesList.add(tempFile)

                val pdfRead = PdfReader(tempFile)

                pdfRead.setMemorySavingMode(true)

                val pdfDocument2 = PdfDocument(pdfRead)

                merger.merge(pdfDocument2, 1, pdfDocument2.numberOfPages)


                pdfDocument.flushCopiedObjects(pdfDocument2)

                pdfDocument2.close()


                utils.deleteTempFiles(listOfTempFiles = listOf(tempFile))

            }
        }
        if (!(listOfTaggingStatusForFilesToMerge[0] || !listOfTaggingStatusForFilesToMerge.contains(
                true
            ))
        ) {
            pdfDocument.removePage(1)
        }

        pdfDocument.close()

        println(mergeResultFile.length())

        utils.deleteTempFiles(listOfTempFiles = listOf(parentTempFile))

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

        mergedPDFPath = mergeResultFile.path
    }

    return mergedPDFPath
}