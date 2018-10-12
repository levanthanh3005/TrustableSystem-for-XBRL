/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.I_DLVClassALL;

import it.unical.xbrl.xbrlcore.OntoClass.ObjectEsd;
import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;

/**
 *
 * @author Admin
 */
public class DLVContext extends ObjectEsd {
    
	private String companyId;
    private String contextId;
    private String dateFrom;
    private String dateTo;
    
    public void setCompanyId(String companyId) {
        this.companyId = ExtrasStatus.normalize(companyId);
    }
    
    @Override
    public String getClassType() {
        return "Context";
    }

    @Override
    public String getContext() {
        try {
            //return "arc (" + fromId + "," + toId + "," + weight + ",\"" + sign + "\"," + calculationalLinkId + ","+useAttribute+").";
            return "context ("+ companyId +", context" + contextId + ",\"" + dateFrom + "\",\"" + dateTo + "\"" +").";
        } catch (NullPointerException e) {
            return "context NULL";
        }
    }

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = ExtrasStatus.normalize(contextId);
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getCompanyId() {
		return companyId;
	}

   
    
}
