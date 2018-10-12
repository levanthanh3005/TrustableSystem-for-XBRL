package it.unical.xbrl.xbrlcore.xbrlcore.instance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import it.unical.xbrl.xbrlcore.xbrlcore.constants.ExceptionConstants;
import it.unical.xbrl.xbrlcore.xbrlcore.constants.GeneralConstants;
import it.unical.xbrl.xbrlcore.xbrlcore.constants.NamespaceConstants;
import it.unical.xbrl.xbrlcore.xbrlcore.dimensions.MultipleDimensionType;
import it.unical.xbrl.xbrlcore.xbrlcore.dimensions.SingleDimensionType;
import it.unical.xbrl.xbrlcore.xbrlcore.exception.InstanceException;
import it.unical.xbrl.xbrlcore.xbrlcore.exception.XBRLException;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.Concept;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.DiscoverableTaxonomySet;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.DTSFactory;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.TaxonomySchema;
import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;

/**
 * This class is responsible for creating an Instance object of an instance
 * file. <br/><br/>
 *
 * @author Daniel Hamm
 *
 */
public class InstanceFactory {

    private static InstanceFactory xbrlInstanceFactory;

    private Instance instance;

    private Document xmlInstance;

    private Map contextMap;

    private Map unitMap;

    private String PATH;

    private List schemaRefNamespaces;

    /**
     * Constructor, private.
     *
     */
    public InstanceFactory() {
        contextMap = new HashMap();
        unitMap = new HashMap();
        schemaRefNamespaces = new ArrayList();
    }

    /**
     *
     * @return Instance of InstanceFactory object.
     */
    public static synchronized InstanceFactory get() {
        if (xbrlInstanceFactory == null) {
            xbrlInstanceFactory = new InstanceFactory();
        }
        return xbrlInstanceFactory;
    }

    /**
     * Creates an xbrlcore.instance.Instance object.
     *
     * @param instanceFile Instance file.
     * @return An object of xbrlcore.instance.Instance.
     * @throws JDOMException
     * @throws IOException
     * @throws XBRLException
     */
    public Instance createInstance(File instanceFile) throws JDOMException,
            IOException, XBRLException, CloneNotSupportedException {
        PATH = instanceFile.getAbsolutePath().substring(0,
                instanceFile.getAbsolutePath().lastIndexOf(File.separator) + 1);

        /* Initialise XML document with SAX of JDOM */
        SAXBuilder builder = new SAXBuilder();
        ExtrasStatus.log("User SAXBuilder to build instance file:" + instanceFile.getName());
        xmlInstance = builder.build(PATH + instanceFile.getName());

        /* determine taxonomy names */
        ExtrasStatus.log("Determine taxonomy names from builded instance : load taxnomy");
        Set taxonomyNameSet = getReferencedSchemaNames(xmlInstance);

        /* now build the taxonomys */
        DTSFactory taxonomyFactory = DTSFactory.get();
        Iterator taxonomyNameSetIterator = taxonomyNameSet.iterator();
        Set dtsSet = new HashSet();
        while (taxonomyNameSetIterator.hasNext()) {
            String currTaxonomyName = (String) taxonomyNameSetIterator.next();
            ExtrasStatus.log("currTaxonomyName:" + currTaxonomyName + ": Import taxnonomy xsd file");
            DiscoverableTaxonomySet currTaxonomy = taxonomyFactory
                    .createTaxonomy(new File(PATH + currTaxonomyName));
            dtsSet.add(currTaxonomy);
        }
        ExtrasStatus.log("Taxnonomy : DONE >>>>");
        return getInstance(dtsSet, instanceFile.getName());
    }

    /**
     * Builds an instance.
     *
     * @param dtsSet Set of discoverable taxonomy sets this instance refers to.
     * @return An object of xbrlcore.instance.Instance.
     * @throws InstanceException
     */
    private Instance getInstance(Set dtsSet, String fileName)
            throws InstanceException, CloneNotSupportedException {
        ExtrasStatus.log("getInstance : input dtsSet & :" + fileName);
        instance = new Instance(dtsSet);
        instance.setFileName(fileName);

        /* set instance namespace */
        setInstanceNamespace();

        /* set additional namespaces of the root element */
        setAdditionalNamespaces(xmlInstance.getRootElement()
                .getAdditionalNamespaces());

        /* set additional namespaces of the schemaRef elements */
        setAdditionalNamespaces(schemaRefNamespaces);

        /* determine the schema location */
        setSchemaLocation();

        /* set context elements */
        setContextElements();

        /* set unit elements */
        setUnitElements();

        /* set facts */
        setFacts();

        return instance;
    }

