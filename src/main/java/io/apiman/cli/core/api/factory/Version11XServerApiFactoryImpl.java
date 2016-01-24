/*
 * Copyright 2016 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.core.api.factory;

import io.apiman.cli.core.api.Version11xServerApi;
import io.apiman.cli.core.api.VersionAgnosticApi;
import io.apiman.cli.core.api.model.Api;
import io.apiman.cli.core.api.model.ApiConfig;
import io.apiman.cli.core.api.model.ApiPolicy;
import io.apiman.cli.core.api.model.ServiceConfig;
import io.apiman.cli.server.factory.AbstractServerApiFactory;
import io.apiman.cli.server.factory.ServerApiFactory;
import org.modelmapper.ModelMapper;
import retrofit.client.Response;

import java.util.List;

/**
 * Provides legacy apiman 1.1.x support.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class Version11XServerApiFactoryImpl extends AbstractServerApiFactory<VersionAgnosticApi, Version11xServerApi> implements ServerApiFactory<VersionAgnosticApi> {
    @Override
    public VersionAgnosticApi build(String endpoint, String username, String password, boolean debugLogging) {
        final Version11xServerApi delegate = buildClient(Version11xServerApi.class, endpoint, username, password, debugLogging);

        return new VersionAgnosticApi() {
            @Override
            public Response create(String orgName, Api api) {
                return delegate.create(orgName, api);
            }

            @Override
            public List<Api> list(String orgName) {
                return delegate.list(orgName);
            }

            @Override
            public Api fetch(String orgName, String apiName, String version) {
                return delegate.fetch(orgName, apiName, version);
            }

            @Override
            public Response configure(String orgName, String apiName, String version, ApiConfig apiConfig) {
                // convert to 1.1.x format
                final ModelMapper mapper = new ModelMapper();
                final ServiceConfig serviceConfig = mapper.map(apiConfig, ServiceConfig.class);

                return delegate.configure(orgName, apiName, version, serviceConfig);
            }

            @Override
            public Response addPolicy(String orgName, String apiName, String version, ApiPolicy policyConfig) {
                return delegate.addPolicy(orgName, apiName, version, policyConfig);
            }

            @Override
            public List<ApiPolicy> fetchPolicies(String orgName, String serviceName, String version) {
                return delegate.fetchPolicies(orgName, serviceName, version);
            }
        };
    }
}
