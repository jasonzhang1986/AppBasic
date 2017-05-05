package me.jasonzhang.app.net.model;

import java.util.List;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public class GankBean {
    public String _id;
    public String createAt;
    public String desc;
    public List<String> images;
    public String publishedAt;
    public String source;
    public String type;
    public String url;
    public boolean userd;
    public String who;

    @Override
    public String toString() {
        return desc + "["+type+"] "+"--"+who+"\n";
    }
}