    /**
     * Determines which taxonomy an instance refers to.
     *
     * @param currDocument Structure of the instance file.
     * @return Set of names of the taxonomy the instance refers to.
     */
    private Set getReferencedSchemaNames(Document currDocument) {
        Set referencedSchemaNameSet = new HashSet();
        List elementList = currDocument.getRootElement().getChildren();
        Iterator elementListIterator = elementList.iterator();
        while (elementListIterator.hasNext()) {
            Element currElement = (Element) elementListIterator.next();
            if (currElement.getName().equals("schemaRef")) {
                referencedSchemaNameSet.add(currElement.getAttributeValue(
                        "href", NamespaceConstants.XLINK_NAMESPACE));
                ExtrasStatus.log("Add schemaRef to referencedSchemaNameSet : " + currElement.getAttributeValue("href", NamespaceConstants.XLINK_NAMESPACE));
                /* set namespaces of schemaRef element */
                schemaRefNamespaces = currElement.getAdditionalNamespaces();
            }
        }
        return referencedSchemaNameSet;
    }

    /**
     * Sets the namespace of the instance.
     *
     */
    private void setInstanceNamespace() {
        Namespace instanceNamespace = xmlInstance.getRootElement()
                .getNamespace();
        ExtrasStatus.log("setInstanceNamespace:" + instanceNamespace.toString());
        instance.setInstanceNamespace(instanceNamespace);
    }

    /**
     * Sets additional namespaces needed in this instance.
     *
     */
    private void setAdditionalNamespaces(List additionalNamespaces) {
        ExtrasStatus.log("setAdditionalNamespaces");
        Iterator additionalNamespacesIterator = additionalNamespaces.iterator();
        while (additionalNamespacesIterator.hasNext()) {
            Namespace currentNamespace = (Namespace) additionalNamespacesIterator
                    .next();
            ExtrasStatus.log("   currentNamespace:" + currentNamespace);

            if (instance.getNamespace(currentNamespace.getURI()) == null || instance.getNamespace(currentNamespace.getURI()).getPrefix() == "") {
                instance.addNamespace(currentNamespace);
            }
            ExtrasStatus.log("      Namespace:" + instance.getNamespace(currentNamespace.getURI()));
        }
    }

    /**
     * Sets schema location information defined in this instance.
     *
     *
     */
    private void setSchemaLocation() {
        ExtrasStatus.log("setSchemaLocation");
        if (xmlInstance.getRootElement().getAttributes().size() > 0
                && xmlInstance.getRootElement().getAttribute(
                        "schemaLocation",
                        instance.getNamespace(NamespaceConstants.XSI_NAMESPACE
                                .getURI())) != null) {
            String schemaLocationValue = xmlInstance
                    .getRootElement()
                    .getAttributeValue(
                            "schemaLocation",
                            instance
                                    .getNamespace(NamespaceConstants.XSI_NAMESPACE
                                            .getURI()));
            ExtrasStatus.log("  schemaLocationValue:" + schemaLocationValue);
            while (schemaLocationValue.indexOf(" ") > 0) {
                String schemaLocationURI = schemaLocationValue.substring(0,
                        schemaLocationValue.indexOf(" "));
                schemaLocationValue = schemaLocationValue.substring(
                        schemaLocationValue.indexOf(" "), schemaLocationValue
                        .length());
                schemaLocationValue = schemaLocationValue.trim();
                String schemaLocationPrefix = null;
                if (schemaLocationValue.indexOf(" ") > 0) {
                    schemaLocationPrefix = schemaLocationValue.substring(0,
                            schemaLocationValue.indexOf(" "));
                    schemaLocationValue = schemaLocationValue.substring(
                            schemaLocationValue.indexOf(" "),
                            schemaLocationValue.length());
                    schemaLocationValue = schemaLocationValue.trim();
                } else {
                    schemaLocationPrefix = schemaLocationValue;
                }
                ExtrasStatus.log("  addSchemaLocation: URI: " + schemaLocationURI + " Prefix:" + schemaLocationPrefix);
                instance.addSchemaLocation(schemaLocationURI,
                        schemaLocationPrefix);
            }
        }
    }

