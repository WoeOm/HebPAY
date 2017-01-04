package heb.pay.entity;

import java.io.Serializable;

public class Result implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String BUSITYPECODE;
    private String PAYERTYPECODE;
    private String PAYERNUM;
    private String STATUS;
    
	public Result(String bUSITYPECODE, String pAYERTYPECODE, String pAYERNUM,
			String sTATUS) {
		super();
		BUSITYPECODE = bUSITYPECODE;
		PAYERTYPECODE = pAYERTYPECODE;
		PAYERNUM = pAYERNUM;
		STATUS = sTATUS;
	}
	
	public String getBUSITYPECODE() {
		return BUSITYPECODE;
	}
	public void setBUSITYPECODE(String bUSITYPECODE) {
		BUSITYPECODE = bUSITYPECODE;
	}
	public String getPAYERTYPECODE() {
		return PAYERTYPECODE;
	}
	public void setPAYERTYPECODE(String pAYERTYPECODE) {
		PAYERTYPECODE = pAYERTYPECODE;
	}
	public String getPAYERNUM() {
		return PAYERNUM;
	}
	public void setPAYERNUM(String pAYERNUM) {
		PAYERNUM = pAYERNUM;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
    
}
