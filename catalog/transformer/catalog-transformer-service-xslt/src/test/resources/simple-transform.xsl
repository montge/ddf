<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" encoding="UTF-8" indent="yes"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Metacard: <xsl:value-of select="$title"/></title>
      </head>
      <body>
        <h1>Metacard Information</h1>
        <div class="metadata">
          <p><strong>ID:</strong> <xsl:value-of select="$id"/></p>
          <p><strong>Title:</strong> <xsl:value-of select="$title"/></p>
          <p><strong>Source:</strong> <xsl:value-of select="$siteName"/></p>
          <p><strong>Type:</strong> <xsl:value-of select="$type"/></p>
        </div>
        <div class="content">
          <h2>Metadata Content</h2>
          <xsl:copy-of select="."/>
        </div>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
