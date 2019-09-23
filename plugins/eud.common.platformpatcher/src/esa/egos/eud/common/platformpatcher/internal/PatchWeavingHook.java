/*******************************************************************************
 * Copyright (c) 2019 European Space Agency
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     European Space Agency - initial API and implementation
 *******************************************************************************/

package esa.egos.eud.common.platformpatcher.internal;

import java.util.Map;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

/**
 * A WeavingHook implementation that applies patches to third party product
 * classes provided in a class map.
 * 
 * @author Jean Schuetz
 * @since 3.1.7
 */
public final class PatchWeavingHook implements WeavingHook
{
    /** The patchClassMap of this PatchWeavingHook */
    private final Map<String, byte[]> patchClassMap;


    /**
     * Creates a new PatchWeavingHook that applies patches to third party
     * product classes provided in the given patchClassMap.
     * 
     * @param patchClassMap
     *            the Map<String, byte[]> mapping the class name to the byte
     *            array containing the patched class bytes
     */
    PatchWeavingHook(Map<String, byte[]> patchClassMap)
    {
        this.patchClassMap = patchClassMap;
    }

    /** {@inheritDoc} */
    @Override
    public final void weave(final WovenClass wovenClass)
    {
        final String thisClassName = wovenClass.getClassName();
        final byte[] patchedBytes = this.patchClassMap.get(thisClassName);
        if (patchedBytes == null)
        {
            return;
        }

        wovenClass.setBytes(patchedBytes);
        System.out.println("PatchWeavingHook INFO : Loaded Patched Class: " + thisClassName);
    }
}