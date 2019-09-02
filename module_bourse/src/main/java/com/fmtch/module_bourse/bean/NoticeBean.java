package com.fmtch.module_bourse.bean;

/**
 * Created by wtc on 2019/5/17
 */
public class NoticeBean {

    /**
     * id : 3
     * title : AKA Trade 交易所上线2
     * content : <p>恭喜AKA Trade 交易所隆重上线2！！！</p><p><br/></p><p>2019-03-22</p>
     * created_at : 2019-03-22 16:57:31
     */

    private String id;
    private String title;
    private String html_url;
    private String created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String content) {
        this.html_url = html_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
