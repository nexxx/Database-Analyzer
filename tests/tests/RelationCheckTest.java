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

import dbaCore.data.Attribute;
import dbaCore.data.FunctionalDependency;
import dbaCore.data.Key;
import dbaCore.data.RelationSchema;
import dbaCore.logic.Analysis.GeneralRelationCheck;
import dbaCore.utils.Utilities;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class RelationCheckTest {

  @Test
  public void testGetPrimaryKey() {
    // R=({A,B,C,D,E,F},{A==>B,C==>DE,AC==>F})
    Key primaryKey;
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", true, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", true, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    Attribute attrF = new Attribute("F", false, false);
    ArrayList<Attribute> attributes = new ArrayList<>();

    attrA.setIsForeignKey(true);
    attrC.setIsForeignKey(true);

    attributes.add(attrA);
    attributes.add(attrB);
    attributes.add(attrC);
    attributes.add(attrD);
    attributes.add(attrE);
    attributes.add(attrF);

    RelationSchema schema = new RelationSchema("TestRelation", attributes, null);

    primaryKey = checker.getPrimaryKey(schema);

    assertEquals("A<pk><fk>,C<pk><fk>", Utilities.getStringFromArrayList(primaryKey.getAttributes()));
  }

  @Test
  public void testgetMinimalSetOfFds() {
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrD = new Attribute("D", false, false);

    FunctionalDependency fd = new FunctionalDependency();
    ArrayList<FunctionalDependency> fds = new ArrayList<>();
    ArrayList<FunctionalDependency> result;

    // B==>A
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrA);
    fds.add(fd);

    // D==>A
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrD);
    fd.getTargetAttributes().add(attrA);
    fds.add(fd);

    // AB==>D
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrD);
    fds.add(fd);

    result = checker.getMinimalSetOfFds(fds);
    assertEquals("[D] -> [A],[B] -> [D]", Utilities.getStringFromArrayList(result));
    assertTrue(checker.areFdSetsEquivalent(fds, result));
  }

  @Test
  public void testgetMinimalSetOfFds2() {
    // G= {A==>BC, B==>C, AB==>D}
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);

    FunctionalDependency fd = new FunctionalDependency();
    ArrayList<FunctionalDependency> fds = new ArrayList<>();
    ArrayList<FunctionalDependency> result;

    // A==>BC
    fd.getSourceAttributes().add(attrA);
    fd.getTargetAttributes().add(attrB);
    fd.getTargetAttributes().add(attrC);
    fds.add(fd);

    // B==>C
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrC);
    fds.add(fd);

    // AB==>D
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrD);
    fds.add(fd);

    result = checker.getMinimalSetOfFds(fds);
    assertEquals("[A] -> [B],[B] -> [C],[A] -> [D]", Utilities.getStringFromArrayList(result));
    assertTrue(checker.areFdSetsEquivalent(fds, result));
  }

  @Test
  public void testIsPrimeAttribute() {
    GeneralRelationCheck checker = new GeneralRelationCheck();
    ArrayList<Key> candidateKeys = new ArrayList<>();
    Key tmpKey = new Key();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);

    tmpKey.getAttributes().add(attrA);
    tmpKey.getAttributes().add(attrB);
    candidateKeys.add(tmpKey);

    tmpKey = new Key();
    tmpKey.getAttributes().add(attrA);
    tmpKey.getAttributes().add(attrC);
    candidateKeys.add(tmpKey);

    assertTrue(checker.isPrimeAttribute(attrA, candidateKeys));
    assertTrue(checker.isPrimeAttribute(attrB, candidateKeys));
    assertTrue(checker.isPrimeAttribute(attrC, candidateKeys));
    assertFalse(checker.isPrimeAttribute(attrD, candidateKeys));
    assertFalse(checker.isPrimeAttribute(attrE, candidateKeys));
  }

  @Test
  public void testRemoveUnneccessarySourceAttributes() {
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrD = new Attribute("D", false, false);

    FunctionalDependency fd = new FunctionalDependency();
    ArrayList<FunctionalDependency> fds = new ArrayList<>();
    ArrayList<FunctionalDependency> result;

    // B==>A
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrA);
    fds.add(fd);

    // D==>A
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrD);
    fd.getTargetAttributes().add(attrA);
    fds.add(fd);

    // AB==>D
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrD);
    fds.add(fd);

    result = checker.removeUnneccessarySourceAttributes(fds);
    assertEquals("[B] -> [A],[D] -> [A],[B] -> [D]", Utilities.getStringFromArrayList(result));
  }

  @Test
  public void testGetAllCandidateKeys() {
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);

    ArrayList<Attribute> attributes = new ArrayList<>();

    attributes.add(attrA);
    attributes.add(attrB);
    attributes.add(attrC);
    attributes.add(attrD);

    GeneralRelationCheck checker = new GeneralRelationCheck();
    ArrayList<FunctionalDependency> fds = new ArrayList<>();
    FunctionalDependency fd;
    ArrayList<Key> candidateKeys;

    // C==>D
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrC);
    fd.getTargetAttributes().add(attrD);
    fds.add(fd);

    // B==>C
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrC);
    fds.add(fd);

    // B==>A
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrA);
    fds.add(fd);

    // AD==>B
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getSourceAttributes().add(attrD);
    fd.getTargetAttributes().add(attrB);
    fds.add(fd);

    RelationSchema schema = new RelationSchema("test");
    schema.getAttributes().addAll(attributes);
    schema.getFunctionalDependencies().addAll(fds);

    candidateKeys = checker.getAllCandidateKeys(schema);
    assertEquals("(B),(A,C),(A,D)", Utilities.getStringFromArrayList(candidateKeys));

  }

  @Test
  public void testGetAllCandidateKeys2() {

    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    Attribute attrF = new Attribute("F", false, false);
    Attribute attrG = new Attribute("G", false, false);

    GeneralRelationCheck checker = new GeneralRelationCheck();
    ArrayList<FunctionalDependency> fds = new ArrayList<>();
    FunctionalDependency fd;
    ArrayList<Key> candidateKeys;
    ArrayList<Attribute> attributes = new ArrayList<>();

    attributes.add(attrA);
    attributes.add(attrB);
    attributes.add(attrC);
    attributes.add(attrD);
    attributes.add(attrE);
    attributes.add(attrF);
    attributes.add(attrG);

    // A==>CD
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getTargetAttributes().add(attrC);
    fd.getTargetAttributes().add(attrD);
    fds.add(fd);

    // C==>F
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrC);
    fd.getTargetAttributes().add(attrF);
    fds.add(fd);

    // FA==>DB
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrF);
    fd.getSourceAttributes().add(attrA);
    fd.getTargetAttributes().add(attrD);
    fd.getTargetAttributes().add(attrB);
    fds.add(fd);

    // G==>C
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrG);
    fd.getTargetAttributes().add(attrC);
    fds.add(fd);

    // A==>EA
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getTargetAttributes().add(attrE);
    fd.getTargetAttributes().add(attrA);
    fds.add(fd);

    // DC==>AB
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrD);
    fd.getSourceAttributes().add(attrC);
    fd.getTargetAttributes().add(attrA);
    fd.getTargetAttributes().add(attrB);
    fds.add(fd);

    RelationSchema schema = new RelationSchema("test");
    schema.getAttributes().addAll(attributes);
    schema.getFunctionalDependencies().addAll(fds);

    candidateKeys = checker.getAllCandidateKeys(schema);
    assertEquals("(G,A),(G,D)", Utilities.getStringFromArrayList(candidateKeys));
  }

  @Test
  public void testGetAllCandidateKeys3() {
    // R=({A,B,C,D,E}, {AB==>C, CD==>E, DE==>B})
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);

    ArrayList<Attribute> attributes = new ArrayList<>();

    attributes.add(attrA);
    attributes.add(attrB);
    attributes.add(attrC);
    attributes.add(attrD);

    GeneralRelationCheck checker = new GeneralRelationCheck();
    ArrayList<FunctionalDependency> fds = new ArrayList<>();
    FunctionalDependency fd;
    ArrayList<Key> candidateKeys;

    // AB==>C
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getSourceAttributes().add(attrB);
    fd.getTargetAttributes().add(attrC);
    fds.add(fd);

    // CD==>E
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrC);
    fd.getSourceAttributes().add(attrD);
    fd.getTargetAttributes().add(attrE);
    fds.add(fd);

    // DE==>B
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrD);
    fd.getSourceAttributes().add(attrE);
    fd.getTargetAttributes().add(attrB);
    fds.add(fd);

    RelationSchema schema = new RelationSchema("test");
    schema.getAttributes().addAll(attributes);
    schema.getFunctionalDependencies().addAll(fds);

    candidateKeys = checker.getAllCandidateKeys(schema);
    assertEquals("(A,D,B),(A,D,C)", Utilities.getStringFromArrayList(candidateKeys));
  }

  @Test
  public void testGetNumberOfMatchingAttributes() {
    GeneralRelationCheck checker = new GeneralRelationCheck();

    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    Attribute attrF = new Attribute("F", false, false);
    Attribute attrG = new Attribute("G", false, false);

    ArrayList<Attribute> list1 = new ArrayList<>();
    ArrayList<Attribute> list2 = new ArrayList<>();

    list1.add(attrA);
    list1.add(attrB);
    list1.add(attrC);
    list1.add(attrD);
    list1.add(attrE);
    list1.add(attrF);

    assertEquals(0, checker.getNumberOfMatchingAttributes(list1, list2));

    list2.add(attrA);
    assertEquals(1, checker.getNumberOfMatchingAttributes(list1, list2));

    list2.add(attrG);
    assertEquals(1, checker.getNumberOfMatchingAttributes(list1, list2));

    list2.add(attrB);
    list2.add(attrC);
    list2.add(attrD);
    list2.add(attrE);
    list2.add(attrF);

    assertEquals(6, checker.getNumberOfMatchingAttributes(list1, list2));
  }

  @Test
  public void testgetSubsetOfAttributes() {
    GeneralRelationCheck checker = new GeneralRelationCheck();
    ArrayList<ArrayList<Attribute>> result;
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);

    // Attributes {A,B,C}
    ArrayList<Attribute> attributes = new ArrayList<>();
    result = new ArrayList<>();

    attributes.add(attrA);
    attributes.add(attrB);
    attributes.add(attrC);

    result = checker.getSubsetOfAttributes(attributes, result);
    assertEquals(6, result.size());

    // Attributes {A,B}
    attributes = new ArrayList<>();
    result = new ArrayList<>();

    attributes.add(attrA);
    attributes.add(attrB);

    result = checker.getSubsetOfAttributes(attributes, result);
    assertEquals(2, result.size());

    // Attributes {A}
    attributes = new ArrayList<>();
    result = new ArrayList<>();

    attributes.add(attrA);

    result = checker.getSubsetOfAttributes(attributes, result);
    assertEquals(0, result.size());

    // Attributes {}
    attributes = new ArrayList<>();
    result = new ArrayList<>();

    result = checker.getSubsetOfAttributes(attributes, result);
    assertEquals(0, result.size());

  }

  @Test
  public void testMakeFdCannonical() {
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    Attribute attrF = new Attribute("F", false, false);

    GeneralRelationCheck checker = new GeneralRelationCheck();
    FunctionalDependency fd;
    ArrayList<FunctionalDependency> result;

    // Let the tests begin
    // A==>B
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getTargetAttributes().add(attrB);
    result = checker.makeFdCannonical(fd);
    assertEquals(1, result.size());
    checkRightSideOfFds(result);

    // C==>DE
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrC);
    fd.getTargetAttributes().add(attrD);
    fd.getTargetAttributes().add(attrE);
    result = checker.makeFdCannonical(fd);
    assertEquals(2, result.size());
    checkRightSideOfFds(result);

    // ABC==>DEF
    fd = new FunctionalDependency();
    fd.getSourceAttributes().add(attrA);
    fd.getSourceAttributes().add(attrB);
    fd.getSourceAttributes().add(attrC);
    fd.getTargetAttributes().add(attrD);
    fd.getTargetAttributes().add(attrE);
    fd.getTargetAttributes().add(attrF);
    result = checker.makeFdCannonical(fd);
    assertEquals(3, result.size());
    checkRightSideOfFds(result);

  }

  private void checkRightSideOfFds(ArrayList<FunctionalDependency> fdList) {
    for (FunctionalDependency fd : fdList) {
      assertEquals(1, fd.getTargetAttributes().size());
    }
  }

  @Test
  public void testIsPKDeterminingEverything() {
    // R=({A,B,C,D,E,F},{A==>B,C==>DE,AC==>F})
    Key testKey;
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    Attribute attrF = new Attribute("F", false, false);
    ArrayList<Attribute> attributes = new ArrayList<>();
    attributes.add(attrA);
    attributes.add(attrB);
    attributes.add(attrC);
    attributes.add(attrD);
    attributes.add(attrE);
    attributes.add(attrF);

    ArrayList<Attribute> sourceAttributes = new ArrayList<>();
    ArrayList<Attribute> targetAttributes = new ArrayList<>();
    ArrayList<Attribute> sourceAttributes2 = new ArrayList<>();
    ArrayList<Attribute> targetAttributes2 = new ArrayList<>();
    ArrayList<Attribute> sourceAttributes3 = new ArrayList<>();
    ArrayList<Attribute> targetAttributes3 = new ArrayList<>();

    sourceAttributes.add(attrA);
    targetAttributes.add(attrB);
    FunctionalDependency fd1 = new FunctionalDependency(sourceAttributes, targetAttributes);

    sourceAttributes2.add(attrC);
    targetAttributes2.add(attrD);
    targetAttributes2.add(attrE);
    FunctionalDependency fd2 = new FunctionalDependency(sourceAttributes2, targetAttributes2);

    sourceAttributes3.add(attrA);
    sourceAttributes3.add(attrC);
    targetAttributes3.add(attrF);

    FunctionalDependency fd3 = new FunctionalDependency(sourceAttributes3, targetAttributes3);
    ArrayList<FunctionalDependency> fdList = new ArrayList<>();
    fdList.add(fd1);
    fdList.add(fd2);
    fdList.add(fd3);

    RelationSchema schema = new RelationSchema("TestRelation", attributes, fdList);

    // Key={A}
    testKey = new Key();
    testKey.getAttributes().add(attrA);
    assertFalse(checker.isKeyDeterminingEverything(schema, testKey));

    // Key={C}
    testKey = new Key();
    testKey.getAttributes().add(attrC);
    assertFalse(checker.isKeyDeterminingEverything(schema, testKey));

    // Key={AC}
    testKey = new Key();
    testKey.getAttributes().add(attrA);
    testKey.getAttributes().add(attrC);
    assertTrue(checker.isKeyDeterminingEverything(schema, testKey));
  }

  @Test
  public void testIsMember() {
    // R=(A,B,C,D,E),{C==>A, BD==>A, D==>BC, D==>BC, A==>E}
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    ArrayList<Attribute> attributes = new ArrayList<>();
    attributes.add(attrA);
    attributes.add(attrB);
    attributes.add(attrC);
    attributes.add(attrD);
    attributes.add(attrE);

    ArrayList<FunctionalDependency> fds = new ArrayList<>();

    // C==>A
    ArrayList<Attribute> sourceAttributes1 = new ArrayList<>();
    sourceAttributes1.add(attrC);
    ArrayList<Attribute> targetAttributes1 = new ArrayList<>();
    targetAttributes1.add(attrA);
    fds.add(new FunctionalDependency(sourceAttributes1, targetAttributes1));

    // BD==>A
    ArrayList<Attribute> sourceAttributes2 = new ArrayList<>();
    sourceAttributes2.add(attrB);
    sourceAttributes2.add(attrD);
    ArrayList<Attribute> targetAttributes2 = new ArrayList<>();
    targetAttributes2.add(attrA);
    fds.add(new FunctionalDependency(sourceAttributes2, targetAttributes2));

    // D==>BC
    ArrayList<Attribute> sourceAttributes3 = new ArrayList<>();
    sourceAttributes3.add(attrD);
    ArrayList<Attribute> targetAttributes3 = new ArrayList<>();
    targetAttributes3.add(attrB);
    targetAttributes3.add(attrC);
    fds.add(new FunctionalDependency(sourceAttributes3, targetAttributes3));

    // D==>BC
    ArrayList<Attribute> sourceAttributes4 = new ArrayList<>();
    sourceAttributes4.add(attrD);
    ArrayList<Attribute> targetAttributes4 = new ArrayList<>();
    targetAttributes4.add(attrB);
    targetAttributes4.add(attrC);
    fds.add(new FunctionalDependency(sourceAttributes4, targetAttributes4));

    // A==>E
    ArrayList<Attribute> sourceAttributes5 = new ArrayList<>();
    sourceAttributes5.add(attrA);
    ArrayList<Attribute> targetAttributes5 = new ArrayList<>();
    targetAttributes5.add(attrE);
    fds.add(new FunctionalDependency(sourceAttributes5, targetAttributes5));

    RelationSchema schemaToTest = new RelationSchema("test", attributes, fds);

    // Functional Dependencies to test
    // AC==>B
    ArrayList<Attribute> sourceAttributes6 = new ArrayList<>();
    sourceAttributes6.add(attrA);
    sourceAttributes6.add(attrC);
    ArrayList<Attribute> targetAttributes6 = new ArrayList<>();
    targetAttributes6.add(attrB);
    FunctionalDependency testFd1 = new FunctionalDependency(sourceAttributes6, targetAttributes6);

    // BD==>E
    ArrayList<Attribute> sourceAttributes7 = new ArrayList<>();
    sourceAttributes7.add(attrB);
    sourceAttributes7.add(attrD);
    ArrayList<Attribute> targetAttributes7 = new ArrayList<>();
    targetAttributes7.add(attrE);
    FunctionalDependency testFd2 = new FunctionalDependency(sourceAttributes7, targetAttributes7);

    assertFalse(checker.isMember(schemaToTest, testFd1));
    assertTrue(checker.isMember(schemaToTest, testFd2));
  }

  @Test
  public void testGetClosure() {
    // R=({A,B,C,D,E,F},{A==>B,C==>DE,AC==>F})
    ArrayList<Attribute> result;
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    Attribute attrF = new Attribute("F", false, false);
    ArrayList<Attribute> attributes = new ArrayList<>();
    attributes.add(attrA);
    attributes.add(attrB);
    attributes.add(attrC);
    attributes.add(attrD);
    attributes.add(attrE);
    attributes.add(attrF);

    ArrayList<Attribute> sourceAttributes = new ArrayList<>();
    ArrayList<Attribute> targetAttributes = new ArrayList<>();
    ArrayList<Attribute> sourceAttributes2 = new ArrayList<>();
    ArrayList<Attribute> targetAttributes2 = new ArrayList<>();
    ArrayList<Attribute> sourceAttributes3 = new ArrayList<>();
    ArrayList<Attribute> targetAttributes3 = new ArrayList<>();

    sourceAttributes.add(attrA);
    targetAttributes.add(attrB);
    FunctionalDependency fd1 = new FunctionalDependency(sourceAttributes, targetAttributes);

    sourceAttributes2.add(attrC);
    targetAttributes2.add(attrD);
    targetAttributes2.add(attrE);
    FunctionalDependency fd2 = new FunctionalDependency(sourceAttributes2, targetAttributes2);

    sourceAttributes3.add(attrA);
    sourceAttributes3.add(attrC);
    targetAttributes3.add(attrF);

    FunctionalDependency fd3 = new FunctionalDependency(sourceAttributes3, targetAttributes3);
    ArrayList<FunctionalDependency> fdList = new ArrayList<>();
    fdList.add(fd1);
    fdList.add(fd2);
    fdList.add(fd3);

    // X ={A}
    ArrayList<Attribute> xList = new ArrayList<>();
    xList.add(attrA);

    RelationSchema schema = new RelationSchema("TestRelation", attributes, fdList);
    result = checker.getClosure(schema, xList);

    assertEquals(2, result.size());
    assertEquals("A,B", Utilities.getStringFromArrayList(result));

    // X ={F}
    xList = new ArrayList<>();
    xList.add(attrF);
    result = checker.getClosure(schema, xList);
    assertEquals(1, result.size());
    assertEquals("F", Utilities.getStringFromArrayList(result));

    // X ={A,C}
    xList = new ArrayList<>();
    xList.add(attrA);
    xList.add(attrC);
    result = checker.getClosure(schema, xList);
    assertEquals(6, result.size());
    assertEquals("A,C,B,D,E,F", Utilities.getStringFromArrayList(result));

    // X ={B}
    xList = new ArrayList<>();
    xList.add(attrB);
    result = checker.getClosure(schema, xList);
    assertEquals(1, result.size());
    assertEquals("B", Utilities.getStringFromArrayList(result));

    // X ={C}
    xList = new ArrayList<>();
    xList.add(attrC);
    result = checker.getClosure(schema, xList);
    assertEquals(3, result.size());
    assertEquals("C,D,E", Utilities.getStringFromArrayList(result));
  }

  @Test
  public void testAreFdSetsEquivalent() {
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);

    FunctionalDependency tempFd;
    ArrayList<FunctionalDependency> list1;
    ArrayList<FunctionalDependency> list2;

    // F={AB==>C, A==>B, A==>C, B==>C)
    // G={AB==>C, A==>B, , B==>C)
    list1 = new ArrayList<>();
    list2 = new ArrayList<>();

    // F
    // AB==>C
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrA);
    tempFd.getSourceAttributes().add(attrB);
    tempFd.getTargetAttributes().add(attrC);
    list1.add(tempFd);

    // A==>B
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrA);
    tempFd.getTargetAttributes().add(attrB);
    list1.add(tempFd);

    // A==>C
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrA);
    tempFd.getTargetAttributes().add(attrC);
    list1.add(tempFd);

    // B==>C
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrB);
    tempFd.getTargetAttributes().add(attrC);
    list1.add(tempFd);

    // G
    // AB==>C
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrA);
    tempFd.getSourceAttributes().add(attrB);
    tempFd.getTargetAttributes().add(attrC);
    list2.add(tempFd);

    // A==>B
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrA);
    tempFd.getTargetAttributes().add(attrB);
    list2.add(tempFd);

    // B==>C
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrB);
    tempFd.getTargetAttributes().add(attrC);
    list2.add(tempFd);

    assertTrue(checker.areFdSetsEquivalent(list1, list2));
  }

  @Test
  public void testAreFdSetsEquivalent2() {
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrB = new Attribute("B", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    Attribute attrF = new Attribute("F", false, false);

    FunctionalDependency tempFd;
    ArrayList<FunctionalDependency> list1;
    ArrayList<FunctionalDependency> list2;

    // F={C==>A, CE==>B, E==>CD, F==>B)
    // G={C==>A, ,E==>CD, F==>B, E==>A, E==>B)
    list1 = new ArrayList<>();
    list2 = new ArrayList<>();

    // F
    // C==>A
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrC);
    tempFd.getTargetAttributes().add(attrA);
    list1.add(tempFd);

    // CE==>B
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrC);
    tempFd.getSourceAttributes().add(attrE);
    tempFd.getTargetAttributes().add(attrB);
    list1.add(tempFd);

    // E==>CD
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrE);
    tempFd.getTargetAttributes().add(attrC);
    tempFd.getTargetAttributes().add(attrD);
    list1.add(tempFd);

    // F==>B
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrF);
    tempFd.getTargetAttributes().add(attrB);
    list1.add(tempFd);

    // G
    // C==>A
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrC);
    tempFd.getTargetAttributes().add(attrA);
    list2.add(tempFd);

    // E==>CD
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrE);
    tempFd.getTargetAttributes().add(attrC);
    tempFd.getTargetAttributes().add(attrD);
    list2.add(tempFd);

    // F==>B
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrF);
    tempFd.getTargetAttributes().add(attrB);
    list2.add(tempFd);

    // E==>A
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrE);
    tempFd.getTargetAttributes().add(attrA);
    list2.add(tempFd);

    // E==>B
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrE);
    tempFd.getTargetAttributes().add(attrB);
    list2.add(tempFd);

    assertTrue(checker.areFdSetsEquivalent(list1, list2));
  }

  @Test
  public void testAreFdSetsEquivalent3() {
    GeneralRelationCheck checker = new GeneralRelationCheck();
    Attribute attrA = new Attribute("A", false, false);
    Attribute attrC = new Attribute("C", false, false);
    Attribute attrD = new Attribute("D", false, false);
    Attribute attrE = new Attribute("E", false, false);
    Attribute attrH = new Attribute("H", false, false);

    FunctionalDependency tempFd;
    ArrayList<FunctionalDependency> list1;
    ArrayList<FunctionalDependency> list2;

    // F={A==>C, AC==>D, E==>AD, E==>H)
    // G={A==>CD, E==>AH}
    list1 = new ArrayList<>();
    list2 = new ArrayList<>();

    // F
    // A==>C
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrA);
    tempFd.getTargetAttributes().add(attrC);
    list1.add(tempFd);

    // AC==>D
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrA);
    tempFd.getSourceAttributes().add(attrC);
    tempFd.getTargetAttributes().add(attrD);
    list1.add(tempFd);

    // E==>AD
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrE);
    tempFd.getTargetAttributes().add(attrA);
    tempFd.getTargetAttributes().add(attrD);
    list1.add(tempFd);

    // E==>H
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrE);
    tempFd.getTargetAttributes().add(attrH);
    list1.add(tempFd);

    // G
    // A==>CD
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrA);
    tempFd.getTargetAttributes().add(attrC);
    tempFd.getTargetAttributes().add(attrD);
    list2.add(tempFd);

    // E==>AH
    tempFd = new FunctionalDependency();
    tempFd.getSourceAttributes().add(attrE);
    tempFd.getTargetAttributes().add(attrA);
    tempFd.getTargetAttributes().add(attrH);
    list2.add(tempFd);

    assertTrue(checker.areFdSetsEquivalent(list1, list2));
  }
}
