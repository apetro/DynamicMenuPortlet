/*******************************************************************************
* Copyright 2004, The Board of Regents of the University of Wisconsin System.
* All rights reserved.
*
* A non-exclusive worldwide royalty-free license is granted for this Software.
* Permission to use, copy, modify, and distribute this Software and its
* documentation, with or without modification, for any purpose is granted
* provided that such redistribution and use in source and binary forms, with or
* without modification meets the following conditions:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* 3. Redistributions of any form whatsoever must retain the following
* acknowledgement:
*
* "This product includes software developed by The Board of Regents of
* the University of Wisconsin System."
*
*THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
*WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
*BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
*PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
*THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
*INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
*PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
*LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
*OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
*ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/
package edu.wisc.my.portlets.dmp.tools;


/**
 * Generates a string representation of the current path
 * location in the XML document. 
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision: 1.1 $
 */
public class XmlLocationStack {
    private final StringBuffer location = new StringBuffer();
    
    /**
     * Starts an element, appending it to the path string.
     * 
     * @param name The name of the element to append.
     * @return The location after the element has been appended.
     */
    public synchronized String startElement(String name) {
        this.location.append(this.getPathElement(name));
        return this.toString();
    }
    
    /**
     * Ends an element, removing it from the end of the path string. The
     * specified name must match the element at the end of the path or an
     * IllegalStateException will be thrown.
     * 
     * @param name The name of the element to remove from the path.
     * @return The location before the element removal.
     */
    public synchronized String endElement(String name) {
        final String currentPath = this.location.toString();
        final String pathElement = this.getPathElement(name).toString();
        
        final int locationBaseLength = this.location.length() - pathElement.length();
        if (locationBaseLength < 0)
            throw new IllegalStateException("Cannot remove '" + name + "' from '" + this.location + "'");
        
        final String currLocElement = this.location.substring(locationBaseLength);
        
        if (!pathElement.equals(currLocElement))
            throw new IllegalStateException("Cannot remove '" + name + "' from '" + this.location + "'");
        
        this.location.delete(locationBaseLength, this.location.length());
        
        return currentPath;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public synchronized String toString() {
        return this.location.toString();
    }
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return obj instanceof XmlLocationStack && this.toString().equals(obj.toString());
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.location.hashCode();
    }
    
    private StringBuffer getPathElement(String name) {
        return new StringBuffer("/").append(name);
    }
}
