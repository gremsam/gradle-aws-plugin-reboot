/*
 * Copyright 2015-2016 the original author or authors.
 *
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
 */
package jp.classmethod.aws.reboot.gradle.cloudformation;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;

public class AmazonCloudFormationWaitChangeSetStatusTask extends ConventionTask {
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String stackName;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private List<String> successStatuses = Arrays.asList(
			"CREATE_COMPLETE",
			"DELETE_COMPLETE");
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private List<String> waitStatuses = Arrays.asList(
			"CREATE_IN_PROGRESS",
			"CREATE_PENDING");
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private int loopTimeout = 900; // sec
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private int loopWait = 10; // sec
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private boolean found;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String lastStatus;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private List<String> printedEvents;
	
	
	public AmazonCloudFormationWaitChangeSetStatusTask() {
		setDescription("Wait cfn change set for specific status.");
		setGroup("AWS");
	}
	
	@TaskAction
	public void waitChangeSetForStatus() throws InterruptedException {
		// to enable conventionMappings feature
		String stackName = getStackName();
		List<String> successStatuses = getSuccessStatuses();
		List<String> waitStatuses = getWaitStatuses();
		int loopTimeout = getLoopTimeout();
		int loopWait = getLoopWait();
		
		if (stackName == null) {
			throw new GradleException("stackName is not specified");
		}
		
		AmazonCloudFormationPluginExtension ext =
				getProject().getExtensions().getByType(AmazonCloudFormationPluginExtension.class);
		AmazonCloudFormation cfn = ext.getClient();
		
		ChangeSetStatusWaiter changeSetStatusWaiter =
				new ChangeSetStatusWaiter(cfn, stackName, getLogger(), successStatuses, waitStatuses, loopTimeout,
						loopWait);
		
		changeSetStatusWaiter.waitForSuccessStatus();
	}
}
