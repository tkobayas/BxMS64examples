package com.sample;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class SerializeTest {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            KieBase kieBase = kContainer.getKieBase();
            KieSession kSession = kieBase.newKieSession();

            Person john = new Person("john", 20);
            kSession.insert(john);

            Marshaller marshaller = createMarshaller(kieBase);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshall(baos, kSession);

            byte[] bytes = baos.toByteArray();
            baos.close();
            
            Path path = Paths.get("./work/ksession.dat");
            Files.write(path, bytes);

            kSession.dispose();
            
            System.out.println("=== finish");
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static Marshaller createMarshaller(KieBase kieBase) {
        KieServices ks = KieServices.Factory.get();
        KieMarshallers marshallers = ks.getMarshallers();
        ObjectMarshallingStrategy strategy = marshallers.newSerializeMarshallingStrategy();
        return marshallers.newMarshaller(kieBase, new ObjectMarshallingStrategy[]{strategy});
    }
}
