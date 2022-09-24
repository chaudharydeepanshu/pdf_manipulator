import 'package:flutter/foundation.dart';
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
  Future<String?> cancelManipulations() async {
    final String? result =
        await methodChannel.invokeMethod<String?>('cancelManipulations');
    return result;
  }
}

/// Parameters for the [mergePDFs] method.
class PDFMergerParams {
  /// Provide uris of pdf files to merge.
  final List<String> pdfsUris;

  /// Create parameters for the [mergePDFs] method.
  const PDFMergerParams({required this.pdfsUris})
      : assert(pdfsUris.length > 1, 'provide uris for at least 2 pdfs');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfsUris': pdfsUris,
    };
  }
}

/// Parameters for the [splitPDF] method.
///
/// pageCount parameter with value 1 is used if no other parameter is provided except pdfUri.
class PDFSplitterParams {
  /// Provide uris of pdf file to split.
  final String pdfUri;

  /// Provide the splitting pageCount.
  final int? pageCount;

  /// Provide the splitting byte size.
  ///
  /// It will give some pdf bigger than the byte size if the some individual pages in pdf are bigger than the byte size.
  final int? byteSize;

  /// Create parameters for the [splitPDF] method.
  const PDFSplitterParams({required this.pdfUri, this.pageCount, this.byteSize})
      : assert(pageCount == null || byteSize == null,
            'provide only any one out of pageCount or byteSize');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfUri': pdfUri,
      'pageCount': pageCount,
      'byteSize': byteSize,
    };
  }
}
