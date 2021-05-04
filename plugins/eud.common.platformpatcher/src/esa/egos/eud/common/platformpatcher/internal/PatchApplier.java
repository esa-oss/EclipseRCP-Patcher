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

import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.CPU_SPEC_64_BIT;
import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.LINUX64;
import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.NOTRAP;
import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.OS_SPEC_LINUX;
import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.RAP;
import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.UNSPEC;
import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.WS_SPEC_GTK;
import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.WS_SPEC_NOTRAP;
import static esa.egos.eud.common.platformpatcher.internal.PlatformFlags.WS_SPEC_RAP;

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
 * Class containing the functionality to apply all patches applicable to the
 * currently running Eclipse Platform and other third party libraries.
 * 
 * @author Jean Schuetz
 * @since 3.1.7
 */
public final class PatchApplier
{
    /** The base path where all patch libraries are located within this plugin. */
    private static final String PATH_OF_BIN = "patchedBin/";

    private static final String PARTSERVICEIMPL_PATCH = PATH_OF_BIN
            + "PartServiceImpl.org.eclipse.e4.ui.workbench_1.2.1.v20140901-1244.binarypatch";
    
    /** Compiled binary class patching org.eclipse.swt.windgets.TableItem */
    private static final String TABLE_ITEM_LINUX_GTK_64_PATCH = PATH_OF_BIN
                                                                + "TableItem.swt.gtk.linux.x86_64_3.103.1.v20140903-1947.binarypatch";

    /** Compiled binary class patching org.eclipse.swt.windgets.Table */
    private static final String TABLE_LINUX_GTK_64_PATCH = PATH_OF_BIN
                                                           + "Table.swt.gtk.linux.x86_64_3.103.1.v20140903-1947.binarypatch";

    /** Compiled binary class patching org.eclipse.swt.windgets.Button */
    private static final String BUTTON_LINUX_GTK_64_PATCH = PATH_OF_BIN
                                                            + "Button.swt.gtk.linux.x86_64_3.103.1.v20140903-1947.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.css.core.impl.engine.AbstractCSSEngine
     */
    private static final String ABSTRACT_CSS_ENGINE_PATCH = PATH_OF_BIN
                                                            + "AbstractCSSEngine_0.10.100.v20140424-2042.binarypatch";

    /** Compiled binary class patching org.eclipse.jface.action.MenuManager */
    private static final String MENU_MANAGER_PATCH = PATH_OF_BIN + "MenuManager_3.10.1.v20140813-1009.binarypatch";

    /**
     * Compiled binary class patching org.eclipse.jface.action.MenuManager for
     * the RAP platform
     */
    private static final String MENU_MANAGER_PATCH_RAP = PATH_OF_BIN
                                                         + "MenuManager_RAP_3.4.0.20171018-1122.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerShowProcessor
     */
    private static final String MENU_MANAGER_SHOW_PROCESSOR_PATCH = PATH_OF_BIN
                                                                    + "MenuManagerShowProcessor_0.12.1.v20140903-1023.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer$10$1
     */
    private static final String MENU_MANAGER_SHOW_PROCESSOR_PATCH_RAP = PATH_OF_BIN
                                                                        + "MenuManagerShowProcessor_RAP_0.13.0.rap-20170515-2147.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.birt.report.model.elements.GridItem
     */
    private static final String GRID_ITEM_BIRT_REPORT_MODEL = PATH_OF_BIN
                                                              + "GridItem.birt.report.model_4.4.1.v201409160530.binarypatch";

    /**
     * Compiled binary org.eclipse.e4.ui.workbench.addons.dndaddon.PartDragAgent
     * */
    private static final String PART_DRAG_AGENT_PATCH = PATH_OF_BIN
                                                        + "PartDragAgent.org.eclipse.e4.ui.workbench.addons.swt_1.1.1.v20140903-0821.binarypatch";

    /*
     * Eclipse View Performance Patch
     */
    /**
     * Compiled binary class patching
     * org.eclipse.e4.core.internal.contexts.EclipseContext
     */
    private static final String VIEW_SWITCH_PERF_ECLIPSECENTEXT = PATH_OF_BIN
                                                                  + "EclipseContext.org.eclipse.e4.core.contexts_1.3.100.v20140407-1019.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.core.internal.contexts.TrackableComputationExt
     */
    private static final String VIEW_SWITCH_PERF_TRACKABLECOMPUTATIONEXT = PATH_OF_BIN
                                                                           + "TrackableComputationExt.org.eclipse.e4.core.contexts_1.3.100.v20140407-1019.binarypatch";

