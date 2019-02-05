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

public class NMEAGPGLLMessageTranslator extends NMEAMessageTranslator {

  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(NMEAGPGLLMessageTranslator.class);

  public NMEAGPGLLMessageTranslator() {
  }

  @Override
  public void translate(GeoEvent geoEvent, String[] data) throws FieldException {
    int i = 1;
    geoEvent.setField(i++, toTime(data[5], null));
    geoEvent.setField(i++, toPoint(data[1], data[3], "N".equals(data[2]), "E".equals(data[4])));
    geoEvent.setField(i++, data[6].split("\\*")[0]);
  }

  @Override
  public void validate(String[] data) throws ValidationException {
    if (data == null || data.length != 7) {
      throw new ValidationException(LOGGER.translate("INVALID_NMEAGPGLL_MSG"));
    }
  }

  @Override
  public GeoEventDefinition getGeoEventDefinition() throws ConfigurationException {
    GeoEventDefinition GPGLL = new DefaultGeoEventDefinition();
    GPGLL.setName("GPGLL");
    GPGLL.setAccessType(AccessType.editable);

    List<FieldDefinition> GPGLLFields = new ArrayList<FieldDefinition>();

    GPGLLFields.add(new DefaultFieldDefinition("DeviceId", FieldType.Long, "TRACK_ID"));
    GPGLLFields.add(new DefaultFieldDefinition("TimeStamp", FieldType.Date, "TIME_START"));
    GPGLLFields.add(new DefaultFieldDefinition("Shape", FieldType.Geometry, "GEOMETRY"));

    GPGLLFields.add(new DefaultFieldDefinition("Validity", FieldType.String));

    GPGLL.setFieldDefinitions(GPGLLFields);

    return GPGLL;
  }
}
