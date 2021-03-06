/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import javax.servlet.http.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Generates an iframe with a wizard list (if the 'list' property is set) or a wizard wizard (if the
 * 'wizard' property is set).
 *
 * Other wizard/list jsp can also be used. E.g.
 <pre><![CDATA[
  &lt;block name="people"&gt;
    &lt;title xml:lang="en"&gt;Simple examples&lt;/title&gt;
    &lt;title xml:lang="nl"&gt;Eenvoudige voorbeelden&lt;/title&gt;
    &lt;body&gt;
      &lt;class name="org.mmbase.framework.EditwizardRenderer"&gt;
        &lt;param name="list"&gt;samples/people&lt;/param&gt;
        &lt;param name="nodepath"&gt;people&lt;/param&gt;&gt;
        &lt;param name="fields"&gt;firstname,lastname,owner&lt;/param&gt;
      &lt;/class&gt;
    &lt;/body&gt;
  &lt;/block&gt;
  ]]></pre>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class EditwizardRenderer extends IFrameRenderer {
    private static final Logger log = Logging.getLoggerInstance(EditwizardRenderer.class);

    protected String list;
    protected String wizard;
    public EditwizardRenderer(Type t, Block parent) {
        super(t, parent);
    }

    public void setList(String l) {
        list = l;
    }
    public void setWizard(String w) {
        wizard = w;
    }

    @Override
    public Parameter<?>[] getParameters() {
        return new Parameter<?>[] {new Parameter.Wrapper(super.getParameters()), Parameter.LOCALE};
    }

    /**
     */
    @Override public String getIFrameUrl(Parameters blockParameters)  {

        if (list != null && wizard != null) throw new IllegalStateException();

        Locale  locale = blockParameters.get(Parameter.LOCALE);
        String templates = JspRenderer.JSP_ROOT + getBlock().getComponent().getName();
        Map<String, Object> props = new TreeMap<String, Object>(properties);

        props.put("wizard", list != null ? list : wizard);
        props.put("language", locale.getLanguage());
        props.put("templates", templates);
        HttpServletRequest request   = blockParameters.get(Parameter.REQUEST);

        String url = list != null ?
            "/mmbase/edit/wizard/jsp/list.jsp" :
            "/mmbase/edit/wizard/jsp/wizard.jsp";

        return org.mmbase.framework.basic.BasicUrlConverter.getUrl(url, props, request, true);
    }
    @Override
    public String toString() {
        return "EW " + (list != null ? ("list.jsp?wizard=" + list) : ("wizard.jsp?wizard=" + wizard)) + "&" + properties;
    }



}
