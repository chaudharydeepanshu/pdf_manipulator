package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import androidx.core.net.toUri
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File

// For converting images to pdf.
suspend fun getPdfsFromImages(
    sourceImagesPaths: List<String>,
    createSinglePdf: Boolean,
    context: Activity,
): List<String> {

    val result = mutableListOf<String>()
    val utils = Utils()
    val imagesTempFiles = mutableListOf<File>()

    withContext(Dispatchers.IO) {

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        for (i in sourceImagesPaths.indices) {
            val imagePathOrUri = sourceImagesPaths[i]
            val sourceFileUri = utils.getURI(imagePathOrUri)
            val tempFileName = utils.getFileNameFromPickedDocumentUri(sourceFileUri, context)
            val tempFileExtension = tempFileName!!.substringAfterLast('.', "")
            val tempFile: File = File.createTempFile(tempFileName, ".$tempFileExtension")
            utils.copyDataFromSourceToDestDocument(
                sourceFileUri = sourceFileUri,
                destinationFileUri = tempFile.toUri(),
                contentResolver = contentResolver
            )
            imagesTempFiles.add(tempFile)
        }

        println(imagesTempFiles.map { it.path }.toList())

        if (createSinglePdf) {

            val pdfWriterFile: File = File.createTempFile("writerTempFile", ".pdf")
            val pdfWriter = PdfWriter(pdfWriterFile)
            pdfWriter.setSmartMode(true)
            pdfWriter.compressionLevel = 9
            var image = Image(ImageDataFactory.create(imagesTempFiles[0].path))
            val pdfDocument = PdfDocument(pdfWriter)
            val doc = Document(pdfDocument, PageSize(image.imageWidth, image.imageHeight))
            for (i in imagesTempFiles.indices) {
                yield()
                image = Image(ImageDataFactory.create(imagesTempFiles[i].path))
                pdfDocument.addNewPage(PageSize(image.imageWidth, image.imageHeight))
                image.setFixedPosition(i + 1, 0f, 0f)
                doc.add(image)
                pdfWriter.flush()
            }
            doc.close()
            pdfDocument.close()
            pdfWriter.close()

            result.add(pdfWriterFile.path)

        } else {
            for (i in imagesTempFiles.indices) {
                yield()
                val pdfWriterFile: File = File.createTempFile("writerTempFile", ".pdf")
                val pdfWriter = PdfWriter(pdfWriterFile)
                pdfWriter.setSmartMode(true)
                pdfWriter.compressionLevel = 9
                var image = Image(ImageDataFactory.create(imagesTempFiles[i].path))
                val pdfDocument = PdfDocument(pdfWriter)
                val doc = Document(pdfDocument, PageSize(image.imageWidth, image.imageHeight))
                image = Image(ImageDataFactory.create(imagesTempFiles[i].path))
                pdfDocument.addNewPage(PageSize(image.imageWidth, image.imageHeight))
                image.setFixedPosition(1, 0f, 0f)
                doc.add(image)
                pdfWriter.flush()
                doc.close()
                pdfDocument.close()
                pdfWriter.close()

                result.add(pdfWriterFile.path)
            }
        }

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")
    }

    utils.deleteTempFiles(listOfTempFiles = imagesTempFiles)

    return result
}