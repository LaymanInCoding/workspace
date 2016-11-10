package com.witmoon.xmb.util;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * HTTP工具类, 此类如果参与UI的更新, 需要异步处理.
 * Created by zhyh on 2015/7/25.
 */
public class HttpUtility {
    private static final String CHARSET_ENCODING = "UTF-8";
    private static final String LINE_FEED = "\r\n";

    private static String multipartBoundary;
    private static char[] MULTIPART_CHARS =
            ("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    /**
     * 发送POST请求
     */
    public static String post(String purl, Map<String, String> paramMap) throws Exception {
        return post(purl, null, paramMap, null);
    }

    /**
     * 发送POST请求
     *
     * @param purl      HTTP请求URL
     * @param headerMap 需要携带的HTTP请求头信息
     * @param paramMap  需要携带的参数Map
     */
    public static String post(String purl, Map<String, String> headerMap, Map<String,
            String> paramMap) throws Exception {
        return post(purl, headerMap, paramMap, null);
    }

    /**
     * 发送文件数组参数的Post请求, 形如file[0] file[1] file[2]...
     * @param fileName  参数名, 将会生成如fileName[0]、fileName[1]、fileName[2]...参数名
     * @param fileList  文件参数数组
     */
    public static String post(String purl, Map<String, String> headerMap, Map<String,
            String> paramMap, String fileName, List<File> fileList) throws Exception {
        Map<String, File> tmap = new HashMap<>(fileList.size());
        for (int i = 0; i < fileList.size(); i++) {
            tmap.put(fileName + "[" + i + "]", fileList.get(i));
        }
        return post(purl, headerMap, paramMap, tmap);
    }

    public static String bitmapPost(String purl, Map<String,String> paramMap, String fileName,
                               List<byte[]> bitmapList) throws Exception {
        multipartBoundary = _generateMultipartBoundary();
        HttpURLConnection connection = null;
        DataOutputStream dataOutStream = null;
        try {
            connection = _openPostConnection(purl);
            dataOutStream = new DataOutputStream(connection.getOutputStream());

            // 添加Post请求参数
            _doAddFormFields(dataOutStream, paramMap);

            for (int i = 0; i < bitmapList.size(); i++) {
                dataOutStream.writeBytes("--" + multipartBoundary);
                dataOutStream.writeBytes(LINE_FEED);

                dataOutStream.writeBytes("Content-Disposition: form-data; name=\"" + fileName +
                        "[" + i + "]\"; filename=\"" + System.currentTimeMillis() + ".jpeg\"");
                dataOutStream.writeBytes(LINE_FEED);

                dataOutStream.writeBytes("Content-Type: " + URLConnection
                        .guessContentTypeFromName("picture.jpeg"));
                dataOutStream.writeBytes(LINE_FEED);
                dataOutStream.writeBytes(LINE_FEED);

                InputStream iStream = null;
                try {
                    iStream = new ByteArrayInputStream(bitmapList.get(i));
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = iStream.read(buffer)) != -1) {
                        dataOutStream.write(buffer, 0, bytesRead);
                    }

                    iStream.close();
                    dataOutStream.writeBytes(LINE_FEED);
                    dataOutStream.flush();
                } catch (IOException ignored) {
                } finally {
                    try {
                        if (iStream != null) iStream.close();
                    } catch (Exception ignored) {
                    }
                }
            }

            dataOutStream.writeBytes(LINE_FEED);
            dataOutStream.writeBytes("--" + multipartBoundary);
            dataOutStream.writeBytes(LINE_FEED);
            dataOutStream.close();

            return _doFetchResponse(connection);
        } finally {
            if (connection != null) connection.disconnect();
            try {
                if (dataOutStream != null) dataOutStream.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 发送POST请求
     *
     * @param purl      HTTP请求URL
     * @param headerMap 需要携带的HTTP请求头信息
     * @param paramMap  需要携带的参数Map
     * @param fileMap   需要上传的文件
     */
    public static String post(String purl, Map<String, String> headerMap, Map<String,
            String> paramMap, Map<String, File> fileMap) throws Exception {
        multipartBoundary = _generateMultipartBoundary();
        return _doPost(purl, headerMap, paramMap, fileMap);
    }

    private static String _doPost(String purl, Map<String, String> headerMap, Map<String,
            String> paramMap, Map<String, File> fileMap) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream dataOutStream = null;
        try {
            connection = _openPostConnection(purl);
            dataOutStream = new DataOutputStream(connection.getOutputStream());

            // 向HTTP请求添加头信息
            _doAddHeaders(dataOutStream, headerMap);

            // 添加Post请求参数
            _doAddFormFields(dataOutStream, paramMap);

            // 向HTTP请求添加上传文件部分
            _doAddFilePart(dataOutStream, fileMap);

            dataOutStream.writeBytes(LINE_FEED);
            dataOutStream.writeBytes("--" + multipartBoundary);
            dataOutStream.writeBytes(LINE_FEED);
            dataOutStream.close();

            return _doFetchResponse(connection);
        } finally {
            if (connection != null) connection.disconnect();
            try {
                if (dataOutStream != null) dataOutStream.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 生成HTTP协议中的边界字符串
     *
     * @return 边界字符串
     */
    private static String _generateMultipartBoundary() {
        Random rand = new Random();
        char[] chars = new char[rand.nextInt(9) + 12]; // 随机长度(12 - 20个字符)
        for (int i = 0; i < chars.length; i++) {
            chars[i] = MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)];
        }
        return "===AndroidFormBoundary" + new String(chars);
    }

    private static HttpURLConnection _openPostConnection(String purl) throws IOException {
        URL url = new URL(purl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" +
                multipartBoundary);
        connection.setRequestProperty("User-Agent", "Android Client Agent");

        return connection;
    }

    private static void _doAddHeaders(DataOutputStream oStream, Map<String,
            String> headerMap) throws IOException {
        if (headerMap == null || headerMap.isEmpty()) return;

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            oStream.writeBytes(entry.getKey() + ":" + entry.getValue());
            oStream.writeBytes(LINE_FEED);
        }
    }

    /**
     * 向HTTP报文中添加Form表单域参数
     *
     * @param oStream  HTTP输出流
     * @param paramMap 参数Map
     * @throws IOException
     */
    private static void _doAddFormFields(DataOutputStream oStream, Map<String,
            String> paramMap) throws IOException {
        if (paramMap == null || paramMap.isEmpty()) return;

        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            oStream.writeBytes("--" + multipartBoundary);
            oStream.writeBytes(LINE_FEED);

            oStream.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"");
            oStream.writeBytes(LINE_FEED);

            oStream.writeBytes(LINE_FEED);
            oStream.writeBytes(URLEncoder.encode(entry.getValue(), CHARSET_ENCODING));
            oStream.writeBytes(LINE_FEED);
        }
    }

    /**
     * 向HTTP请求添加上传文件部分
     *
     * @param oStream 由HTTPURLConnection获取的输出流
     * @param fileMap 文件Map, key为文件域名, value为要上传的文件
     */
    private static void _doAddFilePart(DataOutputStream oStream, Map<String,
            File> fileMap) throws IOException {
        if (fileMap == null || fileMap.isEmpty()) return;

        for (Map.Entry<String, File> fileEntry : fileMap.entrySet()) {
            String fileName = fileEntry.getValue().getName();

            oStream.writeBytes("--" + multipartBoundary);
            oStream.writeBytes(LINE_FEED);

            oStream.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey() +
                    "\"; filename=\"" + fileName + "\"");
            oStream.writeBytes(LINE_FEED);

            oStream.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(fileName));
            oStream.writeBytes(LINE_FEED);
            oStream.writeBytes(LINE_FEED);

            InputStream iStream = null;
            try {
                iStream = new FileInputStream(fileEntry.getValue());
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = iStream.read(buffer)) != -1) {
                    oStream.write(buffer, 0, bytesRead);
                }

                iStream.close();
                oStream.writeBytes(LINE_FEED);
                oStream.flush();
            } catch (IOException ignored) {
            } finally {
                try {
                    if (iStream != null) iStream.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 获取HTTP响应
     *
     * @param connection HTTP请求连接
     * @return 响应字符串
     * @throws IOException
     */
    private static String _doFetchResponse(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("服务器返回状态非正常响应状态.");
        }
        return new String(streamToByteArray(connection.getInputStream(), 1024));
    }

    public static byte[] streamToByteArray(InputStream aInputStream, int aBufferSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // copy stream
        byte[] buf = new byte[aBufferSize];
        int bytesRead;
        try {
            //
            while (true) {
                bytesRead = aInputStream.read(buf);
                if (bytesRead <= 0) break;
                baos.write(buf, 0, bytesRead);
            }
            //
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return baos.toByteArray();
    }
}
