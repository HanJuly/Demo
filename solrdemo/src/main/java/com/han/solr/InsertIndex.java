package com.han.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InsertIndex {

    @Test
    public void inertIndex() throws IOException, SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
        SolrInputDocument document1 = new SolrInputDocument();
        document1.addField("id", "5");
        document1.addField("title","我要踢球");
        document1.addField("content","蓝瘦香菇");
        document1.addField("docurl_s" ,"http://www.slf4j.org/codes.html#StaticLoggerBinde");
        document1.addField("update__dt","2019-7-19T17:12:55Z");
        documents.add(document1);

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id","2");
        doc.addField("title","双十二马上到了");
        doc.addField("content","钱包准备好了");
        doc.addField("docurl_s" ,"http://www.slf4j.org/codes.html#StaticLoggerBinde");
        doc.addField("update__dt","2019-7-19T12:12:12Z");
        documents.add(doc);

        SolrInputDocument doc2 = new SolrInputDocument();
        doc2.addField("id","3");
        doc2.addField("title","圣诞节也要到了");
        doc2.addField("content","女朋友准备好了");
        doc2.addField("docurl_s" ,"http://www.slf4j.org/codes.html#StaticLoggerBinde");
        doc2.addField("update__dt","2019-7-19T12:12:12Z");
        documents.add(doc2);

        SolrInputDocument doc3 = new SolrInputDocument();
        doc3.addField("id","4");
        doc3.addField("title","Apache is a nb org.");
        doc3.addField("content","女朋友准备");
        doc3.addField("docurl_s" ,"http://www.slf4j.org/codes.html#StaticLoggerBinde");
        doc3.addField("update__dt","2019-7-18T17:12:12Z");
        documents.add(doc3);

        SolrInputDocument doc4 = new SolrInputDocument();
        doc4.addField("id","5");
        doc4.addField("title","郭风英");
        doc4.addField("content","I Love you");
        doc4.addField("docurl_s" ,"http://www.slf4j.org/codes.html#StaticLoggerBinde");
        doc4.addField("update__dt","2019-7-19T17:12:12Z");
        documents.add(doc4);

        solrServer.add(documents);

        solrServer.commit();

    }

    @Test
    public void insertJavaBean() throws IOException, SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        News news  = new News();
        news.setId(UUID.randomUUID().toString());
        news.setTitle("有内鬼停止交易");
        news.setContent("安排上了");
        news.setDocurl(" http://www.slf4j.org/codes.html#StaticLoggerBinde");

        solrServer.addBean(news);
        solrServer.commit();
    }

    @Test
    public void deleteIndex() throws IOException, SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        solrServer.deleteById("3");
        solrServer.commit();
    }

    @Test
    public void query() throws SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        SolrQuery solrQuery = new SolrQuery("title:内鬼");
        solrQuery.add("title","我");
        QueryResponse response = solrServer.query(solrQuery);
        SolrDocumentList results = response.getResults();
        for(SolrDocument solrDocument :results){
            String id = (String)solrDocument.get("id");
            String title = (String) ((ArrayList)solrDocument.get("title")).get(0);
            String content = (String) ((ArrayList)solrDocument.get("content")).get(0);
            String docurlS = (String) solrDocument.get("docurl_s");
            System.out.println(String.format("id : %s , content: %s , title: %s, docurl: %s ;"
                    ,id,content,title,docurlS));
        }
    }

    @Test
    public void complexQuery() throws SolrServerException {
        // ? *通配符和占位符
//        SolrQuery solrQuery = new SolrQuery("title:apach?");
//        SolrQuery solrQuery = new SolrQuery("title:apa*");
//        SolrQuery solrQuery = new SolrQuery("id:[1 TO 5]");
        SolrQuery solrQuery = new SolrQuery("update__dt:[2019-07-19T12:12:12Z TO 2019-07-19T17:12:12Z]");
//        SolrQuery solrQuery = new SolrQuery("(content:apache OR id:4 ) AND id:4");
        // AND   :  与lucene的MUST  必须
        // NOT  : 与 lucene的 MUST_NOT
        // OR  :  与 lucene的SHOULD
//        SolrQuery solrQuery = new SolrQuery("title:到了 AND id:2 OR content:钱包");

//        int page = 2;
//        int pageSize = 2;
////        SolrQuery solrQuery = new SolrQuery("*:*");
//        SolrQuery solrQuery = new SolrQuery("title:到了");
//
//        solrQuery.setSort("id", SolrQuery.ORDER.desc);
//
////        solrQuery.setStart((page-1)*2);
////        solrQuery.setRows(pageSize);
//
//        solrQuery.setHighlight(true);
//        solrQuery.addHighlightField("title");
//        solrQuery.addHighlightField("content");
//        solrQuery.setHighlightSimplePre("<p>");
//        solrQuery.setHighlightSimplePost("</p>");


        QueryResponse response =common(solrQuery);
//        Map<String,Map<String,List<String>>> mapMap = response.getHighlighting();
//        for(Map.Entry<String,Map<String,List<String>>> entry : mapMap.entrySet()){
//            String id = entry.getKey();
//            Map<String,List<String>> data = entry.getValue();
//            System.out.println("id: "+id+"  "+data);
//        }
    }

    public QueryResponse common(SolrQuery solrQuery) throws SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        QueryResponse response = solrServer.query(solrQuery);
        SolrDocumentList results = response.getResults();
        for(SolrDocument solrDocument :results){
            String id = (String)solrDocument.get("id");
            String title = (String) ((ArrayList)solrDocument.get("title")).get(0);
            String content = (String) ((ArrayList)solrDocument.get("content")).get(0);
            String docurlS = (String) solrDocument.get("docurl_s");
            System.out.println(String.format("id : %s , content: %s , title: %s, docurl: %s ;"
                    ,id,content,title,docurlS));
        }
        return response;
    }
}
