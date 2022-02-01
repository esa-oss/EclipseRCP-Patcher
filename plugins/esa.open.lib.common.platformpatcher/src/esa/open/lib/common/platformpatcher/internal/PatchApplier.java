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

import static esa.open.lib.common.platformpatcher.internal.PlatformFlags.CPU_SPEC_64_BIT;
import static esa.open.lib.common.platformpatcher.internal.PlatformFlags.NOTRAP;
import static esa.open.lib.common.platformpatcher.internal.PlatformFlags.OS_SPEC_LINUX;
import static esa.open.lib.common.platformpatcher.internal.PlatformFlags.RAP;
import static esa.open.lib.common.platformpatcher.internal.PlatformFlags.WS_SPEC_GTK;
import static esa.open.lib.common.platformpatcher.internal.PlatformFlags.WS_SPEC_NOTRAP;
import static esa.open.lib.common.platformpatcher.internal.PlatformFlags.WS_SPEC_RAP;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Class to apply all applicable patches to the Eclipse Platform and other third
 * party libraries essential for the correct functioning of EUD and EUD based
 * applications.
 * 
 * @author Jean Schuetz
 * @since 3.1.7
 */
public final class PatchApplier
{
    /**
     * The base path where all patch libraries are located within this plugin.
     */
    private static final String PATH_OF_BIN = "patchedBin/";

    /**
     * The reference suffix for SWT GTK Linux binary patch files
     */
    private static final String SWT_GTK_LINUX_REF = "swt.gtk.linux.x86_64_3.109.0.v20181204-1801";

    /** Compiled binary class patching org.eclipse.jface.action.MenuManager */
    private static final String MENU_MANAGER_PATCH = PATH_OF_BIN + "MenuManager_3.15.0.v20181123-1505.binarypatch";

    /**
     * Compiled binary class patching org.eclipse.jface.action.MenuManager for
     * the RAP platform
     */
    private static final String MENU_MANAGER_PATCH_RAP = PATH_OF_BIN
                                                         + "MenuManager_RAP_3.9.0.20190320-1512.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer$10$1
     */
    private static final String MENU_MANAGER_SHOW_PROCESSOR_PATCH_RAP = PATH_OF_BIN
                                                                        + "MenuManagerShowProcessor_RAP_0.13.0.rap-20170515-2147.binarypatch";

    /**
     * Compiled binary org.eclipse.e4.ui.workbench.addons.dndaddon.PartDragAgent
     */
    private static final String PART_DRAG_AGENT_PATCH = PATH_OF_BIN
                                                        + "PartDragAgent.org.eclipse.e4.ui.workbench.addons.swt_1.3.300.v20181102-1042.binarypatch";

    /**
     * Compiled binary org.eclipse.swt.widgets.Text
     */
    private static final String TEXT_LINUX_PATCH = PATH_OF_BIN + "Text." + SWT_GTK_LINUX_REF + ".binarypatch";
    
    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.workbench.renderers.swt.StackRenderer
     */
    private static final String STACKRENDERER_RAP_CLASS = PATH_OF_BIN
                                                          + "StackRenderer_RAP_0.13.0.20170515-2147.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.workbench.renderers.swt.StackRenderer$10
     */
    private static final String STACKRENDERER10_RAP_CLASS = PATH_OF_BIN
                                                            + "StackRenderer$10_RAP_0.13.0.20170515-2147.binarypatch";

    /**
     * Compiled binary classes patching SWT GTK memory leak
     */
    private static final String[] SWT_GTK_MEMORY_LEAK_PATCH_BINS = new String[] { PATH_OF_BIN
                                                                                  + "GC." + SWT_GTK_LINUX_REF
                                                                                  + ".binarypatch",
                                                                                  PATH_OF_BIN + "Canvas."
                                                                                                    + SWT_GTK_LINUX_REF
                                                                                                    + ".binarypatch",
                                                                                  PATH_OF_BIN + "Control." + SWT_GTK_LINUX_REF + ".binarypatch",
                                                                                  PATH_OF_BIN + "Display." + SWT_GTK_LINUX_REF + ".binarypatch",
                                                                                  PATH_OF_BIN + "ToolTip." + SWT_GTK_LINUX_REF + ".binarypatch" };

