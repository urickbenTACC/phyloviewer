package org.iplantc.iptol.server;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.iplantc.treedata.model.File;
import org.iplantc.treedata.model.Matrix;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;

public class ExtractMatrixInfoTransformer extends AbstractTransformer {

	@SuppressWarnings("unchecked")
	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		Collection<Matrix> matrices;
		if (arg0 instanceof Collection<?>) {
			matrices = (Collection<Matrix>)arg0;
		} else if (arg0 instanceof File) {
			matrices = ((File)arg0).getMatrices();
		} else {
			throw new TransformerException(MessageFactory.createStaticMessage("Received object that was not a File or list of Matrices"));
		}
		List<MatrixInfo> matrixInfos = new LinkedList<MatrixInfo>();

		for (Matrix matrix : matrices) {
			MatrixInfo matrixInfo = new MatrixInfo();
			matrixInfo.setId(matrix.getId());
			matrixInfo.setFilename(matrix.getFile().getName());
			matrixInfo.setUploaded(matrix.getFile().getUploaded().toString());
			matrixInfos.add(matrixInfo);
		}

		return matrixInfos;
	}
}
