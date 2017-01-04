package heb.pay.entity;

import java.util.Date;

public class PaymentOrder {

	/**
	 * 主键
	 */
	private String id;
	/**
	 * 版本号
	 */
	private int version;
	/**
	 * 支付产品名称
	 */
	private String product_name;
	/**
	 * 商户编号
	 */
	private String merchant_no;
	/**
	 * 商户名称
	 */
	private String merchant_name;
	/**
	 * 订单号
	 */
	private String merchant_order_no;
	/**
	 * 支付金额
	 */
	private double order_amount;
	/**
	 * 订单时间
	 */
	private Date order_time;
	/**
	 * 订单日期
	 */
	private Date order_date;
	/**
	 * 订单时效：单位S
	 */
	private int order_period;
	/**
	 * 订单过期时间
	 */
	private Date order_expire_time;
	/**
	 * 页面回调通知url
	 */
	private String return_url;
	/**
	 * 后台异步通知url
	 */
	private String notify_url;
	/**
	 * 下单ip(客户端ip,在网关页面获取)
	 */
	private String order_ip;
	/**
	 * 从哪个页面链接过来的(可用于防诈骗)
	 */
	private String order_referer_url;
	/**
	 * 支付方式编号
	 */
	private String pay_way_code;
	/**
	 * 支付方式名称
	 */
	private String pay_way_name;
	/**
	 * 支付备注
	 */
	private String remark;
	/**
	 * 支付类型编号
	 */
	private String pay_type_code;
	/**
	 * 支付类型名称
	 */
	private String pay_type_name;
	/**
	 * 支付状态（SUCCESS,FAILED,CREATED,CANCELED,WAITING_PAYMENT）
	 */
	private String status;
	/**
	 * 支付流水号
	 */
	private String trx_no;
	/**
	 * 创建者
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private Date create_time;
	/**
	 * 修改人
	 */
	private String modificator;
	/**
	 * 修改时间
	 */
	private Date modify_time;
	/**
	 * 预留字段
	 */
	private String field1;
	/**
	 * 预留字段
	 */
	private String field2;
	/**
	 * 预留字段
	 */
	private String field3;
	/**
	 * 预留字段
	 */
	private String field4;
	/**
	 * 预留字段
	 */
	private String field5;
	/**
	 * 业务类型
	 */
	private String busi_type_code;
	
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
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getMerchant_no() {
		return merchant_no;
	}
	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}
	public String getMerchant_name() {
		return merchant_name;
	}
	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}
	public String getMerchant_order_no() {
		return merchant_order_no;
	}
	public void setMerchant_order_no(String merchant_order_no) {
		this.merchant_order_no = merchant_order_no;
	}
	public double getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(double order_amount) {
		this.order_amount = order_amount;
	}
	public Date getOrder_time() {
		return order_time;
	}
	public void setOrder_time(Date order_time) {
		this.order_time = order_time;
	}
	public Date getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}
	public int getOrder_period() {
		return order_period;
	}
	public void setOrder_period(int order_period) {
		this.order_period = order_period;
	}
	public Date getOrder_expire_time() {
		return order_expire_time;
	}
	public void setOrder_expire_time(Date order_expire_time) {
		this.order_expire_time = order_expire_time;
	}
	public String getReturn_url() {
		return return_url;
	}
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getOrder_ip() {
		return order_ip;
	}
	public void setOrder_ip(String order_ip) {
		this.order_ip = order_ip;
	}
	public String getOrder_referer_url() {
		return order_referer_url;
	}
	public void setOrder_referer_url(String order_referer_url) {
		this.order_referer_url = order_referer_url;
	}
	public String getPay_way_code() {
		return pay_way_code;
	}
	public void setPay_way_code(String pay_way_code) {
		this.pay_way_code = pay_way_code;
	}
	public String getPay_way_name() {
		return pay_way_name;
	}
	public void setPay_way_name(String pay_way_name) {
		this.pay_way_name = pay_way_name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPay_type_code() {
		return pay_type_code;
	}
	public void setPay_type_code(String pay_type_code) {
		this.pay_type_code = pay_type_code;
	}
	public String getPay_type_name() {
		return pay_type_name;
	}
	public void setPay_type_name(String pay_type_name) {
		this.pay_type_name = pay_type_name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTrx_no() {
		return trx_no;
	}
	public void setTrx_no(String trx_no) {
		this.trx_no = trx_no;
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
	public String getField1() {
		return field1;
	}
	public void setField1(String field1) {
		this.field1 = field1;
	}
	public String getField2() {
		return field2;
	}
	public void setField2(String field2) {
		this.field2 = field2;
	}
	public String getField3() {
		return field3;
	}
	public void setField3(String field3) {
		this.field3 = field3;
	}
	public String getField4() {
		return field4;
	}
	public void setField4(String field4) {
		this.field4 = field4;
	}
	public String getField5() {
		return field5;
	}
	public void setField5(String field5) {
		this.field5 = field5;
	}
	public String getBusi_type_code() {
		return busi_type_code;
	}
	public void setBusi_type_code(String busi_type_code) {
		this.busi_type_code = busi_type_code;
	}
	
	
}
