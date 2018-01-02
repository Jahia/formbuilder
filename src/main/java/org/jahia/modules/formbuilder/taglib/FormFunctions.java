/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2018 Jahia Solutions Group SA. All rights reserved.
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
package org.jahia.modules.formbuilder.taglib;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: toto
 * Date: 11/7/11
 * Time: 6:11 PM
 */
public class FormFunctions {

    public static Map<String,String> getFormFields(JCRNodeWrapper formNode) throws RepositoryException {
        Map<String, String> m = new LinkedHashMap<String, String>();
        
        NodeIterator ni = formNode.getNode("fieldsets").getNodes();
        while (ni.hasNext()) {
            JCRNodeWrapper fieldSet = (JCRNodeWrapper) ni.next();
            List<JCRNodeWrapper> fields = JCRContentUtils.getChildrenOfType(fieldSet, "jnt:formElement");
            for (JCRNodeWrapper field : fields) {
                if (field.isNodeType("jnt:automaticList")) {
                    if (field.hasProperty("type")) {
                        String[] renderers = field.getProperty("type").getString().split("=");
                        String renderer = null;
                        if (renderers.length > 1) {
                            renderer = renderers[1];
                        }
                        m.put(field.getName(), renderer);
                    }
                } else if (field.isNodeType("jnt:htmlInput")) {
                    String html = field.getProperty("html").getString();
                    Source source = new Source(html);
                    List<StartTag> inputTags = source.getAllStartTags();
                    for (StartTag inputTag : inputTags) {
                        if ((inputTag.getName().equalsIgnoreCase("input") || inputTag.getName().equalsIgnoreCase("select") || inputTag.getName().equalsIgnoreCase("textarea"))
                                && inputTag.getAttributeValue("name") != null) {
                            if (StringUtils.equals(inputTag.getAttributeValue("type"),"file")) {
                                m.put(inputTag.getAttributeValue("name"), "file");
                            } else {
                                m.put(inputTag.getAttributeValue("name"), null);
                            }
                        }
                    }
                } else {
                    if (field.isNodeType("jnt:fileElement")) {
                        m.put(field.getName(), "file");
                    } else {
                        m.put(field.getName(), null);
                    }
                }
            }
        }

        return m;
    }

}
