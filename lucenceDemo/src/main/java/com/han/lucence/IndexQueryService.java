package com.han.lucence;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class IndexQueryService {


    IndexReader reader;
    private final static Logger LOGGER = LoggerFactory.getLogger(IndexQueryService.class);

    public void readerIndex(Query query) {
           int pageSize = 2;
           int pageNum = 1;
           int start = (pageNum-1)*pageSize;
           int end = start + pageSize;
        try {
            reader = DirectoryReader.open(FSDirectory.open(new File("E://lucence/index")));
            IndexSearcher searcher = new IndexSearcher(reader);
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>","</font>");
            QueryScorer queryScorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(simpleHTMLFormatter,queryScorer);

            Sort sort = new Sort();
            sort.setSort(new SortField("id", SortField.Type.LONG,true));

            TopDocs topDocs = searcher.search(query, end);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            int total = topDocs.totalHits;
            LOGGER.info("Found doc num is {} ", total);


            for (ScoreDoc scoreDoc : scoreDocs) {
                float score = scoreDoc.score;
                int id = scoreDoc.doc;
                Document document = searcher.doc(id);
                String content = document.get("content");
                String title = document.get("title");
                String tempId = document.get("id");
                //高亮必须是查询条件里的列或者属性
                title = highlighter.getBestFragment(new IKAnalyzer(),"tile",title);

                System.out.println("[  ");
                System.out.println("id: " + tempId + " content: " + content + " tilte : " + title + " score : " + score);
                System.out.println(" ]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }
    }

    /**
     * 正常
     */
    @Test
    public void testQuery() throws ParseException {
        QueryParser queryParser = new QueryParser("title", new IKAnalyzer());
            Query query = queryParser.parse("一个贸易");
       readerIndex(query);
    }

    /**
     * 词条全匹配，不模糊的匹配
     */
    @Test
    public void testTermQuery(){
        TermQuery query = new TermQuery(new Term("content","评论"));
        readerIndex(query);
    }

    /**
     * 统配符匹配
     *  * && ?
     */
    @Test
    public void testWaildQuery(){
//        WildcardQuery query = new WildcardQuery(new Term("content","评论?"));
        WildcardQuery query = new WildcardQuery(new Term("content","吊*"));
        readerIndex(query);
    }

    /**
     * 模糊查询
     *  最大修正次数为2，一个词条最多只能错两处。
     *  框架自动使用替换，移动，补位 组合在一块最多只能2处
     */
    @Test
    public void testFuzzyQuery(){
//        WildcardQuery query = new WildcardQuery(new Term("content","评论?"));
        FuzzyQuery query = new FuzzyQuery(new Term("title","lucen"),2);
        readerIndex(query);
    }

    /**
     * 数值范围
     */
    @Test
    public void testNumQuery(){
//        WildcardQuery query = new WildcardQuery(new Term("content","评论?"));
        NumericRangeQuery query = NumericRangeQuery.newLongRange("id",1L,4L,true,true);
        readerIndex(query);
    }

    /**
     * 多条件查询
     */
    @Test
    public void testManyConditionQuery(){
//        WildcardQuery query = new WildcardQuery(new Term("content","评论?"));
        BooleanQuery booleanClauses = new BooleanQuery();
        NumericRangeQuery query1 = NumericRangeQuery.newLongRange("id",1L,4L,true,true);
        WildcardQuery query2 = new WildcardQuery(new Term("content","吊*"));
        booleanClauses.add(query1, BooleanClause.Occur.MUST);
        booleanClauses.add(query2,BooleanClause.Occur.MUST);
        readerIndex(booleanClauses);

    }

    @Test
    public void update() throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST,analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        Directory directory = FSDirectory.open(new File("E://lucence/index"));
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        Document document = new Document();
        document.add(new TextField("title","这是修改", Field.Store.YES));
        document.add(new LongField("id",6, Field.Store.YES));
        indexWriter.updateDocument(new Term("title","杭州"),document);
        indexWriter.commit();
        indexWriter.close();
    }


    @Test
    public void delete() throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST,analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        Directory directory = FSDirectory.open(new File("E://lucence/index"));
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);

        indexWriter.deleteDocuments(new Term("title","修改"));
        indexWriter.commit();
        indexWriter.close();
    }




}
