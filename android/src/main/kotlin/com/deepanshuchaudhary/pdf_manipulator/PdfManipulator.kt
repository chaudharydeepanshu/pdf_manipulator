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
        sourceFilesPaths: List<String>?,
    ) {
        Log.d(
            LOG_TAG,
            "mergePdfs - IN, sourceFilesPaths=$sourceFilesPaths"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val mergedPDFPath: String? = getMergedPDFPath(sourceFilesPaths!!, activity)

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

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val splitPDFPaths: List<String>? = if (byteSize != null) {
                    getSplitPDFPathsByByteSize(
                        sourceFilePath!!,
                        byteSize.toLong(), activity
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

    // For removing pages from pdf.
    fun pdfPageDeleter(
        result: MethodChannel.Result,
        sourceFilePath: String?,
        pageNumbers: List<Int>?,
    ) {
        Log.d(
            LOG_TAG,
            "removePdfPages - IN, sourceFilePath=$sourceFilePath"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val resultPDFPath: String? =
                    getPDFPageDeleter(sourceFilePath!!, pageNumbers!!, activity)

                finishMergeSuccessfully(resultPDFPath)
            } catch (e: Exception) {
                finishWithError(
                    "removePdfPages_exception",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            } catch (e: OutOfMemoryError) {
                finishWithError(
                    "removePdfPages_OutOfMemoryError",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            }
        }
        Log.d(LOG_TAG, "removePdfPages - OUT")
    }

    // For reordering pages of pdf.
    fun pdfPageReorder(
        result: MethodChannel.Result,
        sourceFilePath: String?,
        pageNumbers: List<Int>?,
    ) {
        Log.d(
            LOG_TAG,
            "pdfPageReorder - IN, sourceFilePath=$sourceFilePath"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val resultPDFPath: String? =
                    getPDFPageReorder(sourceFilePath!!, pageNumbers!!, activity)

                finishMergeSuccessfully(resultPDFPath)
            } catch (e: Exception) {
                finishWithError(
                    "pdfPageReorder_exception",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            } catch (e: OutOfMemoryError) {
                finishWithError(
                    "pdfPageReorder_OutOfMemoryError",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            }
        }
        Log.d(LOG_TAG, "pdfPageReorder - OUT")
    }

    // For rotating pages of pdf.
    fun pdfPageRotator(
        result: MethodChannel.Result,
        sourceFilePath: String?,
        pagesRotationInfo: List<Map<String, Int>>?,
    ) {
        Log.d(
            LOG_TAG,
            "pdfPageRotator - IN, sourceFilePath=$sourceFilePath"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val newPagesRotationInfo: MutableList<PageRotationInfo> = mutableListOf()

                pagesRotationInfo!!.forEach {
                    val temp = PageRotationInfo(
                        pageNumber = it["pageNumber"]!!,
                        rotationAngle = it["rotationAngle"]!!
                    )
                    newPagesRotationInfo.add(temp)
                }

                val resultPDFPath: String? =
                    getPDFPageRotator(sourceFilePath!!, newPagesRotationInfo, activity)

                finishMergeSuccessfully(resultPDFPath)
            } catch (e: Exception) {
                finishWithError(
                    "pdfPageRotator_exception",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            } catch (e: OutOfMemoryError) {
                finishWithError(
                    "pdfPageRotator_OutOfMemoryError",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            }
        }
        Log.d(LOG_TAG, "pdfPageRotator - OUT")
    }

    // For reordering, deleting, rotating pages of pdf.
    fun pdfPageRotatorDeleterReorder(
        result: MethodChannel.Result,
        sourceFilePath: String?,
        pageNumbersForReorder: List<Int>,
        pageNumbersForDeleter: List<Int>,
        pagesRotationInfo: List<Map<String, Int>>,
    ) {
        Log.d(
            LOG_TAG,
            "pdfPageRotatorDeleterReorder - IN, sourceFilePath=$sourceFilePath"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val newPagesRotationInfo: MutableList<PageRotationInfo> = mutableListOf()

                pagesRotationInfo.forEach {
                    val temp = PageRotationInfo(
                        pageNumber = it["pageNumber"]!!,
                        rotationAngle = it["rotationAngle"]!!
                    )
                    newPagesRotationInfo.add(temp)
                }

                val resultPDFPath: String? =
                    getPDFPageRotatorDeleterReorder(
                        sourceFilePath!!,
                        pageNumbersForReorder,
                        pageNumbersForDeleter,
                        newPagesRotationInfo,
                        activity
                    )

                finishMergeSuccessfully(resultPDFPath)
            } catch (e: Exception) {
                finishWithError(
                    "pdfPageRotatorDeleterReorder_exception",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            } catch (e: OutOfMemoryError) {
                finishWithError(
                    "pdfPageRotatorDeleterReorder_OutOfMemoryError",
                    e.stackTraceToString(),
                    null
                )
//            withContext(Dispatchers.IO) {
//                clearPDFFilesFromCache(context = activity)
//            }
            }
        }
        Log.d(LOG_TAG, "pdfPageRotatorDeleterReorder - OUT")
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
