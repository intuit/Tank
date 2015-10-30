package com.intuit.tank.jenkins.configuration;

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

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Job;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import com.intuit.tank.jenkins.constants.StringConstants;

/**
 * This class allows us to persist configuration data across all of our
 * builders.
 * 
 * @author bfiola
 *
 */
public class GlobalConfiguration extends JobProperty<Job<?, ?>> {
	 
	@Extension
	public static final class GlobalConfigurationDescriptor extends
			JobPropertyDescriptor {
		
		public GlobalConfigurationDescriptor() {
			super(GlobalConfiguration.class);
			load();
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData)
				throws FormException {
			req.bindJSON(this, formData);
			save();
			return super.configure(req, formData);
		}

		@Override
		public String getDisplayName() {
			return StringConstants.DisplayName.GLOBAL_PLUGIN_CONFIGURATION;
		}

	}
}
