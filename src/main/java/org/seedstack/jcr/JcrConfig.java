/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jcr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.seedstack.coffig.Config;
import org.seedstack.coffig.SingleValue;

@Config("jcr")
public class JcrConfig {

    private String defaultSession = "default";

    private Map<String, SessionConfig> sessions = new HashMap<>();

    public JcrConfig addSession(String key, SessionConfig sessionConfig) {
        sessions.put(key, sessionConfig);
        return this;
    }

    public String getDefaultSession() {
        return defaultSession;
    }

    public Map<String, SessionConfig> getSessions() {
        return Collections.unmodifiableMap(sessions);
    }

    public void setDefaultsession(String defaultsession) {
        this.defaultSession = defaultsession;
    }

    public enum RepositoryAddressType {
        JNDI_NAME, JNDI_URI, LOCAL_PATH, REMOTE_URI
    }

    public static class SessionConfig {

        private String username;
        private String password;

        private String repository = "default";

        @SingleValue
        @NotNull
        private RepositoryAddressType type;
        @NotNull
        private String address;

        private Properties vendorProperties;

        @Override
        public String toString() {
            return "SessionConfig [username=" + username + ", repository=" + repository + ", type="
                    + type + ", address=" + address + ", vendorProperties=" + vendorProperties
                    + "]";
        }

        public String getAddress() {
            return address;
        }

        public String getPassword() {
            return password;
        }

        public String getRepository() {
            return repository;
        }

        public boolean hasAuthenticationInfo() {

            return !(StringUtils.isBlank(username) || StringUtils.isBlank(password));
        }

        public RepositoryAddressType getType() {
            return type;
        }

        public String getUsername() {
            return username;
        }

        public Properties getVendorProperties() {
            return vendorProperties;
        }

        public SessionConfig setAddress(String address) {
            this.address = address;
            return this;
        }

        public SessionConfig setPassword(String password) {
            this.password = password;
            return this;
        }

        public SessionConfig setRepository(String repository) {
            this.repository = repository;
            return this;
        }

        public SessionConfig setType(RepositoryAddressType type) {
            this.type = type;
            return this;
        }

        public SessionConfig setUsername(String username) {
            this.username = username;
            return this;
        }

        public SessionConfig setVendorProperties(Properties vendorProperties) {
            this.vendorProperties = vendorProperties;
            return this;

        }
    }
}
