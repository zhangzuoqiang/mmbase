/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;

import org.mmbase.util.logging.*;

/**
 * Replace 1 or more spaces by 1 space, and 1 or more newlines by 1
 * newline. Any other combination of newlines and spaces is replaced
 * by one newline.
 *
 * Except if they are in between "&lt;pre&gt;" and "&lt;/pre&gt;". (Note: perhaps this last behaviour should be made
 * configurable).
 *
 * @todo 'pre' stuff not yet implemented.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: SpaceReducer.java,v 1.16 2007-08-04 08:09:14 michiel Exp $
 */

public class SpaceReducer extends BufferedReaderTransformer implements CharTransformer {

    private static Logger log = Logging.getLoggerInstance(SpaceReducer.class);


    protected void transform(PrintWriter bw, String line) {
        if (!line.trim().equals("")) {
            bw.write(line);
        }
    }

    /**
     * This was the original, now unused implementation (not efficient enough)
     */
    protected Writer transform2(Reader r, Writer w) {

        int space = 1;  // 'open' spaces (on this line)
        int nl    = 1;  // 'open' newlines
        // we start at 1, rather then 0, because in that way, all leading space is deleted too

        StringBuilder indent = new StringBuilder();  // 'open' indentation of white-space
        int l = 0; // number of non-white-space (letter) on the current line

        int lines = 0; // for debug: the total number of lines read.
        try {
            log.debug("Starting spacereducing");
            int c = r.read();
            while (c != -1) {
                if (c == '\n' || c == '\r' ) {
                    if (nl == 0) w.write('\n');
                    nl++;
                    l = 0;
                    space = 0; indent.setLength(0);
                } else if (Character.isWhitespace((char) c)) {
                    if (space == 0 && l > 0) w.write(' ');
                    if (l == 0) indent.append((char) c);
                    space++;
                } else {
                    if (l == 0 && space > 0) {
                        w.write(indent.toString());
                        indent.setLength(0);
                    }
                    space = 0; lines += nl; nl = 0; l++;
                    w.write(c);
                }
                c = r.read();
            }
            log.debug("Finished: read " + lines + " lines");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    public String toString() {
        return "SPACEREDUCER";
    }
}
