package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public class CameraCircular extends Camera {
	static final double labelMargin = 150;

	public Camera create() {
		return new CameraCircular();
	}
	
	public Matrix33 getMatrix() {
		double canvasScale = Math.min(getCanvasWidth(),getCanvasHeight());
		Matrix33 m = Matrix33.makeTranslate(labelMargin / 2.0, labelMargin / 2.0).multiply(Matrix33.makeScale(canvasScale - labelMargin, canvasScale - labelMargin));
		return m.multiply(this.getViewMatrix());
	}

	@Override
	public void zoomToFitSubtree(INode node, ILayout layout) {
		Vector2 position = layout.getPosition(node);
		Box2D bounds = layout.getBoundingBox(node);
		
		double xFactor = 1.0 / bounds.getWidth();
		double yFactor = 1.0 / bounds.getHeight();
		double factor = Math.min(xFactor, yFactor);
		this.zoom(position.getX(), position.getY(), factor, factor);
	}
}
