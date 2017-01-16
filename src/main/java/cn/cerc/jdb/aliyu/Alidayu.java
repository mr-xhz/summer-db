package cn.cerc.jdb.aliyu;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.BizResult;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import cn.cerc.jdb.core.IConfig;

public class Alidayu {
	public static final String AppName = "appName";
	public static final String ServerUrl = "dayu.serverUrl";
	public static final String AppKey = "dayu.appKey";
	public static final String AppSecret = "dayu.appSecret";
	private static final Logger log = Logger.getLogger(Alidayu.class);
	private static Map<String, String> tpl = new HashMap<>();
	private String message;
	//各类设置
	private String serverUrl;
	private String appKey;
	private String appSecret;
	private String appName;
	private String signName;
	//接收手机号
	private String mobileNo;
	//简讯模版编号
	private String templateNo;

	public Alidayu(IConfig conf, String signName) {
		this.appName = conf.getProperty(AppName, "none");
		this.serverUrl = conf.getProperty(ServerUrl, "http://gw.api.taobao.com/router/rest");
		this.appKey = conf.getProperty(AppKey);
		this.appSecret = conf.getProperty(AppSecret);
		this.signName = signName; 
	}

	static {
		tpl.put("SMS_1190001", "系统信息变更验证码"); // 验证码${code}，您正在尝试变更${product}重要信息，请妥善保管账户信息。
		tpl.put("SMS_1190002", "系统修改密码验证码"); // 验证码${code}，您正在尝试修改${product}登录密码，请妥善保管账户信息。
		tpl.put("SMS_1190003", "系统活动确认验证码"); // 验证码${code}，您正在参加${product}的${item}活动，请确认系本人申请。
		tpl.put("SMS_1190004", "系统用户注册验证码"); // 验证码${code}，您正在注册成为${product}用户，感谢您的支持！
		tpl.put("SMS_1190005", "系统登陆异常验证码"); // 验证码${code}，您正尝试异地登陆${product}，若非本人操作，请勿泄露。
		tpl.put("SMS_1190006", "系统登陆确认验证码"); // 验证码${code}，您正在登录${product}，若非本人操作，请勿泄露。
		tpl.put("SMS_1190007", "系统身份验证验证码"); // 验证码${code}，您正在进行${product}身份验证，打死不要告诉别人哦！
	}

	public Alidayu() {

	}

	boolean send(String corpNo, Object data) {
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setRecNum(mobileNo);
		req.setSmsTemplateCode(this.templateNo);
		String serverUrl = this.serverUrl;
		String appKey = this.appKey;
		String appSecret = this.appSecret;

		if (serverUrl == null) {
			this.message = "无法读取简讯发送配置：serverUrl！";
			return false;
		}
		if (appKey == null) {
			this.message = "无法读取简讯发送配置：appKey！";
			return false;
		}
		if (appSecret == null) {
			this.message = "无法读取简讯发送配置：appSercret！";
			return false;
		}

		String sessionKey = "";
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		req.setExtend(corpNo);
		req.setSmsType("normal");
		req.setSmsFreeSignName(this.signName);
		// 活动验证, 变更验证，登录验证，注册验证，身份验证
		req.setSmsParam(data);
		AlibabaAliqinFcSmsNumSendResponse rsp;

		try {
			rsp = client.execute(req, sessionKey);
			BizResult result = rsp.getResult();
			if (result != null) {
				if (result.getSuccess()) {
					log.info(rsp.getBody());
					return true;
				} else {
					message = result.getMsg();
					return false;
				}
			} else {
				message = "Alidayu error: rsp.getResult is null, app host: " + appName;
				return false;
			}
		} catch (ApiException e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	public String getMessage() {
		return message;
	}

	protected String getMobileNo() {
		return mobileNo;
	}

	protected void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	protected String getTemplateNo() {
		return templateNo;
	}

	protected void setTemplateNo(String templateNo) {
		this.templateNo = templateNo;
	}
}
