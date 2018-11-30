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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy.PropertyNamingStrategyBase;
import org.kie.server.api.marshalling.json.JSONMarshaller;
import org.kie.server.api.marshalling.json.JSONMarshallerExtension;

public class JSONMarshallerExtensionPascalCase implements JSONMarshallerExtension {

    @Override
    public void extend(JSONMarshaller marshaller, ObjectMapper serializer, ObjectMapper deserializer) {

        System.out.println("JSONMarshallerExtensionPascalCase !");
        serializer.setPropertyNamingStrategy(new PascalCaseStrategy());
        deserializer.setPropertyNamingStrategy(new PascalCaseStrategy());
    }

    public static class PascalCaseStrategy extends PropertyNamingStrategyBase {

        /**
         * Converts camelCase to PascalCase
         * 
         * For example, "userName" would be converted to
         * "UserName".
         *
         * @param input formatted as camelCase string
         * @return input converted to PascalCase format
         */
        @Override
        public String translate(String input) {
            if (input == null || input.length() == 0) {
                return input; // garbage in, garbage out
            }
            // Replace first lower-case letter with upper-case equivalent
            char c = input.charAt(0);
            char uc = Character.toUpperCase(c);
            if (c == uc) {
                return input;
            }
            StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(0, uc);
            return sb.toString();
        }
    }
}
