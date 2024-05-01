package com.intuit.tank.clients;

import com.intuit.tank.agent.models.VMStatus;
import com.intuit.tank.clients.util.ClientException;
import com.intuit.tank.projects.models.ProjectContainer;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.vm.agent.messages.*;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;

import java.io.*;

public class ClientTest {
    public static void main(String[] args) {
        AgentClient agentClient = new AgentClient("http://localhost:8080/tank_war", "mdD75HJmr1VZWuNjzG4CfnJDptVSrMRy");
        ProjectClient projectClient = new ProjectClient("http://localhost:8080/tank_war", "mdD75HJmr1VZWuNjzG4CfnJDptVSrMRy");

        try {
//            AgentData agentData = new AgentData("4099", "i-903334221121", "ip4-333/0.0/22",, VMRegion.US_WEST_2, "theZone");
//            Headers headers = agentClient.getHeaders();
//            System.out.println(headers.getHeaders().get(0).getKey());
             System.out.println(projectClient.getProject(1000).getName());

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static void writeFile(InputStream supportFiles, String filename) throws IOException {
        try(OutputStream outputStream = new FileOutputStream(filename)){
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = supportFiles.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
