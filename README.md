# EclipseRCP-Patcher
This repository contains modifications on Eclipse classes in order to provide known patches or new functionalities.

Each branch contains one Eclipse plugin different versions and each version is meant to patch a different Eclipse release (e.g. branch 3.2.5 contains patches for Eclipse 4.4.1 - Luna). The plugin, known as the platform patcher, contains the following elements:
  - A class implementing service "org.osgi.framework.hooks.weaving.WeavingHook"
  - A class that selects what patches are applicable for the current runtime environment and that registers the OSGi weaving service
  - A set of binaries of the patches that will be provided to the weaving service
  - The source code of the patch files in the form of Java classes. These Java classes are modifications of Eclipse classes and they can be found at https://www.eclipse.org/downloads/
