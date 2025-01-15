package uk.ac.kcl.inf.mdd7b.flowchart.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.junit.Test;

public class SiriusBaseTest extends SiriusTest {
	@Test
	public void doTest() {
		var odesign = loadODesignFile();

		assertNotNull("Was expecting to find an odesign file with a viewpoint specification in it", odesign);
		var ownedViewpoints = odesign.getOwnedViewpoints();
		assertEquals("Was expecting to find an odesign file with exactly one viewpoint specification in it", 1,
				ownedViewpoints.size());

		var viewPoint = ownedViewpoints.get(0);
		var ownedRepresentations = viewPoint.getOwnedRepresentations();
		assertEquals("Was expecting to find exactly one representation specification", 1, ownedRepresentations.size());
		var representation = ownedRepresentations.get(0);
		assertTrue("Representation should be a Diagram.", representation instanceof DiagramDescription);
		var diagram = (DiagramDescription) representation;
		assertEquals("Diagram should be for FlowChart class.", "FlowChart", diagram.getDomainClass());
//		var metamodels = diagram.getMetamodel();
//		metamodels.get(0);
//		assertTrue("Diagram should reference flowchart metamodel.", metamodels.contains(FlowchartPackage.eINSTANCE));
	}

}
