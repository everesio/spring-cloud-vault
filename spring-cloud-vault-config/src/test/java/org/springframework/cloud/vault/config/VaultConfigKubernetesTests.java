/*
 * Copyright 2016-2017 the original author or authors.
 *
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
 */
package org.springframework.cloud.vault.config;

import static org.junit.Assume.assumeTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.vault.util.Settings;
import org.springframework.cloud.vault.util.VaultRule;
import org.springframework.cloud.vault.util.Version;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.vault.core.VaultOperations;

/**
 * Integration test using config infrastructure with Kubernetes authentication.
 *
 * @author Michal Budzyn
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = VaultConfigKubernetesTests.TestApplication.class, properties = {
		"spring.cloud.vault.authentication=kubernetes",
		"spring.cloud.vault.kubernetes.role=my-role",
		"spring.cloud.vault.application-name=VaultConfigKubernetesTests" })
public class VaultConfigKubernetesTests {

	@BeforeClass
	public static void beforeClass() throws Exception {

		VaultRule vaultRule = new VaultRule();
		vaultRule.before();

		VaultProperties vaultProperties = Settings.createVaultProperties();
		vaultProperties.getKubernetes().setRole("my-role");

		assumeTrue(vaultRule.prepare().getVersion()
				.isGreaterThanOrEqualTo(Version.parse("0.8.3")));

		if (!vaultRule.prepare()
				.hasAuth(vaultProperties.getKubernetes().getKubernetesPath())) {
			vaultRule.prepare()
					.mountAuth(vaultProperties.getKubernetes().getKubernetesPath());
		}

		VaultOperations vaultOperations = vaultRule.prepare().getVaultOperations();

		// TODO: implement
		// vaultOperations.write("secret/" +
		// VaultConfigAppRoleTests.class.getSimpleName(),
		// Collections.singletonMap("vault.value", "foo"));
	}

	// @Value("${vault.value}")
	// String configValue;

	@Test
	public void contextLoads() {
		// assertThat(configValue).isEqualTo("foo");
	}

	@SpringBootApplication
	public static class TestApplication {

		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}
	}
}
