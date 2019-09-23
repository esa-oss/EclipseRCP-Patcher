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

/**
 * Class containing declarations of the platform flags used to specify
 * applicability of patched for certain platform/OS/WS combinations.
 * 
 * @author Jean Schuetz
 * @since 3.2.5
 */
final class PlatformFlags
{
    /** The flag that indicates a 64 bit CPU */
    static final int CPU_SPEC_32_BIT = 1;

    /** The flag that indicates a 64 bit CPU */
    static final int CPU_SPEC_64_BIT = 2;

    /** The flag that indicates a Linux OS */
    static final int OS_SPEC_LINUX = 4;

    /** The flag that indicates a MAC OS */
    static final int OS_SPEC_MAC = 8;

    /** The flag that indicates a Windows OS */
    static final int OS_SPEC_WIN = 16;

    /** The flag that indicates a GTK windowing system */
    static final int WS_SPEC_GTK = 32;

    /** The flag that indicates the cocoa windowing system */
    static final int WS_SPEC_COCOA = 64;

    /** The flag that indicates the windows windowing system */
    static final int WS_SPEC_WIN = 128;

    /** The flag that indicates Eclipse RAP */
    static final int WS_SPEC_RAP = 256;

    /** The flag that indicates Eclipse RAP */
    static final int WS_SPEC_NOTRAP = 512;

    /** The system specification for arbitrary OS/Hardware */
    static final int UNSPEC = 0;

    /** The system specification for standard 64 bit linux with GTK */
    static final int LINUX64 = CPU_SPEC_64_BIT | OS_SPEC_LINUX | WS_SPEC_GTK;

    /** The system specification for an arbitrary platform that is using RAP */
    static final int RAP = WS_SPEC_RAP;

    /** The system specification for an arbitrary platform that is not using RAP */
    static final int NOTRAP = WS_SPEC_NOTRAP;


    /**
     * default constructor
     */
    private PlatformFlags()
    {
        // No instance allowed
    }
}
