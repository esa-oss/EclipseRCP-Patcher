# EclipseRCP-Patcher
This repository contains one Eclipse plugin that applies patches for Eclipse RCP and RAP target platforms in order to fix known bugs or improve the behaviour of the platform components.

This plugin -called platform patcher- provides one implementation of the OSGi weaving service which allows to intercept the class loading of certain Java classes and injects the patched binary bytes on-the-fly.

This repository contains different branches for different Eclipse platforms as follows:
  - Branch 3.2.5 - Patches Eclipse RCP 4.4.1 (Luna) and Eclipse RAP 3.4
  - Branch 4.1.0 - Patches Eclipse RCP 4.10 (Eclipse 2018-12) and Eclipse RAP 3.9
  - Branch 4.1.7 - Patches Eclipse RCP 4.10 (Eclipse 2018-12) and Eclipse RAP 3.9, including additional patches

Patches provided by Branch 3.2.5: <br>
  - <b>TableItem (Eclipse 4.4.1):</b> Fixes miscalculation in method <code>TableItem#getBounds()</code> under Linux/GTK2 (SLES12) 
  - <b>Table (Eclipse 4.4.1):</b> Fixes miscalculation in method <code>Table.getItemHeight()</code> under Linux/GTK2. Also removes visibility of vertical lines when <code>Table#setLinesVisible(<b><em>true</em></b>)</code> is invoked, which slows down drastically the table performance under Linux/GTK2. (SLES12)
  - <b>Button (Eclipse 4.4.1):</b> Fixes toggle button size miscalculation. Improves general button appearance by moderating the relief. These changes apply to Linux/GTK2 (SLES12)
  - <b>BIRT - GridItem (Eclipse-BIRT 4.4.1):</b> Integrates Eclipse patch for [Eclipse bug #423106 - text wrapping problem in merged cells](https://bugs.eclipse.org/bugs/show_bug.cgi?id=423106)
  - <b>AbstractCSSEngine (Eclipse 4.4.1):</b> Integrates Eclipse patch for [Eclipse bug #506120 - [CSS] NPE if CSS styling is disabled](https://bugs.eclipse.org/bugs/show_bug.cgi?id=506120)
  - <b>MenuManager (Eclipse 4.4.1):</b> Workaround for [Eclipse bug #485931 - Context sub-menu items appear twice when using ExtensionContributionFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=485931) 
  - <b>MenuManager (Eclipse RAP 3.4):</b> Workaround for [Eclipse bug #485931 - Context sub-menu items appear twice when using ExtensionContributionFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=485931)
  - <b>MenuManagerShowProcessor (Eclipse 4.4.1):</b> Integrates Eclipse patch for [Eclipse bug #485931 - Menu are not correctly filled calling menu service](https://bugs.eclipse.org/bugs/show_bug.cgi?id=486474)
  - <b>MenuManagerShowProcessor (Eclipse RAP 3.4):</b> Integrates Eclipse patch for [Eclipse bug #485931 - Menu are not correctly filled calling menu service](https://bugs.eclipse.org/bugs/show_bug.cgi?id=486474)
  - <b>Part switch performance (Eclipse 4.4.1):</b> Increases the performance while switching the active part when a big amount of parts are open simultaneously.
  - <b>StackRenderer (Eclipse RAP 3.4):</b> Allows part Coolbar to wrap when there is no space for all the icons - This behaviour was missing in RAP E4 3.9
  - <b>PartDragAgent (Eclipse 4.4.1):</b> Disables the possibility of dragging a full PartStack out of its container, which may corrupt the application layout.
  - <b>ToolItem (Eclipse RAP 3.4):</b> Improves the overall performance by reducing the frequency that ToolItem height calculation is requested repeatedly (~= 500 times/second)

  
Patches provided by Branch 4.1.0: <br>
  - <b>MenuManager (Eclipse 2018-12):</b> Workaround for [Eclipse bug #485931 - Context sub-menu items appear twice when using ExtensionContributionFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=485931) 
  - <b>MenuManager (Eclipse RAP 3.9):</b> Workaround for [Eclipse bug #485931 - Context sub-menu items appear twice when using ExtensionContributionFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=485931)
  - <b>MenuManagerShowProcessor (Eclipse RAP 3.9):</b> Integrates Eclipse patch for [Eclipse bug #485931 - Menu are not correctly filled calling menu service](https://bugs.eclipse.org/bugs/show_bug.cgi?id=486474)
  - <b>StackRenderer (Eclipse RAP 3.9):</b> Allows part Coolbar to wrap when there is no space for all the icons - This behaviour was missing in RAP E4 3.9
  - <b>PartDragAgent (Eclipse 2018-12):</b> Disables the possibility of dragging a full PartStack out of its container, which may corrupt the application layout.
  - <b>ToolItem (Eclipse RAP 3.9):</b> Improves the overall performance by reducing the frequency that ToolItem height calculation is requested repeatedly (~= 500 times/second)<

Patches provided by Branch 4.1.7: <br>
  - <b>MenuManager (Eclipse 2018-12):</b> Workaround for [Eclipse bug #485931 - Context sub-menu items appear twice when using ExtensionContributionFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=485931) 
  - <b>MenuManager (Eclipse RAP 3.9):</b> Workaround for [Eclipse bug #485931 - Context sub-menu items appear twice when using ExtensionContributionFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=485931)
  - <b>MenuManagerShowProcessor (Eclipse RAP 3.9):</b> Integrates Eclipse patch for [Eclipse bug #485931 - Menu are not correctly filled calling menu service](https://bugs.eclipse.org/bugs/show_bug.cgi?id=486474)
  - <b>StackRenderer (Eclipse RAP 3.9):</b> Allows part Coolbar to wrap when there is no space for all the icons - This behaviour was missing in RAP E4 3.9
  - <b>PartDragAgent (Eclipse 2018-12):</b> Disables the possibility of dragging a full PartStack out of its container, which may corrupt the application layout.
  - <b>ToolItem (Eclipse RAP 3.9):</b> Improves the overall performance by reducing the frequency that ToolItem height calculation is requested repeatedly (~= 500 times/second)
  - <b>Text (Eclipse 2018-12):</b> Improves the look and feel of  disabled Text widgets in Linux GTK when CSS styling is applied.
  - <b>GC (Eclipse 2018-12):</b> Fixes memory leak in SLES15 SWT Linux GTK, by reverting changes introduced with fix for [Eclipse bug #485931 - [GTK3] Replace deprecated gdk_cairo_create()](https://bugs.eclipse.org/bugs/show_bug.cgi?id=539730)
  - <b>Canvas (Eclipse 2018-12):</b> Fixes memory leak in SLES15 SWT Linux GTK, by reverting changes introduced with fix for [Eclipse bug #485931 - [GTK3] Replace deprecated gdk_cairo_create()](https://bugs.eclipse.org/bugs/show_bug.cgi?id=539730)
  - <b>Control (Eclipse 2018-12):</b> Fixes memory leak in SLES15 SWT Linux GTK, by reverting changes introduced with fix for [Eclipse bug #485931 - [GTK3] Replace deprecated gdk_cairo_create()](https://bugs.eclipse.org/bugs/show_bug.cgi?id=539730)
  - <b>Display (Eclipse 2018-12):</b> Fixes memory leak in SLES15 SWT Linux GTK, by reverting changes introduced with fix for [Eclipse bug #485931 - [GTK3] Replace deprecated gdk_cairo_create()](https://bugs.eclipse.org/bugs/show_bug.cgi?id=539730)
  - <b>ToolTip (Eclipse 2018-12):</b> Fixes memory leak in SLES15 SWT Linux GTK, by reverting changes introduced with fix for [Eclipse bug #485931 - [GTK3] Replace deprecated gdk_cairo_create()](https://bugs.eclipse.org/bugs/show_bug.cgi?id=539730)
