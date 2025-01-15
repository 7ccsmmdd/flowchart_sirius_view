package uk.ac.kcl.inf.mdd7b.flowchart.tests;

import java.util.Arrays;

import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.junit.Test;

public class SiriusEdgeToolTest extends SiriusTest {
	@Test
	public void doTest() {
		var odesign = loadODesignFile();

		var ownedViewpoints = odesign.getOwnedViewpoints();
		var viewPoint = ownedViewpoints.get(0);
		var ownedRepresentations = viewPoint.getOwnedRepresentations();
		var representation = ownedRepresentations.get(0);
		var diagram = (DiagramDescription) representation;

		var defaultLayer = diagram.getDefaultLayer();
		var toolSections = defaultLayer.getToolSections();
		var toolSection = toolSections.get(0);
		var tools = toolSection.getOwnedTools();
		assertHasEdgeToolFor(tools, "Step", Arrays.asList("Step", "Fork", "Join"), "next");
		assertHasEdgeToolFor(tools, "Fork", Arrays.asList("Step", "Fork", "Join"), "nextSteps");
	}

}
