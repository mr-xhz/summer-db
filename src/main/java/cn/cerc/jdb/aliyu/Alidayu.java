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
	private static final Logger log = Logger.getLogger(Alidayu.class);
	// 常量
	public static final String AppName = "appName";
	public static final String ServerUrl = "dayu.serverUrl";
	public static final String AppKey = "dayu.appKey";
	public static final String AppSecret = "dayu.appSecret";
	public static final String SingName = "dayu.singName";
	// 各类设置
	private String serverUrl;
	private String appKey;
	private String appSecret;
	private String appName;
	private String signName;
	// 接收手机号
	private String mobileNo;
	// 简讯模版编号
	private String templateNo;
	// 执行结果
	private String message;

	// 系统模版列表
	private static Map<String, String> tpl = new HashMap<>();
	static {
		tpl.put("SMS_1190001", "系统信息变更验证码"); // 验证码${code}，您正在尝试变更${product}重要信息，请妥善保管账户信息。
		tpl.put("SMS_1190002", "系统修改密码验证码"); // 验证码${code}，您正在尝试修改${product}登录密码，请妥善保管账户信息。
		tpl.put("SMS_1190003", "系统活动确认验证码"); // 验证码${code}，您正在参加${product}的${item}活动，请确认系本人申请。
		tpl.put("SMS_1190004", "系统用户注册验证码"); // 验证码${code}，您正在注册成为${product}用户，感谢您的支持！
		tpl.put("SMS_1190005", "系统登陆异常验证码"); // 验证码${code}，您正尝试异地登陆${product}，若非本人操作，请勿泄露。
		tpl.put("SMS_1190006", "系统登陆确认验证码"); // 验证码${code}，您正在登录${product}，若非本人操作，请勿泄露。
		tpl.put("SMS_1190007", "系统身份验证验证码"); // 验证码${code}，您正在进行${product}身份验证，打死不要告诉别人哦！
	}

	public Alidayu(IConfig conf) {
		this.appName = conf.getProperty(AppName, "none");
		this.serverUrl = conf.getProperty(ServerUrl, "http://gw.api.taobao.com/router/rest");
		this.appKey = conf.getProperty(AppKey);
		this.appSecret = conf.getProperty(AppSecret);
		this.signName = conf.getProperty(SingName, "地藤");
	}

	public boolean send(String corpNo, String smsParam) {

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

		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend(corpNo);
		req.setSmsType("normal");
		req.setSmsFreeSignName(this.signName);

		req.setSmsParamString(smsParam);
		// req.setSmsParamString("{code:'785456',product:'阿里大于'}");

		req.setRecNum(mobileNo);
		req.setSmsTemplateCode(this.templateNo);
		AlibabaAliqinFcSmsNumSendResponse rsp;

		try {
			rsp = client.execute(req);
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
				log.info(rsp.getBody());
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

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getTemplateNo() {
		return templateNo;
	}

	public void setTemplateNo(String templateNo) {
		this.templateNo = templateNo;
	}
}
