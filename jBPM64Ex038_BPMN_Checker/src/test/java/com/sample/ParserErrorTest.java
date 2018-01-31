package com.sample;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.drools.core.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.junit.Test;

public class ParserErrorTest {

    private final String BPMN_BASE_DIR = "./bpmn/";

    @Test
    public void testXML() throws Exception {

        try {
            SemanticModules modules = new SemanticModules();
            modules.addSemanticModule(new BPMNSemanticModule());
            modules.addSemanticModule(new BPMNDISemanticModule());
            XmlProcessReader processReader = new XmlProcessReader(modules, getClass().getClassLoader());

            Path bpmnDir = Paths.get(BPMN_BASE_DIR);
            Files.walk(bpmnDir, Integer.MAX_VALUE)
            .filter(s -> !Files.isDirectory(s) && (s.toString().endsWith("bpmn") || s.toString().endsWith("bpmn2")))
            .forEach(s -> {
                System.out.println(s.toString());
                try (InputStream is = Files.newInputStream(s)) {
                    processReader.read(is);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            });

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

}