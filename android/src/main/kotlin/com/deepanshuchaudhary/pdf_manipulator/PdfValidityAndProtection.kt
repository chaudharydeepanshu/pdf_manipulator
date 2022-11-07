package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import android.util.Log
import com.deepanshuchaudhary.pdf_manipulator.PdfManipulatorPlugin.Companion.LOG_TAG
import com.itextpdf.kernel.exceptions.BadPasswordException
import com.itextpdf.kernel.exceptions.PdfException
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfEncryptor
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.ReaderProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

// For checking pdf validity and encryption.
suspend fun getPdfValidityAndProtection(
    sourceFilePath: String,
    ownerPassword: String,
    context: Activity,
): List<Boolean?> {

    var isPDFValid = true
    var isOwnerPasswordProtected: Boolean? =
        null // Also means that this pdf is owner password protected.
    var isOpenPasswordProtected = false // Means that this pdf is user password protected.
    var isPrintingAllowed: Boolean? = null
    var isModifyContentsAllowed: Boolean? = null

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

        try {
            pdfReader = PdfReader(
                sourceFileInputStream,
                ReaderProperties().setPassword(ownerPassword.toByteArray())
            ).setMemorySavingMode(true).setUnethicalReading(true)
            pdfDocument = PdfDocument(pdfReader)
            isOwnerPasswordProtected = pdfReader.isEncrypted
            val perm = pdfReader.permissions.toInt()
            if (perm != 0) {
                isPrintingAllowed = PdfEncryptor.isPrintingAllowed(perm)
                isModifyContentsAllowed = PdfEncryptor.isModifyContentsAllowed(perm)
            } else {
                isPrintingAllowed = true
                isModifyContentsAllowed = true
            }
            pdfDocument.close()
            pdfReader.close()
        } catch (e: BadPasswordException) {
            isOpenPasswordProtected = true
            Log.d(
                LOG_TAG,
                e.stackTraceToString(),
            )
            sourceFileInputStream?.close()
            throw e
        } catch (e: PdfException) {
            isPDFValid = false
            Log.d(
                LOG_TAG,
                e.stackTraceToString(),
            )
            sourceFileInputStream?.close()
            throw e
        } finally {
            sourceFileInputStream?.close()
        }

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")
    }

    return listOf(
        isPDFValid,
        isOwnerPasswordProtected,
        isOpenPasswordProtected,
        isPrintingAllowed,
        isModifyContentsAllowed
    )
}