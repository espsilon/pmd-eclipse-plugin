/*
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

public class FolderRecord extends AbstractPMDRecord {
    private final IFolder folder;
    private final ProjectRecord parent;
    private AbstractPMDRecord[] children;

    public FolderRecord(IFolder folder, ProjectRecord record) {
        super();

        if (folder == null) {
            throw new IllegalArgumentException("folder cannot be null");
        }

        if (record == null) {
            throw new IllegalArgumentException("record cannot be null");
        }

        this.folder = folder;
        this.parent = record;
        this.children = createChildren();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getParent()
     */
    @Override
    public AbstractPMDRecord getParent() {
        return this.parent;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getChildren()
     */
    @Override
    public AbstractPMDRecord[] getChildren() {
        return children; // NOPMD by Herlin on 09/10/06 00:22
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getResource()
     */
    @Override
    public IResource getResource() {
        return (IResource) folder;
    }

    /**
     * Gets the Package's Fragment
     *
     * @return the Fragment
     */
    public IFolder getFolder() {
        return folder;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#createChildren()
     */
    @Override
    protected final AbstractPMDRecord[] createChildren() {
        List<FileRecord> fileList = new ArrayList<FileRecord>();
        try {
            for (IResource member : folder.members()) {
                if (member != null) {
                    fileList.add(new FileRecord(member, this)); // NOPMD
                    // by
                    // Herlin
                    // on
                    // 09/10/06
                    // 00:25
                }
            }
        } catch (CoreException ce) {
            PMDPlugin.getDefault().logError(StringKeys.ERROR_CORE_EXCEPTION + this.toString(), ce);
        }

        return fileList.toArray(new AbstractPMDRecord[fileList.size()]);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource)
     */
    @Override
    public AbstractPMDRecord addResource(IResource resource) {
        //final ICompilationUnit unit = this.packageFragment.getCompilationUnit(resource.getName());
        FileRecord file = null;

        // TODO This should be more question of whether PMD is interested in the File!
        // we want the File to be a java-File
//        if (unit != null) {
            // we create a new FileRecord and add it to the List
            file = new FileRecord(resource, this);
            final List<AbstractPMDRecord> files = getChildrenAsList();
            files.add(file);

            children = new AbstractPMDRecord[files.size()];
            files.toArray(this.children);
//        }

        return file;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource)
     */
    @Override
    public AbstractPMDRecord removeResource(IResource resource) {
        final List<AbstractPMDRecord> files = getChildrenAsList();
        AbstractPMDRecord removedFile = null;
        boolean removed = false;

        for (int i = 0; i < files.size() && !removed; i++) {
            final AbstractPMDRecord file = files.get(i);

            // if the file is in here, remove it
            if (file.getResource().equals(resource)) {
                files.remove(i);

                children = new AbstractPMDRecord[files.size()]; // NOPMD
                // by
                // Herlin
                // on
                // 09/10/06
                // 00:31
                files.toArray(this.children);
                removed = true;
                removedFile = file;
            }
        }

        return removedFile;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getName()
     */
    @Override
    public String getName() {
        return folder.getName();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getResourceType()
     */
    @Override
    public int getResourceType() {
        return TYPE_PACKAGE;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof FolderRecord ? folder.equals(((FolderRecord) obj).folder) : false;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return folder.hashCode();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getNumberOfViolationsToPriority(int)
     */
    @Override
    public int getNumberOfViolationsToPriority(int prio, boolean invertMarkerAndFileRecords) {
        int number = 0;
        for (AbstractPMDRecord element : children) {
            number += element.getNumberOfViolationsToPriority(prio, false);
        }

        return number;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getLOC()
     */
    @Override
    public int getLOC() {
        int number = 0;
        for (AbstractPMDRecord element : children) {
            number += element.getLOC();
        }

        return number;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getNumberOfMethods()
     */
    @Override
    public int getNumberOfMethods() {
        int number = 0;
        for (AbstractPMDRecord element : children) {
            number += element.getNumberOfMethods();
        }

        return number;
    }

}
