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

import org.seedstack.coffig.Config;
import org.seedstack.coffig.SingleValue;

@Config("jcr")
public class JcrConfig {

    private String defaultRepository = "default";

    private Map<String, RepositoryConfig> repositories = new HashMap<>();

    public JcrConfig addRepository(String key, RepositoryConfig repositoryConfig) {
        repositories.put(key, repositoryConfig);
        return this;
    }

    public String getDefaultRepository() {
        return defaultRepository;
    }

    public Map<String, RepositoryConfig> getRepositories() {
        return Collections.unmodifiableMap(repositories);
    }

    public void setDefaultRepository(String defaultRepository) {
        this.defaultRepository = defaultRepository;
    }

    public enum RepositoryAddressType {
        JNDI_NAME, JNDI_URI, LOCAL_PATH, REMOTE_URI
    }

    public static class RepositoryConfig {

        private String username;
        private String password;

        private String repository;

        @SingleValue
        @NotNull
        private RepositoryAddressType type;
        private String address;

        private Properties vendorProperties;

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
            return !(this.username.isEmpty() && this.password.isEmpty());
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

        public RepositoryConfig setAddress(String address) {
            this.address = address;
            return this;
        }

        public RepositoryConfig setPassword(String password) {
            this.password = password;
            return this;
        }

        public RepositoryConfig setRepository(String repository) {
            this.repository = repository;
            return this;
        }

        public RepositoryConfig setType(RepositoryAddressType type) {
            this.type = type;
            return this;
        }

        public RepositoryConfig setUsername(String username) {
            this.username = username;
            return this;
        }

        public RepositoryConfig setVendorProperties(Properties vendorProperties) {
            this.vendorProperties = vendorProperties;
            return this;

        }

    }

}
