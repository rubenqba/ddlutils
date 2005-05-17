package org.apache.ddlutils.io;

/*
 * Copyright 1999-2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An entity resolver that matches the specific database dtds to the one that comes
 * with commons-sql, and that can handle file url's.
 * 
 * @author <a href="mailto:tomdz@apache.org">Thomas Dudziak</a>
 */
public class LocalEntityResolver implements EntityResolver
{
    private static final String DTD_PREFIX = "http://db.apache.org/torque/dtd/database";

    /* (non-Javadoc)
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException
    {
        InputSource result = null;

        if (systemId.startsWith(DTD_PREFIX))
        {
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/database.dtd");

            if (input != null)
            {
                result = new InputSource(input);
            }
        }
        else if (systemId.startsWith("file:"))
        {
            try
            {
                URL url = new URL(systemId);

                if ("file".equals(url.getProtocol()))
                {
                    String path = systemId.substring("file:".length());

                    if (path.startsWith("//"))
                    {
                        path = path.substring(2);
                    }
                    result = new InputSource(new FileInputStream(path));
                }
                else
                {
                    result = new InputSource(url.openStream());
                }
            }
            catch (Exception ex)
            {
                throw new SAXException(ex);
            }
        }
        return result;
    }
}