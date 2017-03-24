package cn.ranta.demo.luceneengine;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * Hello world!
 *
 */
public class Program {
    public static void main(String[] args) {

        Directory directory = new RAMDirectory();

        Analyzer analyzer = new StandardAnalyzer();

        CreateIndex(directory, analyzer);

        Search(directory, analyzer);

        System.out.println("the end.");
    }

    private static void CreateIndex(Directory directory, Analyzer analyzer) {

        try {

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

            for (int i = 0; i < 100; i++) {
                Document document = new Document();

                FieldType storeOnlyFieldType = new FieldType();
                storeOnlyFieldType.setStored(true);
                document.add(new Field("StoreOnly", String.format("Store Only %d %d", i / 10, i % 10), storeOnlyFieldType));

                FieldType indexOnlyFieldType = new FieldType();
                indexOnlyFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
                document.add(new Field("IndexOnly", String.format("Index Only %d %d", i / 10, i % 10), indexOnlyFieldType));

                FieldType storeIndexFieldType = new FieldType();
                storeIndexFieldType.setStored(true);
                storeIndexFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
                document.add(new Field("StoreIndex", String.format("Store Index %d %d", i / 10, i % 10), storeIndexFieldType));

                indexWriter.addDocument(document);
            }

            // indexWriter.commit();

            indexWriter.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private static void Search(Directory directory, Analyzer analyzer) {

        try {

            IndexReader indexReader = DirectoryReader.open(directory);

            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            TopDocs topDocs = null;

            int demo = 6;

            switch (demo) {
            case 1: { // TermQuery
                TermQuery termQuery = new TermQuery(new Term("StoreOnly", "7"));

                topDocs = indexSearcher.search(termQuery, 5);
            }

                break;
            case 2: { //TermQuery
                TermQuery termQuery = new TermQuery(new Term("IndexOnly", "7"));

                topDocs = indexSearcher.search(termQuery, 5);
            }

                break;
            case 3: { //TermQuery
                TermQuery termQuery = new TermQuery(new Term("StoreIndex", "7"));

                topDocs = indexSearcher.search(termQuery, 5);
            }

                break;
            case 4: { //BooleanQuery MUST

                TermQuery termQuery6 = new TermQuery(new Term("IndexOnly", "6"));
                TermQuery termQuery7 = new TermQuery(new Term("IndexOnly", "7"));

                BooleanQuery booleanQuery = new BooleanQuery.Builder()
                        .add(new BooleanClause(termQuery6, Occur.MUST))
                        .add(new BooleanClause(termQuery7, Occur.MUST))
                        .build();

                topDocs = indexSearcher.search(booleanQuery, 20);
            }

                break;
            case 5: { //BooleanQuery SHOULD

                TermQuery termQuery6 = new TermQuery(new Term("IndexOnly", "6"));
                TermQuery termQuery7 = new TermQuery(new Term("IndexOnly", "7"));

                BooleanQuery booleanQuery = new BooleanQuery.Builder()
                        .add(new BooleanClause(termQuery6, Occur.SHOULD))
                        .add(new BooleanClause(termQuery7, Occur.SHOULD))
                        .build();

                topDocs = indexSearcher.search(booleanQuery, 20);
            }
            
            break;
        case 6: { //BooleanQuery MUST & SHOULD

            TermQuery termQuery5 = new TermQuery(new Term("IndexOnly", "5"));
            TermQuery termQuery6 = new TermQuery(new Term("IndexOnly", "6"));
            TermQuery termQuery7 = new TermQuery(new Term("IndexOnly", "7"));
            TermQuery termQuery8 = new TermQuery(new Term("IndexOnly", "8"));

            BooleanQuery must56 = new BooleanQuery.Builder()
                    .add(new BooleanClause(termQuery5, Occur.MUST))
                    .add(new BooleanClause(termQuery6, Occur.MUST))
                    .build();
            
            BooleanQuery must78 = new BooleanQuery.Builder()
                    .add(new BooleanClause(termQuery7, Occur.MUST))
                    .add(new BooleanClause(termQuery8, Occur.MUST))
                    .build();

            BooleanQuery booleanQuery = new BooleanQuery.Builder()
                    .add(new BooleanClause(must56, Occur.SHOULD))
                    .add(new BooleanClause(must78, Occur.SHOULD))
                    .build();

            topDocs = indexSearcher.search(booleanQuery, 20);
        }

                break;
            default:
                break;
            }

            if (topDocs != null && topDocs.scoreDocs.length > 0) {

                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

                    Document document = indexReader.document(scoreDoc.doc);

//                    IndexableField storeOnlyField = document.getField("StoreOnly");
//                    if (storeOnlyField != null) {
//                        System.out.println(storeOnlyField.stringValue());
//                    } else {
//                        System.out.println("null");
//                    }

//                    IndexableField indexOnlyField = document.getField("IndexOnly");
//                    if (indexOnlyField != null) {
//                        System.out.println(indexOnlyField.stringValue());
//                    } else {
//                        System.out.println("null");
//                    }

                    IndexableField storeIndexField = document.getField("StoreIndex");
                    if (storeIndexField != null) {
                        System.out.println(storeIndexField.stringValue());
                    } else {
                        System.out.println("null");
                    }

                    System.out.println("");

                }
            }

            indexReader.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
