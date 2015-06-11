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


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Parses command line arguments and calls the XmlMenuPublisher
 * class to publish menus to the database.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @since 1.0
 */
public class XmlMenuPublisherRunner {
    private static final String XML_FILE_OPT = "f";

    public static void main(String[] args) throws MalformedURLException {
        final CommandLine line = parseCommandLine(args);
        if (line == null)
            return;
        
        final String xmlFile = line.getOptionValue(XML_FILE_OPT);
        final URL xmlFileUrl = new File(xmlFile).toURL();
        
        final XmlMenuPublisher publisher = new XmlMenuPublisher();
        publisher.publishMenus(xmlFileUrl);
    }
    
    /**
     * Takes the arguments, tries to parse them, prints an err
     * message if there is a problem and returns the parsed line.
     */
    private static CommandLine parseCommandLine(String[] args) {
        final Options opts = setupOptions();
        final CommandLineParser parser = new PosixParser();
        
        CommandLine line = null;
        try {
            line = parser.parse(opts, args);
        }
        catch(ParseException exp) {
            System.out.println("Error parsing arguments: " + exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java XmlMenuPublisherRunner", opts );
        }
        
        return line;
    }
    
    /**
     * Set up the command line options
     */
    private static Options setupOptions() {
        final Options opts = new Options();
        
        final Option xmlSource = new Option(XML_FILE_OPT, true, "The relative or absolute file system location of the menu XML document.");
        xmlSource.setRequired(true);
        xmlSource.setArgs(1);
        xmlSource.setArgName("path");
        xmlSource.setLongOpt("menuXmlFile");
        
        opts.addOption(xmlSource);
        
        return opts;
    }
}