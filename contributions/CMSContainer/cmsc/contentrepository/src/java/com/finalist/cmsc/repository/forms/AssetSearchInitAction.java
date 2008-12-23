package com.finalist.cmsc.repository.forms;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.SortOrder;

import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AssetSearchInitAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      AssetSearchForm assetSearchForm = (AssetSearchForm) form;

      if (StringUtils.isEmpty(assetSearchForm.getExpiredate())) {
         assetSearchForm.setExpiredate("0");
      }

      if (StringUtils.isEmpty(assetSearchForm.getPublishdate())) {
         assetSearchForm.setPublishdate("0");
      }

      if (StringUtils.isEmpty(assetSearchForm.getOffset())) {
         assetSearchForm.setOffset("0");
      }

      if (StringUtils.isEmpty(assetSearchForm.getOrder())) {
         assetSearchForm.setOrder("title");
      }

      if (assetSearchForm.getDirection() != SortOrder.ORDER_DESCENDING) {
         assetSearchForm.setDirection(SortOrder.ORDER_ASCENDING);
      }
      List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();

      List<NodeManager> types = AssetElementUtil.getAssetTypes(cloud);
      List<String> hiddenTypes = AssetElementUtil.getHiddenAssetTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
            typesList.add(bean);
         }
      }
      addToRequest(request, "typesList", typesList);
    //reset the show mode of assets in the session if exist
      request.getSession().removeAttribute("show");
      request.getSession().removeAttribute("imageOnly");

      return mapping.findForward("searchoptions");
   }

}
