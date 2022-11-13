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
  Future<List<PageSizeInfo>?> pdfPagesSize({PDFPagesSizeParams? params}) async {
    final List? result = await methodChannel.invokeMethod<List?>(
        'pdfPagesSize', params?.toJson());
    result?.cast<List<double>>();
    if (result == null) {
      return null;
    } else {
      return List<PageSizeInfo>.generate(
          result.length,
          (int index) => PageSizeInfo(
                pageNumber: (result[index][0] as double).toInt(),
                widthOfPage: result[index][1] as double,
                heightOfPage: result[index][2] as double,
              ));
    }
  }

  @override
  Future<PdfValidityAndProtection?> pdfValidityAndProtection(
      {PDFValidityAndProtectionParams? params}) async {
    final List? result = await methodChannel.invokeMethod<List?>(
        'pdfValidityAndProtection', params?.toJson());
    result?.cast<List<bool?>>();
    if (result == null) {
      return null;
    } else {
      return PdfValidityAndProtection(
          isPDFValid: result[0],
          isOwnerPasswordProtected: result[1],
          isOpenPasswordProtected: result[2],
          isPrintingAllowed: result[3],
          isModifyContentsAllowed: result[4]);
    }
  }

  @override
  Future<String?> pdfDecryption({PDFDecryptionParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'pdfDecryption', params?.toJson());
    return path;
  }

  @override
  Future<String?> pdfEncryption({PDFEncryptionParams? params}) async {
    final String? path = await methodChannel.invokeMethod<String?>(
        'pdfEncryption', params?.toJson());
    return path;
  }

  @override
  Future<List<String>?> imagesToPdfs({ImagesToPDFsParams? params}) async {
    final List? paths = await methodChannel.invokeMethod<List?>(
        'imagesToPdfs', params?.toJson());
    return paths?.cast<String>();
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
            pageCount != null
                ? (byteSize == null &&
                    pageNumbers == null &&
                    pageRanges == null &&
                    pageRange == null)
                : byteSize != null
                    ? (pageCount == null &&
                        pageNumbers == null &&
                        pageRanges == null &&
                        pageRange == null)
                    : pageNumbers != null
                        ? (pageCount == null &&
                            byteSize == null &&
                            pageRanges == null &&
                            pageRange == null)
                        : pageRanges != null
                            ? (pageCount == null &&
                                byteSize == null &&
                                pageNumbers == null &&
                                pageRange == null)
                            : pageRange != null
                                ? (pageCount == null &&
                                    byteSize == null &&
                                    pageNumbers == null &&
                                    pageRanges == null)
                                : false,
            'Provide only anyone out of pageCount, byteSize, pageNumbers, pageRanges, pageRange');

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

  /// Provide pdf page images quality greater than 0 and less tan or equal to 100.
  final int imageQuality;

  /// Provide pdf page images scale greater than 0 and less tan or equal to 5.
  final double imageScale;

  /// Provide true to unEmbed all fonts to decrease size further.
  final bool unEmbedFonts;

  /// Create parameters for the [pdfCompressor] method.
  const PDFCompressorParams(
      {required this.pdfPath,
      required this.imageQuality,
      required this.imageScale,
      this.unEmbedFonts = false})
      : assert(imageScale > 0 || imageScale <= 5,
            'imageScale should be greater than 0 and less tan or equal to 5'),
        assert(imageQuality > 0 || imageQuality <= 100,
            'imageQuality should be greater than 0 and less tan or equal to 100');

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

enum PositionType {
  topLeft,
  topCenter,
  topRight,
  centerLeft,
  center,
  centerRight,
  bottomLeft,
  bottomCenter,
  bottomRight,
  custom
}

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

  /// Provide position of text.
  final PositionType positionType;

  /// Provide custom PositionType X coordinates list.
  final List<double>? customPositionXCoordinatesList;

  /// Provide custom PositionType Y coordinates list.
  final List<double>? customPositionYCoordinatesList;

  /// Create parameters for the [pdfWatermark] method.
  const PDFWatermarkParams({
    required this.pdfPath,
    required this.text,
    this.fontSize = 30,
    this.watermarkLayer = WatermarkLayer.overContent,
    this.opacity = 0.5,
    this.rotationAngle = 45,
    this.watermarkColor = Colors.black,
    this.positionType = PositionType.center,
    this.customPositionXCoordinatesList,
    this.customPositionYCoordinatesList,
  }) : assert(
            positionType == PositionType.custom
                ? (customPositionXCoordinatesList != null &&
                        customPositionXCoordinatesList.length != 0) &&
                    (customPositionYCoordinatesList != null &&
                        customPositionYCoordinatesList.length != 0)
                : true,
            'if positionType == PositionType.custom then customPositionXCoordinatesList and customPositionYCoordinatesList can\'t be null or empty');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'text': text,
      'fontSize': fontSize,
      'watermarkLayer': watermarkLayer.toString(),
      'opacity': opacity,
      'rotationAngle': rotationAngle,
      'watermarkColor': '#${watermarkColor.value.toRadixString(16)}',
      'positionType': positionType.toString(),
    };
  }
}

