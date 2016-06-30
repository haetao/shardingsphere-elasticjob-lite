/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.cloud.mesos;

import com.dangdang.ddframe.job.cloud.job.config.CloudJobConfiguration;
import com.dangdang.ddframe.job.cloud.task.ElasticJobTask;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.mesos.Protos;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mesos资源工具类.
 *
 * @author zhangliang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MesosUtil {
    
    /**
     * 创建Mesos任务对象.
     * 
     * @param offer 资源提供对象
     * @param cloudJobConfig 云作业配置
     * @param shardingItem 分片项
     * @return
     */
    public static Protos.TaskInfo createTaskInfo(final Protos.Offer offer, final CloudJobConfiguration cloudJobConfig, final int shardingItem) {
        Protos.TaskID taskId = Protos.TaskID.newBuilder().setValue(new ElasticJobTask(cloudJobConfig.getJobName(), shardingItem).getId()).build();
        return Protos.TaskInfo.newBuilder()
                .setName(taskId.getValue())
                .setTaskId(taskId)
                .setSlaveId(offer.getSlaveId())
                .addResources(getResources("cpus", cloudJobConfig.getCpuCount()))
                .addResources(getResources("mem", cloudJobConfig.getMemoryMB()))
                .setExecutor(Protos.ExecutorInfo.newBuilder()
                        .setExecutorId(Protos.ExecutorID.newBuilder().setValue(taskId.getValue()))
                        .setCommand(Protos.CommandInfo.newBuilder().setValue("/Users/zhangliang/docker-sample/elastic-job-example/bin/start.sh " + taskId.getValue() + " > /Users/zhangliang/docker-sample/elastic-job-example/logs/log" + ElasticJobTask.from(taskId.getValue()).getShardingItem() + ".log")))
                .build();
    }
    
    private static Protos.Resource.Builder getResources(final String type, final double value) {
        return Protos.Resource.newBuilder().setName(type).setType(Protos.Value.Type.SCALAR).setScalar(Protos.Value.Scalar.newBuilder().setValue(value));
    }
    
    /**
     * 获取资源值.
     * 
     * @param resources 资源集合
     * @param type 资源类型
     * @return 资源值
     */
    public static BigDecimal getValue(final List<Protos.Resource> resources, final String type) {
        for (Protos.Resource each : resources) {
            if (type.equals(each.getName())) {
                return new BigDecimal(each.getScalar().getValue());
            }
        }
        return BigDecimal.ZERO;
    }
}