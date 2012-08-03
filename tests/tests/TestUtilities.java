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
import dbaCore.data.Utilities;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestUtilities {

  @Test
  public void testTryParse() {
    Integer test;

    assertNull(Utilities.tryParseInt("noNumber"));

    test = 666;
    assertEquals(test, Utilities.tryParseInt("666"));

    assertNull(Utilities.tryParseInt(""));

    assertNull(Utilities.tryParseInt(null));

    test = 12;
    assertEquals(test, Utilities.tryParseInt("12"));

    assertNull(Utilities.tryParseInt("2 x 2"));

  }

  @Test
  public void testGetStringFromArrayList() {
    // String
    ArrayList<String> testList = new ArrayList<>();
    testList.add("A");
    testList.add("B");
    testList.add("C");

    assertEquals("A,B,C", Utilities.getStringFromArrayList(testList));

    // Integer
    ArrayList<Integer> intList = new ArrayList<>();
    intList.add(1);
    intList.add(2);
    intList.add(3);

    assertEquals("1,2,3", Utilities.getStringFromArrayList(intList));

    // Attribute
    ArrayList<Attribute> attrList = new ArrayList<>();
    attrList.add(new Attribute("one"));
    attrList.add(new Attribute("two"));
    attrList.add(new Attribute("three"));

    assertEquals("one,two,three", Utilities.getStringFromArrayList(attrList));

  }

}