class PageSizeInfo {
  /// Pdf page number.
  final int pageNumber;

  /// Pdf page width.
  final double widthOfPage;

  /// Pdf page height.
  final double heightOfPage;

  PageSizeInfo({
    required this.pageNumber,
    required this.widthOfPage,
    required this.heightOfPage,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pageNumber': pageNumber,
      'widthOfPage': widthOfPage,
      'heightOfPage': heightOfPage,
    };
  }

  // Implement toString to make it easier to see information
  // when using the print statement.
  @override
  String toString() {
    return 'PageSizeInfo{pageNumber: $pageNumber, widthOfPage: $widthOfPage, heightOfPage: $heightOfPage}';
  }
}

/// Parameters for the [pdfPagesSize] method.
class PDFPagesSizeParams {
  /// Provide path of pdf file which you want pages size info.
  final String pdfPath;

  /// Create parameters for the [pdfPagesSize] method.
  const PDFPagesSizeParams({
    required this.pdfPath,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
    };
  }
}

class PdfValidityAndProtection {
  /// Is true if pdf is valid.
  final bool? isPDFValid;

  /// Is true if pdf is owner/permission password protected.
  final bool? isOwnerPasswordProtected;

  /// Is true if pdf is user/open password protected.
  final bool? isOpenPasswordProtected;

  /// Is true if pdf printing is allowed.
  final bool? isPrintingAllowed;

  /// Is true if pdf changes are allowed.
  final bool? isModifyContentsAllowed;

  PdfValidityAndProtection({
    required this.isPDFValid,
    required this.isOwnerPasswordProtected,
    required this.isOpenPasswordProtected,
    required this.isPrintingAllowed,
    required this.isModifyContentsAllowed,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'isPDFValid': isPDFValid,
      'isOwnerPasswordProtected': isOwnerPasswordProtected,
      'isOpenPasswordProtected': isOpenPasswordProtected,
      'isPrintingAllowed': isPrintingAllowed,
      'isModifyContentsAllowed': isModifyContentsAllowed,
    };
  }

  // Implement toString to make it easier to see information
  // when using the print statement.
  @override
  String toString() {
    return 'PdfValidityAndProtection{isPDFValid: $isPDFValid, isOwnerPasswordProtected: $isOwnerPasswordProtected, isOpenPasswordProtected: $isOpenPasswordProtected, isPrintingAllowed: $isPrintingAllowed, isModifyContentsAllowed: $isModifyContentsAllowed}';
  }
}

/// Parameters for the [pdfValidityAndProtection] method.
class PDFValidityAndProtectionParams {
  /// Provide path of pdf file which you want validity and protection info.
  final String pdfPath;

  /// Provide owner or user password.
  final String? password;

  /// Create parameters for the [pdfValidityAndProtection] method.
  const PDFValidityAndProtectionParams({
    required this.pdfPath,
    this.password = "",
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'password': password,
    };
  }
}

/// Parameters for the [pdfDecryption] method.
class PDFDecryptionParams {
  /// Provide path of pdf file which you want decrypted.
  final String pdfPath;

  /// Provide owner or user password.
  final String? password;

  /// Create parameters for the [pdfDecryption] method.
  const PDFDecryptionParams({
    required this.pdfPath,
    this.password = "",
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'password': password,
    };
  }
}

/// Parameters for the [pdfEncryption] method.
class PDFEncryptionParams {
  /// Provide path of pdf file which you want encrypted.
  final String pdfPath;

  /// Provide owner password.
  final String ownerPassword;

  /// Provide user password.
  final String userPassword;

  /// Set true to allow printing permission.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool allowPrinting;