    /**
     * Sets unit elements defined in this instance.
     *
     * @throws InstanceException
     */
    private void setUnitElements() throws InstanceException {
        ExtrasStatus.log("setUnitElements");
        List unitElementList = xmlInstance.getRootElement().getChildren("unit",
                instance.getInstanceNamespace());
        Iterator unitElementListIterator = unitElementList.iterator();
        while (unitElementListIterator.hasNext()) {
            Element currUnitElement = (Element) unitElementListIterator.next();
            String id = currUnitElement.getAttributeValue("id");
            ExtrasStatus.log("  id:" + id);
            if (id == null || id.length() == 0) {
                throw new InstanceException(
                        ExceptionConstants.EX_INSTANCE_CREATION_NOID_UNIT);
            }

            InstanceUnit currUnit = new InstanceUnit(id);

            /* set the value and its namespace */
            Element measureElement = currUnitElement.getChild("measure",
                    instance.getInstanceNamespace());
            String value = measureElement.getValue();

            String namespacePrefix = value.indexOf(":") != -1 ? value.substring(0, value.indexOf(":")) : "";
            String unitValue = value.substring(value.indexOf(":") + 1, value
                    .length());
            ExtrasStatus.log("  setNamespaceURI:" + instance.getNamespaceURI(namespacePrefix));
            currUnit.setNamespaceURI(instance.getNamespaceURI(namespacePrefix));
            currUnit.setValue(unitValue);

            unitMap.put(id, currUnit);
        }
    }

    /**
     * @param name
     * @return Returns a Concept object to a given name from all the
     * Discoverable Taxonomy Sets the instance refers to.
     */
    private Concept getConceptByName(String name) {
        Set dtsSet = instance.getDiscoverableTaxonomySet();
        Iterator dtsSetIterator = dtsSet.iterator();
        while (dtsSetIterator.hasNext()) {
            DiscoverableTaxonomySet currDts = (DiscoverableTaxonomySet) dtsSetIterator
                    .next();
            Concept currConcept = currDts.getConceptByName(name);
            if (currConcept != null) {
                return currConcept;
            }
        }
        return null;
    }

