package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.PdfReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

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

        val sourceFileInputStream: InputStream? = contentResolver.openInputStream(uri)

        val pdfReader = PdfReader(sourceFileInputStream)
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
                    pageSize.left.toDouble(),
                    pageSize.right.toDouble(),
                    pageSize.top.toDouble(),
                    pageSize.bottom.toDouble(),
                    pageSize.width.toDouble(),
                    pageSize.height.toDouble()
                )
            )

        }

        pdfDocument.close()

        sourceFileInputStream?.close()

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")
    }

    return result
}