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

import com.esri.geoevent.processor.nmea.decoder.translator.NMEAMessageTranslator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.validation.ValidationException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class NMEADecoder extends GeoEventProcessorBase {

  private static final Log LOG = LogFactory.getLog(NMEADecoder.class);

  private final GeoEventCreator geoEventCreator;
  private final GeoEventDefinitionManager geoDefinitionManager;
  private final Map<String, NMEAMessageTranslator> translators;
  private String nmeaDataField;

  public NMEADecoder(GeoEventProcessorDefinition definition, GeoEventCreator geoEventCreator, GeoEventDefinitionManager geoDefinitionManager, Map<String, NMEAMessageTranslator> translators) throws ComponentException {
    super(definition);
    this.geoEventCreator = geoEventCreator;
    this.geoDefinitionManager = geoDefinitionManager;
    this.translators = translators;
  }

  @Override
  public boolean isGeoEventMutator() {
    return true;
  }

  @Override
  public void afterPropertiesSet() {
    if (hasProperty("nmeaDataField")) {
      nmeaDataField = getProperty("nmeaDataField").getValueAsString();
    }
  }

  @Override
  public GeoEvent process(GeoEvent ge) throws Exception {

    if (nmeaDataField == null || ge.getField(nmeaDataField) == null) {
      LOG.debug(String.format("Unable to process event"));
      return null;
    }

    String nmeaData = StringUtils.trimToEmpty(ge.getField(nmeaDataField).toString());
    String[] elements = nmeaData.split(",");

    if (elements == null || elements.length == 0) {
      LOG.debug(String.format("Invalid NMEA data: %s", nmeaData));
      return null;
    }

    String type = elements[0].substring(1);
    NMEAMessageTranslator translator = translators.get(type);
    GeoEventDefinition eventDefinition = ((NMEADecoderDefinition) definition).getGeoEventDefinition(type);

    if (translator == null || eventDefinition == null) {
      LOG.debug(String.format("Unsupported NMEA type: %s", type));
      return null;
    }

    try {
      translator.validate(elements);
    } catch (ValidationException ex) {
      LOG.debug(String.format("Invalid NMEA data: %s", nmeaData), ex);
      return null;
    }

    eventDefinition = eventDefinition.augment(ge.getGeoEventDefinition().getFieldDefinitions());
    ((NMEADecoderDefinition)definition).getGeoEventDefinitions().put(eventDefinition.getName(), eventDefinition);
    
    GeoEvent outEvent = geoEventCreator.create(eventDefinition.getGuid());
    translator.translate(outEvent, elements);
    outEvent.setAllFields(ge.getAllFields());

    return outEvent;
  }
}
