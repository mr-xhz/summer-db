package cn.cerc.jdb.sms;

import org.apache.log4j.Logger;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import cn.cerc.jdb.core.IConfig;

@Deprecated // 改使用聚安服务
public class Aliyundysms {
    // 常量设置
    private static final Logger log = Logger.getLogger(Aliyundysms.class);
    private static final String product = "Dysmsapi";
    private static final String domain = "dysmsapi.aliyuncs.com";
    public static final String SingName = "dayu.singName";
    public static final String aliyun_accessKeyId = "oss.accessKeyId";
    public static final String aliyun_accessSecret = "oss.accessKeySecret";

    // 环境配置
    private String accessKeyId;
    private String accessSecret;
    // 签名模版
    private String signName;
    // 接收手机
    private String phoneNumbers;
    // 模版编号
    private String templateCode;
    // 模版内容
    private String templateParam;
    // 扩展字段
    private String outId;
    // 执行结果
    private String message;

    public Aliyundysms(IConfig conf) {
        this.signName = conf.getProperty(SingName, "地藤");
        this.accessKeyId = conf.getProperty(aliyun_accessKeyId);
        this.accessSecret = conf.getProperty(aliyun_accessSecret);
    }

    public boolean send(String outId, String templateParam) {
        // 设置超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        if (accessKeyId == null) {
            this.message = "无法读取短信发送配置：accessKeyId！";
            return false;
        }

        if (accessSecret == null) {
            this.message = "无法读取短信发送配置：appSercret！";
            return false;
        }

        if (signName == null) {
            this.message = "签名模版不允许为空";
            return false;
        }

        if (phoneNumbers == null) {
            this.message = "电话号码不允许为空";
            return false;
        }

        if (templateCode == null) {
            this.message = "短信模版不允许为空";
            return false;
        }

        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();
            request.setMethod(MethodType.POST);
            request.setSignName(signName);
            request.setPhoneNumbers(phoneNumbers);
            request.setTemplateCode(templateCode);
            request.setTemplateParam(templateParam);
            request.setOutId(outId);

            SendSmsResponse response = acsClient.getAcsResponse(request);
            this.message = response.getMessage();
            log.info("短信接口返回的数据----------------");
            log.info("Code=" + response.getCode());
            log.info("Message=" + response.getMessage());
            log.info("RequestId=" + response.getRequestId());
            log.info("BizId=" + response.getBizId());
        } catch (ServerException e) {
            log.error(e.getMessage());
        } catch (ClientException e) {
            log.error(e.getMessage());
        }
        return true;
    }

    public String getMessage() {
        return message;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateParam() {
        return templateParam;
    }

    public void setTemplateParam(String templateParam) {
        this.templateParam = templateParam;
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }

}
