package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val LOG_TAG = "PdfManipulator"

class PdfManipulator(
    private val activity: Activity
) {

    private var job: Job? = null

    private val utils = Utils()

    // For merging multiple pdf files.
    fun mergePdfs(
        resultCallback: MethodChannel.Result,
        sourceFilesPaths: List<String>?,
    ) {
        Log.d(
            LOG_TAG, "mergePdfs - IN, sourceFilesPaths=$sourceFilesPaths"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val mergedPDFPath: String? = getMergedPDFPath(sourceFilesPaths!!, activity)

                utils.finishSuccessfullyWithString(mergedPDFPath, resultCallback)
            } catch (e: Exception) {
                utils.finishWithError(
                    "mergePdfs_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "mergePdfs_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "mergePdfs - OUT")
    }

    // For merging multiple pdf files.
    fun splitPdf(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        pageCount: Int,
        byteSize: Number?,
        pageNumbers: List<Int>?,
        pageRanges: List<String>?,
        pageRange: String?,
    ) {
        Log.d(
            LOG_TAG,
            "splitPdf - IN, sourceFilePath=$sourceFilePath, pageCount=$pageCount, byteSize=$byteSize, pageNumbers=$pageNumbers, pageRanges=$pageRanges, pageRange=$pageRange"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val splitPDFPaths: List<String>? = if (byteSize != null) {
                    getSplitPDFPathsByByteSize(
                        sourceFilePath!!, byteSize.toLong(), activity
                    )
                } else if (pageNumbers != null) {
                    getSplitPDFPathsByPageNumbers(sourceFilePath!!, pageNumbers, activity)
                } else if (pageRanges != null) {
                    getSplitPDFPathsByPageRanges(sourceFilePath!!, pageRanges, activity)
                } else if (pageRange != null) {
                    getSplitPDFPathsByPageRange(sourceFilePath!!, pageRange, activity)
                } else {
                    getSplitPDFPathsByPageCount(sourceFilePath!!, pageCount, activity)
                }
                utils.finishSplitSuccessfullyWithListOfString(splitPDFPaths, resultCallback)
            } catch (e: Exception) {
                utils.finishWithError(
                    "splitPdf_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "splitPdf_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "splitPdf - OUT")
    }

    // For removing pages from pdf.
    fun pdfPageDeleter(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        pageNumbers: List<Int>?,
    ) {
        Log.d(
            LOG_TAG, "removePdfPages - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val resultPDFPath: String? =
                    getPDFPageDeleter(sourceFilePath!!, pageNumbers!!, activity)

                utils.finishSuccessfullyWithString(resultPDFPath, resultCallback)
            } catch (e: Exception) {
                utils.finishWithError(
                    "removePdfPages_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "removePdfPages_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "removePdfPages - OUT")
    }

    // For reordering pages of pdf.
    fun pdfPageReorder(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        pageNumbers: List<Int>?,
    ) {
        Log.d(
            LOG_TAG, "pdfPageReorder - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val resultPDFPath: String? =
                    getPDFPageReorder(sourceFilePath!!, pageNumbers!!, activity)

                utils.finishSuccessfullyWithString(resultPDFPath, resultCallback)
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfPageReorder_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfPageReorder_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfPageReorder - OUT")
    }

    // For rotating pages of pdf.
    fun pdfPageRotator(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        pagesRotationInfo: List<Map<String, Int>>?,
    ) {
        Log.d(
            LOG_TAG, "pdfPageRotator - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val newPagesRotationInfo: MutableList<PageRotationInfo> = mutableListOf()

                pagesRotationInfo!!.forEach {
                    val temp = PageRotationInfo(
                        pageNumber = it["pageNumber"]!!, rotationAngle = it["rotationAngle"]!!
                    )
                    newPagesRotationInfo.add(temp)
                }

                val resultPDFPath: String? =
                    getPDFPageRotator(sourceFilePath!!, newPagesRotationInfo, activity)

                utils.finishSuccessfullyWithString(resultPDFPath, resultCallback)
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfPageRotator_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfPageRotator_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfPageRotator - OUT")
    }

    // For reordering, deleting, rotating pages of pdf.
    fun pdfPageRotatorDeleterReorder(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        pageNumbersForReorder: List<Int>,
        pageNumbersForDeleter: List<Int>,
        pagesRotationInfo: List<Map<String, Int>>,
    ) {
        Log.d(
            LOG_TAG, "pdfPageRotatorDeleterReorder - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val newPagesRotationInfo: MutableList<PageRotationInfo> = mutableListOf()

                pagesRotationInfo.forEach {
                    val temp = PageRotationInfo(
                        pageNumber = it["pageNumber"]!!, rotationAngle = it["rotationAngle"]!!
                    )
                    newPagesRotationInfo.add(temp)
                }

                val resultPDFPath: String? = getPDFPageRotatorDeleterReorder(
                    sourceFilePath!!,
                    pageNumbersForReorder,
                    pageNumbersForDeleter,
                    newPagesRotationInfo,
                    activity
                )

                utils.finishSuccessfullyWithString(resultPDFPath, resultCallback)
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfPageRotatorDeleterReorder_exception",
                    e.stackTraceToString(),
                    null,
                    resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfPageRotatorDeleterReorder_OutOfMemoryError",
                    e.stackTraceToString(),
                    null,
                    resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfPageRotatorDeleterReorder - OUT")
    }


    // For compressing pdf.
    fun pdfCompressor(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        imageQuality: Int?,
        imageScale: Double?,
        unEmbedFonts: Boolean?,
    ) {
        Log.d(
            LOG_TAG, "pdfCompressor - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val resultPDFPath: String? = getCompressedPDFPath(
                    sourceFilePath!!, imageQuality!!, imageScale!!, unEmbedFonts!!, activity
                )

                utils.finishSuccessfullyWithString(resultPDFPath, resultCallback)
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfCompressor_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfCompressor_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfCompressor - OUT")
    }

    // For compressing pdf.
    fun watermarkPdf(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        text: String?,
        fontSize: Double?,
        watermarkLayer: WatermarkLayer?,
        opacity: Double?,
        rotationAngle: Double?,
        watermarkColor: String?,
        positionType: PositionType?,
        customPositionXCoordinatesList: List<Double>?,
        customPositionYCoordinatesList: List<Double>?,
    ) {
        Log.d(
            LOG_TAG, "pdfCompressor - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val resultPDFPath: String? = getWatermarkedPDFPath(
                    sourceFilePath!!,
                    text!!,
                    fontSize!!,
                    watermarkLayer!!,
                    opacity!!,
                    rotationAngle!!,
                    watermarkColor!!,
                    positionType!!,
                    customPositionXCoordinatesList ?: listOf(),
                    customPositionYCoordinatesList ?: listOf(),
                    activity
                )

                utils.finishSuccessfullyWithString(resultPDFPath, resultCallback)
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfCompressor_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfCompressor_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfCompressor - OUT")
    }

    // For pdf pages size.
    fun pdfPagesSize(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
    ) {
        Log.d(
            LOG_TAG, "pdfCompressor - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val result: List<List<Double>> = getPDFPagesSize(
                    sourceFilePath!!, activity
                )
                if (result.isEmpty()) {
                    utils.finishSplitSuccessfullyWithListOfListOfDouble(null, resultCallback)
                } else {
                    utils.finishSplitSuccessfullyWithListOfListOfDouble(result, resultCallback)
                }
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfCompressor_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfCompressor_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfCompressor - OUT")
    }

    // For pdf validity and protection.
    fun pdfValidityAndProtection(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        ownerPassword: String?,
    ) {
        Log.d(
            LOG_TAG, "pdfValidityAndProtection - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val result: List<Boolean?> = getPdfValidityAndProtection(
                    sourceFilePath!!, ownerPassword!!, activity
                )

                utils.finishSplitSuccessfullyWithListOfBoolean(result, resultCallback)

            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfValidityAndProtection_exception",
                    e.stackTraceToString(),
                    null,
                    resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfValidityAndProtection_OutOfMemoryError",
                    e.stackTraceToString(),
                    null,
                    resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfValidityAndProtection - OUT")
    }

    // For pdf decryption.
    fun pdfDecryption(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        ownerPassword: String?,
    ) {
        Log.d(
            LOG_TAG, "pdfDecryption - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val result: String? = getPdfDecrypted(
                    sourceFilePath!!, ownerPassword!!, activity
                )

                utils.finishSuccessfullyWithString(result, resultCallback)

            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfDecryption_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfDecryption_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfDecryption - OUT")
    }

    // For pdf encryption.
    fun pdfEncryption(
        resultCallback: MethodChannel.Result,
        sourceFilePath: String?,
        ownerPassword: String?,
        userPassword: String?,
        allowPrinting: Boolean,
        allowModifyContents: Boolean,
        allowCopy: Boolean,
        allowModifyAnnotations: Boolean,
        allowFillIn: Boolean,
        allowScreenReaders: Boolean,
        allowAssembly: Boolean,
        allowDegradedPrinting: Boolean,
        standardEncryptionAES40: Boolean,
        standardEncryptionAES128: Boolean,
        encryptionAES128: Boolean,
        encryptEmbeddedFilesOnly: Boolean,
        doNotEncryptMetadata: Boolean,
    ) {
        Log.d(
            LOG_TAG, "pdfEncryption - IN, sourceFilePath=$sourceFilePath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val pdfPath: String? = getPdfEncrypted(
                    sourceFilePath!!,
                    ownerPassword!!,
                    userPassword!!,
                    allowPrinting,
                    allowModifyContents,
                    allowCopy,
                    allowModifyAnnotations,
                    allowFillIn,
                    allowScreenReaders,
                    allowAssembly,
                    allowDegradedPrinting,
                    standardEncryptionAES40,
                    standardEncryptionAES128,
                    encryptionAES128,
                    encryptEmbeddedFilesOnly,
                    doNotEncryptMetadata,
                    activity
                )

                utils.finishSuccessfullyWithString(pdfPath, resultCallback)

            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfEncryption_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfEncryption_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfEncryption - OUT")
    }

    // For converting images to pdfs.
    fun imagesToPdfs(
        resultCallback: MethodChannel.Result,
        sourceImagesPaths: List<String>?,
        createSinglePdf: Boolean?
    ) {
        Log.d(
            LOG_TAG,
            "imagesToPdfs - IN, sourceImagesPaths = $sourceImagesPaths, createSinglePdf = $createSinglePdf"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val result: List<String> = getPdfsFromImages(
                    sourceImagesPaths!!, createSinglePdf!!, activity, resultCallback
                )
                if (result.isEmpty()) {
                    utils.finishSplitSuccessfullyWithListOfString(null, resultCallback)
                } else {
                    utils.finishSplitSuccessfullyWithListOfString(result, resultCallback)
                }

            } catch (e: Exception) {
                Log.e(LOG_TAG, "imagesToPdfs_exception", e)
                utils.finishWithError(
                    "imagesToPdfs_exception", e.stackTraceToString(), null, resultCallback
                )
            } catch (e: OutOfMemoryError) {
                Log.e(LOG_TAG, "imagesToPdfs_OutOfMemoryError", e)
                utils.finishWithError(
                    "imagesToPdfs_OutOfMemoryError", e.stackTraceToString(), null, resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "imagesToPdfs - OUT")
    }

    fun cancelManipulations(
    ) {
        job?.cancel()
        Log.d(LOG_TAG, "Canceled Manipulations")
    }
}
