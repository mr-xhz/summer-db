package cn.cerc.jdb.oss;

import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.ObjectMetadata;

import cn.cerc.jdb.core.IHandle;

public class OssDisk {
    private static final Logger log = LoggerFactory.getLogger(OssDisk.class);
    private OssSession session;
    private OSSClient client;
    private String localPath;

    public OssDisk(IHandle handle) {
        session = (OssSession) handle.getProperty(OssSession.sessionId);
        client = session.getClient();
    }

    // 上传文件流
    public void upload(String fileName, InputStream inputStream) {
        session.upload(fileName, inputStream);
    }

    // 上传文件
    public boolean upload(String remoteFile, String localFile) {
        // 上传本地文件到服务器
        // 例：upload("D:\\oss\\temp.png", "131001/Default/131001/temp.png")
        File file = new File(localFile);
        if (!file.exists())
            throw new RuntimeException("文件不存在：" + localFile);
        try {
            ObjectMetadata summary = client.getObjectMetadata(session.getBucket(), remoteFile);
            if (summary != null && summary.getContentLength() == file.length()) {
                log.info("本地文件与云端文件大小一致，忽略上传请求");
                return true;
            }
        } catch (OSSException e) {
            log.info("服务器上无此文件，开始上传");
        }

        client.putObject(session.getBucket(), remoteFile, file);
        ObjectMetadata metadata = client.getObjectMetadata(session.getBucket(), remoteFile);
        return file.exists() && metadata.getContentLength() == file.length();
    }

    // 下载文件
    public boolean download(String fileName) {
        if (localPath == null || "".equals(localPath))
            throw new RuntimeException("localPath 必须先进行设置！");

        // 创建本地目录
        String localFile = localPath + fileName.replace('/', '\\');
        createFolder(localFile);

        return session.download(fileName, localFile);
    }

    // 删除文件
    public void delete(String fileName) {
        session.delete(fileName);
    }

    // 拷贝Object
    public void copyObject(String srcBucketName, String srcKey, String destBucketName, String destKey) {
        /*
         * sample: srcBucketName = "scmfiles" srcKey = "Products\010001\钻石.jpg";
         * destBucketName = "vinefiles"; destKey = "131001\product\0100001\钻石.jpg";
         */
        CopyObjectResult result = client.copyObject(srcBucketName, srcKey, destBucketName, destKey);

        // 打印结果
        log.info("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());
    }

    public OSSClient getClient() {
        return client;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public OssSession getSession() {
        return session;
    }

    public void setSession(OssSession session) {
        this.session = session;
    }

    // 如果文件所在的文件目录不存在，则创建之
    private void createFolder(String fileName) {
        String tmpPath = fileName.substring(0, fileName.lastIndexOf("\\") + 1);
        String subPath = tmpPath;
        int fromIndex = 0;
        while (tmpPath.indexOf("\\", fromIndex) > -1) {
            int beginIndex = tmpPath.indexOf("\\", fromIndex);
            if (fromIndex == -1)
                break;
            subPath = tmpPath.substring(0, beginIndex);
            fromIndex = subPath.length() + 1;
            if (subPath.length() > 2) {
                File file = new File(subPath);
                // 如果文件夹不存在则创建
                if (!file.exists() && !file.isDirectory())
                    file.mkdir();
            }
        }
    }
}
