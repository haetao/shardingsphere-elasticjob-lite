/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.api.type.dataflow.throughput;

import com.dangdang.ddframe.job.api.dataflow.DataflowType;
import com.dangdang.ddframe.job.api.type.dataflow.AbstractDataflowElasticJobExecutorTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class UnstreamingThroughputDataflowElasticJobTest extends AbstractDataflowElasticJobExecutorTest {
    
    public UnstreamingThroughputDataflowElasticJobTest() {
        super(DataflowType.THROUGHPUT, false);
    }
    
    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyAndDataIsOne() {
        when(getJobCaller().fetchData(0)).thenReturn(Collections.<Object>singletonList(1));
        when(getJobCaller().fetchData(1)).thenReturn(Collections.emptyList());
        when(getJobFacade().getConcurrentDataProcessThreadCount()).thenReturn(2);
        getDataflowElasticJobExecutor().execute();
        verify(getJobCaller()).processData(1);
        verify(getJobFacade()).getConcurrentDataProcessThreadCount();
    }
    
    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyAndConcurrentDataProcessThreadCountIsOne() {
        when(getJobCaller().fetchData(0)).thenReturn(Collections.<Object>singletonList(1));
        when(getJobCaller().fetchData(1)).thenReturn(Collections.<Object>singletonList(2));
        when(getJobFacade().getConcurrentDataProcessThreadCount()).thenReturn(1);
        getDataflowElasticJobExecutor().execute();
        verify(getJobCaller()).processData(1);
        verify(getJobCaller()).processData(2);
        verify(getJobFacade()).getConcurrentDataProcessThreadCount();
    }
    
    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyAndConcurrentDataProcessThreadCountIsOneAndProcessFailureWithException() {
        when(getJobCaller().fetchData(0)).thenReturn(Collections.<Object>singletonList(1));
        when(getJobCaller().fetchData(1)).thenReturn(Arrays.<Object>asList(2, 3));
        doThrow(IllegalStateException.class).when(getJobCaller()).processData(2);
        when(getJobFacade().getConcurrentDataProcessThreadCount()).thenReturn(1);
        getDataflowElasticJobExecutor().execute();
        verify(getJobCaller()).processData(1);
        verify(getJobCaller()).processData(2);
        verify(getJobCaller(), times(0)).processData(3);
        verify(getJobFacade()).getConcurrentDataProcessThreadCount();
    }
    
    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyForMultipleThread() {
        when(getJobCaller().fetchData(0)).thenReturn(Arrays.<Object>asList(1, 2));
        when(getJobCaller().fetchData(1)).thenReturn(Arrays.<Object>asList(3, 4));
        when(getJobFacade().getConcurrentDataProcessThreadCount()).thenReturn(2);
        getDataflowElasticJobExecutor().execute();
        verify(getJobCaller()).processData(1);
        verify(getJobCaller()).processData(2);
        verify(getJobCaller()).processData(3);
        verify(getJobCaller()).processData(4);
        verify(getJobFacade()).getConcurrentDataProcessThreadCount();
    }
}