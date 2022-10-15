import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'pdf_manipulator_platform_interface.dart';

/// An implementation of [PdfManipulatorPlatform] that uses method channels.
class MethodChannelPdfManipulator extends PdfManipulatorPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('pdf_manipulator');

  @override
  Future<String?> mergePDFs({PDFMergerParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'mergePDFs', params?.toJson());
    return path;
  }

  @override
  Future<List<String>?> splitPDF({PDFSplitterParams? params}) async {
    final List? paths =
        await methodChannel.invokeMethod<List?>('splitPDF', params?.toJson());
    return paths?.cast<String>();
  }

  @override
  Future<String?> pdfPageDeleter({PDFPageDeleterParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'pdfPageDeleter', params?.toJson());
    return path;
  }

  @override
  Future<String?> pdfPageReorder({PDFPageReorderParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'pdfPageReorder', params?.toJson());
    return path;
  }

  @override
  Future<String?> pdfPageRotator({PDFPageRotatorParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'pdfPageRotator', params?.toJson());
    return path;
  }

  @override
  Future<String?> pdfPageRotatorDeleterReorder(
      {PDFPageRotatorDeleterReorderParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'pdfPageRotatorDeleterReorder', params?.toJson());
    return path;
  }

  @override
  Future<String?> pdfCompressor({PDFCompressorParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'pdfCompressor', params?.toJson());
    return path;
  }

  @override
  Future<String?> pdfWatermark({PDFWatermarkParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'pdfWatermark', params?.toJson());
    return path;
  }

  @override
  Future<String?> cancelManipulations() async {
    final String? result =
        await methodChannel.invokeMethod<String?>('cancelManipulations');
    return result;
  }
}

/// Parameters for the [mergePDFs] method.
class PDFMergerParams {
  /// Provide paths of pdf files to merge.
  final List<String> pdfsPaths;

  /// Create parameters for the [mergePDFs] method.
  const PDFMergerParams({required this.pdfsPaths})
      : assert(pdfsPaths.length > 1, 'provide paths for at least 2 pdfs');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfsPaths': pdfsPaths,
    };
  }
}

/// Parameters for the [splitPDF] method.
///
/// pageCount parameter with value 1 is used if no other parameter is provided except pdfPath.
class PDFSplitterParams {
  /// Provide path of pdf file to split.
  final String pdfPath;

  /// Provide the splitting page count.
  final int? pageCount;

  /// Provide the splitting byte size.
  ///
  /// It will give some pdf bigger than the byte size if the some individual pages in pdf are bigger than the byte size.
  final int? byteSize;

  /// Provide the splitting page numbers.
  final List<int>? pageNumbers;

  /// Provide the splitting page range list.
  final List<String>? pageRanges;

  /// Provide the splitting page range.
  final String? pageRange;

  /// Create parameters for the [splitPDF] method.
  const PDFSplitterParams(
      {required this.pdfPath,
      this.pageCount,
      this.byteSize,
      this.pageNumbers,
      this.pageRanges,
      this.pageRange})
      : assert(
            pageCount == null ||
                byteSize == null ||
                pageNumbers == null ||
                pageRanges == null ||
                pageRange == null,
            'provide only any one out of pageCount or byteSize or pageNumbers or pageRanges or pageRange');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pageCount': pageCount,
      'byteSize': byteSize,
      'pageNumbers': pageNumbers,
      'pageRanges': pageRanges,
      'pageRange': pageRange,
    };
  }
}

/// Parameters for the [pdfPageDeleter] method.
class PDFPageDeleterParams {
  /// Provide path of pdf files from which page should be deleted.
  final String pdfPath;

  /// Provide the page numbers to delete.
  final List<int> pageNumbers;

  /// Create parameters for the [pdfPageDeleter] method.
  const PDFPageDeleterParams({required this.pdfPath, required this.pageNumbers})
      : assert(
            pageNumbers.length > 0, 'provide at least 1 page number to delete');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pageNumbers': pageNumbers,
    };
  }
}

/// Parameters for the [pdfPageReorder] method.
class PDFPageReorderParams {
  /// Provide path of pdf files for which page should be reordered.
  final String pdfPath;

  /// Provide the reordered page numbers.
  final List<int> pageNumbers;

  /// Create parameters for the [pdfPageReorder] method.
  const PDFPageReorderParams({required this.pdfPath, required this.pageNumbers})
      : assert(pageNumbers.length > 0, 'pageNumbers cant be empty');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pageNumbers': pageNumbers,
    };
  }
}

