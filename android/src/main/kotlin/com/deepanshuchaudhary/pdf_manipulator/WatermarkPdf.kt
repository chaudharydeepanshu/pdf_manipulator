package com.deepanshuchaudhary.pdf_manipulator

import android.app.Activity
import android.content.ContentResolver
import android.graphics.Color
import android.util.Log
import androidx.core.net.toUri
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


enum class WatermarkLayer {
    UnderContent, OverContent
}

enum class PositionType {
    TopLeft, TopCenter, TopRight, CenterLeft, Center, CenterRight, BottomLeft, BottomCenter, BottomRight, Custom
}

// For compressing pdf.
suspend fun getWatermarkedPDFPath(
    sourceFilePath: String,
    text: String,
    fontSize: Double,
    watermarkLayer: WatermarkLayer,
    opacity: Double,
    rotationAngle: Double,
    watermarkColor: String,
    positionType: PositionType,
    customPositionXCoordinatesList: List<Double>,
    customPositionYCoordinatesList: List<Double>,
    context: Activity,
): String? {

    val resultPDFPath: String?

    withContext(Dispatchers.IO) {

        val utils = Utils()

        val begin = System.nanoTime()

        val contentResolver: ContentResolver = context.contentResolver

        val uri = Utils().getURI(sourceFilePath)

        val pdfReaderFile: File = File.createTempFile("readerTempFile", ".pdf")
        utils.copyDataFromSourceToDestDocument(
            sourceFileUri = uri,
            destinationFileUri = pdfReaderFile.toUri(),
            contentResolver = contentResolver
        )

        val pdfReader = PdfReader(pdfReaderFile).setUnethicalReading(true)
        pdfReader.setMemorySavingMode(true)

        val pdfWriterFile: File = File.createTempFile("writerTempFile", ".pdf")

        val pdfWriter = PdfWriter(pdfWriterFile)

        pdfWriter.setSmartMode(true)
        pdfWriter.compressionLevel = 9

        val pdfDocument = PdfDocument(pdfReader, pdfWriter)

        fun watermark() {

            val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            val paragraph = Paragraph(text).setFont(font).setFontSize(fontSize.toFloat())

            val color = try {
                Color.parseColor(watermarkColor)
            } catch (e: Exception) {
                Log.e("Parse", "Error parsing watermarkColor $watermarkColor. $e")
                Color.BLACK
            }

            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            var layer: PdfCanvas

            var position: PositionType = positionType

            if (position == PositionType.Custom) {
                if (customPositionXCoordinatesList.size == pdfDocument.numberOfPages && customPositionYCoordinatesList.size == pdfDocument.numberOfPages) {
                } else {
                    Log.e(
                        "Warning",
                        "customPositionXCoordinatesList or customPositionYCoordinatesList length is not equal to the total number of pages so assigning positionType to PositionType.center"
                    )
                    position = PositionType.Center
                }
            }

            // Implement transformation matrix usage in order to scale image
            for (i in 1..pdfDocument.numberOfPages) {

                val pdfPage: PdfPage = pdfDocument.getPage(i)
                val pageSize: Rectangle = pdfPage.pageSizeWithRotation

                // When "true": in case the page has a rotation, then new content will be automatically rotated in the
                // opposite direction. On the rotated page this would look as if new content ignores page rotation.
                pdfPage.isIgnorePageRotationForContent = true

                layer = if (watermarkLayer == WatermarkLayer.UnderContent) {
                    PdfCanvas(
                        pdfPage.newContentStreamBefore(), PdfResources(), pdfDocument
                    )
                } else {
                    PdfCanvas(pdfPage)
                }

                layer.setFillColor(DeviceRgb(red, green, blue))
                layer.saveState()
                // Creating a dictionary that maps resource names to graphics state parameter dictionaries
                val gs1 = PdfExtGState()
                gs1.fillOpacity = opacity.toFloat()
                layer.setExtGState(gs1)

                val x: Float
                val y: Float

                when (position) {
                    PositionType.TopLeft -> {
                        x = (0).toFloat()
                        y = pageSize.height
                    }
                    PositionType.TopCenter -> {
                        x = pageSize.width / 2
                        y = pageSize.height
                    }
                    PositionType.TopRight -> {
                        x = pageSize.width
                        y = pageSize.height
                    }
                    PositionType.CenterLeft -> {
                        x = (0).toFloat()
                        y = pageSize.height / 2
                    }
                    PositionType.Center -> {
                        x = pageSize.width / 2
                        y = pageSize.height / 2
                    }
                    PositionType.CenterRight -> {
                        x = pageSize.width
                        y = pageSize.height / 2
                    }
                    PositionType.BottomLeft -> {
                        x = (0).toFloat()
                        y = (0).toFloat()
                    }
                    PositionType.BottomCenter -> {
                        x = pageSize.width / 2
                        y = (0).toFloat()
                    }
                    PositionType.BottomRight -> {
                        x = pageSize.width
                        y = (0).toFloat()
                    }
                    else -> {
                        x = customPositionXCoordinatesList[i].toFloat()
                        y = customPositionYCoordinatesList[i].toFloat()
                    }
                }


                val canvasWatermark = Canvas(layer, pdfDocument.defaultPageSize).showTextAligned(
                    paragraph,
                    x,
                    y,
                    i,
                    TextAlignment.CENTER,
                    VerticalAlignment.TOP,
                    rotationAngle.toFloat()
                )
                canvasWatermark.close()

                layer.restoreState()

            }
        }

        watermark()

        pdfDocument.close()
        pdfReader.close()
        pdfWriter.close()

        utils.deleteTempFiles(listOfTempFiles = listOf(pdfReaderFile))

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

        resultPDFPath = pdfWriterFile.path
    }

    return resultPDFPath
}