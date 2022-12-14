## 0.5.9

* Fixes error "Build file '\pdf_manipulator\android\build.gradle' should not contain a package statement".

## 0.5.8

* Updated documentation regarding PlatformException in Release build: AbstractITextEvent is only for internal usage [#2](https://github.com/chaudharydeepanshu/pdf_manipulator/issues/2).

## 0.5.7

* Attempts to fix "x + width must be <= bitmap.width()" exception on some devices in some case of PDF compressing.

## 0.5.6

* Updated dependency.

## 0.5.5

* Updated documentation.
* Improved example app.

## 0.5.2

* Fixes crash on `pdfCompressor` with `unEmbedFonts` set true for some pdfs.

## 0.5.1

* Now `pdfDecryption` finishes with error on BadPasswordException and PdfException.

## 0.4.8

* Fixed `imagesToPdfs` method not working for absolute file paths of files with names containing `:`.

## 0.4.7

* Fixed `imagesToPdfs` method not working for android uris and absolute file paths.
* Fixed `imagesToPdfs` adding unnecessary padding to pdfs when `createSinglePdf` is set to false or default.
* Updated example and example dependency.

## 0.4.5

* Added `imagesToPdfs` method for converting images to single pdf or multiple pdfs.

## 0.4.4

* Added `pdfEncryption` method for pdf encryption.
* Added `pdfDecryption` method for pdf decryption.
* Added `pdfValidityAndProtection` method for getting pdf validity and protection info. Also, you can use [pdf_bitmaps](https://pub.dev/packages/pdf_bitmaps) for getting pdf validity and protection info quickly.

## 0.4.3

* Fixed `pdfPagesSize` method OOM issue also removed redundant properties. Also, you can use [pdf_bitmaps](https://pub.dev/packages/pdf_bitmaps) for getting size info specific page of pdf quickly.

## 0.4.2

* Added `positionType` in `pdfWatermark` method which provides various predefined and allows providing custom watermark positions.`
* Added `pdfPagesSize` method for getting size info of pages of pdf.
* Added `customPositionXCoordinatesList`, `customPositionYCoordinatesList` in `pdfWatermark` method for more fine controlling of watermark position.

## 0.4.1

* Added `pdfWatermark` method for watermarking pdf.

## 0.3.2

* Added `unEmbedFonts` for `pdfCompressor`.

## 0.3.1

* Added `pdfCompressor` method for compressing pdf.
 
## 0.2.2

* Fixes OOM error in some cases.

## 0.2.1

* BREAKING: `pdfUri` is replaced with `pdfPath` as now `pdfPath` is capable of taking care both URI path and absolute file path.
* BREAKING: `pdfsUris` is replaced with `pdfsPaths` as now `pdfsPaths` is capable of taking care both URI paths and absolute file paths.
* Fixed issue of not being able to use absolute file paths.

## 0.1.1

* Support for rotating, deleting and reordering pdf pages.
* Fixes OOfM error in some cases.
* Fixes few errors in splitting by byte size.

## 0.0.2

* Readme Typo fix.

## 0.0.1

* Initial release.
