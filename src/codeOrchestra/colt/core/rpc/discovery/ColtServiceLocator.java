package codeOrchestra.colt.core.rpc.discovery;

import codeOrchestra.colt.core.rpc.ColtRemoteService;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public class ColtServiceLocator extends AbstractProjectComponent implements ProjectComponent {

    private static final String COLT_SERVICE_TYPE = "_colt._tcp.local.";

    public static final int COLT_SERVICE_LOOKOUT_TIMEOUT = 10000;
    public static final int SERVICE_AVAILABILITY_CHECK_PERIOD = 300;

    private JmDNS jmDNS;

    private Map<String, ColtServiceDescriptor.Key> servicesKeysByName = new HashMap<String, ColtServiceDescriptor.Key>();
    private Map<ColtServiceDescriptor.Key, ColtServiceDescriptor> servicesByKey = Collections.synchronizedMap(new HashMap<ColtServiceDescriptor.Key, ColtServiceDescriptor>());

    private ServiceListener serviceListener = new ServiceListener() {
        @Override
        public void serviceAdded(ServiceEvent event) {
        }

        @Override
        public synchronized void serviceRemoved(ServiceEvent event) {
            ColtServiceDescriptor.Key removedKey = servicesKeysByName.remove(event.getName());
            servicesByKey.remove(removedKey);
        }

        @Override
        public synchronized void serviceResolved(ServiceEvent event) {
            ColtServiceDescriptor newServiceDescriptor = new ColtServiceDescriptor(event.getInfo());
            ColtServiceDescriptor.Key key = newServiceDescriptor.getKey();

            // Take the most recent service descriptor
            ColtServiceDescriptor existingDescriptor = servicesByKey.get(key);
            if (existingDescriptor != null) {
                if (existingDescriptor.getTimestamp() > newServiceDescriptor.getTimestamp()) {
                    return;
                }
            }

            servicesByKey.put(key, newServiceDescriptor);
            servicesKeysByName.put(event.getName(), key);
        }
    };

    public ColtServiceLocator(Project project) {
        super(project);

        try {
            jmDNS = JmDNS.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void projectOpened() {
        jmDNS.addServiceListener(COLT_SERVICE_TYPE, serviceListener);
    }

    @Override
    public void projectClosed() {
        jmDNS.removeServiceListener(COLT_SERVICE_TYPE, serviceListener);
    }

    public <S extends ColtRemoteService> S waitForService(Class<S> serviceClass, String projectPath, String name) {
        long timeout = COLT_SERVICE_LOOKOUT_TIMEOUT;
        while (timeout > 0) {
            try {
                Thread.sleep(SERVICE_AVAILABILITY_CHECK_PERIOD);
            } catch (InterruptedException e) {
                // ignore
            }

            timeout -= SERVICE_AVAILABILITY_CHECK_PERIOD;

            S service = locateService(serviceClass, projectPath, name);
            if (service != null) {
                return service;
            }
        }

        return null;
    }

    public <S extends ColtRemoteService> S locateService(Class<S> serviceClass, String projectPath, String name) {
        ColtServiceDescriptor coltServiceDescriptor = getColtServiceDescriptor(projectPath, name);
        if (coltServiceDescriptor == null) {
            return null;
        }

        try {
            JsonRpcHttpClient client = null;
            try {
                client = new JsonRpcHttpClient(new URL("http://" + coltServiceDescriptor.getHost() + ":" + coltServiceDescriptor.getPort() + "/rpc/coltService"));
            } catch (MalformedURLException e) {
                // should not happen
            }
            return ProxyUtil.createClientProxy(getClass().getClassLoader(), serviceClass, client);
        } catch (Throwable t) {
            return null;
        }
    }

    private ColtServiceDescriptor getColtServiceDescriptor(String projectPath, String name) {
        return servicesByKey.get(new ColtServiceDescriptor.Key(projectPath, name));
    }

}
