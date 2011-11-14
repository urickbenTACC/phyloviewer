package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeIntersect")
public interface TreeIntersectService extends RemoteService {

	String intersectTree(int rootNodeId, String layoutID, double x, double y);
}
