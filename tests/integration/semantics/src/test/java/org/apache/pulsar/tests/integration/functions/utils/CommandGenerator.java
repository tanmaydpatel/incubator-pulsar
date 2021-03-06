/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.tests.integration.functions.utils;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.pulsar.tests.topologies.PulsarCluster;

@Getter
@Setter
@ToString
public class CommandGenerator {
    public enum Runtime {
        JAVA,
        PYTHON,
    };
    private String functionName;
    private String tenant = "public";
    private String namespace = "default";
    private String functionClassName;
    private String sourceTopic;
    private Map<String, String> customSereSourceTopics;
    private String sinkTopic;
    private String logTopic;
    private String outputSerDe;
    private String processingGuarantees;
    private Runtime runtime;
    private Integer parallelism;
    private String adminUrl;
    private Integer windowLengthCount;
    private Long windowLengthDurationMs;
    private Integer slidingIntervalCount;
    private Long slidingIntervalDurationMs;

    private Map<String, String> userConfig = new HashMap<>();
    private static final String JAVAJAR = "/pulsar/examples/api-examples.jar";
    private static final String PYTHONBASE = "/pulsar/examples/python-examples/";

    public static CommandGenerator createDefaultGenerator(String sourceTopic, String functionClassName) {
        CommandGenerator generator = new CommandGenerator();
        generator.setSourceTopic(sourceTopic);
        generator.setFunctionClassName(functionClassName);
        generator.setRuntime(Runtime.JAVA);
        return generator;
    }

    public static CommandGenerator createDefaultGenerator(Map<String, String> customSereSourceTopics,
                                                          String functionClassName) {
        CommandGenerator generator = new CommandGenerator();
        generator.setCustomSereSourceTopics(customSereSourceTopics);
        generator.setFunctionClassName(functionClassName);
        generator.setRuntime(Runtime.JAVA);
        return generator;
    }

    public static CommandGenerator createDefaultGenerator(String tenant, String namespace, String functionName) {
        CommandGenerator generator = new CommandGenerator();
        generator.setTenant(tenant);
        generator.setNamespace(namespace);
        generator.setFunctionName(functionName);
        generator.setRuntime(Runtime.JAVA);
        return generator;
    }

    public void createAdminUrl(String workerHost, int port) {
        adminUrl = "http://" + workerHost + ":" + port;
    }

    public String generateCreateFunctionCommand() {
        return generateCreateFunctionCommand(null);
    }

    public String generateCreateFunctionCommand(String codeFile) {
        StringBuilder commandBuilder = new StringBuilder(PulsarCluster.ADMIN_SCRIPT);
        if (adminUrl != null) {
            commandBuilder.append(" --admin-url ");
            commandBuilder.append(adminUrl);
        }
        commandBuilder.append(" functions create");
        if (tenant != null) {
            commandBuilder.append(" --tenant " + tenant);
        }
        if (namespace != null) {
            commandBuilder.append(" --namespace " + namespace);
        }
        if (functionName != null) {
            commandBuilder.append(" --name " + functionName);
        }
        commandBuilder.append(" --className " + functionClassName);
        if (sourceTopic != null) {
            commandBuilder.append(" --inputs " + sourceTopic);
        }
        if (logTopic != null) {
            commandBuilder.append(" --logTopic " + logTopic);
        }
        if (customSereSourceTopics != null && !customSereSourceTopics.isEmpty()) {
            commandBuilder.append(" --customSerdeInputs \'" + new Gson().toJson(customSereSourceTopics) + "\'");
        }
        if (sinkTopic != null) {
            commandBuilder.append(" --output " + sinkTopic);
        }
        if (outputSerDe != null) {
            commandBuilder.append(" --outputSerdeClassName " + outputSerDe);
        }
        if (processingGuarantees != null) {
            commandBuilder.append(" --processingGuarantees " + processingGuarantees);
        }
        if (!userConfig.isEmpty()) {
            commandBuilder.append(" --userConfig \'" + new Gson().toJson(userConfig) + "\'");
        }
        if (parallelism != null) {
            commandBuilder.append(" --parallelism " + parallelism);
        }
        if (windowLengthCount != null) {
            commandBuilder.append(" --windowLengthCount " + windowLengthCount);
        }
        if (windowLengthDurationMs != null)  {
            commandBuilder.append(" --windowLengthDurationMs " + windowLengthDurationMs);
        }
        if (slidingIntervalCount != null)  {
            commandBuilder.append( " --slidingIntervalCount " + slidingIntervalCount);
        }
        if (slidingIntervalDurationMs != null)  {
            commandBuilder.append(" --slidingIntervalDurationMs " + slidingIntervalDurationMs);
        }

        if (runtime == Runtime.JAVA) {
            commandBuilder.append(" --jar " + JAVAJAR);
        } else {
            if (codeFile != null) {
                commandBuilder.append(" --py " + PYTHONBASE + codeFile);
            } else {
                commandBuilder.append(" --py " + PYTHONBASE);
            }
        }
        return commandBuilder.toString();
    }

