package cn.cerc.jdb.oss;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;

import cn.cerc.jdb.core.ISession;

public class OssSession implements ISession {
    private static final Logger log = LoggerFactory.getLogger(OssSession.class);
    // 设置连接地址
    public static final String oss_endpoint = "oss.endpoint";
    // 连接区域
    public static final String oss_bucket = "oss.bucket";
    // 对外访问地址
    public static final String oss_site = "oss.site";
    // 连接id
    public static final String oss_accessKeyId = "oss.accessKeyId";
    // 连接密码
    public static final String oss_accessKeySecret = "oss.accessKeySecret";
    // IHandle 标识
    public static final String sessionId = "ossSession";
    private OSSClient client;
    private String bucket;
    private String site;

    public OSSClient getClient() {
        if (client == null)
            throw new RuntimeException("OssSession初始化失败: client is null");
        return client;
    }

    public void setClient(OSSClient ossClient) {
        this.client = ossClient;
    }

    @Override
    public void closeSession() {
        // 关闭OSSClient
        client = null;
        log.debug("关闭ossSession成功");
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    // 上传文件
    public void upload(String fileName, InputStream inputStream) {
        // 例：upload(inputStream, "131001/Default/131001/temp.txt")
        client.putObject(getBucket(), fileName, inputStream);
    }

    // 下载文件
    public boolean download(String fileName, String localFile) {
        GetObjectRequest param = new GetObjectRequest(getBucket(), fileName);
        File file = new File(localFile);
        ObjectMetadata metadata = client.getObject(param, file);
        return file.exists() && metadata.getContentLength() == file.length();
    }

    // 删除文件
    public void delete(String fileName) {
        client.deleteObject(getBucket(), fileName);
    }

    public String getContent(String fileName) {
        try {
            StringBuffer sb = new StringBuffer();
            // ObjectMetadata meta = client.getObjectMetadata(this.getBucket(),
            // fileName);
            // if (meta.getContentLength() == 0)
            // return null;
            OSSObject obj = client.getObject(getBucket(), fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
            while (true) {
                String line;
                line = reader.readLine();
                if (line == null)
                    break;
                sb.append(line);
            }
            return sb.toString();
        } catch (OSSException | IOException e) {
            return null;
        }
    }

    /**
     * 判断指定的文件名是否存在
     * 
     * @param fileName
     *            带完整路径的文件名
     * @return 若存在则返回true
     */
    public boolean existsFile(String fileName) {
        try {
            OSSObject obj = client.getObject(getBucket(), fileName);
            return obj.getObjectMetadata().getContentLength() > 0;
        } catch (OSSException e) {
            return false;
        }
    }

    /**
     * 返回可用的文件名称
     * 
     * @param fileName
     *            带完整路径的文件名
     * @param def
     *            默认值
     * @return 若存在则返回路径，否则返回默认值
     */
    public String getFileUrl(String fileName, String def) {
        if (existsFile(fileName))
            return String.format("%s/%s", site, fileName);
        else
            return def;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
