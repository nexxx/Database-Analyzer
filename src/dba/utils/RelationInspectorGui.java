/******************************************************************************
 * Copyright: GPL v3                                                          *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package dba.utils;

import data.FunctionalDependency;
import data.Key;
import data.RelationSchema;
import logic.Analysis.GeneralRelationCheck;
import logic.Analysis.RelationInformation;

import java.util.ArrayList;

/**
 * Relation-inspection for the GUI
 */
public class RelationInspectorGui implements constants {
  private Localization locale;
  private RelationInformation checker;
  private String result;

  public RelationInspectorGui() {
    super();
    checker = new GeneralRelationCheck();
    locale = Localization.getInstance();
    result = "";
  }

  public String inspectRelation(RelationSchema schema) {
    String highestForm = "BCNF";
    boolean schemaSanity;
    ArrayList<FunctionalDependency> violatingFds;
    ArrayList<FunctionalDependency> prevViolatingFds;

    schemaSanity = isSchemaSane(schema);

    // Don't inspect when Schema is not proper
    if (!schemaSanity) {
      return result;
    }

    // BCNF
    violatingFds = checker.checkForBCNF(schema);
    if (violatingFds.isEmpty()) {
      highestForm = "BCNF";
    }
    prevViolatingFds = violatingFds;

    // Third NF
    if (!violatingFds.isEmpty()) {
      violatingFds = checker.checkForThirdNF(schema);
      if (violatingFds.isEmpty()) {
        highestForm = "3.NF";
        addToResult(locale.getString("RI_NotBCNF1"));
        addToResult(locale.getString("RI_NotBCNF2"));
        tellViolatingFds(prevViolatingFds);
      }
      prevViolatingFds = violatingFds;
    }

    // Second NF
    if (!violatingFds.isEmpty()) {
      violatingFds = checker.checkForSecondNF(schema);
      if (violatingFds.isEmpty()) {
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

  private boolean isSchemaSane(RelationSchema schema) {
    boolean sane = true;
    ArrayList<Key> candidateKeys = checker.getAllCandidateKeys(schema);
    Key primaryKey = checker.getPrimaryKey(schema);

    if (schema.getFunctionalDependencies().isEmpty()) {
      addToResult(locale.getString("RI_NoFd"));
      sane = false;
    } else {
      for (FunctionalDependency fd : schema.getFunctionalDependencies()) {
        if (fd.getSourceAttributes().isEmpty() || fd.getTargetAttributes().isEmpty()) {
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
    } else if (!checker.isCandidateKey(primaryKey.getAttributes(), candidateKeys)) {
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
    if (candidateKeys.isEmpty()) {
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
