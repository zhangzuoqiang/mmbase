/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.io.*;
import org.mmbase.util.functions.Parameters;

/**
 * A View is a thing that can actually be rendered, and can be returned by a {@link Component}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Renderer.java,v 1.5 2006-10-14 09:43:59 johannes Exp $
 * @since MMBase-1.9
 */
public interface Renderer {

    enum Type {
        HEAD, BODY;
        Renderer getEmpty(final Block block) {
            return new Renderer() {
                public Type getType() { return Type.this; }
                public Parameters createParameters() { return Parameters.VOID; };
                public void render(Parameters parameters, Parameters urlparameters, Writer w) { };
                public Block getBlock() { return block ; };
            };
        }
    }

    public Type getType();

    public Block getBlock();

    /**
     * Before rendering, it may have to be fed with certain parameters. Obtain a parameters
     * object which this method, fill it, and feed it back into {@link #render}.
     */
    Parameters createParameters();

    /**
     * Renders to a writer. In case of e.g. a JSPView, the parameters must also contain
     * the Http Servlet response and request, besided specific parameters for this component.
     */
    void render(Parameters blockParameters, Parameters frameworkParameters, Writer w) throws IOException;
}
