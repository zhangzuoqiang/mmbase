/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.subsite.tree;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.subsite.util.SubSiteUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;


public class SubSiteTreeItemRenderer implements NavigationTreeItemRenderer {

    private static final String RESOURCEBUNDLE = "cmsc-modules-subsite";

    public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
         Node parentParentNode = NavigationUtil.getParent(parentNode);
         UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentParentNode, false);
         
         String name = parentNode.getStringValue(SubSiteUtil.TITLE_FIELD);
         String fragment = parentNode.getStringValue( NavigationUtil.getFragmentFieldname(parentNode) );

         String id = String.valueOf(parentNode.getNumber());
         TreeElement element = renderer.createElement(parentNode, role, name, fragment, false);

         if (SecurityUtil.isEditor(role)) {
            element.addOption(renderer.createTreeOption("edit_defaults.png", "site.sub.edit",
                        RESOURCEBUNDLE, "../subsite/SubSiteEdit.do?number=" + id));
            element.addOption(renderer.createTreeOption("delete.png", "site.sub.remove", 
                    RESOURCEBUNDLE, "../subsite/SubSiteDelete.do?number=" + id));
         }
         
         if (SecurityUtil.isChiefEditor(role)) {
             element.addOption(renderer.createTreeOption("cut.png", "site.page.cut", "javascript:cut('" + id + "');"));
             element.addOption(renderer.createTreeOption("copy.png", "site.page.copy", "javascript:copy('" + id + "');"));
             element.addOption(renderer.createTreeOption("paste.png", "site.page.paste", "javascript:paste('" + id + "');"));
          }

         element.addOption(renderer.createTreeOption("rights.png", "site.page.rights",
                 "../usermanagement/pagerights.jsp?number=" + id));
         
         return element;
      }

	public void addParentOption(NavigationRenderer renderer,
			TreeElement element, String parentId) {
        element.addOption(renderer.createTreeOption("subsite_new.png", "site.sub.new",
                "cmsc-modules-subsite", "../subsite/SubSiteCreate.do?parentpage=" + parentId));

		
	}

}
