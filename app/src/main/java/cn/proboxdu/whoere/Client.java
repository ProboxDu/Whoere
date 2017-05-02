package cn.proboxdu.whoere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Probox on 2017/4/23.
 */

public class Client {
    private static String URL = "http://123.207.217.88:8080";
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    private static String result;
    public static String sendPost(String url, Map<String, String> parameters,String addition) {
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// 处理请求参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(parameters.get(name),
                                    "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(parameters.get(name),
                                    "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // 创建URL对象
            java.net.URL connURL= new java.net.URL(url);
            // 打开URL连接
            HttpURLConnection httpConn;
            httpConn = (HttpURLConnection) connURL
                    .openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 设置POST方式
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            // 获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(httpConn.getOutputStream());
            // 发送请求参数
            out.write(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line+addition;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    public static boolean AddAccount(AccountUtils Account)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("name",Account.getUsername());
        parameters.put("password",Account.getPassword());
        parameters.put("channel",Account.getChannel());
        parameters.put("options",Account.getOptions());
        parameters.put("gpsx",Account.getGpsx());
        parameters.put("gpsy",Account.getGpsy());
        parameters.put("ip",Account.getIp());
        final Map<String, String> p = parameters;
        Thread opt=new Thread(new Thread() {
            public void run() {
                result = sendPost(URL+"/AddServlet",p,"");
            };
        });
        opt.start();
        try {
            opt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result.equals("Successful")) return true;
        return false;
    }
    public static boolean DeleteAccount(AccountUtils Account)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("name",Account.getUsername());
        parameters.put("password",Account.getPassword());
        final Map<String, String> p = parameters;
        Thread opt=new Thread(new Thread() {
            public void run() {
                result = sendPost(URL+"/DeleteServlet",p,"");
            };
        });
        opt.start();
        try {
            opt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result.equals("Successful")) return true;
        return false;
    }
    public static boolean UpdateAccount(AccountUtils Account)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("name",Account.getUsername());
        parameters.put("password",Account.getPassword());
        parameters.put("newpassword",Account.getNewpassword());
        parameters.put("channel",Account.getChannel());
        parameters.put("options",Account.getOptions());
        parameters.put("gpsx",Account.getGpsx());
        parameters.put("gpsy",Account.getGpsy());
        parameters.put("ip",Account.getIp());
        final Map<String, String> p = parameters;
        Thread opt=new Thread(new Thread() {
            public void run() {
                result = sendPost(URL+"/UpdateServlet",p,"");
            };
        });
        opt.start();
        try {
            opt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result.equals("Successful")) return true;
        return false;
    }
    public static boolean SendMessage(AccountUtils Account,String messages)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("name",Account.getUsername());
        parameters.put("channel",Account.getChannel());
        parameters.put("options",Account.getOptions());
        parameters.put("messages",messages);
        parameters.put("datetime",df.format(new Date()));
        final Map<String, String> p = parameters;
        Thread opt=new Thread(new Thread() {
            public void run() {
                result = sendPost(URL+"/SendMessageServlet",p,"");
            };
        });
        opt.start();
        try {
            opt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result.equals("Successful")) return true;
        return false;
    }
    public static String[] RecieveMessage(AccountUtils Account)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("lastMessageId",""+Account.getLastMessageId());
        parameters.put("channel",Account.getChannel());
        final Map<String, String> p = parameters;
        Thread opt=new Thread(new Thread() {
            public void run() {
                result = sendPost(URL+"/ReceiveMessageServlet",p,"\n");
            };
        });
        opt.start();
        try {
            opt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String tmp[] = result.split("\n");
        return tmp;
    }
    public static String[] getAccountList(AccountUtils Account) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("name",Account.getUsername());
        parameters.put("password",Account.getPassword());
        final Map<String, String> p = parameters;
        Thread opt=new Thread(new Thread() {
            public void run() {
                result = sendPost(URL+"/AccountListServlet",p,"\n");
            };
        });
        opt.start();
        try {
            opt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String tmp[] = result.split("\n");
        return tmp;
    }
}
