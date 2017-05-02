package cn.proboxdu.whoere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Probox on 2017/4/24.
 */

public class AccountUtils {
    private String username = "";
    private String password = "";
    private String newpassword = "";
    private String channel = "";
    private String options = "";
    private String gpsx = "";
    private String gpsy = "";
    private String ip = "";
    private int lastMessageId=0;
    public AccountUtils(String username,String password,String channel,String options,String gpsx,String gpsy,String ip)
    {
        this.username = username;
        this.password = password;
        this.channel = channel;
        this.options = options;
        this.gpsx = gpsx;
        this.gpsy = gpsy;
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getGpsx() {
        return gpsx;
    }
    public void setGpsx(String gpsx) {
        this.gpsx = gpsx;
    }
    public String getGpsy() {
        return gpsy;
    }

    public void setGpsy(String gpsy) {
        this.gpsy = gpsy;
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(int lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
    public String getNewpassword()
    {
        return newpassword;
    }
    public ArrayList<Map<String,Object>> toList()
    {
        ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
        String[] mListTitle = {"UserName","Channel","Options","Latitude","Longitude","IpAddress"};
        String[] mListStr = {username,channel,options,gpsx,gpsy,ip};
        int len = mListTitle.length;
        for (int i=0;i<len;i++)
        {
            Map<String,Object> item = new HashMap<String,Object>();
            item.put("title", mListTitle[i]);
            item.put("text", mListStr[i]);
            mData.add(item);
        }
        return mData;
    }
}
