/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2017 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.formbuilder.flow;

import org.apache.commons.lang.StringUtils;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.*;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.render.URLResolverFactory;
import org.slf4j.Logger;
import org.springframework.webflow.execution.RequestContext;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.util.*;

public class CustomFormFlowHandler implements Serializable {

    private static final long serialVersionUID = -4761267217553714173L;
    
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(CustomFormFlowHandler.class);
    
    private String workspace;
    private Locale locale;
    private String formUuid;
    private List<String> fieldSets;
    private Map<String, List<String>> formDatas;
    private Map<String,Integer> storedSteps;
    private int currentFieldSetIndex;

    public void init(JCRNodeWrapper form, HttpServletRequest request) {
        try {
            formDatas = new HashMap<String, List<String>>();
            storedSteps = new HashMap<String, Integer>();
            request.getSession(true).setAttribute("formDatas", formDatas);
            formDatas.put("jcrNodeType", Arrays.asList("jnt:responseToForm"));

            formUuid = form.getIdentifier();
            fieldSets = new ArrayList<String>();
            NodeIterator ni = form.getNode("fieldsets").getNodes();
            while (ni.hasNext()) {
                JCRNodeWrapper next = (JCRNodeWrapper) ni.next();
                if (next.isNodeType("jnt:fieldset")) {
                    fieldSets.add(next.getIdentifier());
                }
            }
            currentFieldSetIndex = 0;

            workspace = form.getSession().getWorkspace().getName();
            locale = form.getSession().getLocale();
        } catch (RepositoryException e) {
            logger.error("Error initializing CustomFormFlowHandler", e);
        }
    }

    public void goToPreviousFieldSet() {
        if (currentFieldSetIndex > 0) {
            currentFieldSetIndex --;
        }
    }

    public JCRNodeWrapper getPreviousFieldSet() {
        try {
            if (currentFieldSetIndex > 0) {
                return JCRSessionFactory.getInstance().getCurrentUserSession(workspace, locale).getNodeByIdentifier(fieldSets.get(currentFieldSetIndex-1));
            }
        } catch (RepositoryException e) {
            logger.error("Cannot get field set", e);
        }
        return null;
    }

    public void goToNextFieldSet() {
        if (currentFieldSetIndex < fieldSets.size() - 1) {
            currentFieldSetIndex ++;
        }
    }

    public JCRNodeWrapper getNextFieldSet() {
        try {
            if (currentFieldSetIndex < fieldSets.size() - 1) {
                return JCRSessionFactory.getInstance().getCurrentUserSession(workspace, locale).getNodeByIdentifier(fieldSets.get(currentFieldSetIndex+1));
            }
        } catch (RepositoryException e) {
            logger.error("Cannot get field set", e);
        }
        return null;
    }

    public JCRNodeWrapper getCurrentFieldSet(HttpServletRequest request) {
        try {
            request.setAttribute("isWebflow", true);
            if (storedSteps.containsKey(request.getQueryString())) {
                currentFieldSetIndex = storedSteps.get(request.getQueryString());
            } else {
                storedSteps.put(request.getQueryString(), currentFieldSetIndex);
            }
            if (fieldSets.size() > 0) {
                return JCRSessionFactory.getInstance().getCurrentUserSession(workspace, locale).getNodeByIdentifier(fieldSets.get(currentFieldSetIndex));
            }
        } catch (RepositoryException e) {
            logger.error("Cannot get field set", e);
        }
        return null;
    }

    public void saveValues(HttpServletRequest request) {
        try {
            JCRNodeWrapper fieldSet = JCRSessionFactory.getInstance().getCurrentUserSession(workspace, locale).getNodeByIdentifier(fieldSets.get(currentFieldSetIndex));
            NodeIterator ni =  fieldSet.getNodes();
            while (ni.hasNext()) {
                JCRNodeWrapper formElement = (JCRNodeWrapper) ni.next();
                if (formElement.isNodeType("jnt:formElement") && request.getParameterValues(formElement.getName()) != null) {
                    formDatas.put(formElement.getName(), Arrays.asList(request.getParameterValues(formElement.getName())));
                }
            }
            request.getSession(true).setAttribute("formDatas",formDatas);
        } catch (RepositoryException e) {
            logger.error("Error saving values", e);
        }
    }

    public String executeActions(RequestContext context, HttpServletRequest request) {
        try {
            JCRNodeWrapper action = JCRSessionFactory.getInstance().getCurrentUserSession(workspace, locale).getNodeByIdentifier(formUuid).getNode("action");

            NodeIterator n = action.getNodes();
            ActionResult r = null;
            while (n.hasNext()) {
                JCRNodeWrapper actionNode = (JCRNodeWrapper) n.next();
                String actionName = actionNode.getProperty("j:action").getString();
                ActionResult result = callAction(request, actionName);
                if(actionName.equals("redirect")){
                    r = result;
                }
            }

            if(r != null && StringUtils.isNotEmpty(r.getUrl())){
                RenderContext renderContext = getRenderContext(context);
                String redirect = StringUtils.isNotEmpty(request.getContextPath()) ? request.getContextPath() + r.getUrl() : r.getUrl();
                renderContext.setRedirect(redirect + ".html");
            }
        } catch (RepositoryException e) {

        }
        return "ok";
    }

    public ActionResult callAction(final HttpServletRequest request, String actionName) {
        try {
            final Action action = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getActions().get(actionName);
            final RenderContext renderContext = (RenderContext) request.getAttribute("renderContext");
            final Map<String,List<String>> formDatas = (Map<String, List<String>>) request.getSession().getAttribute("formDatas");
            Resource mainResource = (Resource) request.getAttribute("currentResource");
            final Resource resource = new Resource(mainResource.getNode().getNode("responses"), mainResource.getTemplateType(), actionName, Resource.CONFIGURATION_PAGE);
            URLResolver mainResolver = (URLResolver) request.getAttribute("urlResolver");
            String urlPathInfo = StringUtils.substringBefore(mainResolver.getUrlPathInfo(), mainResolver.getPath()) + resource.getNode().getPath();
            if (!actionName.equals("default")) {
                urlPathInfo += "."+ actionName + ".do";
            } else {
                urlPathInfo += "/*";
            }

            URLResolverFactory f = (URLResolverFactory) SpringContextSingleton.getBean("urlResolverFactory");
            final URLResolver resolver = f.createURLResolver(urlPathInfo, request.getServerName(), request);
            JCRSessionWrapper sessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession(workspace, locale);

            return JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(),
                    workspace,
                    locale, new JCRCallback<ActionResult>() {
                public ActionResult doInJCR(JCRSessionWrapper session)
                        throws RepositoryException {

                    try {
                        return action.doExecute(request, renderContext, resource, session, formDatas, resolver);
                    } catch (Exception e) {
                        logger.error("Error executing action", e);
                    }

                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Error in action", e);
        }
        return null;
    }

    private RenderContext getRenderContext(RequestContext ctx) {
        return (RenderContext) ctx.getExternalContext().getRequestMap().get("renderContext");
    }
}

