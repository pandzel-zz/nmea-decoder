/*
  Copyright 2019 Esri

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

  For additional information, contact:
  Environmental Systems Research Institute, Inc.
  Attn: Contracts Dept
  380 New York Street
  Redlands, California, USA 92373

  email: contracts@esri.com
*/
package com.esri.geoevent.processor.nmea.decoder;



import com.esri.ges.core.AccessType;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.DefaultGeoEventDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;
import java.util.ArrayList;
import java.util.List;

public class NMEADecoderDefinition extends GeoEventProcessorDefinitionBase {

	public NMEADecoderDefinition() throws PropertyException {
    PropertyDefinition nmeaDataField = new PropertyDefinition(
            "nmeaDataField", PropertyType.String, "", 
            "${com.esri.geoevent.processor.nmea-decoder-processor.DATA_FIELD_LBL}", 
            "${com.esri.geoevent.processor.nmea-decoder-processor.DATA_FIELD_DESC}", 
            Boolean.TRUE, Boolean.FALSE);
    propertyDefinitions.put(nmeaDataField.getPropertyName(), nmeaDataField);
    
    try {
      GeoEventDefinition def = new DefaultGeoEventDefinition();
      def.setName("NMEAGPRMC");
      def.setAccessType(AccessType.editable);

      List<FieldDefinition> topLevelFields = new ArrayList<FieldDefinition>();
      
      topLevelFields.add(new DefaultFieldDefinition("DeviceId", FieldType.Long, "TRACK_ID"));
      topLevelFields.add(new DefaultFieldDefinition("TimeStamp", FieldType.Date, "TIME_START"));
      topLevelFields.add(new DefaultFieldDefinition("Shape", FieldType.Geometry, "GEOMETRY"));
      
      topLevelFields.add(new DefaultFieldDefinition("Validity", FieldType.String));
      topLevelFields.add(new DefaultFieldDefinition("Speed", FieldType.Double));
      topLevelFields.add(new DefaultFieldDefinition("Course", FieldType.Double));
      topLevelFields.add(new DefaultFieldDefinition("Variation", FieldType.Double));
      topLevelFields.add(new DefaultFieldDefinition("EastWest", FieldType.String));
      topLevelFields.add(new DefaultFieldDefinition("Mode", FieldType.String));
      
      def.setFieldDefinitions(topLevelFields);

      geoEventDefinitions.put(def.getName(), def);
    } catch (ConfigurationException ex) {
      throw new PropertyException(String.format("Error creating geoevent definition. %s", ex.getMessage()));
    }
  }

	@Override
	public String getName() {
		return "NMEADecoder";
	}

	@Override
	public String getDomain() {
		return "com.esri.geoevent.processor.nmea";
	}

	@Override
	public String getVersion() {
		return "10.6.1";
	}

	@Override
	public String getLabel() {
		return "${com.esri.geoevent.processor.nmea-decoder-processor.MAIN_LBL}";
	}

	@Override
	public String getDescription() {
		return "${com.esri.geoevent.processor.nmea-decoder-processor.MAIN_DESC}";
	}

	@Override
	public String getContactInfo() {
		return "geoeventprocessor@esri.com";
	}
}
