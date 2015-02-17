<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
    <div data-role="content">
    <html>
      <body>
        <xsl:apply-templates/>
      </body>
    </html>
    </div>
  </xsl:template>
  <xsl:template match="menus">
    <ul data-role="listview">
      <!-- skip parent menu_item; jump to first child menu_item -->
      <xsl:apply-templates select="menu/menu_item/children/menu_item"/>
    </ul>
  </xsl:template>
  <xsl:template match="menu_item">
    <li>
      <!-- if this menu has a url, build a link -->
      <xsl:choose>
        <xsl:when test="url != ''">
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="url"/>
            </xsl:attribute>
            <!-- if this menu has a target, add it to the link -->
            <xsl:if test="target != ''">
              <xsl:attribute name="target">
                <xsl:value-of select="target"/>
              </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="name"/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="name"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="children"/>
    </li>
  </xsl:template>
  <xsl:template match="children">
    <!-- if children exist, then write nested ul/li tags -->
    <xsl:if test="count(child::node()) > 0">
      <xsl:text>
      </xsl:text>
      <ul data-role="listview">
        <xsl:apply-templates select="menu_item"/>
      </ul>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
