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

package dbaCore.logic.Analysis;

import dbaCore.data.*;

import java.util.ArrayList;

/**
 * Interface incorporating all important methods to check a relation
 *
 * @author Sebastian Theuermann
 */
public interface RelationInformation {

  public abstract NormalForm getNF(RelationSchema schema, ArrayList<FunctionalDependency> violatingFds);

  public NormalForm getNF(ArrayList<RelationSchema> relations);

  public ArrayList<FunctionalDependency> checkForSecondNF(RelationSchema schema);

  public boolean isSecondNF(RelationSchema schema);

  public ArrayList<FunctionalDependency> checkForThirdNF(RelationSchema schema);

  public boolean isThirdNF(RelationSchema schema);

  public ArrayList<FunctionalDependency> checkForBCNF(RelationSchema schema);

  public boolean isBCNF(RelationSchema schema);

  public ArrayList<FunctionalDependency> getMinimalSetOfFds(ArrayList<FunctionalDependency> fds);

  public Key getPrimaryKey(RelationSchema schema);

  public ArrayList<Key> getAllCandidateKeys(RelationSchema schema);

  public boolean isCandidateKey(ArrayList<Attribute> keyToTest, ArrayList<Key> candidateKeys);

  public boolean isKeyDeterminingEverything(RelationSchema schema, Key key);

  public boolean areFdSetsEquivalent(ArrayList<FunctionalDependency> list1, ArrayList<FunctionalDependency> list2);
}
