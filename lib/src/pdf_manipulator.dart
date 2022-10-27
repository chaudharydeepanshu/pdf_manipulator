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
  /// Returns the path or uri of the resultant file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfPageDeleter({PDFPageDeleterParams? params}) {
    return PdfManipulatorPlatform.instance.pdfPageDeleter(params: params);
  }

  /// Reorders pages of provided pdf file.
  ///
  /// Returns the path or uri of the resultant file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfPageReorder({PDFPageReorderParams? params}) {
    return PdfManipulatorPlatform.instance.pdfPageReorder(params: params);
  }

  /// Rotate pages of provided pdf file.
  ///
  /// Returns the path or uri of the resultant file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfPageRotator({PDFPageRotatorParams? params}) {
    return PdfManipulatorPlatform.instance.pdfPageRotator(params: params);
  }

  /// Rotate, delete, reorder pages of provided pdf file.
  ///
  /// Returns the path or uri of the resultant file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfPageRotatorDeleterReorder(
      {PDFPageRotatorDeleterReorderParams? params}) {
    return PdfManipulatorPlatform.instance
        .pdfPageRotatorDeleterReorder(params: params);
  }

  /// Compresses provided pdf file.
  ///
  /// Returns the path or uri of the resultant file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfCompressor({PDFCompressorParams? params}) {
    return PdfManipulatorPlatform.instance.pdfCompressor(params: params);
  }

  /// Watermarks provided pdf file.
  ///
  /// Returns the path or uri of the resultant file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfWatermark({PDFWatermarkParams? params}) {
    return PdfManipulatorPlatform.instance.pdfWatermark(params: params);
  }

  /// Provides pdf file pages size info.
  ///
  /// Returns List<PageSizeInfo> for pages size info or null if operation was cancelled.
  /// Throws exception on error.
  Future<List<PageSizeInfo>?> pdfPagesSize({PDFPagesSizeParams? params}) {
    return PdfManipulatorPlatform.instance.pdfPagesSize(params: params);
  }

  /// Provides pdf file validity and protection info.
  ///
  /// Returns PdfValidityAndProtection for pdf file or null if operation was cancelled.
  /// Throws exception on error.
  Future<PdfValidityAndProtection?> pdfValidityAndProtection(
      {PDFValidityAndProtectionParams? params}) {
    return PdfManipulatorPlatform.instance
        .pdfValidityAndProtection(params: params);
  }

  /// Provides pdf file for decryption.
  ///
  /// Returns the path or uri of the resultant file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfDecryption({PDFDecryptionParams? params}) {
    return PdfManipulatorPlatform.instance.pdfDecryption(params: params);
  }

  /// Provides pdf file for encryption.
  ///
  /// Returns the path or uri of the resultant file or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfEncryption({PDFEncryptionParams? params}) {
    return PdfManipulatorPlatform.instance.pdfEncryption(params: params);
  }

  /// Provide images to convert to pdfs.
  ///
  /// Returns the paths or uris of pdf files or null if operation was cancelled.
  /// Throws exception on error.
  Future<List<String>?> imagesToPdfs({ImagesToPDFsParams? params}) {
    return PdfManipulatorPlatform.instance.imagesToPdfs(params: params);
  }

  /// Cancels running manipulations.
  ///
  /// Returns the cancelling message.
  Future<String?> cancelManipulations() {
    return PdfManipulatorPlatform.instance.cancelManipulations();
  }
}
