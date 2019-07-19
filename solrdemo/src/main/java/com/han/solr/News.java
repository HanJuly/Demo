package com.han.solr;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

public class News implements Serializable {
    @Field
    private String id;
    @Field
    private String title;
    @Field
    private String content;

    @Field("docurl_s")
    private String docurl;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocurl() {
        return docurl;
    }

    public void setDocurl(String docurl) {
        this.docurl = docurl;
    }
}
