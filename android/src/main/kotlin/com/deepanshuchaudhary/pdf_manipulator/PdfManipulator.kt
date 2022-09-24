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

    private var pendingResult: MethodChannel.Result? = null

    private var job: Job? = null

    // For merging multiple pdf files.
    fun mergePdfs(
        result: MethodChannel.Result,
        sourceFilesUris: List<String>?,
    ) {
        Log.d(
            LOG_TAG,
            "mergePdfs - IN, sourceFilesUris=$sourceFilesUris"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val mergedPDFPath: String? = getMergedPDFPath(sourceFilesUris!!, activity)

                finishMergeSuccessfully(mergedPDFPath)
            } catch (e: Exception) {
                finishWithError(
                    "mergePdfs_exception",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            } catch (e: OutOfMemoryError) {
                finishWithError(
                    "mergePdfs_OutOfMemoryError",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            }
        }
        Log.d(LOG_TAG, "mergePdfs - OUT")
    }

    // For merging multiple pdf files.
    fun splitPdf(
        result: MethodChannel.Result,
        sourceFileUri: String?,
        pageCount: Int,
        byteSize: Int?,
        pageNumbers: List<Int>?,
        pageRanges: List<String>?,
        pageRange: String?,
    ) {
        Log.d(
            LOG_TAG,
            "splitPdf - IN, sourceFileUri=$sourceFileUri"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val splitPDFPaths: List<String>? = if (byteSize != null) {
                    getSplitPDFPathsByByteSize(sourceFileUri!!, byteSize.toLong(), activity)
                } else if (pageNumbers != null) {
                    getSplitPDFPathsByPageNumbers(sourceFileUri!!, pageNumbers, activity)
                } else if (pageRanges != null) {
                    getSplitPDFPathsByPageRanges(sourceFileUri!!, pageRanges, activity)
                } else if (pageRange != null) {
                    getSplitPDFPathsByPageRange(sourceFileUri!!, pageRange, activity)
                } else {
                    getSplitPDFPathsByPageCount(sourceFileUri!!, pageCount, activity)
                }
                finishSplitSuccessfully(splitPDFPaths)
            } catch (e: Exception) {
                finishWithError(
                    "splitPdf_exception",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            } catch (e: OutOfMemoryError) {
                finishWithError(
                    "splitPdf_OutOfMemoryError",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            }
        }
        Log.d(LOG_TAG, "splitPdf - OUT")
    }

    fun cancelManipulations(
    ) {
        job?.cancel()
//        finishSuccessfully(null)
//        clearPendingResult()
        Log.d(LOG_TAG, "Canceled Manipulations")
    }

    private fun setPendingResult(
        result: MethodChannel.Result
    ): Boolean {
//        if (pendingResult != null) {
//            return false
//        }
        pendingResult = result
        return true
    }

    private fun finishWithAlreadyActiveError(result: MethodChannel.Result) {
        result.error("already_active", "Merging is already active", null)
    }

    private fun clearPendingResult() {
        pendingResult = null
    }

    private fun finishMergeSuccessfully(result: String?) {
        pendingResult?.success(result)
        clearPendingResult()
    }

    private fun finishSplitSuccessfully(result: List<String>?) {
        pendingResult?.success(result)
        clearPendingResult()
    }

    private fun finishWithError(errorCode: String, errorMessage: String?, errorDetails: String?) {
        pendingResult?.error(errorCode, errorMessage, errorDetails)
        clearPendingResult()
    }
}
