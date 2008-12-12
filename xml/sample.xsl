<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml"/>

<xsl:template match="email">
<![CDATA[
    <html>
        <meta>
            <title>Congratulations!</title>
        </meta>
        <body bgcolor="#ffffff" text="#777777" link="#b8080c" vlink="#b8080c" alink="#ef8080" topmargin="0" bottommargin="0" leftmargin="0" marginheight="0" marginwidth="0">

            <img src="http://192.168.0.109:7001/carco/img/copyright.gif"/>
            <table cellpadding="20"><tr><td>
            Dear <xsl:value-of select="customer/userName"/>,
            <p>
            We are pleased to inform you that your brand new <b><xsl:value-of select="customer/modelName"/></b> has been delivered to your closest dealer <b><xsl:value-of select="customer/compoundName"/></b>.
            </p>
            <p>
            We are looking forward to seeing you soon at this place and we wish you many miles of pleasure with your new car.
            </p>

            <p>
            Best regards,
            </p>
            CarCo.
            </td></tr></table>
        </body>
    </html>
]]>
</xsl:template>

<xsl:template match="customer">
<html>
<body>

<xsl:copy-of select="."/>
</body>
</html>
</xsl:template>


</xsl:stylesheet>

