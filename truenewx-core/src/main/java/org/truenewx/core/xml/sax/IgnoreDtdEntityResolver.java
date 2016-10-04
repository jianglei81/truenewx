package org.truenewx.core.xml.sax;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.truenewx.core.Strings;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 忽略DTD内容的EntityResolver
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class IgnoreDtdEntityResolver implements EntityResolver {

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId)
                    throws SAXException, IOException {
        return new InputSource(new ByteArrayInputStream(Strings.EMPTY.getBytes()));
    }

}