    /**
     * Set of class names patching SWT GTK memoy leak
     */
    private static final String[] SWT_GTK_MEMORY_LEAK_CLASS_NAMES = new String[] { "org.eclipse.swt.graphics.GC",
                                                                                   "org.eclipse.swt.widgets.Canvas",
                                                                                   "org.eclipse.swt.widgets.Control",
                                                                                   "org.eclipse.swt.widgets.Display",
                                                                                   "org.eclipse.swt.widgets.ToolTip" };

    /*
     * Eclipse RAP Performance Patches
     */
    /**
     * Compiled binary class patching org.eclipse.swt.widgets.ToolItem in RAP
     */
    private static final String TOOL_ITEM_RAP = PATH_OF_BIN
                                                + "ToolItem.org.eclipse.rap.rwt_3.9.0.20190320-1512.binarypatch";


    /**
     * Property key to enable or disable performance improvement features in RAP
     * The property is enabled by default and it can be deactivated by providing
     * a different property value as application argument
     */
    private static final String RAP_PERFORMANCE_FEATURE = "rap.applyPerformanceFeature";

    /** The String identifying WEAWING HOOK SERVICE */
    private static final String WEAWING_HOOK_SERVICE = "org.osgi.framework.hooks.weaving.WeavingHook";

    /** The BundleContext of this PatchApplier */
    private final BundleContext bundleContext;


    /**
     * Creates a new PatchApplier
     * 
     * @param bundleContext
     *            the BundleContext
     */
    public PatchApplier(final BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    private static final void log(final String msg)
    {
        System.out.println("PatchApplier INFO : " + msg);
    }

    private static final void logError(final String msg)
    {
        System.out.println("PatchApplier ERROR: " + msg);
    }

    /**
     * Apply all applicable patches to the Eclipse Platform and other third
     * party libraries essential for the correct functioning of EUD and EUD
     * based applications.
     */
    public final void applyPatches()
    {
        log("Applying Platform Patches...");

        // create patch map containing all classes to be patched
        final Map<String, byte[]> patchClassMap = new HashMap<String, byte[]>();

        /*
         * Patch to workaround bug 485931 - Context sub-menu items appear twice
         * when using ExtensionContributionFactory.
         */
        patchMenuManager(patchClassMap);
        patchMenuManagerRAP(patchClassMap);

        /*
         * Patch for bug 486474. This needs to be done or else menu items get
         * added multiple times to MenuModel which results in incorrect behavior
         * and memory leak.
         */
        patchMenuManagerShowProcessorRAP(patchClassMap);

        /*
         * Patch for StackRenderer CoolBar wrap in RAP.
         */
        patchStackRendererRAP(patchClassMap);

        /*
         * Patch for preventing PartStack dragging - EUD-1227
         */
        patchPartDragAgent(patchClassMap);
        
        /*
         * Patch for enabling Text reskin after change in the enable state - EUD-1339
         */
        patchText(patchClassMap);

        /*
         * Patch solving memory leak introduced with Bug#539730 [GTK3] Replace
         * deprecated gdk_cairo_create() - EUD-1392
         */
        patchSWTGTKMemoryLeak(patchClassMap);

        /*
         * Apply the following patches unless the user disables the RAP
         * performance feature (apply by default)
         */
        if (isFeatureEnabled(RAP_PERFORMANCE_FEATURE))
        {
            patchToolItemRAP(patchClassMap);
        }

        /*
         * Register Weaving hook to load patched classes.
         */
        if (!patchClassMap.isEmpty())
        {
            final PatchWeavingHook pwh = new PatchWeavingHook(patchClassMap);
            final Dictionary<String, Object> props = new Hashtable<String, Object>();
            this.bundleContext.registerService(WEAWING_HOOK_SERVICE, pwh, props);
        }
        else
        {
            log(" - not patch to apply - ");
        }

        log("Completed.");
    }
    
    private final void patchSWTGTKMemoryLeak(final Map<String, byte[]> patchClassMap)
    {
        for (int i = 0; i < SWT_GTK_MEMORY_LEAK_PATCH_BINS.length; i++)
        {
            final String patchName = "Memory leak patch [" + (i + 1) + "/" + SWT_GTK_MEMORY_LEAK_PATCH_BINS.length
                                     + "]";
            final String className = SWT_GTK_MEMORY_LEAK_CLASS_NAMES[i];
            final String bundleName = "org.eclipse.swt";
            final String bundleVer = "3.109.0.v20181204-1801";
            patchClassBytecode(patchClassMap,
                               patchName,
                               className,
                               bundleName,
                               bundleVer,
                               PlatformFlags.LINUX64,
                               SWT_GTK_MEMORY_LEAK_PATCH_BINS[i]);
        }
    }

    private final void patchMenuManager(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "MenuManager patch";
        final String className = "org.eclipse.jface.action.MenuManager";
        final String bundleNam = "org.eclipse.jface";
        final String bundleVer = "3.15.0.v20181123-1505";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, NOTRAP, MENU_MANAGER_PATCH);
    }
    
