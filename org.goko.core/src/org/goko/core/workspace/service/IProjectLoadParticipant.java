package org.goko.core.workspace.service;

import org.eclipse.core.runtime.IProgressMonitor;
import org.goko.core.common.exception.GkException;
import org.goko.core.workspace.io.LoadContext;
import org.goko.core.workspace.io.XmlProjectContainer;

/**
 * @author PsyKo
 * @date 9 d�c. 2015
 */
public interface IProjectLoadParticipant {
	
	int getPriority();
	
	void clearContent() throws GkException;
	
	void load(LoadContext context, XmlProjectContainer container, IProgressMonitor monitor) throws GkException;
	
	String getContainerType();

}
