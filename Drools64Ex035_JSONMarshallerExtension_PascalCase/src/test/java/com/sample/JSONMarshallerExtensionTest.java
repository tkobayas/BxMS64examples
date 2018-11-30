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

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;

public class JSONMarshallerExtensionTest {

    @Test
    public void testCustomExtensionMarshaller() {
        Set<Class<?>> extraClasses = new HashSet<Class<?>>();
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());
        
        Person person = new Person();
        person.setName("John");
        
        String marshall = marshaller.marshall(person);
        System.out.println(marshall);
        assertEquals("{\n  \"Name\" : \"John\"\n}", marshall);
        
        Person unmarshall = marshaller.unmarshall(marshall, Person.class);
        assertEquals(unmarshall.getName(), person.getName());
    }
    
}
