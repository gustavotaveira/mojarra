/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * NewVariableResolver.java
 *
 * Created on April 29, 2006, 1:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.faces.test.servlet30.customresolvers;

import com.sun.faces.util.Util;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;
import javax.servlet.ServletContext;

/**
 *
 * @author edburns
 */
public class NewVariableResolver extends VariableResolver {
    
    private VariableResolver original = null;
    
    /** Creates a new instance of NewVariableResolver */
    public NewVariableResolver(VariableResolver original) {
        this.original = original;
        
        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("newVR", this);
    }

    public NewVariableResolver(VariableResolver original, FacesContext context) {
        this.original = original;

        context.getExternalContext().getApplicationMap().put("newVR", this);
    }

    public Object resolveVariable(FacesContext context, String name) throws EvaluationException {
        Object result = null;
        
        // This expects a plain old bean that is not configured as a Faces
        // managed bean.  However, an additional check is done to make sure
        // that is not configured as a managed bean.  So, we've resolved 
        // the name as a "non" managed bean, but want to do some additional
        // checks to make sure that name does not also resolve to a 
        // managed bean. 
        //  
        if (name.equals("nonmanaged")) {
            Object bean = null;
            Object managedBean = null;             
            try {
                Class clazz = Util.loadClass("com.sun.faces.systest.model.TestBean", context);
                bean = clazz.newInstance();
            } catch (Exception e) {
            } 
            managedBean = original.resolveVariable(context, name); 
            if (bean == null) {
                if (managedBean==null) {
                    return null;
                } else {
                    result = managedBean;
                }
            } else {
                result = bean;
            }
            return result;
        }
        if (name.equals("traceResolution")) {
            Bean.captureStackTrace(context);
        }

        if (name.equals("custom")) {
            result = "custom";
        }
        else {
            result = original.resolveVariable(context, name);
        }
        
        return result;
    }
    
}
