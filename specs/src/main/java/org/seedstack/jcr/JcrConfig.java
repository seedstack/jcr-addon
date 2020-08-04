/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jcr;

import org.seedstack.coffig.Config;
import org.seedstack.coffig.SingleValue;
import org.seedstack.jcr.spi.JcrRepositoryFactory;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Config("jcr")
public class JcrConfig {

    private String defaultRepository = "default";

    private Map<String, RepositoryConfig> repositories = new HashMap<>();

    public String getDefaultRepository() {
        return defaultRepository;
    }

    public void setDefaultRepository(String defaultRepository) {
        this.defaultRepository = defaultRepository;
    }

    public Map<String, RepositoryConfig> getRepositories() {
        return Collections.unmodifiableMap(repositories);
    }

    @Override
    public String toString() {
        return "JcrConfig [defaultRepository=" + defaultRepository + ", repositories=" + repositories + "]";
    }

    public enum RepositoryAddressType {
        JNDI_NAME, JNDI_URI, LOCAL_PATH, REMOTE_URI
    }

    public static class RepositoryConfig {

        @NotNull
        private String address;

        private String password;
        private Class<? extends JcrRepositoryFactory> repositoryFactory;

        @SingleValue
        @NotNull
        private RepositoryAddressType type;
        private String username;

        private Properties vendorProperties;

        public String getAddress() {
            return address;
        }

        public RepositoryConfig setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public RepositoryConfig setPassword(String password) {
            this.password = password;
            return this;
        }

        public Class<? extends JcrRepositoryFactory> getRepositoryFactory() {
            return repositoryFactory;
        }

        public RepositoryConfig setRepositoryFactory(Class<? extends JcrRepositoryFactory> repositoryFactory) {
            this.repositoryFactory = repositoryFactory;
            return this;
        }

        public RepositoryAddressType getType() {
            return type;
        }

        public RepositoryConfig setType(RepositoryAddressType type) {
            this.type = type;
            return this;
        }

        public String getUsername() {
            return username;
        }

        public RepositoryConfig setUsername(String username) {
            this.username = username;
            return this;
        }

        public Properties getVendorProperties() {
            return vendorProperties;
        }

        public RepositoryConfig setVendorProperties(Properties vendorProperties) {
            this.vendorProperties = vendorProperties;
            return this;

        }

        public boolean hasAuthenticationInfo() {
            return !(isBlank(username) || isBlank(password));
        }

        @Override
        public String toString() {
            return "RepositoryConfig [address=" + address + ", repositoryFactory=" + repositoryFactory + ", type="
                    + type + ", username=" + username + ", vendorProperties=" + vendorProperties + "]";
        }

        private boolean isBlank(String val) {
            return val == null || val.isEmpty();
        }
    }
}
