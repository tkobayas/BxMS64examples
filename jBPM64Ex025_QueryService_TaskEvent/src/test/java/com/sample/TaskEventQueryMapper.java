package com.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.jbpm.kie.services.impl.query.mapper.AbstractQueryMapper;
import org.jbpm.services.api.query.QueryResultMapper;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.kie.internal.task.api.model.TaskEvent;

public class TaskEventQueryMapper extends AbstractQueryMapper<TaskEvent> implements QueryResultMapper<List<TaskEvent>> {
        
        private static final long serialVersionUID = 1L;

        public TaskEventQueryMapper() {
        }
        
        public static TaskEventQueryMapper get() {
            return new TaskEventQueryMapper();
        }

        @Override
        public List<TaskEvent> map(Object result) {
            if (result instanceof DataSet) {
                DataSet dataSetResult = (DataSet) result;
                List<TaskEvent> mappedResult = new ArrayList<TaskEvent>();
                
                if (dataSetResult != null) {
                    
                    for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                        TaskEvent te = buildInstance(dataSetResult, i);
                        mappedResult.add(te);
                    
                    }
                }
                
                return mappedResult;
            }
            
            throw new IllegalArgumentException("Unsupported result for mapping " + result);
        }
        
        protected TaskEvent buildInstance(DataSet dataSetResult, int index) {
            TaskEventImpl te = new TaskEventImpl(
                    getColumnLongValue(dataSetResult, "TASKID", index),
                    getColumnTaskEventTypeValue(dataSetResult, "TYPE", index),
                    getColumnLongValue(dataSetResult, "PROCESSINSTANCEID", index),
                    getColumnLongValue(dataSetResult, "WORKITEMID", index),
                    getColumnStringValue(dataSetResult, "USERID", index),
                    getColumnDateValue(dataSetResult, "LOGTIME", index)
                    );
            return te;
        }

        @Override
        public String getName() {
            return "ProcessInstances";
        }

        @Override
        public Class<?> getType() {
            return TaskEvent.class;
        }

        @Override
        public QueryResultMapper<List<TaskEvent>> forColumnMapping(Map<String, String> columnMapping) {
            return new TaskEventQueryMapper();
        }

        protected TaskEvent.TaskEventType getColumnTaskEventTypeValue(DataSet currentDataSet, String columnId, int index){
            
            String string = getColumnStringValue(currentDataSet, columnId, index);
            
            TaskEvent.TaskEventType[] typeArray = TaskEvent.TaskEventType.values();

            for(TaskEvent.TaskEventType type : typeArray) {
                if (string.equals(type.toString())){
                    return type;
                }
            }
            return null;
        }
    }
