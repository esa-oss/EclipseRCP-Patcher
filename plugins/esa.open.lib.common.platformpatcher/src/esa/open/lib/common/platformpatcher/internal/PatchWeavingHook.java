/*********************************************************************
 * Copyright (c) 2019 European Space Agency
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     European Space Agency - initial API and implementation
 **********************************************************************/

package esa.open.lib.common.platformpatcher.internal;

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
