package com.han.solr.cluster;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class SolrCluster {

    @Test
    public void test() throws IOException, SolrServerException {
        String zkHost = "192.168.72.141:2181,192.168.72.142:2181,192.168.72.143:2181";
        CloudSolrServer cloudSolrServer = new CloudSolrServer(zkHost);
        cloudSolrServer.setDefaultCollection("collection2");
        cloudSolrServer.setZkClientTimeout(60);

        SolrInputDocument document = new SolrInputDocument();
        document.addField("id",123);
        document.addField("title","我要去西班牙");
        document.addField("content","我没钱");
        cloudSolrServer.add(document);
        cloudSolrServer.commit();
    }

    @Test
    public void testQuery() throws SolrServerException {
        String zkHost = "192.168.72.141:2181,192.168.72.142:2181,192.168.72.143:2181";
        CloudSolrServer cloudSolrServer = new CloudSolrServer(zkHost);
        cloudSolrServer.setDefaultCollection("collection2");
        cloudSolrServer.setZkClientTimeout(60);

        SolrQuery solrQuery = new SolrQuery("*:*");
        QueryResponse queryResponse = cloudSolrServer.query(solrQuery);
        SolrDocumentList solrDocuments = queryResponse.getResults();
        for(SolrDocument solrDocument: solrDocuments){
            String id = (String) solrDocument.get("id");
            String title = (String)((ArrayList) solrDocument.get("title")).get(0);
            String content =(String)((ArrayList) solrDocument.get("content")).get(0);;
            System.out.println(String.format("id: %s title:%s content:%s ",id,title,content));
        }

    }


    @Test
    public void delete() throws SolrServerException, IOException {
        String zkHost = "192.168.72.141:2181,192.168.72.142:2181,192.168.72.143:2181";
        CloudSolrServer cloudSolrServer = new CloudSolrServer(zkHost);
        cloudSolrServer.setDefaultCollection("collection2");
        cloudSolrServer.setZkClientTimeout(60);

        cloudSolrServer.deleteById("123");
        cloudSolrServer.commit();
    }
}
