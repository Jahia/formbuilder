/**
 * ==========================================================================================
 * =                        DIGITAL FACTORY v7.0 - Community Distribution                   =
 * ==========================================================================================
 *
 *     Rooted in Open Source CMS, Jahia's Digital Industrialization paradigm is about
 *     streamlining Enterprise digital projects across channels to truly control
 *     time-to-market and TCO, project after project.
 *     Putting an end to "the Tunnel effect", the Jahia Studio enables IT and
 *     marketing teams to collaboratively and iteratively build cutting-edge
 *     online business solutions.
 *     These, in turn, are securely and easily deployed as modules and apps,
 *     reusable across any digital projects, thanks to the Jahia Private App Store Software.
 *     Each solution provided by Jahia stems from this overarching vision:
 *     Digital Factory, Workspace Factory, Portal Factory and eCommerce Factory.
 *     Founded in 2002 and headquartered in Geneva, Switzerland,
 *     Jahia Solutions Group has its North American headquarters in Washington DC,
 *     with offices in Chicago, Toronto and throughout Europe.
 *     Jahia counts hundreds of global brands and governmental organizations
 *     among its loyal customers, in more than 20 countries across the globe.
 *
 *     For more information, please visit http://www.jahia.com
 *
 * JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION
 * ============================================
 *
 *     Copyright (C) 2002-2014 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==========================================================
 *
 *     IF YOU DECIDE TO CHOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     "This program is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation; either version 2
 *     of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 *     As a special exception to the terms and conditions of version 2.0 of
 *     the GPL (or any later version), you may redistribute this Program in connection
 *     with Free/Libre and Open Source Software ("FLOSS") applications as described
 *     in Jahia's FLOSS exception. You should have received a copy of the text
 *     describing the FLOSS exception, and it is also available here:
 *     http://www.jahia.com/license"
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ==========================================================
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
package org.jahia.modules.formbuilder.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;

import org.jahia.services.content.JCRNodeWrapper;
import org.slf4j.Logger;

public class FormBuilderHelper {
	private transient static Logger logger = org.slf4j.LoggerFactory.getLogger(FormBuilderHelper.class);
	
	public static String getNodeTitle(JCRNodeWrapper formNode, String fieldname) {
	    JCRNodeWrapper node = getFieldNode(formNode, fieldname);
	    if(node != null) {
	    	return node.getPropertyAsString("jcr:title");
	    }
		return fieldname;
	}
	
	public static String getNodeValue(JCRNodeWrapper formNode, Map<String, List<String>> fieldvalues, String fieldname) {
		
		try{
			List<String> values = (List<String>)fieldvalues.get(fieldname);

			if(values == null) { //special type
				JCRNodeWrapper node = getFieldNode(formNode, fieldname);
				if(node.getNodeTypes().contains("jnt:addressBlock")) { //Address
					values = fieldvalues.get("street");
					if(values != null && values.size() > 0) {
						if(values.size() == 1) {
							return getAddress(fieldvalues, 1);
						} else { //multiple address fields
							return getAddress(fieldvalues, getFieldPosition(formNode, fieldname, "jnt:addressBlock"));
						}
					}
					return "";  //Address not found
				} else if(node.getNodeTypes().contains("jnt:birthdate")) {
					values = fieldvalues.get("year");
					if(values != null && values.size() > 0) {
						if(values.size() == 1) {
							return getBirthday(fieldvalues, 1);
						} else { //multiple address fields
							return getBirthday(fieldvalues, getFieldPosition(formNode, fieldname, "jnt:birthdate"));
						}						
					}
					
				} else if(node.getNodeTypes().contains("jnt:fileElement")) { //File
				//TODO: when attachments are implemented
					return "File Attachment currently not implemented";  
				} 
				return "";  //for instance for captcha
			}
			if(values.size() == 1) { //only one entry (single field)
				return values.get(0);
			} 
			if(values.size() > 1) {  //multiple fields 
				//just possible when you add textfields with same name as in address
				//but not possible to calculate the correct position
				logger.warn("In your form "+ formNode.getName() + " you have duplicate fields, check it and rename one field, because wrong value could appear in email: "  + fieldname);
				return values.get(0);			
			}
		}catch(Exception ex) {
			logger.error("Error when reading Fieldvalue for " + fieldname,  ex);
		}
		return "";
	}
	
	private static JCRNodeWrapper getFieldNode(JCRNodeWrapper formNode, String fieldname) {
		try{
		    NodeIterator ni = formNode.getNodes();
		    while(ni.hasNext()) {
		    	JCRNodeWrapper node = (JCRNodeWrapper)ni.next();
		    	if(node.getPath().endsWith("fieldsets")) {
		    	   NodeIterator nifieldsets = node.getNodes();  //get all Fieldsets
		    	   while(nifieldsets.hasNext()) {
		    		   JCRNodeWrapper nodefieldset = (JCRNodeWrapper)nifieldsets.next();
		    		   NodeIterator nifield = nodefieldset.getNodes(fieldname); //get field by name
		    		   if(nifield.getSize() > 0) {
		    			  JCRNodeWrapper nodefield = (JCRNodeWrapper)nifield.next();
		    		      return nodefield;
		    		   }
		    	   }
		    	}
		    }
		}catch(Exception ex) {
			logger.error("Error when try to get formfield node " + fieldname,  ex);
		}
		return null;		
	}
	
	private static int getFieldPosition(JCRNodeWrapper formNode, String fieldname, String fieldtype) {
		int position = 1;
		try{
		    NodeIterator ni = formNode.getNodes();
		    while(ni.hasNext()) {
		    	JCRNodeWrapper node = (JCRNodeWrapper)ni.next();
		    	if(node.getPath().endsWith("fieldsets")) {
		    	   NodeIterator nifieldsets = node.getNodes();  //get all Fieldsets
		    	   while(nifieldsets.hasNext()) {
		    		   JCRNodeWrapper nodefieldset = (JCRNodeWrapper)nifieldsets.next();
		    		   NodeIterator nifields = nodefieldset.getNodes(); //get all fields
		    		   while(nifields.hasNext()) {
		    			  JCRNodeWrapper nodefield = (JCRNodeWrapper)nifields.next();
		    		      if(nodefield.getNodeTypes().contains(fieldtype)) {
		    		    	 if(fieldname.equals(nodefield.getName())) {
		    		    	   return position;
		    		    	 }
		    		    	 position ++;
		    		      }
		    		   }
		    	   }
		    	}
		    }
		}catch(Exception ex) {
			logger.error("Error when try to get formfield node " + fieldname + " to analyze position",  ex);
		}	
		
		return 1;
	}
	
	private static String getAddress(Map<String, List<String>> values, int position) {
		
		StringBuffer address = new StringBuffer();
		List<String> subvalues = values.get("street");
		if(subvalues != null && subvalues.size() >= position) {
			address.append(subvalues.get(position-1));
		}
		subvalues = values.get("street2");
		if(subvalues != null && subvalues.size() >= position) {
			address.append(", ").append(subvalues.get(position-1));
		}
		subvalues = values.get("city");
		if(subvalues != null && subvalues.size() >= position) {
			address.append(", ").append(subvalues.get(position-1));
		}	
		subvalues = values.get("zip");
		if(subvalues != null && subvalues.size() >= position) {
			address.append(", ").append(subvalues.get(position-1));
		}	
		subvalues = values.get("state");
		if(subvalues != null && subvalues.size() >= position) {
			address.append(", ").append(subvalues.get(position-1));
		}
		subvalues = values.get("country");
		if(subvalues != null && subvalues.size() >= position) {
			address.append(", ").append(subvalues.get(position-1));
		}		
		return address.toString();
	}
private static String getBirthday(Map<String, List<String>> values, int position) {
	
		Calendar date = Calendar.getInstance();
		try {
			List<String> subvalues = values.get("year");
			if(subvalues != null && subvalues.size() >= position) {
				date.set(Calendar.YEAR, Integer.parseInt(subvalues.get(position-1)));
			}
			subvalues = values.get("month");
			if(subvalues != null && subvalues.size() >= position) {
				date.set(Calendar.MONTH, Integer.parseInt(subvalues.get(position-1)) - 1); //starts from 0: UI: starts from 1
			}
			subvalues = values.get("day");
			if(subvalues != null && subvalues.size() >= position) {
				date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(subvalues.get(position-1)));  
			}	
		}catch(NumberFormatException ex) {
			logger.warn("Dateformat is not correct for datefield, see values: " + values, ex);
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date.getTime());
	}
}
