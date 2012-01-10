package org.iplantc.phyloviewer.shared.model.metadata;

import org.iplantc.phyloviewer.shared.model.INode;

public class AllPassFilter implements ValueMap<INode, Boolean>
{
	@Override
	public Boolean get(INode node)
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return "All nodes";
	}
}