    /**
     * Sets context elements of this instance.
     *
     * @throws InstanceException
     */
    private void setContextElements() throws InstanceException,
            CloneNotSupportedException {
        ExtrasStatus.log("setContextElements");
        List contextElementList = xmlInstance.getRootElement().getChildren("context", instance.getInstanceNamespace());
        Iterator contextElementListIterator = contextElementList.iterator();
        while (contextElementListIterator.hasNext()) {
            Element currContextElement = (Element) contextElementListIterator
                    .next();
            String id = currContextElement.getAttributeValue("id");

            if (id == null || id.length() == 0) {
                throw new InstanceException(
                        ExceptionConstants.EX_INSTANCE_CREATION_NOID_CONTEXT);
            }

            InstanceContext currContext = new InstanceContext(id);

            /* set identifier scheme and identifier */
            Element identifierElement = currContextElement.getChild("entity",
                    instance.getInstanceNamespace()).getChild("identifier",
                    instance.getInstanceNamespace());
            currContext.setIdentifierScheme(identifierElement
                    .getAttributeValue("scheme"));
            currContext.setIdentifier(identifierElement.getValue());

            /* set period type and period */
            Element periodElement = currContextElement.getChild("period",
                    instance.getInstanceNamespace());
            if (periodElement != null) {
                if (periodElement.getChild("startDate", instance
                        .getInstanceNamespace()) != null
                        && periodElement.getChild("endDate", instance
                                .getInstanceNamespace()) != null) {
                    currContext.setPeriodStartDate(periodElement.getChild(
                            "startDate", instance.getInstanceNamespace())
                            .getText());
                    currContext.setPeriodEndDate(periodElement.getChild(
                            "endDate", instance.getInstanceNamespace())
                            .getText());
                } else if (periodElement.getChild("instant", instance
                        .getInstanceNamespace()) != null) {
                    if (periodElement.getChild("instant",
                            instance.getInstanceNamespace()).getChild(
                            "forever", instance.getInstanceNamespace()) != null) {
                        currContext.setPeriodValue("forever");
                    } else {
                        currContext.setPeriodValue(periodElement.getChild(
                                "instant", instance.getInstanceNamespace())
                                .getText());
                    }
                }
            }

            /*
             * set multidimensional information - parse both <scenario> and
             * <segment> element
             */
            List scenSegElementList = new ArrayList();
            Element scenarioElement = currContextElement.getChild("scenario",
                    instance.getInstanceNamespace());
            /* <segment> is a child element of <entity> */
            Element segmentElement = currContextElement.getChild("entity",
                    instance.getInstanceNamespace()).getChild("segment",
                    instance.getInstanceNamespace());
            if (scenarioElement != null) {
                scenSegElementList.add(scenSegElementList.size(),
                        scenarioElement);
            }
            if (segmentElement != null) {
                scenSegElementList.add(scenSegElementList.size(),
                        segmentElement);
            }

            for (int i = 0; i < scenSegElementList.size(); i++) {
                Element currElement = (Element) scenSegElementList.get(i);
                List explicitMemberElementList = currElement
                        .getChildren(
                                "explicitMember",
                                instance
                                        .getNamespace(NamespaceConstants.XBRLDI_NAMESPACE
                                                .getURI()));
                List typedMemberElementList = currElement
                        .getChildren(
                                "typedMember",
                                instance
                                        .getNamespace(NamespaceConstants.XBRLDI_NAMESPACE
                                                .getURI()));
                Iterator explicitMemberElementListIterator = explicitMemberElementList
                        .iterator();
                Iterator typedMemberElementListIterator = typedMemberElementList
                        .iterator();
                MultipleDimensionType mdt = null;
                /* set explicit member */
//                LogStatus.log("MultipleDimensionType >>>>>> DELETE");
                while (explicitMemberElementListIterator.hasNext()) {
                    Element currExplicitMemberElement = (Element) explicitMemberElementListIterator
                            .next();

                    /* determine dimension element */
                    String dimensionAttribute = currExplicitMemberElement
                            .getAttributeValue("dimension");
                    String prefix = dimensionAttribute.substring(0,
                            dimensionAttribute.indexOf(":"));
                    String dimensionElementName = dimensionAttribute.substring(
                            dimensionAttribute.indexOf(":") + 1,
                            dimensionAttribute.length());
                    Concept dimensionElement = null;

                    dimensionElement = instance.
                            getSchemaForPrefix(prefix).
                            getConceptByName(dimensionElementName);

                    /* determine domain member element */
                    String value = currExplicitMemberElement.getValue();
                    String domainMemberElementName = value.substring(value
                            .indexOf(":") + 1, value.length());
                    Concept domainMemberElement = getConceptByName(domainMemberElementName);

                    if (dimensionElement == null || domainMemberElement == null) {
//                        LogStatus.log(new InstanceException(ExceptionConstants.EX_INSTANCE_CREATION_DIMENSIONS + id));
//                        continue;
                        throw new InstanceException(ExceptionConstants.EX_INSTANCE_CREATION_DIMENSIONS + id);
                    }

                    if (mdt == null) {
                        mdt = new MultipleDimensionType(dimensionElement,
                                domainMemberElement);
                    } else {
                        mdt
                                .addPredecessorDimensionDomain(new SingleDimensionType(
                                        dimensionElement, domainMemberElement));
                    }
                }
                /* set typed member */
                while (typedMemberElementListIterator.hasNext()) {
                    Element currTypedMemberElement = (Element) typedMemberElementListIterator
                            .next();

                    /* determine dimension element */
                    String dimensionAttribute = currTypedMemberElement
                            .getAttributeValue("dimension");
                    String prefix = dimensionAttribute.substring(0,
                            dimensionAttribute.indexOf(":"));
                    String dimensionElementName = dimensionAttribute.substring(
                            dimensionAttribute.indexOf(":") + 1,
                            dimensionAttribute.length());
                    Concept dimensionElement = instance.getSchemaForPrefix(
                            prefix).getConceptByName(dimensionElementName);

                    /*
                     * SingleDimensionType represtents the typed dimension
                     * element and its content
                     */
                    SingleDimensionType sdt = null;

                    /* set typed dimension element */
                    if (currTypedMemberElement.getChildren().size() != 0) {
                        Element childElement = (Element) currTypedMemberElement
                                .getChildren().get(0);
                        sdt = new SingleDimensionType(dimensionElement,
                                childElement);
                    }

                    if (mdt == null) {
                        mdt = new MultipleDimensionType(sdt);
                    } else {
                        mdt.addPredecessorDimensionDomain(sdt);
                    }
                }

                if (mdt != null && i == 0) {
                    currContext.setDimensionalInformation(mdt,
                            GeneralConstants.DIM_SCENARIO);
                } else if (mdt != null && i == 1) {
                    currContext.setDimensionalInformation(mdt,
                            GeneralConstants.DIM_SEGMENT);
                }
            }

            contextMap.put(id, currContext);
            instance.addContext(currContext);
        }

    }

    /**
     * Sets facts of the instance.
     *
     * @throws InstanceException
     */
    Map<String, ArrayList> lsRestFact = new HashMap<>();
    public String factGroupId = null;
    public int factGroupIndex = 0;

