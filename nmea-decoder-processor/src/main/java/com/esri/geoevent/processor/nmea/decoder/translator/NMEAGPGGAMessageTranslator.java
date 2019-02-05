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

public class NMEAGPGGAMessageTranslator extends NMEAMessageTranslator {

  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(NMEAGPGGAMessageTranslator.class);

  public NMEAGPGGAMessageTranslator() {
  }

  @Override
  public void translate(GeoEvent geoEvent, String[] data) throws FieldException {
    int i = 1;
    geoEvent.setField(i++, toTime(data[1], null));
    geoEvent.setField(i++, toPoint(data[2], data[4], "N".equals(data[3]), "E".equals(data[5])));
    geoEvent.setField(i++, convertToShort(data[6]));
    geoEvent.setField(i++, convertToShort(data[7]));
    geoEvent.setField(i++, convertToDouble(data[8]));
    geoEvent.setField(i++, convertToDouble(data[9]));
    geoEvent.setField(i++, data[10]);
    geoEvent.setField(i++, convertToDouble(data[11]));
    geoEvent.setField(i++, data[12]);
    geoEvent.setField(i++, data[13]);
    geoEvent.setField(i++, data[14].split("\\*")[0]);
  }

  @Override
  public void validate(String[] data) throws ValidationException {
    if (data == null || data.length != 15) {
      throw new ValidationException(LOGGER.translate("INVALID_NMEAGPGG_MSG"));
    }
  }

  @Override
  public GeoEventDefinition getGeoEventDefinition() throws ConfigurationException {
    GeoEventDefinition GPGGA = new DefaultGeoEventDefinition();
    GPGGA.setName("GPGGA");
    GPGGA.setAccessType(AccessType.editable);

    List<FieldDefinition> GPGGAFields = new ArrayList<FieldDefinition>();

    GPGGAFields.add(new DefaultFieldDefinition("DeviceId", FieldType.Long, "TRACK_ID"));
    GPGGAFields.add(new DefaultFieldDefinition("TimeStamp", FieldType.Date, "TIME_START"));
    GPGGAFields.add(new DefaultFieldDefinition("Shape", FieldType.Geometry, "GEOMETRY"));

    GPGGAFields.add(new DefaultFieldDefinition("GPSQuality", FieldType.Short));
    GPGGAFields.add(new DefaultFieldDefinition("Satellites", FieldType.Short));
    GPGGAFields.add(new DefaultFieldDefinition("HDOP", FieldType.Double));
    GPGGAFields.add(new DefaultFieldDefinition("Altitude", FieldType.Double));
    GPGGAFields.add(new DefaultFieldDefinition("AltitudeUnits", FieldType.String));
    GPGGAFields.add(new DefaultFieldDefinition("Height", FieldType.Double));
    GPGGAFields.add(new DefaultFieldDefinition("HeightUnits", FieldType.String));
    GPGGAFields.add(new DefaultFieldDefinition("Update", FieldType.String));
    GPGGAFields.add(new DefaultFieldDefinition("StationId", FieldType.String));

    GPGGA.setFieldDefinitions(GPGGAFields);

    return GPGGA;
  }
}
