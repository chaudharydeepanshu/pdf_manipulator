[![pub package](https://img.shields.io/pub/v/pdf_manipulator.svg)](https://pub.dev/packages/pdf_manipulator) [![wakatime](https://wakatime.com/badge/user/83f3b15d-49de-4c01-b8de-bbc132f11be1/project/db0907ad-0c7e-49cb-bbbb-a0fba05b6bc9.svg)](https://wakatime.com/badge/user/83f3b15d-49de-4c01-b8de-bbc132f11be1/project/db0907ad-0c7e-49cb-bbbb-a0fba05b6bc9)

## Package description

A flutter plugin for easy pdf manipulations.

Note 1: This package is using iText7 for pdf manipulations and it uses its AGPL Licence.

Note 2: This package currently supports only Android native URIs of files not absolute file paths and to get the Android native URIs of files you can use [pick_or_save](https://pub.dev/packages/pick_or_save) plugin.

## Features

- Merge pdfs using Android native URI.
- Split pdfs using Android native URI.

## Getting started

- In pubspec.yaml, add this dependency:

```yaml
pdf_manipulator: 
```

- Add this package to your project:

```dart
import 'package:pdf_manipulator/pdf_manipulator.dart';
```
