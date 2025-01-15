package uk.ac.kcl.inf.mdd7b.flowchart.tests;

import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.junit.Test;

public class SiriusNodeTest extends SiriusTest {
	@Test
	public void doTest() {
		var odesign = loadODesignFile();

		var ownedViewpoints = odesign.getOwnedViewpoints();
		var viewPoint = ownedViewpoints.get(0);
		var ownedRepresentations = viewPoint.getOwnedRepresentations();
		var representation = ownedRepresentations.get(0);
		var diagram = (DiagramDescription) representation;

		var defaultLayer = diagram.getDefaultLayer();

		var nodeMappings = defaultLayer.getNodeMappings();
		assertHasNodeFor(nodeMappings, "Step");
		assertHasNodeFor(nodeMappings, "Fork");
		assertHasNodeFor(nodeMappings, "Join");
	}

}