class PageRotationInfo {
  final int pageNumber;
  final int rotationAngle;

  PageRotationInfo({
    required this.pageNumber,
    required this.rotationAngle,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pageNumber': pageNumber,
      'rotationAngle': rotationAngle,
    };
  }

  // Implement toString to make it easier to see information
  // when using the print statement.
  @override
  String toString() {
    return 'PageRotationInfo{pageNumber: $pageNumber, rotationAngle: $rotationAngle}';
  }
}

/// Parameters for the [pdfPageRotator] method.
class PDFPageRotatorParams {
  /// Provide path of pdf files for which page should be reordered.
  final String pdfPath;

  /// Provide the rotation info for pdf pages.
  final List<PageRotationInfo> pagesRotationInfo;

  /// Create parameters for the [pdfPageRotator] method.
  const PDFPageRotatorParams(
      {required this.pdfPath, required this.pagesRotationInfo})
      : assert(pagesRotationInfo.length > 0, 'pageNumbers cant be empty');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pagesRotationInfo': pagesRotationInfo.map((e) => e.toJson()).toList(),
    };
  }
}

/// Parameters for the [pdfPageRotator] method.
class PDFPageRotatorDeleterReorderParams {
  /// Provide path of pdf files for which page should be reordered.
  final String pdfPath;

  /// Provide the rotation info for pdf pages.
  final List<PageRotationInfo>? pagesRotationInfo;

  /// Provide the page numbers to delete.
  final List<int>? pageNumbersForDeleter;

  /// Provide the reordered page numbers.
  final List<int>? pageNumbersForReorder;

  /// Create parameters for the [pdfPageRotator] method.
  const PDFPageRotatorDeleterReorderParams(
      {required this.pdfPath,
      this.pagesRotationInfo,
      this.pageNumbersForDeleter,
      this.pageNumbersForReorder})
      : assert(
            (pagesRotationInfo != null && pagesRotationInfo.length > 0) ||
                (pageNumbersForDeleter != null &&
                    pageNumbersForDeleter.length > 0) ||
                (pageNumbersForReorder != null &&
                    pageNumbersForReorder.length > 0),
            'out of pagesRotationInfo, pageNumbersForDeleter, pageNumbersForReorder provide at least one non empty');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pagesRotationInfo': pagesRotationInfo?.map((e) => e.toJson()).toList(),
      'pageNumbersForDeleter': pageNumbersForDeleter,
      'pageNumbersForReorder': pageNumbersForReorder,
    };
  }
}

/// Parameters for the [pdfCompressor] method.
class PDFCompressorParams {
  /// Provide path of pdf file which should be compressed.
  final String pdfPath;

  /// Provide path of pdf file which should be compressed.
  final int imageQuality;

  /// Provide path of pdf file which should be compressed.
  final double imageScale;

  /// Provide true to unEmbed all fonts to decrease size further.
  final bool unEmbedFonts;

  /// Create parameters for the [pdfCompressor] method.
  const PDFCompressorParams(
      {required this.pdfPath,
      required this.imageQuality,
      required this.imageScale,
      this.unEmbedFonts = false});

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'imageQuality': imageQuality,
      'imageScale': imageScale,
      'unEmbedFonts': unEmbedFonts,
    };
  }
}

enum WatermarkLayer { underContent, overContent }

/// Parameters for the [pdfWatermark] method.
class PDFWatermarkParams {
  /// Provide path of pdf file which should be compressed.
  final String pdfPath;

  /// Provide watermark text.
  final String text;

  /// Provide watermark text font size.
  final double fontSize;

  /// Provide layer for watermark printing like over or under content.
  final WatermarkLayer watermarkLayer;

  /// Provide watermark text opacity.
  final double opacity;

  /// Provide watermark text rotation Angle.
  final double rotationAngle;

  /// Provide watermark text color.
  final Color watermarkColor;

  /// Create parameters for the [pdfWatermark] method.
  const PDFWatermarkParams({
    required this.pdfPath,
    required this.text,
    this.fontSize = 30,
    this.watermarkLayer = WatermarkLayer.overContent,
    this.opacity = 0.5,
    this.rotationAngle = 45,
    this.watermarkColor = Colors.black,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'text': text,
      'fontSize': fontSize,
      'watermarkLayer': watermarkLayer.toString(),
      'opacity': opacity,
      'rotationAngle': rotationAngle,
      'watermarkColor': '#${watermarkColor.value.toRadixString(16)}',
    };
  }
}
