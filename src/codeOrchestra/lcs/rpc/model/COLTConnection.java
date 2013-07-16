package codeOrchestra.lcs.rpc.model;

import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public class COLTConnection {

    private long startTimestamp;

    private String broadcastId;
    private String clientId;
    private Map<String, String> clientInfo;

    private int connectionNumber;

    public COLTConnection() {
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Map<String, String> getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(Map<String, String> clientInfo) {
        this.clientInfo = clientInfo;
    }

    public int getConnectionNumber() {
        return connectionNumber;
    }

    public void setConnectionNumber(int connectionNumber) {
        this.connectionNumber = connectionNumber;
    }

    @Override
    public String toString() {
        return "COLTConnection{" +
                "startTimestamp=" + startTimestamp +
                ", broadcastId='" + broadcastId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientInfo=" + clientInfo +
                ", connectionNumber=" + connectionNumber +
                '}';
    }
}
