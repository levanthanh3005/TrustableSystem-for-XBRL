package it.unical.xbrl.xbrlcore.OntoClass;

import java.util.HashMap;
import java.util.List;

public interface OntoDLVTransform {
	void readFact();
	void readArc();
	void writeFileCollection();
	void addToCollectDLVObjectSd(ObjectSd osd);
	boolean execDLVValidator();
	HashMap<String, Object> execDLVValidatorHashMap();
	List getListOfFact();
	List getListOfArc();
	List getListOfPureFact(String traceCalFact);
}
