
package com.luminous.mpartner.network.entities.EscalationMatrix;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EscalationMatrixResponseEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("escalation_matrix")
    @Expose
    private List<EscalationMatrix> escalationMatrix = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EscalationMatrix> getEscalationMatrix() {
        return escalationMatrix;
    }

    public void setEscalationMatrix(List<EscalationMatrix> escalationMatrix) {
        this.escalationMatrix = escalationMatrix;
    }

}