    /**
     * Compiled binary class patching org.eclipse.e4.core.contexts.RunAndTrack
     */
    private static final String VIEW_SWITCH_PERF_RUNANDTRACK = PATH_OF_BIN
                                                               + "RunAndTrack.org.eclipse.e4.core.contexts_1.3.100.v20140407-1019.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer
     */
    private static final String VIEW_SWITCH_PERF_TOOLBARMANAGERRENDERER = PATH_OF_BIN
                                                                          + "ToolBarManagerRenderer.org.eclipse.e4.ui.workbench.renderers.swt_0.12.1.v20140903-1023.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer$10
     */
    private static final String VIEW_SWITCH_PERF_TOOLBARMANAGERRENDERER10 = PATH_OF_BIN
                                                                            + "ToolBarManagerRenderer$10.org.eclipse.e4.ui.workbench.renderers.swt_0.12.1.v20140903-1023.binarypatch";

    /**
     * Compiled binary class patching
     * org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer$10$1
     */
    private static final String VIEW_SWITCH_PERF_TOOLBARMANAGERRENDERER10_1 = PATH_OF_BIN
                                                                              + "ToolBarManagerRenderer$10$1.org.eclipse.e4.ui.workbench.renderers.swt_0.12.1.v20140903-1023.binarypatch";

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

    /*
     * Eclipse View Performance Patch END
     */

    /*
     * Eclipse RAP Performance Patches
     */
    /**
     * Compiled binary class patching org.eclipse.swt.widgets.ToolItem in RAP
     */
    private static final String TOOL_ITEM_RAP = PATH_OF_BIN
                                                + "ToolItem.org.eclipse.rap.rwt_3.4.0.20171130-0837.binarypatch";

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
         * Patch for bug in Eclipse SWT: TableItem.getBounds() methods return
         * false values under Linux/GTK.
         */
        patchTableItem(patchClassMap);
        patchTable(patchClassMap);

        /*
         * Patch for Bug in Eclipse SWT: Push Button has different size than
         * Toggle Button.
         */
        patchButton(patchClassMap);

        /*
         * Patch that enables the use non-closable views. It includes/adapts the
         * following Eclipse patches:
         * 
         * - Bug 553338 - One multiple instance view cannot be shown at right
         * place based on placeholder when some additional view is added to the
         * current perspective
         * 
         * - Bug 516403 - [Compatibility][PerspectiveExtension] closeable
         * ignored on placeholders
         * 
         * - Bug 529182 - NPE on opening second instance of call hierarchy from
         * editor
         */
        patchPartServiceImpl(patchClassMap);
        
        /*
         * Patch for eclipse bug 423106. Setting cell span on BIRT reports
         * causes NPE.
         */
        patchGridItem(patchClassMap);

        /*
         * Patch for bug 506120 - [CSS] NPE if CSS styling is disabled.
         */
        patchAbstractCSSEngine(patchClassMap);

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
        patchMenuManagerShowProcessor(patchClassMap);
        patchMenuManagerShowProcessorRAP(patchClassMap);

        /*
         * Performance Patch for Eclipse Part switch EUD-1145.
         */
        patchPartSwitchPerformance(patchClassMap);

        /*
         * Patch for StackRenderer CoolBar wrap in RAP.
         */
        patchStackRendererRAP(patchClassMap);

        /*
         * Patch for preventing PartStack dragging - EUD-1227
         */
        patchPartDragAgent(patchClassMap);

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
    
