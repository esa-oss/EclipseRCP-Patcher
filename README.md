# EclipseRCP-Patcher
This repository contains one Eclipse plugin that applies patches for Eclipse RCP and RAP target platforms in order to fix known bugs or improve the behaviour of the platform components.

This plugin -called platform patcher- provides one implementation of the OSGi weaving service which allows to intercept the class loading of certain Java classes and injects the patched binary bytes on-the-fly.
 
Patches provided by this branch (4.1.0) apply the following patches for Eclipse RCP 4.10 (Eclipse 2018-12) and Eclipse RAP 3.9: <br>
  - <b>MenuManager (Eclipse 2018-12):</b> Workaround for [Eclipse bug #485931 - Context sub-menu items appear twice when using ExtensionContributionFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=485931) 
  - <b>MenuManager (Eclipse RAP 3.9):</b> Workaround for [Eclipse bug #485931 - Context sub-menu items appear twice when using ExtensionContributionFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=485931)
  - <b>MenuManagerShowProcessor (Eclipse RAP 3.9):</b> Integrates Eclipse patch for [Eclipse bug #485931 - Menu are not correctly filled calling menu service](https://bugs.eclipse.org/bugs/show_bug.cgi?id=486474)
  - <b>StackRenderer (Eclipse RAP 3.9):</b> Allows part Coolbar to wrap when there is no space for all the icons - This behaviour was missing in RAP E4 3.9
  - <b>PartDragAgent (Eclipse 2018-12):</b> Disables the possibility of dragging a full PartStack out of its container, which may corrupt the application layout.
  - <b>ToolItem (Eclipse RAP 3.9):</b> Improves the overall performance by reducing the frequency that ToolItem height calculation is requested repeatedly (~= 500 times/second)