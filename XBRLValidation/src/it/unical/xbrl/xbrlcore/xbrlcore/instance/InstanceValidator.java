package it.unical.xbrl.xbrlcore.xbrlcore.instance;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import it.unical.xbrl.xbrlcore.xbrlcore.exception.CalculationValidationException;
import it.unical.xbrl.xbrlcore.xbrlcore.exception.InstanceValidationException;
import it.unical.xbrl.xbrlcore.xbrlcore.exception.XBRLException;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.Concept;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.DiscoverableTaxonomySet;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.TaxonomySchema;
import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;

/**
 * The purpose of this class is to do some basic instance (XBRL) validation.
 * Currently, the class is incomplete since it does not perform a full
 * validation, but it will be extended in the future. <br/><br/>
 *
 * @author Daniel Hamm
 */
public class InstanceValidator {

    private Instance instance;

    private File instanceFile;

    private String schemaLocation;

    /**
     * Constructor.
     *
     * @param instance Instance to validate.
     */
    public InstanceValidator(Instance instance) {
        this.instance = instance;
    }

    public InstanceValidator(File instanceFile) throws JDOMException,
            IOException, CloneNotSupportedException, XBRLException {
        this.instance = InstanceFactory.get().createInstance(instanceFile);
        this.instanceFile = instanceFile;
    }

    /**
     * Validates an instance file against the DTS by using only XML Schema (no
     * additional XBRL validation is performed).
     *
     * @throws JDOMException
     * @throws XBRLException
     * @throws IOException
     * @throws CloneNotSupportedException
     */
    public void schemaValidation() throws JDOMException, XBRLException,
            IOException, CloneNotSupportedException {

        /* if schema location is not set, it is tried to be determined */
        if (schemaLocation == null || schemaLocation.length() == 0) {
            schemaLocation = instance.getInstanceNamespace().getURI()
                    + " "
                    + instance.getSchemaForURI(instance.getInstanceNamespace())
                            .getName();

            Set additionalNamespaceSet = instance.getAdditionalNamespaceSet();
            Iterator additionalNamespaceIterator = additionalNamespaceSet
                    .iterator();
            while (additionalNamespaceIterator.hasNext()) {
                Namespace currNamespace = (Namespace) additionalNamespaceIterator
                        .next();
                TaxonomySchema currSchema = instance
                        .getSchemaForURI(currNamespace);
                if (currSchema != null) {
                    schemaLocation += " " + currNamespace.getURI() + " "
                            + currSchema.getName();
                }
            }
        }

        SAXBuilder saxBuilder = new SAXBuilder(
                "org.apache.xerces.parsers.SAXParser", true);
        saxBuilder.setFeature(
                "http://apache.org/xml/features/validation/schema", true);
        saxBuilder
                .setProperty(
                        "http://apache.org/xml/properties/schema/external-schemaLocation",
                        schemaLocation);

        /* if a file is set this is validated, otherwise the object */
        if (instanceFile != null) {
            saxBuilder.build(instanceFile);
        } else {
            saxBuilder.build(new InputSource(new StringReader(
                    new InstanceOutputter(instance).getXMLString())));
        }
    }

    /**
     * Performs all validations this class offers. Currently, this is just the
     * validation against the calculation linkbase (NO XML Schema validation is
     * performed).
     *
     * @throws InstanceValidationException
     */
    public void validate() throws InstanceValidationException, XBRLException {
        validateCalculations();
    }

    /**
     * Performs a full validation of the instance against the calculation
     * linkbase of the taxonomy.
     *
     * @throws InstanceValidationException
     * @throws CalculationValidationException
     */
    public void validateCalculations() throws InstanceValidationException,
            CalculationValidationException, XBRLException {
        /* get all the facts */
        List factSet = instance.getFactSet();
        Iterator factSetIterator = factSet.iterator();
        while (factSetIterator.hasNext()) {
            Fact currFact = (Fact) factSetIterator.next();
            validateCalculation(currFact);
        }
    }

