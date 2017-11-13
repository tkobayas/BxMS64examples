/**
 * 
 */
package com.sample;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.kie.api.runtime.rule.AccumulateFunction;

public class MyAverageAccumulator implements AccumulateFunction {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    public static class AverageData implements Externalizable {
        public int    count = 0;
        public double total = 0;

        public AverageData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            count   = in.readInt();
            total   = in.readDouble();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(count);
            out.writeDouble(total);
        }

    }


    public Serializable createContext() {
        return new AverageData();
    }

    public void init(Serializable context) throws Exception {
        AverageData data = (AverageData) context;
        data.count = 0;
        data.total = 0;
    }



    public void accumulate(Serializable context,
                           Object value) {
        
        Person person = (Person)value;
        double age = person.getAge();
        
        AverageData data = (AverageData) context;
        data.count++;
        data.total += ((Number) age).doubleValue();
    }


    public void reverse(Serializable context,
                        Object value) throws Exception {
        
        Person person = (Person)value;
        double age = person.getAge();
        
        AverageData data = (AverageData) context;
        data.count--;
        data.total -= ((Number) age).doubleValue();
    }


    public Object getResult(Serializable context) throws Exception {
        AverageData data = (AverageData) context;
        return new Double( data.count == 0 ? 0 : data.total / data.count );
    }


    public boolean supportsReverse() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Class< ? > getResultType() {
        return Number.class;
    }

}
