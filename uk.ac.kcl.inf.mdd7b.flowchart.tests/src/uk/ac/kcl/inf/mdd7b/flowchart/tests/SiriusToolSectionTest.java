package uk.ac.kcl.inf.mdd7b.flowchart.tests;

import static org.junit.Assert.assertNotNull;

import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.junit.Test;

public class SiriusToolSectionTest extends SiriusTest {
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
		assertNotNull("Was expecting to find a tool section for the creation tools", toolSection);
	}

}
