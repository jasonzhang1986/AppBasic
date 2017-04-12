package me.jasonzhang.appbase.net.model;

import java.util.List;

/**
 * Created by JifengZhang on 2017/4/6.
 */

public class InstallNeceModel {
    public int id;			//应用ID
    public String name;	//应用名称
    public String icon;	//icon url
    public boolean backup;	//是否是替补池
    public String pkg;		//应用包名
    public String url;//下载地址
    public List<String> tags;//标签
}
