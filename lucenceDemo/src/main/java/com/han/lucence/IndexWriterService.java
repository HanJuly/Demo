package com.han.lucence;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexWriterService {

    public static void main(String[] args) {
        IndexWriterService indexWriterService = new IndexWriterService();
        indexWriterService.indexWrite();
    }
    public void indexWrite(){
        try {
//            Analyzer analyzer = new StandardAnalyzer();
            Analyzer analyzer = new IKAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST,analyzer);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            Directory directory = FSDirectory.open(new File("E://lucence/index"));
            IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);

            List<Document> docs = new ArrayList<Document>();

            Document doc1 = new Document();
            doc1.add(new LongField("id",1, Field.Store.YES));
            doc1.add(new TextField("title","杭州女子退货18件", Field.Store.YES));
            doc1.add(new TextField("content","网友评论, 吊炸天了", Field.Store.YES));

            Document doc2 = new Document();
            doc2.add(new LongField("id",2, Field.Store.YES));
            doc2.add(new TextField("title","美国贸易战争, 加2500亿关税", Field.Store.YES));
            doc2.add(new TextField("content","中国新闻报道说, 愿谈愿谈, 能打则打", Field.Store.YES));


            Document doc3 = new Document();
            doc3.add(new LongField("id",3, Field.Store.YES));
            doc3.add(new TextField("title","高考马上就要到了", Field.Store.YES));
            doc3.add(new TextField("content","再有一个月, 高考就要来临了, 希望都中状元", Field.Store.YES));

            Document doc4 = new Document();
            doc4.add(new LongField("id",4, Field.Store.YES));
            doc4.add(new TextField("title","lucene的简介", Field.Store.YES));
            doc4.add(new TextField("content","lucene是apache的开源的全文检索引擎的工具包, 使用lucene来构建一个搜索引擎", Field.Store.YES));


            docs.add(doc1);
            docs.add(doc2);
            docs.add(doc3);
            docs.add(doc4);

            indexWriter.addDocuments(docs);

            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
