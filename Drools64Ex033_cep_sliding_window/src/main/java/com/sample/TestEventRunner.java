package com.sample;

import org.drools.core.WorkingMemoryEntryPoint;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

public class TestEventRunner implements Runnable {

    private EntryPoint ep;

    private KieSession ksession;

    public KieSession getKsession() {
        return ksession;
    }

    public void setKsession(KieSession ksession) {
        this.ksession = ksession;
    }

    public EntryPoint getEp() {
        return ep;
    }

    public void setEp(EntryPoint ep) {
        this.ep = ep;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000); // make sure ksession started

            //---- Write test events here
            ep.insert(new ParkingEvent("CarA", "ParkingA", ParkingEvent.ENTER));
            Thread.sleep(500);

            ep.insert(new ParkingEvent("CarB", "ParkingA", ParkingEvent.ENTER));
            Thread.sleep(2000);

            ep.insert(new ParkingEvent("CarA", "ParkingA", ParkingEvent.EXIT));
            Thread.sleep(500);

            ep.insert(new ParkingEvent("CarA", "ParkingB", ParkingEvent.ENTER));
            Thread.sleep(2000);

            //----

            Thread.sleep(1000); // make sure all events processed
            ksession.halt();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
