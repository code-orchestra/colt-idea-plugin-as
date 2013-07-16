package codeOrchestra.lcs.rpc.model;

import java.util.Arrays;

/**
 * @author Alexander Eliseyev
 */
public class COLTState {
  
  private COLTConnection[] activeConnections;

  public COLTConnection[] getActiveConnections() {
    return activeConnections;
  }

  public void setActiveConnections(COLTConnection[] activeConnections) {
    this.activeConnections = activeConnections;
  }

    @Override
    public String toString() {
        return "COLTState{" +
                "activeConnections=" + Arrays.toString(activeConnections) +
                '}';
    }
}
