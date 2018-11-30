/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.sample;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.kie.server.api.marshalling.json.JSONMarshaller;
import org.kie.server.api.marshalling.json.JSONMarshallerExtension;

public class JSONMarshallerExtensionGregorianCalendar implements JSONMarshallerExtension {

    private static final GregorianCalendarDeser DESERIALIZER = new GregorianCalendarDeser();
    private static final GregorianCalendarSer SERIALIZER = new GregorianCalendarSer();
    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


    @Override
    public void extend(JSONMarshaller marshaller, ObjectMapper serializer, ObjectMapper deserializer) {
        registerModule(serializer);
        registerModule(deserializer);
    }

    private void registerModule(ObjectMapper objectMapper) {
        SimpleModule gregorianCalendarModule = new SimpleModule("gregoriancalendar-module", Version.unknownVersion());
        gregorianCalendarModule.addDeserializer(GregorianCalendar.class, DESERIALIZER);
        gregorianCalendarModule.addSerializer(GregorianCalendar.class, SERIALIZER);
        objectMapper.registerModule(gregorianCalendarModule);
    }

    private static class GregorianCalendarDeser extends JsonDeserializer<GregorianCalendar> {

        @Override
        public GregorianCalendar deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            try {
                String text = jp.getText();
                Date date;
                date = FORMATTER.parse(text);
                gregorianCalendar.setTime(date);
            } catch (ParseException e) {
                throw new IOException(e);
            }
            return gregorianCalendar;                
        }
    }

    private static class GregorianCalendarSer extends JsonSerializer<GregorianCalendar> {

        @Override
        public void serialize(GregorianCalendar value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            
            String formattedValue = FORMATTER.format(value.getTime());
            jgen.writeString(formattedValue);
        }

    }

}