    public String generateUpdateFunctionCommand() {
        return generateUpdateFunctionCommand(null);
    }

    public String generateUpdateFunctionCommand(String codeFile) {
        StringBuilder commandBuilder = new StringBuilder("PULSAR_MEM=-Xmx1024m ");
        if (adminUrl == null) {
            commandBuilder.append("/pulsar/bin/pulsar-admin functions update");
        } else {
            commandBuilder.append("/pulsar/bin/pulsar-admin");
            commandBuilder.append(" --admin-url ");
            commandBuilder.append(adminUrl);
            commandBuilder.append(" functions update");
        }
        if (tenant != null) {
            commandBuilder.append(" --tenant " + tenant);
        }
        if (namespace != null) {
            commandBuilder.append(" --namespace " + namespace);
        }
        if (functionName != null) {
            commandBuilder.append(" --name " + functionName);
        }
        commandBuilder.append(" --className " + functionClassName);
        if (sourceTopic != null) {
            commandBuilder.append(" --inputs " + sourceTopic);
        }
        if (customSereSourceTopics != null && !customSereSourceTopics.isEmpty()) {
            commandBuilder.append(" --customSerdeInputs \'" + new Gson().toJson(customSereSourceTopics) + "\'");
        }
        if (sinkTopic != null) {
            commandBuilder.append(" --output " + sinkTopic);
        }
        if (logTopic != null) {
            commandBuilder.append(" --logTopic " + logTopic);
        }
        if (outputSerDe != null) {
            commandBuilder.append(" --outputSerdeClassName " + outputSerDe);
        }
        if (processingGuarantees != null) {
            commandBuilder.append(" --processingGuarantees " + processingGuarantees);
        }
        if (!userConfig.isEmpty()) {
            commandBuilder.append(" --userConfig \'" + new Gson().toJson(userConfig) + "\'");
        }
        if (parallelism != null) {
            commandBuilder.append(" --parallelism " + parallelism);
        }
        if (windowLengthCount != null) {
            commandBuilder.append(" --windowLengthCount " + windowLengthCount);
        }
        if (windowLengthDurationMs != null)  {
            commandBuilder.append(" --windowLengthDurationMs " + windowLengthDurationMs);
        }
        if (slidingIntervalCount != null)  {
            commandBuilder.append(" --slidingIntervalCount " + slidingIntervalCount);
        }
        if (slidingIntervalDurationMs != null)  {
            commandBuilder.append(" --slidingIntervalDurationMs " + slidingIntervalDurationMs);
        }

        if (runtime == Runtime.JAVA) {
            commandBuilder.append(" --jar " + JAVAJAR);
        } else {
            if (codeFile != null) {
                commandBuilder.append(" --py " + PYTHONBASE + codeFile);
            } else {
                commandBuilder.append(" --py " + PYTHONBASE);
            }
        }
        return commandBuilder.toString();
    }

    public String genereateDeleteFunctionCommand() {
        StringBuilder commandBuilder = new StringBuilder("/pulsar/bin/pulsar-admin functions delete");
        if (tenant != null) {
            commandBuilder.append(" --tenant " + tenant);
        }
        if (namespace != null) {
            commandBuilder.append(" --namespace " + namespace);
        }
        if (functionName != null) {
            commandBuilder.append(" --name " + functionName);
        }
        return commandBuilder.toString();
    }

    public String generateTriggerFunctionCommand(String triggerValue) {
        StringBuilder commandBuilder = new StringBuilder("/pulsar/bin/pulsar-admin functions trigger");
        if (tenant != null) {
            commandBuilder.append(" --tenant " + tenant);
        }
        if (namespace != null) {
            commandBuilder.append(" --namespace " + namespace);
        }
        if (functionName != null) {
            commandBuilder.append(" --name " + functionName);
        }
        commandBuilder.append(" --triggerValue " + triggerValue);
        return commandBuilder.toString();
    }
}
