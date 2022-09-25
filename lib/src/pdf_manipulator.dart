import 'package:pdf_manipulator/src/pdf_manipulator_method_channel.dart';
import 'pdf_manipulator_platform_interface.dart';

class PdfManipulator {
  /// Merges provided pdf files.
  ///
  /// Returns the path or uri of the resultant merged file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> mergePDFs({PDFMergerParams? params}) {
    return PdfManipulatorPlatform.instance.mergePDFs(params: params);
  }

  /// Split provided pdf file.
  ///
  /// Returns the paths or uris of the resultant split file or null if operation was cancelled.
  /// Throws exception on error.
  Future<List<String>?> splitPDF({PDFSplitterParams? params}) {
    return PdfManipulatorPlatform.instance.splitPDF(params: params);
  }

  /// Deletes pages from provided pdf file.
  ///
  /// Returns the path or uri of the resultant merged file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfPageDeleter({PDFPageDeleterParams? params}) {
    return PdfManipulatorPlatform.instance.pdfPageDeleter(params: params);
  }

  /// Cancels running manipulations.
  ///
  /// Returns the cancelling message.
  Future<String?> cancelManipulations() {
    return PdfManipulatorPlatform.instance.cancelManipulations();
  }
}
