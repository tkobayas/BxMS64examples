package com.sample;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.drools.compiler.compiler.xml.RulesSemanticModule;
import org.drools.core.xml.ChangeSetSemanticModule;
import org.drools.core.xml.SemanticModules;
import org.drools.core.xml.WrapperSemanticModule;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
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
            modules.addSemanticModule(new BPMNExtensionsSemanticModule());
            modules.addSemanticModule(new BPMNExtensionsSemanticModule());

            RulesSemanticModule ruleModule = new RulesSemanticModule("http://ddefault");
            modules.addSemanticModule(new WrapperSemanticModule("http://drools.org/drools-5.0", ruleModule));
            modules.addSemanticModule(new WrapperSemanticModule("http://drools.org/drools-5.2", ruleModule));
            modules.addSemanticModule(new ChangeSetSemanticModule());
            
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