package com.cisco.applicationprofiler.view;

import java.util.ArrayList;

import com.cisco.applicationprofiler.ui.models.AciSizerModelUi;

public class ViewNodes {
	private ArrayList<AciSizerModelUi> nodes = new ArrayList<AciSizerModelUi>();
	
	private ArrayList<ViewNodeConnection> connections = new ArrayList<ViewNodeConnection>();
	public ArrayList<ViewNodeConnection> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<ViewNodeConnection> connections) {
		this.connections = connections;
	}

	/**
	 * @return the nodes
	 */
	public ArrayList<AciSizerModelUi> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(ArrayList<AciSizerModelUi> nodes) {
		this.nodes = nodes;
	}

	
	
}
