VectorGraphics2D 0.11 (2016-06-04)
==================================

- Allow headless build
- Added support for CMYK color space
- Added optional compression to PDFGraphics2D

VectorGraphics2D 0.10 (2016-03-06)
==================================

- Processing a stream of commands instead of directly mapping method calls
- Return valid graphics device configuration when calling
  ``getDeviceConfiguration()`` instead of ``null``
- Switch from Maven to Gradle for build management

VectorGraphics2D 0.9.3 (2015-11-21)
===================================

- Publish on MavenCentral

VectorGraphics2D 0.9.2 (2015-09-15)
===================================

- Fixed a bug that caused missing transformations in EPS files when calling
  ``EPSGraphics.transform(AffineTransform)``.
- Switched from Subversion to git as version control system

VectorGraphics2D 0.9.1 (2013-05-31)
===================================

- Added Maven repository location to POM
- Fixed issue 14 which caused invalid SVG clipping

VectorGraphics2D 0.9 (2013-05-12)
=================================

- Support for rendering paints (gradients, patterns, etc.)
- Improvements to build system
- Fixed transformation of clipping paths in SVG files
- Fixed build with Java 7

VectorGraphics2D 0.8 (2010-12-03)
=================================

- Added image support
- Support for transforms
- Various bug fixes

VectorGraphics2D 0.7 (2010-11-09)
=================================

- Clipping support
- Dashed strokes
- Added special handling for rounded rectangles

VectorGraphics2D 0.6 (2010-07-11)
=================================

First public release with very basic EPS, PDF, and SVG support
