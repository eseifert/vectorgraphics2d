.. image:: https://eseifert.github.io/vectorgraphics2d/logo.png

.. image:: https://travis-ci.org/eseifert/vectorgraphics2d.svg?branch=master
    :target: https://travis-ci.org/eseifert/vectorgraphics2d

VectorGraphics2D
################

VectorGraphics2D provides implementations of Java's ``Graphics2D`` interface
and exports the graphics in various vector file formats.
Currently, there is support for the following vector file formats:

- Encapsulated PostScript® (EPS)
- Scalable Vector Graphics (SVG)
- Portable Document Format (PDF)

Additional formats can be easily added.


Features
========

- Support for EPS, PDF, and SVG formats
- Rendering of all geometric shapes provided by the ``java.awt.Graphics2D``
  interface
- Shapes and text can be made transparent (except in EPS)
- Arbitrary clipping paths can be defined
- Output of bitmap images
- Easily extensible
- Small footprint (JAR is below 100 kilobytes)

Currently, most operations are supported, i.e. VectorGraphics2D is able to
handle clipping gradients, or compression. Some features are still missing,
like text encodings, embedded fonts, or metadata support. Although its early
stage VectorGraphics2D is already used successfully in several projects to
export vector graphics.

Installation
============

Without build management system
-------------------------------

You can just add ``VectorGraphics2D-0.12.jar`` to the classpath of your project.

Using VectorGraphics2D with Maven
---------------------------------

.. code:: xml

	<dependency>
	    <groupId>de.erichseifert.vectorgraphics2d</groupId>
	    <artifactId>VectorGraphics2D</artifactId>
	    <version>0.12</version>
	</dependency>

Using VectorGraphics2D with Gradle
----------------------------------

.. code:: groovy

    dependencies {
        compile group: 'de.erichseifert.vectorgraphics2d', name: 'VectorGraphics2D', version: '0.12'
    }

Using VectorGraphics2D with sbt
-------------------------------

.. code:: scala

    libraryDependencies += "de.erichseifert.vectorgraphics2d" % "VectorGraphics2D" % "0.12"


Usage
=====

A ``VectorGraphics2D`` object can be used as a replacement for a ``Graphics2D``
object. All calls to the ``VectorGraphics2D`` instance will be recorded and can
later be retrieved as a ``CommandSequence``:

.. code:: java

    Graphics2D vg2d = new VectorGraphics2D();
    vg2d.draw(new Rectangle2D.Double(0.0, 0.0, 20.0, 20.0));
    CommandSequence commands = vg2d.getCommands();

This command sequence can then be exported to a EPS, PDF or SVG document using
a processor for the desired file type, i.e. ``EPSProcessor`` for EPS,
``PDFProcessor`` for PDF and ``SVGProcessor`` for SVG. Additionally, format
specific output options can be passed to the processor when it is created.
For example, a compression option can be passed for PDF:

.. code:: java

    PDFProcessor pdfProcessor = new PDFProcessor(true);
    Document doc = pdfProcessor.getDocument(commands, PageSize.A4);

Finally, the document can be written to a file:

.. code:: java

    doc.writeTo(new FileOutputStream("rect.pdf"));