    private final void patchPartServiceImpl(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "PartServiceImpl patch";
        final String className = "org.eclipse.e4.ui.internal.workbench.PartServiceImpl";
        final String bundleNam = "org.eclipse.e4.ui.workbench";
        final String bundleVer = "1.2.1.v20140901-1244";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, PARTSERVICEIMPL_PATCH);
    }

    private final void patchGridItem(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "GridItem patch";
        final String className = "org.eclipse.birt.report.model.elements.GridItem";
        final String bundleNam = "org.eclipse.birt.report.model";
        final String bundleVer = "4.4.1.v201409160530";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, GRID_ITEM_BIRT_REPORT_MODEL);
    }

    private final void patchTableItem(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "TableItem patch";
        final String className = "org.eclipse.swt.widgets.TableItem";
        final String bundleNam = "org.eclipse.swt";
        final String bundleVer = "3.103.1.v20140903-1947";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, LINUX64, TABLE_ITEM_LINUX_GTK_64_PATCH);
    }

    private final void patchTable(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "Table patch";
        final String className = "org.eclipse.swt.widgets.Table";
        final String bundleNam = "org.eclipse.swt";
        final String bundleVer = "3.103.1.v20140903-1947";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, LINUX64, TABLE_LINUX_GTK_64_PATCH);
    }

    private final void patchButton(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "Button patch";
        final String className = "org.eclipse.swt.widgets.Button";
        final String bundleNam = "org.eclipse.swt";
        final String bundleVer = "3.103.1.v20140903-1947";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, LINUX64, BUTTON_LINUX_GTK_64_PATCH);
    }

    private final void patchAbstractCSSEngine(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "AbstractCSSEngine patch";
        final String className = "org.eclipse.e4.ui.css.core.impl.engine.AbstractCSSEngine";
        final String bundleNam = "org.eclipse.e4.ui.css.core";
        final String bundleVer = "0.10.100.v20140424-2042";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, NOTRAP, ABSTRACT_CSS_ENGINE_PATCH);
    }

    private final void patchMenuManager(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "MenuManager patch";
        final String className = "org.eclipse.jface.action.MenuManager";
        final String bundleNam = "org.eclipse.jface";
        final String bundleVer = "3.10.1.v20140813-1009";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, NOTRAP, MENU_MANAGER_PATCH);
    }

    private final void patchMenuManagerRAP(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "MenuManager patch RAP";
        final String className = "org.eclipse.jface.action.MenuManager";
        final String bundleNam = "org.eclipse.rap.jface";
        final String bundleVer = "3.4.0.20171018-1122";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, RAP, MENU_MANAGER_PATCH_RAP);
    }

    private final void patchMenuManagerShowProcessor(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "MenuManagerShowProcessor patch";
        final String className = "org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerShowProcessor";
        final String bundleNam = "org.eclipse.e4.ui.workbench.renderers.swt";
        final String bundleVer = "0.12.1.v20140903-1023";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, NOTRAP, MENU_MANAGER_SHOW_PROCESSOR_PATCH);
    }

    private final void patchMenuManagerShowProcessorRAP(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "MenuManagerShowProcessor patch RAP";
        final String className = "org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerShowProcessor";
        final String bundleNam = "org.eclipse.e4.ui.workbench.renderers.swt";
        final String bundleVer = "0.13.0.rap-20170515-2147";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, RAP, MENU_MANAGER_SHOW_PROCESSOR_PATCH_RAP);
    }

    private final void patchPartSwitchPerformance(final Map<String, byte[]> patchClassMap)
    {
        String patchName = "PartSwitchPerformance patch 1";
        String className = "org.eclipse.e4.core.internal.contexts.EclipseContext";
        final String bundleNam = "org.eclipse.e4.core.contexts";
        final String bundleVer = "1.3.100.v20140407-1019";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, VIEW_SWITCH_PERF_ECLIPSECENTEXT);
        
        patchName = "PartSwitchPerformance patch 2";
        className = "org.eclipse.e4.core.internal.contexts.TrackableComputationExt";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, VIEW_SWITCH_PERF_TRACKABLECOMPUTATIONEXT);

        patchName = "PartSwitchPerformance patch 3";
        className = "org.eclipse.e4.core.contexts.RunAndTrack";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, VIEW_SWITCH_PERF_RUNANDTRACK);

        patchName = "PartSwitchPerformance patch 4";
        className = "org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, VIEW_SWITCH_PERF_TOOLBARMANAGERRENDERER);

        patchName = "PartSwitchPerformance patch 5";
        className = "org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer$10";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, VIEW_SWITCH_PERF_TOOLBARMANAGERRENDERER10);

        patchName = "PartSwitchPerformance patch 6";
        className = "org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer$10$1";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, VIEW_SWITCH_PERF_TOOLBARMANAGERRENDERER10_1);
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
        final String bundleVer = "3.4.0.20171130-0837";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, RAP, TOOL_ITEM_RAP);
    }

    private final void patchPartDragAgent(final Map<String, byte[]> patchClassMap)
    {
        final String patchName = "PartDragAgent patch";
        final String className = "org.eclipse.e4.ui.workbench.addons.dndaddon.PartDragAgent";
        final String bundleNam = "org.eclipse.e4.ui.workbench.addons.swt";
        final String bundleVer = "1.1.1.v20140903-0821";
        patchClassBytecode(patchClassMap, patchName, className, bundleNam, bundleVer, UNSPEC, PART_DRAG_AGENT_PATCH);
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
    private final boolean isFeatureEnabled(final String featureName)
    {
        final String propertyValue = System.getProperty(featureName, "true").trim();
        return Boolean.valueOf(propertyValue);
    }
}