    private void setFacts() throws InstanceException {
        ExtrasStatus.log("setFacts:");
        factGroupId = null;
        List factElementList = xmlInstance.getRootElement().getChildren();
        setFactsByList(factElementList);
        ExtrasStatus.log("Fact Group :");
        for (Map.Entry<String, ArrayList> entry : lsRestFact.entrySet()) {
            factGroupId = entry.getKey();
            ExtrasStatus.log(entry.getKey() + "/" + ((List) entry.getValue()).size());
            setFactsByList(((List) entry.getValue()));
        }
//        setFactsByList(lsRestFact);
    }

    private void setFactsByList(List factElementList) throws InstanceException {
        Iterator factElementListIterator = factElementList.iterator();
//        int maxValue = factElementList.size();
//        int elementIndex = 0;
        while (factElementListIterator.hasNext()) {
            Element currFactElement = (Element) factElementListIterator.next();
//        while (elementIndex < maxValue) {
//            Element currFactElement = (Element) factElementList.get(elementIndex);
            ExtrasStatus.log("$$$$:" + currFactElement.toString());
            if (!currFactElement.getName().equals("context")
                    && !currFactElement.getName().equals("schemaRef")
                    && !currFactElement.getName().equals("unit")
                    && !currFactElement.getName().equals("footnoteLink")) {

                String factElementName = currFactElement.getName();
                ExtrasStatus.log("  Schema For URI: prefix:" + currFactElement.getNamespace().getPrefix() + " URI:" + currFactElement.getNamespace().getURI());
                TaxonomySchema schema = instance.getSchemaForURI(currFactElement.getNamespace());
                Concept currFactXBRLElement = schema.getConceptByName(factElementName);
//                LogStatus.log("  Find concept by:" + factElementName );//id = prefix+"_"+factElementName
                if (currFactXBRLElement == null) {
//                    LogStatus.log(new InstanceException(ExceptionConstants.EX_INSTANCE_CREATION_FACT + factElementName));
//                    currFactXBRLElement = instance.getConceptByName(factElementName);
                    throw new InstanceException(
                            ExceptionConstants.EX_INSTANCE_CREATION_FACT
                            + factElementName);
                }
                /* now it is a fact element */
                Fact newFact = new Fact(currFactXBRLElement);

                /* check if it refers to a valid context and unit */
                String contextID = currFactElement
                        .getAttributeValue("contextRef");
                String unitID = currFactElement.getAttributeValue("unitRef");
                InstanceContext ctx = (InstanceContext) contextMap
                        .get(contextID);
                InstanceUnit unit = (InstanceUnit) unitMap.get(unitID);
                if (ctx == null) {
                    ExtrasStatus.log(new InstanceException(
                            ExceptionConstants.EX_INSTANCE_CREATION_NO_CONTEXT
                            + factElementName));
                    ExtrasStatus.log("   currFactElement.getChildren() :" + currFactElement.getChildren().size());
                    if (currFactElement.getChildren() != null && currFactElement.getChildren().size() > 0) {
                        ArrayList newList = lsRestFact.get(newFact.getConcept().getId());
                        if (newList == null) {
                            newList = new ArrayList();
                            newList.addAll(currFactElement.getChildren());
                        } else {
                            newList = (ArrayList) newList.clone();
                            for (Object eo : currFactElement.getChildren()) {
                                newList.add(eo);
                            }
                            lsRestFact.put(newFact.getConcept().getId(), newList);
                        }
                        lsRestFact.put(newFact.getConcept().getId()+factGroupIndex, newList);
                        factGroupIndex++;
                    }
                    continue;
                }

                newFact.setInstanceContext(ctx);
                newFact.setInstanceUnit(unit);

                /* set remaining information */
                if (currFactElement.getAttributeValue("decimals") != null) {
                    newFact.setDecimals(currFactElement
                            .getAttributeValue("decimals"));
                }
                if (currFactElement.getAttributeValue("precision") != null) {
                    newFact.setPrecision(currFactElement
                            .getAttributeValue("precision"));
                }
                newFact.setValue(currFactElement.getValue());
                if (factGroupId != null) {
                    newFact.setFactGroupId(factGroupId);
                }
                ExtrasStatus.log("  fact: concept id:" + newFact.getConcept().getId() + " contextRef:" + newFact.getContextRef());
                /* finally, add the fact to the instance */
                instance.addFact(newFact);
            }
        }
    }
}
