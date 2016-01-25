/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2016 Jahia Solutions Group SA. All rights reserved.
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
package org.jahia.modules.formbuilder.actions;

import org.jahia.bin.Action;
import org.jahia.modules.formbuilder.helper.FormBuilderHelper;
import org.jahia.modules.formbuilder.taglib.FormFunctions;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.tools.generic.DateTool;
import org.jahia.bin.ActionResult;
import org.jahia.bin.Render;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.mail.MailService;
import org.jahia.services.preferences.user.UserPreferencesHelper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUserManagerService;

import javax.jcr.NodeIterator;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Action handler that sends e-mail message.
 *
 * @author rincevent
 * @since JAHIA 6.5
 *        Created : 9 mars 2010
 */
public class MailAction extends Action {
    private transient static Logger logger = LoggerFactory.getLogger(MailAction.class);
    private MailService mailService;
    private JahiaUserManagerService userManagerService;
    private String mailTemplatePath;

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setUserManagerService(JahiaUserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    public void setMailTemplatePath(String mailTemplatePath) {
        this.mailTemplatePath = mailTemplatePath;
    }

    public ActionResult doExecute(HttpServletRequest req, final RenderContext renderContext,
                                  final Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        JCRNodeWrapper node = resource.getNode();
        JCRNodeWrapper actionNode = null;
        NodeIterator nodes = node.getParent().getNode("action").getNodes();
        while (nodes.hasNext()) {
            JCRNodeWrapper nodeWrapper = (JCRNodeWrapper) nodes.nextNode();
            if(nodeWrapper.isNodeType("jnt:mailFormAction")) {
                actionNode = (JCRNodeWrapper) nodeWrapper;
            }
        }
        if (actionNode!=null) {
            JCRUserNode to = userManagerService.lookupUser(node.getSession().getNode(actionNode.getProperty("j:to").getValue().getString()).getName());
            Set<String> reservedParameters = Render.getReservedParameters();
            final Map<String, List<String>> formDatas = new HashMap<String, List<String>>();
            Set<Map.Entry<String, List<String>>> set = parameters.entrySet();
            for (Map.Entry<String, List<String>> entry : set) {
                String key = entry.getKey();
                if (!reservedParameters.contains(key)) {
                    List<String> values = entry.getValue();
                    formDatas.put(key, values);
                }
            }
            String toMail = UserPreferencesHelper.getEmailAddress(to);
            /*
            Define objects to be binded with the script engine to evaluate the scripts
            Same bindings for body and subject
            */        
            Map<String,Object> bindings = new HashMap<String,Object>();
            bindings.put("formDatas",formDatas);
            bindings.put("formNode",node.getParent());
            bindings.put("formFields", FormFunctions.getFormFields(node.getParent()));
            bindings.put("helper", new FormBuilderHelper());            
            bindings.put("submitter",renderContext.getUser());
            bindings.put("date",new DateTool());
            bindings.put("submissionDate", Calendar.getInstance());
            bindings.put("locale", resource.getLocale());
            mailService.sendMessageWithTemplate(mailTemplatePath,bindings,toMail, mailService.getSettings().getFrom(),
                                                          null,null,resource.getLocale(), "Jahia Form Builder");
            logger.info("Form data is sent by e-mail");
        }
        return ActionResult.OK;
    }
}
