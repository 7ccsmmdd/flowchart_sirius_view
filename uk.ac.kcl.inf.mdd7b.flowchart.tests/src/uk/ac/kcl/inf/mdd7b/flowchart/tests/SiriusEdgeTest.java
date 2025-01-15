package uk.ac.kcl.inf.mdd7b.flowchart.tests;

import java.util.Arrays;

import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.junit.Test;

public class SiriusEdgeTest extends SiriusTest {
	@Test
	public void doTest() {
		var odesign = loadODesignFile();

		var ownedViewpoints = odesign.getOwnedViewpoints();
		var viewPoint = ownedViewpoints.get(0);
		var ownedRepresentations = viewPoint.getOwnedRepresentations();
		var representation = ownedRepresentations.get(0);
		var diagram = (DiagramDescription) representation;

		var defaultLayer = diagram.getDefaultLayer();

		var edgeMappings = defaultLayer.getEdgeMappings();
		assertHasEdgeFor(edgeMappings, "Step", Arrays.asList("Step", "Fork", "Join"), "next");
		assertHasEdgeFor(edgeMappings, "Fork", Arrays.asList("Step", "Fork", "Join"), "nextSteps");
	}

}