    private final void patchMenuManagerRAP(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "MenuManager patch RAP";
        final String className = "org.eclipse.jface.action.MenuManager";
        final String bundleNam = "org.eclipse.rap.jface";
        final String bundleVer = "3.9.0.20190320-1512";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, RAP, MENU_MANAGER_PATCH_RAP);
    }

    private final void patchMenuManagerShowProcessorRAP(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "MenuManagerShowProcessor patch RAP";
        final String className = "org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerShowProcessor";
        final String bundleNam = "org.eclipse.e4.ui.workbench.renderers.swt";
        final String bundleVer = "0.13.0.rap-20170515-2147";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, RAP, MENU_MANAGER_SHOW_PROCESSOR_PATCH_RAP);
    }

    private final void patchStackRendererRAP(final Map<String, byte[]> patchClassMap)
    {
        String patchName = "StackRenderer Patch RAP 1";
        String className = "org.eclipse.e4.ui.workbench.renderers.swt.StackRenderer";
        final String bundleNam = "org.eclipse.e4.ui.workbench.renderers.swt";
        final String bundleVer = "0.13.0.rap-20170515-2147";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, RAP, STACKRENDERER_RAP_CLASS);
        
        patchName = "StackRenderer Patch RAP 2";
        className = "org.eclipse.e4.ui.workbench.renderers.swt.StackRenderer$10";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, RAP, STACKRENDERER10_RAP_CLASS);
    }

    private final void patchToolItemRAP(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "ToolItem patch RAP";
        final String className = "org.eclipse.swt.widgets.ToolItem";
        final String bundleNam = "org.eclipse.rap.rwt";
        final String bundleVer = "3.9.0.20190320-1512";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, RAP, TOOL_ITEM_RAP);
    }

    private final void patchPartDragAgent(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "PartDragAgent patch";
        final String className = "org.eclipse.e4.ui.workbench.addons.dndaddon.PartDragAgent";
        final String bundleNam = "org.eclipse.e4.ui.workbench.addons.swt";
        final String bundleVer = "1.3.300.v20181102-1042";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, NOTRAP, PART_DRAG_AGENT_PATCH);
    }
    
    private final void patchText(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "Text Linux patch";
        final String className = "org.eclipse.swt.widgets.Text";
        final String bundleNam = "org.eclipse.swt";
        final String bundleVer = "3.109.0.v20181204-1801";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, PlatformFlags.LINUX64, TEXT_LINUX_PATCH);
    }

    /**
     * @param patchClassMap
     *            a Map that contains the fully qualifying name of the class to
     *            be patched as key and the patched bytecode as byte array as
     *            data.
     * @param patchName
     *            a String containing a human readable name for the patch that
     *            is to be applied
     * @param className
     *            a String containing the fully qualifying name of the class to
     *            be patched
     * @param bundleName
     *            a String containing the symbolic name of the bundle to be
     *            patched
     * @param bundleVersion
     *            a String containing the version of the bundle to which the
     *            patch is applicable
     * @param platformSpec
     *            the platform applicability flags
     * @param patchBinFile
     *            a String containing the file name containing the bytecode of
     *            the patched class to be applied
     */
    private final void patchClassBytecode(final Map<String, byte[]> patchClassMap,
                                          final String patchName,
                                          final String className,
                                          final String bundleName,
                                          final String bundleVersion,
                                          final int platformSpec,
                                          final String patchBinFile)
    {
        final int majorVer = Integer.parseInt(bundleVersion.split("\\.")[0]);
        final int minorVer = Integer.parseInt(bundleVersion.split("\\.")[1]);

        /*
         * Precondition checks: Verify selected applicability criteria
         */

        // applies only to linux
        if ((platformSpec & OS_SPEC_LINUX) > 0 && !Platform.OS_LINUX.equals(Platform.getOS()))
        {
            return;
        }
        // applies only to GTK
        if ((platformSpec & WS_SPEC_GTK) > 0 && !Platform.WS_GTK.equals(Platform.getWS()))
        {
            return;
        }
        // applies only to 64bit OS
        if ((platformSpec & CPU_SPEC_64_BIT) > 0 && !Platform.ARCH_X86_64.equals(Platform.getOSArch()))
        {
            return;
        }
        // applies only if RAP
        if ((platformSpec & WS_SPEC_RAP) > 0 && Platform.getBundle("org.eclipse.rap.rwt") == null)
        {
            return;
        }
        // applies only if not RAP
        if ((platformSpec & WS_SPEC_NOTRAP) > 0 && Platform.getBundle("org.eclipse.rap.rwt") != null)
        {
            return;
        }
        // applies only if matching bundle is present
        final Bundle swtBundle = Platform.getBundle(bundleName);
        if (swtBundle == null)
        {
            return;
        }

        try
        {
            // Check compatibility before application of patch.
            if (swtBundle.getVersion().getMajor() != majorVer || swtBundle.getVersion().getMinor() != minorVer)
            {
                String msg = "Application of '" + patchName + "' failed due to incorrect " + bundleName
                             + " version in product.";
                msg += "Expected " + bundleVersion + " but found: " + swtBundle.getVersion().toString() + ".";
                logError(msg);
                return;
            }

            /*
             * Apply patch
             */
            doApplyPatch(patchName, className, patchClassMap, patchBinFile);
        }
        catch (final Exception e)
        {
            logError("applying patch " + patchName + " failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private final void doApplyPatch(final String patchName,
                                    final String className,
                                    final Map<String, byte[]> patchClassMap,
                                    final String patchedFile)
    {
        final byte[] classBytes = loadFileFromBundle(patchedFile);
        if (classBytes == null || classBytes.length <= 4)
        {
            logError("Patch file invalid: " + patchedFile);
            return;
        }

        patchClassMap.put(className, classBytes);
        log("Applied: " + patchName);
    }

    private final byte[] loadFileFromBundle(final String location)
    {
        byte[] result = null;

        try (InputStream is = FileLocator.openStream(this.bundleContext.getBundle(), new Path(location), false);
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();)
        {
            int bytesRead;
            final byte[] buffer = new byte[16384];
            while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1)
            {
                baos.write(buffer, 0, bytesRead);
            }
            baos.flush();
            result = baos.toByteArray();
        }
        catch (final Exception e)
        {
            logError("Failed to load file: " + location + " : " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Returns <code>false</code> if the user has set the property to a value
     * different from <code>true</code>. If nothing is set, returns
     * <code>true</code>
     * 
     * @param featureName
     *            the feature property to check
     * @return <code>true</code> if the property value is <code>true</code> or
     *         not set
     */
    private boolean isFeatureEnabled(final String featureName)
    {
        final String propertyValue = System.getProperty(featureName, "true").trim();
        return Boolean.valueOf(propertyValue);
    }
}
