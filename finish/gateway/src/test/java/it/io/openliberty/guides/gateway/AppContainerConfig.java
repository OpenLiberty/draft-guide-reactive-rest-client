// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.gateway;

import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.MicroProfileApplication;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;

public class AppContainerConfig implements SharedContainerConfiguration {

	private static Network network = Network.newNetwork();
	
    @Container
    public static MockServerContainer mockServer = new MockServerContainer()
                    .withNetworkAliases("mock-server")
                    .withNetwork(network);

    public static MockServerClient mockClient;
  
    @Container
    public static MicroProfileApplication gateway = new MicroProfileApplication()
                    .withAppContextRoot("/")
                    .withReadinessPath("/health/ready")
                    .withNetwork(network)
                    .withEnv("inventoryClient_mp_rest_url", "http://mock-server:" + MockServerContainer.PORT)
                    .withEnv("GATEWAY_JOB_BASE_URI", "http://mock-server:" + MockServerContainer.PORT);
    
    @Override
    public void startContainers() {
        mockServer.start();
        gateway.start();
        mockClient = new MockServerClient(
  	          mockServer.getContainerIpAddress(),
  	          mockServer.getServerPort());
        try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
		}
    }
    
}
