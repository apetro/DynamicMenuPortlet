An XML based menu publishing tool is included for convenience. The tool
can be using the 'publishXml' ant target. The XML document to be
published must be specified.

ex:
ant publishXml -DmenuXmlFile=/my/uportal-dev/portlets/DynamicMenuPortlet/doc
/menu.xml

There is a sample menu.xml file included in the docs directory.

The XML is validated agaist the menu.xsd schema in the source directory
to ensure its structure is correct.



java edu.wisc.my.portlets.dmp.tools.XmlMenuPublisherRunner -f c:\temp\menu.xml
