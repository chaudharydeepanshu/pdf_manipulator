import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'pdf_manipulator_method_channel.dart';

abstract class PdfManipulatorPlatform extends PlatformInterface {
  /// Constructs a PdfManipulatorPlatform.
  PdfManipulatorPlatform() : super(token: _token);

  static final Object _token = Object();

  static PdfManipulatorPlatform _instance = MethodChannelPdfManipulator();

  /// The default instance of [PdfManipulatorPlatform] to use.
  ///
  /// Defaults to [MethodChannelPdfManipulator].
  static PdfManipulatorPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PdfManipulatorPlatform] when
  /// they register themselves.
  static set instance(PdfManipulatorPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> mergePDFs({PDFMergerParams? params}) {
    throw UnimplementedError('mergePDFs() has not been implemented.');
  }

  Future<List<String>?> splitPDF({PDFSplitterParams? params}) {
    throw UnimplementedError('splitPDF() has not been implemented.');
  }

  Future<String?> pdfPageDeleter({PDFPageDeleterParams? params}) {
    throw UnimplementedError('pdfPageDeleter() has not been implemented.');
  }

  Future<String?> pdfPageReorder({PDFPageReorderParams? params}) {
    throw UnimplementedError('pdfPageReorder() has not been implemented.');
  }

  Future<String?> pdfPageRotator({PDFPageRotatorParams? params}) {
    throw UnimplementedError('pdfPageRotator() has not been implemented.');
  }

  Future<String?> pdfPageRotatorDeleterReorder(
      {PDFPageRotatorDeleterReorderParams? params}) {
    throw UnimplementedError(
        'pdfPageRotatorDeleterReorder() has not been implemented.');
  }

  Future<String?> pdfCompressor({PDFCompressorParams? params}) {
    throw UnimplementedError('pdfCompressor() has not been implemented.');
  }

  Future<String?> cancelManipulations() {
    throw UnimplementedError('cancelManipulations() has not been implemented.');
  }
}
