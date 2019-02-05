/*
  Copyright 1995-2013 Esri

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
package com.esri.geoevent.processor.nmea.decoder.translator;

import com.esri.ges.core.AccessType;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.DefaultGeoEventDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.validation.ValidationException;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class NMEAGPRMCMessageTranslator extends NMEAMessageTranslator {

  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(NMEAGPRMCMessageTranslator.class);

  public NMEAGPRMCMessageTranslator() {
  }

  @Override
  public void translate(GeoEvent geoEvent, String[] data) throws FieldException {
    int i = 1;
    geoEvent.setField(i++, toTime(data[1], data[9]));
    geoEvent.setField(i++, toPoint(data[3], data[5], "N".equals(data[4]), "E".equals(data[6])));
    geoEvent.setField(i++, data[2]);
    geoEvent.setField(i++, convertToDouble(data[7]));
    geoEvent.setField(i++, convertToDouble(data[8]));
    geoEvent.setField(i++, convertToDouble(data[10]));

    if (data.length == 12) {
      geoEvent.setField(i++, data[11].split("\\*")[0]);
    } else {
      geoEvent.setField(i++, data[11]);
      geoEvent.setField(i++, data[12].split("\\*")[0]);
    }
  }

  @Override
  public void validate(String[] data) throws ValidationException {
    if (data == null || data.length < 12 || data.length > 13) {
      throw new ValidationException(LOGGER.translate("INVALID_NMEAGPRMC_MSG"));
    }
  }

  @Override
  public GeoEventDefinition getGeoEventDefinition() throws ConfigurationException {
    GeoEventDefinition GPRMC = new DefaultGeoEventDefinition();
    GPRMC.setName("GPRMC");
    GPRMC.setAccessType(AccessType.editable);

    List<FieldDefinition> GPRMCFields = new ArrayList<FieldDefinition>();

    GPRMCFields.add(new DefaultFieldDefinition("DeviceId", FieldType.Long, "TRACK_ID"));
    GPRMCFields.add(new DefaultFieldDefinition("TimeStamp", FieldType.Date, "TIME_START"));
    GPRMCFields.add(new DefaultFieldDefinition("Shape", FieldType.Geometry, "GEOMETRY"));

    GPRMCFields.add(new DefaultFieldDefinition("Validity", FieldType.String));
    GPRMCFields.add(new DefaultFieldDefinition("Speed", FieldType.Double));
    GPRMCFields.add(new DefaultFieldDefinition("Course", FieldType.Double));
    GPRMCFields.add(new DefaultFieldDefinition("Variation", FieldType.Double));
    GPRMCFields.add(new DefaultFieldDefinition("EastWest", FieldType.String));
    GPRMCFields.add(new DefaultFieldDefinition("Mode", FieldType.String));

    GPRMC.setFieldDefinitions(GPRMCFields);

    return GPRMC;
  }
}
