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

            {
                TermQuery termQuery = new TermQuery(new Term("StoreOnly", "7"));

                TopDocs topDocs = indexSearcher.search(termQuery, 5);

                if (topDocs.scoreDocs.length > 0) {

                    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

                        Document document = indexReader.document(scoreDoc.doc);

                        IndexableField storeOnlyField = document.getField("StoreOnly");
                        if (storeOnlyField != null) {
                            System.out.println(storeOnlyField.stringValue());
                        } else {
                            System.out.println("null");
                        }

                        IndexableField indexOnlyField = document.getField("IndexOnly");
                        if (indexOnlyField != null) {
                            System.out.println(indexOnlyField.stringValue());
                        } else {
                            System.out.println("null");
                        }

                        IndexableField storeIndexField = document.getField("StoreIndex");
                        if (storeIndexField != null) {
                            System.out.println(storeIndexField.stringValue());
                        } else {
                            System.out.println("null");
                        }

                        System.out.println("");

                    }
                }

                System.out.println("----------------------------------");
            }

            indexReader.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
