/*******************************************************************************
 * Copyright (c) 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.core.contexts;

import java.util.Stack;

import org.eclipse.e4.core.internal.contexts.Computation;
import org.eclipse.e4.core.internal.contexts.EclipseContext;

/**
 * Instances of this class contain behavior that is executed within an
 * {@link IEclipseContext}. The context records all values accessed by this
 * object, and will re-evaluate this runnable whenever any accessed value changes.
 * 
 * @see IEclipseContext#runAndTrack(RunAndTrack)
 * @since 1.3
 */
abstract public class RunAndTrack {

    ///############### START PATCH CONTRIBUTION ###############
    static
    {
        System.out.println("ViewPart switch performance patch 3/4 effective.");
    }
    ///###############  END PATCH CONTRIBUTION  ###############
    
    /**
     * Creates a new instance of trackable computation
     */
    public RunAndTrack() {
        // placeholder
    }

    /**
     * This method is initially called by the framework when an instance of this
     * class is associated with the context via {@link IEclipseContext#runAndTrack(RunAndTrack)}.
     * <p>
     * After the initial call this method is executed when one or more values it retrieved 
     * from the context change.
     * </p>
     * @param context modified context
     * @return <code>true</code> to continue to be called on updates; <code>false</code> otherwise
     */
    abstract public boolean changed(IEclipseContext context);
    
    
    ///############### START PATCH CONTRIBUTION ###############
    /**
     * As {@link #changed(IEclipseContext)} but with and additional argument
     * that allows to skip updates that have already been done in this 
     * change/update cycle.
     * @param context modified context
     * @param skipRedundantUpdates true to skip expensive redundant updates
     * @return <code>true</code> to continue to be called on updates; <code>false</code> otherwise
     */
    public boolean changedWithSkipRedundantUpdates(IEclipseContext context, boolean skipRedundantUpdates)
    {
        return changed(context);
    }
    ///###############  END PATCH CONTRIBUTION  ###############

    /**
     * Use this method to wrap calls to external code. For instance, while in {@link #changed(IEclipseContext)}.
     * consider calling listeners from this method. This wrapper will pause dependency recording while
     * in the 3rd party code, reducing potential dependency circularity issues.
     * @param runnable
     */
    synchronized protected void runExternalCode(Runnable runnable) {
        Stack<Computation> computationStack = EclipseContext.getCalculatedComputations();
        computationStack.push(null);
        try {
            runnable.run();
        } finally {
            computationStack.pop();
        }
    }

}
