package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import android.util.Log
import androidx.core.net.toUri
import com.deepanshuchaudhary.pdf_manipulator.PdfManipulatorPlugin.Companion.LOG_TAG
import com.itextpdf.kernel.exceptions.BadPasswordException
import com.itextpdf.kernel.exceptions.PdfException
import com.itextpdf.kernel.pdf.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream

// For encrypting pdf.
suspend fun getPdfEncrypted(
    sourceFilePath: String,
    ownerPassword: String,
    userPassword: String,
    allowPrinting: Boolean,
    allowModifyContents: Boolean,
    allowCopy: Boolean,
    allowModifyAnnotations: Boolean,
    allowFillIn: Boolean,
    allowScreenReaders: Boolean,
    allowAssembly: Boolean,
    allowDegradedPrinting: Boolean,
    // standardEncryptionAES40 implicitly sets doNotEncryptMetadata and encryptEmbeddedFilesOnly as false.
    standardEncryptionAES40: Boolean,
    // standardEncryptionAES128 implicitly sets EncryptionConstants.EMBEDDED_FILES_ONLY as false.
    standardEncryptionAES128: Boolean,
    encryptionAES128: Boolean,
    encryptEmbeddedFilesOnly: Boolean,
    doNotEncryptMetadata: Boolean,
    context: Activity,
): String? {

    var result: String? = null

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uri = utils.getURI(sourceFilePath)

        val pdfReaderFile: File = File.createTempFile("readerTempFile", ".pdf")
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uri,
            destinationFileUri = pdfReaderFile.toUri(),
            contentResolver = contentResolver
        )
        val pdfReader: PdfReader
        val pdfDocument: PdfDocument

        val pdfWriterFile: File = File.createTempFile("writerTempFile", ".pdf")

        val resultFileOutputStream: OutputStream? =
            contentResolver.openOutputStream(pdfWriterFile.toUri())

        var permissions = 0

        if (allowPrinting) {
            permissions = EncryptionConstants.ALLOW_PRINTING
        }
        if (allowModifyContents) {
            permissions = permissions or EncryptionConstants.ALLOW_MODIFY_CONTENTS
        }
        if (allowCopy) {
            permissions = permissions or EncryptionConstants.ALLOW_COPY
        }
        if (allowModifyAnnotations) {
            permissions = permissions or EncryptionConstants.ALLOW_MODIFY_ANNOTATIONS
        }
        if (allowFillIn) {
            permissions = permissions or EncryptionConstants.ALLOW_FILL_IN
        }
        if (allowScreenReaders) {
            permissions = permissions or EncryptionConstants.ALLOW_SCREENREADERS
        }
        if (allowAssembly) {
            permissions = permissions or EncryptionConstants.ALLOW_ASSEMBLY
        }
        if (allowDegradedPrinting) {
            permissions = permissions or EncryptionConstants.ALLOW_DEGRADED_PRINTING
        }

        var encryptionAlgorithm: Int

        encryptionAlgorithm = if (standardEncryptionAES40) {
            EncryptionConstants.STANDARD_ENCRYPTION_40
        } else if (standardEncryptionAES128) {
            EncryptionConstants.STANDARD_ENCRYPTION_128
        } else if (encryptionAES128) {
            EncryptionConstants.ENCRYPTION_AES_128
        } else {
            EncryptionConstants.ENCRYPTION_AES_256
        }
        if (encryptEmbeddedFilesOnly) {
            encryptionAlgorithm = encryptionAlgorithm or EncryptionConstants.EMBEDDED_FILES_ONLY
        }
        if (doNotEncryptMetadata) {
            encryptionAlgorithm = encryptionAlgorithm or EncryptionConstants.DO_NOT_ENCRYPT_METADATA
        }

        val pdfWriter = PdfWriter(
            resultFileOutputStream, WriterProperties().setStandardEncryption(
                userPassword.toByteArray(),
                ownerPassword.toByteArray(),
                permissions,
                encryptionAlgorithm
            )
        )

        pdfWriter.setSmartMode(true)
        pdfWriter.compressionLevel = 9

        try {
            pdfReader = PdfReader(pdfReaderFile).setMemorySavingMode(true).setUnethicalReading(true)
            pdfDocument = PdfDocument(pdfReader, pdfWriter)

            result = pdfWriterFile.path

            pdfDocument.close()
            pdfReader.close()
            pdfWriter.close()
        } catch (e: BadPasswordException) {
            Log.d(
                LOG_TAG,
                e.stackTraceToString(),
            )
            pdfWriter.close()
            resultFileOutputStream?.close()
            throw e
        } catch (e: PdfException) {
            Log.d(
                LOG_TAG,
                e.stackTraceToString(),
            )
            pdfWriter.close()
            resultFileOutputStream?.close()
            throw e
        } finally {
            resultFileOutputStream?.close()
        }

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")
    }

    return result
}