package heb.pay.entity;

import java.util.Date;

public class MerchantPayConfig {

	private String id;
	private int version;
	private String creator;
	private Date create_time;
	private String modificator;
	private Date modify_time;
	private String status;
	private String product_code;
	private String product_name;
	private String pay_key;
	private String pay_secret;
	private String user_no;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getModificator() {
		return modificator;
	}
	public void setModificator(String modificator) {
		this.modificator = modificator;
	}
	public Date getModify_time() {
		return modify_time;
	}
	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getPay_key() {
		return pay_key;
	}
	public void setPay_key(String pay_key) {
		this.pay_key = pay_key;
	}
	public String getPay_secret() {
		return pay_secret;
	}
	public void setPay_secret(String pay_secret) {
		this.pay_secret = pay_secret;
	}
	public String getUser_no() {
		return user_no;
	}
	public void setUser_no(String user_no) {
		this.user_no = user_no;
	}
	
	

}
