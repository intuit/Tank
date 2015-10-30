package com.intuit.tank.jenkins.publisher;

/*
 * #%L
 * Intuit Tank Jenkins Plugin
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.EnvironmentContributingAction;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.intuit.tank.jenkins.callables.CreateProxyCallable;
import com.intuit.tank.jenkins.callables.ProxyRequest;
import com.intuit.tank.jenkins.callables.StopProxyCallable;
import com.intuit.tank.jenkins.constants.StringConstants;
import com.intuit.tank.jenkins.exceptions.TankJenkinsPluginException;
import com.intuit.tank.jenkins.printer.LogPrinter;

/**
 * This is our main plug-in class. It extends from the Jenkins' Publisher
 * extension point, which gives it hooks into pre-build and post-build (via the
 * perform) method functionality. We don't care when the pre-build code
 * executes, but we do care when the post-build code executes - using the
 * Publisher extension satisfies these requirements.
 * 
 * Through VirtualChannels, we issue commands to slaves (whether local or
 * remote).
 * 
 * The only configurable entity of this plugin is the port from which we're
 * trying to start the proxy.
 * 
 * @author bfiola
 *
 */
public class ProxyPlugin extends Recorder implements Describable<Publisher>,
		Serializable {

	private static final long serialVersionUID = 1L;

	private static final String PROXY_PORT_ENVIRONMENT_KEY = "tankProxyPort";
	
	private Integer proxyPort;
	private boolean startedProxy;
	
	@DataBoundConstructor
	public ProxyPlugin(Integer proxyPort) {
		super();
		this.proxyPort = proxyPort;
	}

	public Integer getProxyPort() {
		return this.proxyPort;
	}

	/**
	 * This function executes after pre-build steps and arbitrarily before the
	 * build itself is run.
	 */
	@Override
	public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
		PrintStream logger = listener.getLogger();
		try {
			LogPrinter.print("Sending remote command to start proxy on port "
					+ proxyPort + ".", logger);
			Launcher launcher = build.getBuiltOn().createLauncher(listener);
			launcher.getChannel().call(
					new CreateProxyCallable(new ProxyRequest(listener,
							proxyPort, build.getExternalizableId(), build.getWorkspace(), getDescriptor())));
			
			build.addAction(new AddEnvironmentVariableAction(PROXY_PORT_ENVIRONMENT_KEY, Integer.toString(proxyPort)));
		} catch (Exception e) {
			LogPrinter.print(e, logger);
			return false;
		}
		return true;
	}

	/**
	 * Once we've finished building, this method gets called (regardless of
	 * build result) to stop the proxy.
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) {
		PrintStream logger = listener.getLogger();
		try {
			LogPrinter.print("Sending remote command to stop proxy on port "
					+ proxyPort + ".", logger);
			launcher.getChannel().call(
					new StopProxyCallable(new ProxyRequest(listener, proxyPort, build.getExternalizableId(), 
							build.getWorkspace(), getDescriptor())));
		} catch (Exception e) {
			LogPrinter.print(e, logger);
			return false;
		}
		return true;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public ProxyPluginDescriptor getDescriptor() {
		return (ProxyPluginDescriptor) super.getDescriptor();
	}

	@Extension
	public static final class ProxyPluginDescriptor extends
			BuildStepDescriptor<Publisher> implements Serializable {
		private static final long serialVersionUID = 1L;

		public ProxyPluginDescriptor() {
			super(ProxyPlugin.class);
			load();
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return StringConstants.DisplayName.PERSIST_SCRIPT;
		}

		public FormValidation doCheckProxyPort(@QueryParameter String proxyPort)
				throws IOException, ServletException {
			return validatePort(proxyPort);
		}

		// this is called at run-time to make sure someone isn't trying to run a
		// test that's misconfigured.  
		public void testPort(Integer value) throws TankJenkinsPluginException {
			testPort(Integer.toString(value));
		}

		// this is called when a user changes the build configuration
		private FormValidation validatePort(String current) {
			try {
				testPort(current);
				return FormValidation.ok();
			} catch (Exception e) {
				return FormValidation.error(e.getMessage());
			}
		}

		private void testPort(String value) throws TankJenkinsPluginException {
			testIsNumber(value);
			testPortRange(Integer.parseInt(value));
		}

		private void testIsNumber(String value) throws TankJenkinsPluginException {
			try {
				Integer.parseInt(value);
			} catch (Exception e) {
				throw new TankJenkinsPluginException(
						StringConstants.ErrorMessages.MUST_BE_INTEGER);
			}
		}

		private void testPortRange(Integer value) throws TankJenkinsPluginException {
			if (value < 0 || value >= 65536) {
				throw new TankJenkinsPluginException(
						StringConstants.ErrorMessages.PORT_INTEGER_RANGE);
			}
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData)
				throws FormException {
			save();
			return super.configure(req, formData);
		}
	}
	
	class AddEnvironmentVariableAction implements EnvironmentContributingAction {
		
		
		private String key;
		private String value;
		
		public AddEnvironmentVariableAction(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public void buildEnvVars(AbstractBuild<?,?> build, EnvVars vars) {
			if(vars != null && key != null && value != null) {
				vars.put(key, value);
			}
		}
		
		public String getDisplayName() {
			return "Proxy Port Environment Variable";
		}
		
		public String getIconFileName() {
			return null;
		}
		
		public String getUrlName() {
			return null;
		}
	}

}