  /// Set true to allow modify permission.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool allowModifyContents;

  /// Set true to allow copy permission.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool allowCopy;

  /// Set true to allow modifying annotations permission.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool allowModifyAnnotations;

  /// Set true to allow fill in permission.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool allowFillIn;

  /// Set true to allow screen readers permission.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool allowScreenReaders;

  /// Set true to allow assembly permission.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool allowAssembly;

  /// Set true to allow degraded printing permission.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool allowDegradedPrinting;

  /// Set true to enable StandardEncryptionAES40 encryption. standardEncryptionAES40 implicitly sets doNotEncryptMetadata and encryptEmbeddedFilesOnly as false.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool standardEncryptionAES40;

  /// Set true to enable StandardEncryptionAES128 encryption. standardEncryptionAES128 implicitly sets EncryptionConstants.EMBEDDED_FILES_ONLY as false.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool standardEncryptionAES128;

  /// Set true to enable encryptionAES128 encryption.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool encryptionAES128;

  /// Set true to enable encryptionAES256 encryption.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool encryptionAES256;

  /// Set true to encrypt embedded files only.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool encryptEmbeddedFilesOnly;

  /// Set true to not encrypt metadata.
  ///
  /// Please be aware that the passed encryption types may override permissions.
  final bool doNotEncryptMetadata;

  /// Create parameters for the [pdfEncryption] method.
  const PDFEncryptionParams({
    required this.pdfPath,
    this.ownerPassword = "",
    this.userPassword = "",
    this.allowPrinting = false,
    this.allowModifyContents = false,
    this.allowCopy = false,
    this.allowModifyAnnotations = false,
    this.allowFillIn = false,
    this.allowScreenReaders = false,
    this.allowAssembly = false,
    this.allowDegradedPrinting = false,
    this.standardEncryptionAES40 = false,
    this.standardEncryptionAES128 = false,
    this.encryptionAES128 = false,
    this.encryptionAES256 = false,
    this.encryptEmbeddedFilesOnly = false,
    this.doNotEncryptMetadata = false,
  }) : assert(
            standardEncryptionAES40 == true
                ? (standardEncryptionAES128 == false &&
                    encryptionAES128 == false &&
                    encryptionAES256 == false)
                : standardEncryptionAES128 == true
                    ? (standardEncryptionAES40 == false &&
                        encryptionAES128 == false &&
                        encryptionAES256 == false)
                    : encryptionAES128 == true
                        ? (standardEncryptionAES40 == false &&
                            standardEncryptionAES128 == false &&
                            encryptionAES256 == false)
                        : encryptionAES256 == true
                            ? (standardEncryptionAES40 == false &&
                                standardEncryptionAES128 == false &&
                                encryptionAES128 == false)
                            : false,
            'Set only anyone encryption out of standardEncryptionAES40, standardEncryptionAES128, encryptionAES128, encryptionAES256 true');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'ownerPassword': ownerPassword,
      'userPassword': userPassword,
      'allowPrinting': allowPrinting,
      'allowModifyContents': allowModifyContents,
      'allowCopy': allowCopy,
      'allowModifyAnnotations': allowModifyAnnotations,
      'allowFillIn': allowFillIn,
      'allowScreenReaders': allowScreenReaders,
      'allowAssembly': allowAssembly,
      'allowDegradedPrinting': allowDegradedPrinting,
      'standardEncryptionAES40': standardEncryptionAES40,
      'standardEncryptionAES128': standardEncryptionAES128,
      'encryptionAES128': encryptionAES128,
      'encryptEmbeddedFilesOnly': encryptEmbeddedFilesOnly,
      'doNotEncryptMetadata': doNotEncryptMetadata,
    };
  }
}

/// Parameters for the [imagesToPdfs] method.
class ImagesToPDFsParams {
  /// Provide paths of images to convert to pdfs.
  final List<String> imagesPaths;

  /// Set createSinglePdf = true to pull all images in single pdf.
  final bool createSinglePdf;

  /// Create parameters for the [imagesToPdfs] method.
  const ImagesToPDFsParams(
      {required this.imagesPaths, this.createSinglePdf = false})
      : assert(imagesPaths.length > 0, 'provide path for at least 1 image');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'imagesPaths': imagesPaths,
      'createSinglePdf': createSinglePdf,
    };
  }
}
