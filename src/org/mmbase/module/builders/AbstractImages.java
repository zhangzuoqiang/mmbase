/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;
import org.mmbase.cache.BlobCache;
import org.mmbase.util.MagicFile;
import org.mmbase.util.images.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;

/**
 * AbstractImages holds the images and provides ways to insert, retrieve and
 * search them.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractImages.java,v 1.32 2005-05-09 10:02:18 michiel Exp $
 * @since   MMBase-1.6
 */
public abstract class AbstractImages extends AbstractServletBuilder {

    private static final Logger log = Logging.getLoggerInstance(AbstractImages.class);

    public final static Parameter[] HEIGHT_PARAMETERS = Parameter.EMPTY;
    public final static Parameter[] WIDTH_PARAMETERS  = Parameter.EMPTY;
    public final static Parameter[] DIMENSION_PARAMETERS  = Parameter.EMPTY;


    public static final String FIELD_ITYPE       = "itype";
    public static final String FIELD_FILESIZE    = "filesize";
    public static final String FIELD_HEIGHT      = "height";
    public static final String FIELD_WIDTH       = "width";

    protected static BlobCache handleCache = new BlobCache(300) {  // a few images are in memory cache.
            public String getName()        { return "ImageHandles"; }
            public String getDescription() { return "Handles of Images (number -> handle)"; }
        };
    static {
        handleCache.putCache();
    }
    /**
     * Cache with 'ckey' keys.
     * @since MMBase-1.6.2
     */
    abstract protected static class CKeyCache extends org.mmbase.cache.Cache {
        protected CKeyCache(int i) {
            super(i);
        }
        /**
         * Remove all cache entries associated with a certain images node
         * This depends now on the fact that ckeys start with the original node-number
         */

