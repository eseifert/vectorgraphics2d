VectorGraphics2D
================

VectorGraphics2D provides implementations of Java's ``Graphics2D`` interface and
exports the graphics in various vector file formats.
Currently, there's support for the following vector file formats:

- Encapsulated PostScript® (EPS)
- Scalable Vector Graphics (SVG)
- Portable Document Format (PDF)

Additional formats can be easily added.


Features
--------

- Support for EPS, PDF, and SVG formats
- Rendering of all geometric shapes provided by the java.awt.Graphics2D interface
- Shapes and text can be made transparent (except in EPS)
- Arbitrary clipping paths can be defined
- Output of bitmap images
- Easily extensible
- Small footprint (JAR is below 100 kilobytes)

Currently, most operations are supported, i.e. VectorGraphics2D is able to handle clipping gradients, or compression.
Some features are still missing, like text encodings, embedded fonts, or metadata support.
Although its early stage VectorGraphics2D is already used successfully in several projects to export vector graphics.


Include in your Maven project
-----------------------------

.. code:: xml

	<dependency>
	    <groupId>de.erichseifert.vectorgraphics2d</groupId>
	    <artifactId>VectorGraphics2D</artifactId>
	    <version>0.9.3</version>
	</dependency>


Include in your sbt project
---------------------------

.. code:: scala

    libraryDependencies += "de.erichseifert.vectorgraphics2d" % "VectorGraphics2D" % "0.9.3"
