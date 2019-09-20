# EclipseRCP-Patcher
This repository contains one Eclipse plugin that applies patches for Eclipse RCP and RAP target platforms in order to fix known bugs or improve the behaviour of the platform components.

This plugin -called platform patcher- provides one implementation of the OSGi weaving service which allows to intercept the class loading of certain Java classes and injects the patched binary bytes on-the-fly.

Patches provided by this branch (3.2.5) apply the following patches for Eclipse RCP 4.4.1 (Luna) and Eclipse RAP 3.4: <br>
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
  - <b>StackRenderer (Eclipse RAP 3.4):</b> Allows part Coolbar to wrap when there is no space for all the icons - This behaviour was missing in RAP E4 3.4
  - <b>PartDragAgent (Eclipse 4.4.1):</b> Disables the possibility of dragging a full PartStack out of its container, which may corrupt the application layout.
  - <b>ToolItem (Eclipse RAP 3.4):</b> Improves the overall performance by reducing the frequency that ToolItem height calculation is requested repeatedly (~= 500 times/second)