        void remove(int originalNodeNumber) {
            String prefix = "" + originalNodeNumber;
            if (log.isDebugEnabled()) {
                log.debug("removing " + prefix);
            }
            Iterator entries  = entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry)entries.next();
                String key = (String)entry.getKey();
                if (log.isDebugEnabled()) {
                    log.debug("checking " + key);
                }
                if (key.startsWith(prefix)) {
                    // check is obviously to crude, e.g. if node number happens to be 4,
                    // about one in 10 cache entries will be removed which need not be removed,
                    // but well, it's only a cache, it's only bad luck...
                    // 4 would be a _very_ odd number for an Image, btw..
                    if (log.isDebugEnabled()) {
                        log.debug("removing " + key + " " + get(key));
                    }
                    entries.remove();
                }

            }
        }

        void removeCacheNumber(int icacheNumber) {
            Iterator entries  = entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Object value = entry.getValue();
                if (value instanceof ByteFieldContainer) {
                    ByteFieldContainer bf = (ByteFieldContainer) value;
                    if (bf.number == icacheNumber) {
                        entries.remove();
                    }
                } else if (value instanceof Integer) {
                    Integer i = (Integer) value;
                    if (i.intValue() == icacheNumber) {
                        entries.remove();
                    }
                }

            }
        }
    }

    protected BlobCache getBlobCache(String fieldName) {
        if (fieldName.equals(Imaging.FIELD_HANDLE)) {
            return handleCache;
        } else {
            return super.getBlobCache(fieldName);
        }
    }

    protected String getAssociation() {
        return "images";
    }
    protected String getDefaultPath() {
        return "/img.db";
    }

    /**
     * An image's gui-indicator is of course some &lt;img src&gt;, but it depends on what kind of image
     * (cached, original) what excactly it must be.
     */
    abstract protected String getGUIIndicatorWithAlt(MMObjectNode node, String title, Parameters a);

    /**
     * Returns GUI Indicator for node
     * @since MMBase-1.7
     */
    protected String getSGUIIndicatorForNode(MMObjectNode node, Parameters a) {
        return getGUIIndicatorWithAlt(node, "*", a); /// Gui indicator of a whole node.
    }

    /**
     * @since MMBase-1.7
     */
    protected String getSGUIIndicator(MMObjectNode node, Parameters a) {
        String field = a.getString("field");
        if (field.equals(Imaging.FIELD_HANDLE) || field.equals("")) {
            return getSGUIIndicatorForNode(node, a);
        } else if (field.equals(FIELD_FILESIZE)) {
            return getFileSizeGUI(node.getIntValue(FIELD_FILESIZE));  
        }
        // other fields can be handled by the orignal gui function...
        return getSuperGUIIndicator(field, node);
    }


    protected final Set IMAGE_HANDLE_FIELDS = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] {FIELD_FILESIZE, FIELD_ITYPE, FIELD_HEIGHT, FIELD_WIDTH})));
    // javadoc inherited
    protected Set getHandleFields() {
        return IMAGE_HANDLE_FIELDS;
    }

    /**
     * Determine the MIME type of this image node, based on the image format.
     */
    public String getMimeType(MMObjectNode node) {
        String ext = getImageFormat(node);
        log.debug("Getting mimetype for node " + node.getNumber() + " " + ext);
        return Imaging.getMimeTypeByExtension(ext);
    }

    /**
     * Whether this builders has width and height fields
     */
    protected boolean storesDimension() {
        return getField(FIELD_WIDTH) != null && getField(FIELD_HEIGHT) != null;
    }
    /**
     * Whether this builders has a filesize field.
     */
    protected boolean storesFileSize() {
        return getField(FIELD_FILESIZE) != null;
    }
    /**
     * Whether this builders has a filesize field.
     */
    protected boolean storesImageType() {
        return getField(FIELD_ITYPE) != null;
    }


    /**
     * Gets the dimension of given node. Also when the fields are missing, it will result a warning then.
     */
    protected Dimension getDimension(MMObjectNode node) {
        if (storesDimension()) {
            int width  = node.getIntValue(FIELD_WIDTH);
            int height = node.getIntValue(FIELD_HEIGHT);
            if (width >= 0 && height > 0 ) {
                return new Dimension(width, height);
            }
        }
        byte[] data = node.getByteValue(Imaging.FIELD_HANDLE);
        if (data == null || data.length == 0) {
            log.warn("Cannot get dimension of Node with not 'handle' " + node.getNumber());
            return new Dimension(-1, -1);
        }
        ImageInformer ii = Factory.getImageInformer();
        Dimension dim;
        try {
            dim = ii.getDimension(data);
            log.debug("Found dimension " + dim);
        } catch (Exception ioe) {
            log.error(ioe);
            dim = new Dimension(-1, -1);
            return dim;
        }
        if (storesDimension()) {
            node.setValue(FIELD_WIDTH,  dim.getWidth());
            node.setValue(FIELD_HEIGHT, dim.getHeight());
        } else {
            log.warn("Requested dimension on image object without height / width fields, this may be heavy on resources!");
        }

        return dim;        
    }


    protected  int getFileSize(MMObjectNode node) {
        if (storesFileSize()) {
            int size = node.getIntValue(FIELD_FILESIZE);
            if (size >= 0) {
                return size;
            }
        }
        byte[] data = node.getByteValue(Imaging.FIELD_HANDLE);
        if (data == null) return -1;
        if (storesFileSize()) {
            node.setValue(FIELD_FILESIZE, data.length);
        } else {
            log.warn("Requested filesize on image object without filesize fields, this may be heavy on resources!");
        }
        return data.length;
    }


    protected String getDefaultImageType() {
        return "jpg";
    }

    /**
     * Determines the image type of an object and stores the content in the itype field.
     * @param node The object to use.
     */
    protected String getImageFormat(MMObjectNode node) {
        String itype = null;
        if (storesImageType()) {
            itype = node.getStringValue(FIELD_ITYPE);            
        }

        if ((itype == null || itype.equals("")) &&  // itype unset
            ! node.isNull(Imaging.FIELD_HANDLE)        // handle present
            ) {
            itype = "";
            try {
                MagicFile magicFile = MagicFile.getInstance();
                String mimeType = magicFile.getMimeType(node.getByteValue(Imaging.FIELD_HANDLE));
                if (!mimeType.equals(MagicFile.FAILED)) {
                    // determine itype
                    if (mimeType.startsWith("image/")) {
                        itype = mimeType.substring(6);
                        log.debug("set itype to " + itype);
                    } else {                        
                        log.warn("Mimetype " + mimeType + " is not an image type");
                        int pos = mimeType.indexOf('/');
                        itype = mimeType.substring(pos + 1);
                    }
                } else {
                    log.warn(MagicFile.FAILED);
                    itype = getDefaultImageType();
                }
            } catch (Exception e) {
                log.warn("Error while determining image mimetype : " + Logging.stackTrace(e));
            }
            if (storesImageType()) {
                node.setValue(FIELD_ITYPE, itype);
            }
        }
        return itype;
    }

    //javadoc inherited
    protected void checkHandle(MMObjectNode node) {
        super.checkHandle(node);
        if (storesFileSize()) {
            getFileSize(node);
        }
        if (storesDimension()) {
            getDimension(node);
        }
        if (storesImageType()) {
            getImageFormat(node);
        }        
    }


    
    /**
     * Every image of course has a format and a mimetype. Two extra functions to get them.
     *
     */

    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (function.equals("mimetype")) {
            return getMimeType(node);
        } else if (function.equals("format")) {
            return getImageFormat(node);
        } else if ("width".equals(function)) {
            return new Integer(getDimension(node).getWidth());
        } else if ("height".equals(function)) {
            return new Integer(getDimension(node).getHeight());
        } else if ("dimension".equals(function)) {
            return getDimension(node);
        } else {
            return super.executeFunction(node, function, args);
        }
    }

}
