package dba.utils;

import java.util.ArrayList;

import logic.Analysis.GeneralRelationCheck;
import logic.Analysis.RelationInformation;
import data.FunctionalDependency;
import data.Key;
import data.RelationSchema;

/**
 * Relation-inspection for the GUI
 */
public class RelationInspectorGui implements constants {
  Localization locale;
  RelationInformation checker;
  private String result;

  public RelationInspectorGui() {
	checker = new GeneralRelationCheck();
	locale = Localization.getInstance();
	result = "";
  }

  public String inspectRelation(RelationSchema schema) {
	String highestForm = "BCNF";
	boolean schemaSanity = true;
	ArrayList<FunctionalDependency> violatingFds = new ArrayList<FunctionalDependency>();
	ArrayList<FunctionalDependency> prevViolatingFds = new ArrayList<FunctionalDependency>();

	schemaSanity = isSchemaSane(schema);

	// Don't inspect when Schema is not proper
	if (!schemaSanity) {
	  return result;
	}

	// BCNF
	violatingFds = checker.checkForBCNF(schema);
	if (violatingFds.size() == 0) {
	  highestForm = "BCNF";
	}
	prevViolatingFds = violatingFds;

	// Third NF
	if (violatingFds.size() != 0) {
	  violatingFds = checker.checkForThirdNF(schema);
	  if (violatingFds.size() == 0) {
		highestForm = "3.NF";
		addToResult(locale.getString("RI_NotBCNF1"));
		addToResult(locale.getString("RI_NotBCNF2"));
		tellViolatingFds(prevViolatingFds);
	  }
	  prevViolatingFds = violatingFds;
	}

	// Second NF
	if (violatingFds.size() != 0) {
	  violatingFds = checker.checkForSecondNF(schema);
	  if (violatingFds.size() == 0) {
		highestForm = "2.NF";
		addToResult(locale.getString("RI_Not3NF1"));
		addToResult(locale.getString("RI_Not3NF2"));
		tellViolatingFds(prevViolatingFds);
	  } else {
		highestForm = "1.NF";
		addToResult(locale.getString("RI_Not2NF1"));
		addToResult(locale.getString("RI_Not2NF2"));
		tellViolatingFds(violatingFds);
	  }
	}

	addToResult(locale.getString("RI_HighestNF") + " " + highestForm);
	return result;
  }

  public boolean isSchemaSane(RelationSchema schema) {
	boolean sane = true;
	ArrayList<Key> candidateKeys = checker.getAllCandidateKeys(schema);
	Key primaryKey = checker.getPrimaryKey(schema);

	if (schema.getFunctionalDependencies().size() == 0) {
	  addToResult(locale.getString("RI_NoFd"));
	  sane = false;
	} else {
	  for (FunctionalDependency fd : schema.getFunctionalDependencies()) {
		if (fd.getSourceAttributes().isEmpty()
		    || fd.getTargetAttributes().isEmpty()) {
		  addToResult(locale.getString("RI_EmptyFDSide"));
		  sane = false;
		  break;
		}
	  }
	}

	if (primaryKey.getAttributes().isEmpty()) {
	  addToResult(locale.getString("RI_NoPk"));
	  tellCandidateKeys(candidateKeys);
	  sane = false;
	} else if (!checker.isKeyDeterminingEverything(schema, primaryKey)) {
	  addToResult(locale.getString("RI_PkNotAllAttr"));
	  sane = false;
	} else if (!checker.isCandidateKey(primaryKey.getAttributes(),
	    candidateKeys)) {
	  addToResult(locale.getString("RI_PkNoCk"));
	  tellCandidateKeys(candidateKeys);
	}

	if (sane) {
	  addToResult(locale.getString("RI_OK"));
	} else {
	  addToResult(locale.getString("RI_Warn"));
	}
	return sane;
  }

  private void tellCandidateKeys(ArrayList<Key> candidateKeys) {
	if (candidateKeys.size() == 0) {
	  addToResult(locale.getString("RI_NoCk"));
	} else {
	  addToResult(locale.getString("RI_Ck"));
	  for (Key key : candidateKeys) {
		addToResult("* " + key.toString());
	  }
	}
  }

  private void tellViolatingFds(ArrayList<FunctionalDependency> violatingFds) {
	for (FunctionalDependency fd : violatingFds) {
	  addToResult("* " + fd.toString());
	}
  }

  private String addToResult(String sentence) {
	result = result + sentence + "\n";
	return result;
  }

}