    /**
     * Performs a validation of only one fact of the instance against the
     * calculation linkbase of the taxonomy.
     *
     * @param fact Fact which is validated against the calculation linkbase.
     * @throws InstanceValidationException
     * @throws CalculationValidationException
     */
    public void validateCalculation(Fact fact)
            throws InstanceValidationException, CalculationValidationException,
            XBRLException {
        /* get all the taxonomies this instance refers to */
        ExtrasStatus.log("validateCalculation fact id:" +  fact.getConcept().getId()+ " contextRef:"+fact.getContextRef());
        Set dtsSet = instance.getDiscoverableTaxonomySet();

        DiscoverableTaxonomySet currDTS = null;
        Iterator dtsSetIterator = dtsSet.iterator();
        while (dtsSetIterator.hasNext()) {
            DiscoverableTaxonomySet tmpDTS = (DiscoverableTaxonomySet) dtsSetIterator
                    .next();
            if (tmpDTS.getConceptByID(fact.getConcept().getId()) != null) {
                currDTS = tmpDTS;
                ExtrasStatus.log("  set currDTS:" + currDTS.getTopTaxonomy().getName());
                break;
            }
        }
        if (currDTS == null) {
            String msg = "Error: Could not find taxonomy schema for fact "
                    + fact.getConcept().getName() + " in instance "
                    + instance.getFileName();
            InstanceValidationException ex = new InstanceValidationException(
                    msg);
            throw ex;
        }
        if (currDTS.getCalculationLinkbase() == null) {
            /* no calculations defined */
            ExtrasStatus.log("  no calculations defined");
            return;
        }
        /*
         * check for every extended link role whether there are calculation
         * rules defined
         */
        Set extendedLinkRoleSet = currDTS.getCalculationLinkbase()
                .getExtendedLinkRoles();
        Iterator extendedLinkRoleSetIterator = extendedLinkRoleSet.iterator();
        while (extendedLinkRoleSetIterator.hasNext()) {
            String currExtendedLinkRole = (String) extendedLinkRoleSetIterator
                    .next();
            Map calculationRules = currDTS.getCalculationLinkbase()
                    .getCalculations(fact.getConcept(), currExtendedLinkRole);
            System.out.println("  currExtendedLinkRole:"+currExtendedLinkRole+"  calculationRules.size():"+calculationRules.size());
            if (calculationRules.size() > 0) {
                /*
                 * there are calculation rules defined for this concpet, check
                 * whether the numbers are correct
                 */
                BigDecimal expectedResult = new BigDecimal(.0F);
                BigDecimal calculatedResult = new BigDecimal(.0F);
                try {
//                    expectedResult = new BigDecimal(new Float(fact.getValue()
//                            .replaceAll(",", ".")).floatValue());
                    expectedResult = new BigDecimal(new Float(fact.getValue()
                            .replaceAll(",", ".")).floatValue());
                    ExtrasStatus.log("   expectedResult:" + fact.getValue());
                } catch (NumberFormatException ex) {
                    throw ex;
                }
                /* calculate currentResult */
                Set calculationRulesEntrySet = calculationRules.entrySet();
                Iterator calculationRulesIterator = calculationRulesEntrySet
                        .iterator();

                while (calculationRulesIterator.hasNext()) {
                    Map.Entry currEntry = (Map.Entry) calculationRulesIterator
                            .next();
                    Concept tmpConcept = (Concept) currEntry.getKey();
                    float currWeight = ((Float) currEntry.getValue()).floatValue();

                    /* get the value of this fact */
                    BigDecimal newValue = new BigDecimal(.0F);
                    ExtrasStatus.log("     fact find concept:" + tmpConcept.getId() + " getInstanceContext:" + fact.getInstanceContext().getId());

                    if (instance.getFact(tmpConcept, fact.getInstanceContext()) == null) {
                        CalculationValidationException ex = new CalculationValidationException("");
                        ex.setDts(currDTS);
                        ex.setMissingValues(true);
                        ex.setMissingConcept(tmpConcept);
//                        throw ex;
                        ExtrasStatus.log(ex);
                        continue;
                    }

                    String stringValue = instance.getFact(tmpConcept,
                            fact.getInstanceContext()).getValue();
//                    LogStatus.log("     fact value:" + stringValue + " from tmpConcept : " + tmpConcept.getId() + " getInstanceContext:" + fact.getInstanceContext().getId());
                    try {
                        newValue = new BigDecimal(new Float(stringValue)
                                .floatValue());
                    } catch (NumberFormatException ex) {
                        throw ex;
                    }
                    /* calculate newValue to calculatedResults */
                    calculatedResult = calculatedResult.add(newValue
                            .multiply(new BigDecimal(currWeight)));
                    ExtrasStatus.log("       calculatedResult:" + calculatedResult);
                }
                /* now compare both results */
//                LogStatus.log("Different:" + ((expectedResult.subtract(calculatedResult)).abs()));
                System.out.println("Different:" + ((expectedResult.subtract(calculatedResult)).abs()));
                if (!expectedResult.equals(calculatedResult)) {
                    String msg = "The calculation result ";
                    msg += calculatedResult;
                    msg += " is not equal to the specified value ";
                    msg += expectedResult;
                    msg += " of the numeric item ";
                    msg += fact.getConcept();
                    msg += " in extended link role " + currExtendedLinkRole;
                    CalculationValidationException ex = new CalculationValidationException(
                            msg);
                    ex.setDts(currDTS);
                    ex.setExpectedResult(expectedResult);
                    ex.setCalculatedResult(calculatedResult);
                    ex.setCalculatedConceptSet(calculationRules.keySet());
//                    throw ex;
//                    System.err.println("Result:" + ((expectedResult.subtract(calculatedResult)).abs()));
                    System.err.println(ex);
                }
            }
        }
    }

}
