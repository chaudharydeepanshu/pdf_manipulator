import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:pdf_manipulator/pdf_manipulator.dart';
import 'package:pick_or_save/pick_or_save.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _mergePdfsPlugin = PdfManipulator();
  final _pickOrSavePlugin = PickOrSave();

  String? _mergedPDFPath;
  List<String>? _splitPDFPaths;
  String? _resultPDFPath;

  bool _isBusy = false;
  final bool _localOnly = false;
  final bool _copyFileToCacheDir = true;
  List<String>? _pickedFilesPaths;
  List<String>? _savedFilePath;

  @override
  void initState() {
    super.initState();
  }

  Future<void> _filePicker(FilePickerParams params) async {
    List<String>? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _pickOrSavePlugin.filePicker(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _pickedFilesPaths = result;
      _isBusy = false;
    });
  }

  Future<void> _fileSaver(FileSaverParams params) async {
    List<String>? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _pickOrSavePlugin.fileSaver(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _savedFilePath = result ?? _savedFilePath;
      _isBusy = false;
    });
  }

  Future<void> _mergePDFs(PDFMergerParams params) async {
    String? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _mergePdfsPlugin.mergePDFs(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _mergedPDFPath = result;
      _isBusy = false;
    });
  }

  Future<void> _splitPDF(PDFSplitterParams params) async {
    List<String>? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _mergePdfsPlugin.splitPDF(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _splitPDFPaths = result;

      _isBusy = false;
    });
  }

  Future<void> _pdfPageDeleter(PDFPageDeleterParams params) async {
    String? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _mergePdfsPlugin.pdfPageDeleter(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _resultPDFPath = result;
      _isBusy = false;
    });
  }

  Future<void> _pdfPageReorder(PDFPageReorderParams params) async {
    String? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _mergePdfsPlugin.pdfPageReorder(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _resultPDFPath = result;
      _isBusy = false;
    });
  }

  Future<void> _pdfPageRotator(PDFPageRotatorParams params) async {
    String? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _mergePdfsPlugin.pdfPageRotator(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _resultPDFPath = result;
      _isBusy = false;
    });
  }

  Future<void> _pdfPageRotatorDeleterReorder(
      PDFPageRotatorDeleterReorderParams params) async {
    String? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result =
          await _mergePdfsPlugin.pdfPageRotatorDeleterReorder(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _resultPDFPath = result;
      _isBusy = false;
    });
  }

  Future<void> _pdfCompressor(PDFCompressorParams params) async {
    String? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _mergePdfsPlugin.pdfCompressor(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _resultPDFPath = result;
      _isBusy = false;
    });
  }

  Future<void> _cancelTask() async {
    String? result;
    try {
      setState(() {
        _isBusy = false;
      });
      result = await _mergePdfsPlugin.cancelManipulations();
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: ListView(
            children: [
              const Text("Merging PDF"),
              Card(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Column(
                      children: [
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FilePickerParams(
                                      localOnly: _localOnly,
                                      copyFileToCacheDir: _copyFileToCacheDir,
                                      filePickingType: FilePickingType.multiple,
                                      mimeTypeFilter: ["application/pdf"],
                                    );
                                    await _filePicker(params);
                                  },
                            child: const Text("Pick multiple file")),
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = PDFMergerParams(
                                      pdfsPaths: _pickedFilesPaths!,
                                    );
                                    await _mergePDFs(params);
                                  },
                            child: const Text("Merge picked pdfs")),
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FileSaverParams(
                                      localOnly: _localOnly,
                                      sourceFilesPaths: [_mergedPDFPath!],
                                      filesNames: ["merged pdf.pdf"],
                                    );
                                    await _fileSaver(params);
                                  },
                            child: const Text("Save merged pdf")),
                        OutlinedButton(
                            onPressed: () async {
                              await _cancelTask();
                            },
                            child: const Text("Cancel manipulation task")),
                      ],
                    ),
                  ],
                ),
              ),
              const Text("Split PDF"),
              Card(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Column(
                      children: [
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FilePickerParams(
                                      localOnly: _localOnly,
                                      copyFileToCacheDir: _copyFileToCacheDir,
                                      filePickingType: FilePickingType.single,
                                      mimeTypeFilter: ["application/pdf"],
                                    );
                                    await _filePicker(params);
                                  },
                            child: const Text("Pick single file")),
                        Row(
                          children: [
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFSplitterParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          pageCount: 1,
                                        );
                                        await _splitPDF(params);
                                      },
                                child: const Text("split(Page count)")),
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFSplitterParams(
                                            pdfPath: _pickedFilesPaths![0],
                                            byteSize: 12000000
                                            // BigInt.from(1000).pow(3).toInt()
                                            // BigInt.from(1000).pow(12).toInt(),
                                            );
                                        await _splitPDF(params);
                                      },
                                child: const Text("split(byte size)")),
                          ],
                        ),
                        Row(
                          children: [
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFSplitterParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          pageNumbers: [2, 10],
                                        );
                                        await _splitPDF(params);
                                      },
                                child: const Text("split(page numbers)")),
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFSplitterParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          pageRanges: ["2", "10-15"],
                                        );
                                        await _splitPDF(params);
                                      },
                                child:
                                    const Text("split(extract page ranges)")),
                          ],
                        ),
                        Row(
                          children: [
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFSplitterParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          pageRange: "2, 10-11, 3",
                                        );
                                        await _splitPDF(params);
                                      },
                                child: const Text("split(extract page range)")),
                          ],
                        ),
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FileSaverParams(
                                      localOnly: _localOnly,
                                      sourceFilesPaths: _splitPDFPaths,
                                    );
                                    await _fileSaver(params);
                                  },
                            child: const Text("Save split pdfs")),
                        OutlinedButton(
                            onPressed: () async {
                              await _cancelTask();
                            },
                            child: const Text("Cancel manipulation task")),
                      ],
                    ),
                  ],
                ),
              ),
              const Text("Delete PDF pages"),
              Card(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Column(
                      children: [
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FilePickerParams(
                                      localOnly: _localOnly,
                                      copyFileToCacheDir: _copyFileToCacheDir,
                                      filePickingType: FilePickingType.single,
                                      mimeTypeFilter: ["application/pdf"],
                                    );
                                    await _filePicker(params);
                                  },
                            child: const Text("Pick single file")),
                        Row(
                          children: [
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFPageDeleterParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          pageNumbers: [1, 2, 3],
                                        );
                                        await _pdfPageDeleter(params);
                                      },
                                child: const Text("delete pages")),
                          ],
                        ),
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FileSaverParams(
                                      localOnly: _localOnly,
                                      sourceFilesPaths: [_resultPDFPath!],
                                    );
                                    await _fileSaver(params);
                                  },
                            child: const Text("Save new pdf")),
                        OutlinedButton(
                            onPressed: () async {
                              await _cancelTask();
                            },
                            child: const Text("Cancel manipulation task")),
                      ],
                    ),
                  ],
                ),
              ),
              const Text("Reorder PDF pages"),
              Card(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Column(
                      children: [
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FilePickerParams(
                                      localOnly: _localOnly,
                                      copyFileToCacheDir: _copyFileToCacheDir,
                                      filePickingType: FilePickingType.single,
                                      mimeTypeFilter: ["application/pdf"],
                                    );
                                    await _filePicker(params);
                                  },
                            child: const Text("Pick single file")),
                        Row(
                          children: [
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFPageReorderParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          pageNumbers: [4, 1],
                                        );
                                        await _pdfPageReorder(params);
                                      },
                                child: const Text("reorder pages")),
                          ],
                        ),
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FileSaverParams(
                                      localOnly: _localOnly,
                                      sourceFilesPaths: [_resultPDFPath!],
                                    );
                                    await _fileSaver(params);
                                  },
                            child: const Text("Save new pdf")),
                        OutlinedButton(
                            onPressed: () async {
                              await _cancelTask();
                            },
                            child: const Text("Cancel manipulation task")),
                      ],
                    ),
                  ],
                ),
              ),
              const Text("Rotate PDF pages"),
              Card(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Column(
                      children: [
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FilePickerParams(
                                      localOnly: _localOnly,
                                      copyFileToCacheDir: _copyFileToCacheDir,
                                      filePickingType: FilePickingType.single,
                                      mimeTypeFilter: ["application/pdf"],
                                    );
                                    await _filePicker(params);
                                  },
                            child: const Text("Pick single file")),
                        Row(
                          children: [
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFPageRotatorParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          pagesRotationInfo: [
                                            PageRotationInfo(
                                                pageNumber: 1,
                                                rotationAngle: 180)
                                          ],
                                        );
                                        await _pdfPageRotator(params);
                                      },
                                child: const Text("rotate pages")),
                          ],
                        ),
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FileSaverParams(
                                      localOnly: _localOnly,
                                      sourceFilesPaths: [_resultPDFPath!],
                                    );
                                    await _fileSaver(params);
                                  },
                            child: const Text("Save new pdf")),
                        OutlinedButton(
                            onPressed: () async {
                              await _cancelTask();
                            },
                            child: const Text("Cancel manipulation task")),
                      ],
                    ),
                  ],
                ),
              ),
              const Text("Rotate, Delete, Reorder PDF pages"),
              Card(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Column(
                      children: [
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FilePickerParams(
                                      localOnly: _localOnly,
                                      copyFileToCacheDir: _copyFileToCacheDir,
                                      filePickingType: FilePickingType.single,
                                      mimeTypeFilter: ["application/pdf"],
                                    );
                                    await _filePicker(params);
                                  },
                            child: const Text("Pick single file")),
                        Row(
                          children: [
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params =
                                            PDFPageRotatorDeleterReorderParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          pagesRotationInfo: [
                                            PageRotationInfo(
                                                pageNumber: 1,
                                                rotationAngle: 180)
                                          ],
                                          pageNumbersForReorder: [
                                            4,
                                            3,
                                            2,
                                            1,
                                          ],
                                          pageNumbersForDeleter: [3, 2],
                                        );
                                        await _pdfPageRotatorDeleterReorder(
                                            params);
                                      },
                                child: const Text(
                                    "Rotate, Delete, Reorder pages")),
                          ],
                        ),
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FileSaverParams(
                                      localOnly: _localOnly,
                                      sourceFilesPaths: [_resultPDFPath!],
                                    );
                                    await _fileSaver(params);
                                  },
                            child: const Text("Save new pdf")),
                        OutlinedButton(
                            onPressed: () async {
                              await _cancelTask();
                            },
                            child: const Text("Cancel manipulation task")),
                      ],
                    ),
                  ],
                ),
              ),
              const Text("PDF Compressor"),
              Card(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Column(
                      children: [
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FilePickerParams(
                                      localOnly: _localOnly,
                                      copyFileToCacheDir: _copyFileToCacheDir,
                                      filePickingType: FilePickingType.single,
                                      mimeTypeFilter: ["application/pdf"],
                                    );
                                    await _filePicker(params);
                                  },
                            child: const Text("Pick single file")),
                        Row(
                          children: [
                            OutlinedButton(
                                onPressed: _isBusy
                                    ? null
                                    : () async {
                                        final params = PDFCompressorParams(
                                          pdfPath: _pickedFilesPaths![0],
                                          imageQuality: 100,
                                          imageScale: 1,
                                        );
                                        await _pdfCompressor(params);
                                      },
                                child: const Text("Compress")),
                          ],
                        ),
                        OutlinedButton(
                            onPressed: _isBusy
                                ? null
                                : () async {
                                    final params = FileSaverParams(
                                      localOnly: _localOnly,
                                      sourceFilesPaths: [_resultPDFPath!],
                                    );
                                    await _fileSaver(params);
                                  },
                            child: const Text("Save new pdf")),
                        OutlinedButton(
                            onPressed: () async {
                              await _cancelTask();
                            },
                            child: const Text("Cancel manipulation task")),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
