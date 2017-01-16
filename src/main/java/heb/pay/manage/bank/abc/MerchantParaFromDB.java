package heb.pay.manage.bank.abc;

import heb.pay.controller.PaymentController;
import heb.pay.util.CheckBankDataUtils;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.abc.pay.client.CertHelper;
import com.abc.pay.client.FileUtil;
import com.abc.pay.client.MerchantPara;
import com.abc.pay.client.MerchantParaFactory;
import com.abc.pay.client.MerchantParaWeb;
import com.abc.pay.client.TrxException;

/**
 * 具体工厂-客运工厂，从数据库中读取配置
 * 生产两种产品：MerchantParaWeb和
 */
public class MerchantParaFromDB extends MerchantParaFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

    //商户端配置文件资源对象
    private static MerchantPara paraWeb = null;
    //初始旗标
    private static boolean iIsInitialedWeb = false;
	
	public void refreshConfig() throws TrxException {
		iIsInitialedWeb = false;
	}
	
	public void init(MerchantPara para){
		try {
			//#网上支付平台通讯方式（http / https）
			para.setTrustPayConnectMethod("https");//公网
			para.setTrustPayConnectMethodLine("https");//专线

			//#网上支付平台服务器名
			para.setTrustPayServerName("pay.abchina.com");//公网
			para.setTrustPayServerNameLine("pay.abchina.com");//专线

		    //#网上支付平台交易端口
			para.setTrustPayServerPort("443");//公网
			para.setTrustPayServerPortLine("443");//专线

			//#网上支付平台交易网址
			para.setTrustPayTrxURL("/ebus/ReceiveMerchantTrxReqServlet");
			para.setTrustPayTrxIEURL("https://pay.abchina.com/ebus/ReceiveMerchantIERequestServlet");
						
			//#页面提交支付请求失败后的转向地址
			para.setMerchantErrorURL("payment_defeat.html");
							
			//#网上支付平台证书
			para.setTrustPayCertFileName(CheckBankDataUtils.getABCCertPath()+"TrustPay.cer");
			//#农行根证书文件
			para.setTrustStoreFileName(CheckBankDataUtils.getABCCertPath()+"abc.truststore");
			//#农行根证书文件密码
			para.setTrustStorePassword("changeit");
			
			//设置商户编号。如果是多商户则在iMerchantIDList放置多条记录
			ArrayList<String> iMerchantIDList = new ArrayList<String>();
			iMerchantIDList.add(PaymentController.merchantPayInfo.getTg_merchant_id());
			para.setMerchantIDList(iMerchantIDList);           
			
			FileUtil util = new FileUtil();
            
			//设置商户证书。如果是多商户则在iMerchantCertNameList放置多条记录。注意：商户证书名称顺序要与商户编号顺序一致
			ArrayList<byte[]> iMerchantCertList = new ArrayList<byte[]>();
			iMerchantCertList.add(util.readFile(CheckBankDataUtils.getABCCertPath()+"xxqh.pfx"));
			para.setMerchantCertFileList(iMerchantCertList);
            
			//设置商户证书密码。如果是多商户则在iMerchantPasswordList放置多条记录。注意：密码顺序要与商户编号顺序一致
			ArrayList<String> iMerchantPasswordList = new ArrayList<String>();
			iMerchantPasswordList.add("12341234");// 商户私钥密码
			para.setMerchantCertPasswordList(iMerchantPasswordList);

			//#交易日志文件存放目录
			para.setLogPath(CheckBankDataUtils.getABCCertPath()+"logs");
			//#证书储存媒体
			para.setMerchantKeyStoreType("0");
			
			//一般商户都选用文件证书
			if (para.getMerchantKeyStoreType().equals(MerchantPara.KEY_STORE_TYPE_FILE)) {
				CertHelper.bindMerchantCertificate(para, iMerchantCertList, iMerchantPasswordList);
			}else if (para.getMerchantKeyStoreType().equals(MerchantPara.KEY_STORE_TYPE_SIGN_SERVER)) {
			}else {
				throw new TrxException(TrxException.TRX_EXC_CODE_1001, TrxException.TRX_EXC_MSG_1001 + " - 证书储存媒体配置错误！");
			}
			//设定上网代理
			para.setProxyIP("");
			para.setProxyPort("");
			//设定连接超时时间
			para.setTrustPayServerTimeout(""); 

		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		logger.info("##ABC商户端API初始化完成……");   
	}

    /**
     * MerchantParaFromDB类必须实现getMerchantPara方法，返回MerchantPara类型对象。
     * 从数据库中读取配置项。
     * 默认实现为web
     */
	public MerchantPara getMerchantPara() throws TrxException {	
        if (!iIsInitialedWeb) {
        	try {
        		paraWeb = MerchantParaWeb.getUniqueInstance();
    		} catch (TrxException e) {
    			e.printStackTrace();
    		}
        	init(paraWeb);
        	iIsInitialedWeb = true;
        }        
		return paraWeb;
	}
}