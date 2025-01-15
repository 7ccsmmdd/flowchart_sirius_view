package uk.ac.kcl.inf.mdd7b.flowchart.tests;

import static org.eclipse.xtext.xbase.lib.IterableExtensions.exists;
import static org.eclipse.xtext.xbase.lib.IterableExtensions.filter;
import static org.eclipse.xtext.xbase.lib.IterableExtensions.forall;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.sirius.diagram.DiagramPackage;
import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.eclipse.sirius.diagram.description.NodeMapping;
import org.eclipse.sirius.diagram.description.tool.EdgeCreationDescription;
import org.eclipse.sirius.diagram.description.tool.NodeCreationDescription;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.eclipse.sirius.viewpoint.description.Group;
import org.eclipse.sirius.viewpoint.description.tool.ChangeContext;
import org.eclipse.sirius.viewpoint.description.tool.CreateInstance;
import org.eclipse.sirius.viewpoint.description.tool.SetValue;
import org.eclipse.sirius.viewpoint.description.tool.ToolEntry;

public class SiriusTest {

	protected Group loadODesignFile() {
		var rs = new ResourceSetImpl();
		// Register Ecore
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		var ecorePackage = EcorePackage.eINSTANCE;

		// Register Sirius stuff
		var diagramPackage = DiagramPackage.eINSTANCE;
		var descriptionPackage = DescriptionPackage.eINSTANCE;
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("odesign", new XMLResourceFactoryImpl());

		var metamodel = rs.getResource(URI.createFileURI("../uk.ac.kcl.inf.mdd7b.flowchart/model/flowchart.ecore"),
				true);
		var rODesign = rs.getResource(
				URI.createFileURI("../uk.ac.kcl.inf.mdd7b.flowchart.design/description/flowchart.odesign"), true);
		EcoreUtil.resolveAll(rs);

		return (Group) rODesign.getContents().get(0);
	}

	protected void assertHasEdgeToolFor(EList<ToolEntry> tools, String srcClassName, List<String> tgtClassNames,
			String tgtFeature) {
		assertTrue("Was expecting an edge-creation tool for edge " + tgtFeature,
				exists(filter(tools, EdgeCreationDescription.class), ecd -> {
					return exists(ecd.getEdgeMappings(), em -> {
						return isEdge(em, srcClassName, tgtClassNames, tgtFeature);
					}) && isEdgeCreationTool(ecd, tgtFeature);
				}));
	}

	private boolean isEdgeCreationTool(EdgeCreationDescription ecd, String tgtFeature) {
		var modelOp = ecd.getInitialOperation().getFirstModelOperations();

		if ((modelOp == null) || !(modelOp instanceof ChangeContext)) {
			return false;
		}

		var changeContext = (ChangeContext) modelOp;

		var subOp = changeContext.getSubModelOperations().get(0);

		if ((subOp == null) || !(subOp instanceof SetValue)) {
			return false;
		}

		var setValue = (SetValue) subOp;

		return changeContext.getBrowseExpression().equals("var:source") && setValue.getFeatureName().equals(tgtFeature)
				&& (setValue.getValueExpression().trim().equals("var:target")
						|| setValue.getValueExpression().trim().equals("aql:self.nextSteps->including(target)"));
	}

	protected void assertHasNodeToolFor(EList<ToolEntry> tools, String nodeClassName) {
		assertHasNodeToolFor(tools, nodeClassName, null);
	}

	protected void assertHasNodeToolFor(EList<ToolEntry> tools, String nodeClassName, String attrInitialisation) {
		assertTrue(
				"Was expecting a tool for creating nodes of type " + nodeClassName
						+ ((attrInitialisation != null) ? " initialising attribute " + attrInitialisation : ""),
				exists(filter(tools, NodeCreationDescription.class), ncd -> {
					return exists(ncd.getNodeMappings(), nm -> {
						return nm.getDomainClass().equals(nodeClassName);
					}) && isNodeCreationTool(ncd, nodeClassName, attrInitialisation);
				}));
	}

	private boolean isNodeCreationTool(NodeCreationDescription ncd, String nodeClassName, String attrInitialisation) {
		var modelOp = ncd.getInitialOperation().getFirstModelOperations();

		if ((modelOp == null) || !(modelOp instanceof ChangeContext)) {
			return false;
		}

		var changeContext = (ChangeContext) modelOp;

		var subOp = changeContext.getSubModelOperations().get(0);

		if ((subOp == null) || !(subOp instanceof CreateInstance)) {
			return false;
		}

		var createOp = (CreateInstance) subOp;

		if (!(changeContext.getBrowseExpression().equals("var:container")
				&& (createOp.getTypeName().equals("flowcharts::" + nodeClassName)
						|| createOp.getTypeName().equals(nodeClassName)))) {
			return false;
		}

		if (attrInitialisation == null) {
			return true;
		}

		var subsubOp = createOp.getSubModelOperations().get(0);
		if ((subsubOp == null) || !(subsubOp instanceof SetValue)) {
			return false;
		}

		var setValue = (SetValue) subsubOp;

		return setValue.getFeatureName().equals(attrInitialisation);
	}

	protected void assertHasEdgeFor(EList<EdgeMapping> edgeMappings, String srcClassName, List<String> tgtClassNames,
			String edgeFeature) {
		assertTrue("Was expecting an edge definition for target feature " + edgeFeature + " from nodes for "
				+ srcClassName + " to nodes for " + tgtClassNames, exists(edgeMappings, em -> {
					return isEdge(em, srcClassName, tgtClassNames, edgeFeature);
				}));
	}

	private boolean isEdge(EdgeMapping em, String srcClassName, List<String> tgtClassNames, String edgeFeature) {
		return (em.getTargetFinderExpression() != null)
				&& em.getTargetFinderExpression().trim().equals("feature:" + edgeFeature)
				&& exists(em.getSourceMapping(), sm -> {
					return ((NodeMapping) sm).getDomainClass().equals(srcClassName);
				}) && forall(tgtClassNames, n -> {
					return exists(em.getTargetMapping(), tm -> {
						return ((NodeMapping) tm).getDomainClass().equals(n);
					});
				});
	}

	protected void assertHasNodeFor(EList<NodeMapping> nodeMappings, String className) {
		assertTrue("Was expecting to find a node for class " + className, exists(nodeMappings, nm -> {
			return nm.getDomainClass().equals(className);
		}));
	}
}
