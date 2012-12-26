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

package dba.gui.auxClasses.jGraph;

import dba.utils.ImageSize;
import dbaCore.data.Attribute;

/**
 * Base-Class for graph-updates
 */
public class RelationUpdater {

  /**
   * Gets the right style for the pk/fk constallation
   *
   * @param attribute attribute to get the style for
   * @return a String representing the Style for the attribute
   */
  protected String getAttributeStyle(Attribute attribute,ImageSize optimalImageSize) {
    if(optimalImageSize.equals(ImageSize.NO)) return "ATTRIBUTE_NOIMAGE";

    if (attribute.getIsPrimaryKey() && attribute.getIsForeignKey()) {
      return "ATTRIBUTE_PKFK";
    } else if (attribute.getIsPrimaryKey()) {
      return  "ATTRIBUTE_PK" + (optimalImageSize.equals(ImageSize.SMALL) ? "_SMALL" : "_BIG");
    } else if (attribute.getIsForeignKey()) {
      return "ATTRIBUTE_FK" + (optimalImageSize.equals(ImageSize.SMALL) ? "_SMALL" : "_BIG");
    } else {
      return "ATTRIBUTE_SPACE" + (optimalImageSize.equals(ImageSize.SMALL) ? "_SMALL" : "_BIG");
    }
  }

  protected ImageSize getImageSizeClass(Attribute attribute){
    if(attribute.getIsPrimaryKey() && attribute.getIsForeignKey()){
      return ImageSize.BIG;
    }else if(attribute.getIsPrimaryKey() || attribute.getIsForeignKey()){
      return ImageSize.SMALL;
    }else {
      return ImageSize.NO;
    }
  }

}
