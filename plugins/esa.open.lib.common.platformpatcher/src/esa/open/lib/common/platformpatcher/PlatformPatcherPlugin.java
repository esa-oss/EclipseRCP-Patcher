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

package esa.open.lib.common.platformpatcher;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import esa.open.lib.common.platformpatcher.internal.PatchApplier;

/**
 * Activator of the Platform Patcher Plugin which has the purpose to patch
 * Eclipse Platform code on-the-fly at runtime in order to resolve bugs.
 * 
 * @author Jean Schuetz
 * @since 3.1.7
 */
public final class PlatformPatcherPlugin implements BundleActivator
{
    /** The BundleContext of this PlatformPatcherPlugin */
    private static BundleContext context;


    /**
     * @return the BundleContext of this PlatformPatcherPlugin
     */
    static final BundleContext getContext()
    {
        return context;
    }

    /** {@inheritDoc} */
    @Override
    public final void start(BundleContext bundleContext) throws Exception
    {
        PlatformPatcherPlugin.context = bundleContext;
        new PatchApplier(bundleContext).applyPatches();
    }

    /** {@inheritDoc} */
    @Override
    public final void stop(BundleContext bundleContext) throws Exception
    {
        PlatformPatcherPlugin.context = null;
    }

}
