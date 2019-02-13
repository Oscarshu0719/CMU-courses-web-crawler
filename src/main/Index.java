package main;

import java.io.*;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static main.myWebCrawler.CheckFolder;

/**
 * @author Zi Jun, Xu
 * @since 12/12/2018
 */
public class Index {
    /** The folder that stores the index files. */
    private final static String INDEX_FILE_PATH = "src\\index";

    /** The folder that stores the data files. */
    private final static String SRC_FILE_PATH = "src\\data";

    /** Maximal number of output results. */
    private final static int    MAX_QUERY_NUM = 10;

    /** Question to search. */
    private static       String WHAT_TO_SEARCH;

    /**
     * Constructor.
     * @param inputString
     * @throws IOException
     */
    Index(String inputString) throws IOException {
        File fileI = new File(INDEX_FILE_PATH);
        if(!fileI.exists()) { // If the folder doesn't exist, create it.
            System.err.println(String.format("%s is NOT found.", INDEX_FILE_PATH));
//            System.exit(1);
            System.out.println(String.format("Start creating %s ...", INDEX_FILE_PATH));
            File parentFile = new File(fileI.getParent());
            parentFile.mkdirs(); // Create teh folder.
            System.out.println(String.format("Finish creating %s ...", INDEX_FILE_PATH));
        }

        WHAT_TO_SEARCH = inputString;

        CheckFolder(INDEX_FILE_PATH);

        CreateIndex();
    }

    /**
     * Convert File type to String type.
     * @param inputFile
     * @return
     * @throws IOException
     */
    public static String readFileToString(File inputFile) throws IOException {
        InputStream inputStream = new FileInputStream(inputFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();

        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }

        String fileToString = stringBuilder.toString();

        return fileToString;
    }

//    private static String ReadAppointedLine(String path, int lineNumber) throws IOException {
//        // RandomAccessFile r = new RandomAccessFile(fileName, "r");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
//        String line = reader.readLine();
//        String appointedLine = line;
//
//        int num = 0;
//        while (line != null) {
//            if (lineNumber == ++num) {
//                appointedLine = line;
//            }
//            line = reader.readLine();
//        }
//
//        reader.close();
//
//        return appointedLine;
//    }

    /**
     * Create index.
     * @throws IOException
     */
    private void CreateIndex() throws IOException {
        System.out.println("Start creating index ...");
        long startTime = System.currentTimeMillis(); // The start time.

        Directory dir = FSDirectory.open(Paths.get(INDEX_FILE_PATH));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(dir, iwConfig);

        /** Traverse the data folder. */
        File file = new File(SRC_FILE_PATH);
        File[] fileList = file.listFiles();
        for (File f: fileList) {
            Document doc = new Document();

            /** Create new field whose name is filename, which is used to store the filename. */
            String fileName = f.getName();
            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);

//            String fileSize = String.valueOf(f.length());
//            Field fileSizeField = new TextField("fileSize", fileSize, Field.Store.YES);
//
//            String filePath = f.getPath();
//            Field filePathField = new StoredField("filePath", filePath);
//
//            String schoolName = ReadAppointedLine(fileName, 1);
//            Field schoolNameField = new TextField("schoolName", schoolName, Field.Store.YES);
//
//            String fieldName = ReadAppointedLine(fileName, 2);
//            Field fieldNameField = new TextField("fieldName", fieldName, Field.Store.YES);
//
//            String courseName = ReadAppointedLine(fileName, 3);
//            Field courseNameField = new TextField("courseName", courseName, Field.Store.YES);
//
//            String introduction = ReadAppointedLine(fileName, 4);
//            Field introductionField = new TextField("introduction", introduction, Field.Store.YES);

            /** Create new field whose name is all, which is used to store the whole file texts. */
            String all = readFileToString(f);
            Field allField = new TextField("all", all, Field.Store.YES);

//            doc.add(fileSizeField);
//            doc.add(filePathField);
//            doc.add(schoolNameField);
//            doc.add(fieldNameField);
//            doc.add(courseNameField);
//            doc.add(introductionField);

            /** Add fields to Document. */
            doc.add(fileNameField);
            doc.add(allField);

            /** Add the new document. */
            indexWriter.addDocument(doc);
        }

        indexWriter.close();

        long stopTime = System.currentTimeMillis(); // The stop time.
        System.out.println("Finish creating index ...\n");

        // The duration of creating index.
        System.out.println(String.format("Duration of creating index: %s ms.\n",
                String.valueOf((double)((stopTime - startTime) / 1000))));
    }

    /**
     * Search the question in the index.
     * @throws IOException
     */
    public void SearchIndex() throws IOException {
        System.out.println("Start searching index ...");
        long startTime = System.currentTimeMillis(); // The start time.

        Directory dir = FSDirectory.open(Paths.get(INDEX_FILE_PATH));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

//        Query fileNameQuery = new TermQuery(new Term("fileName", WHAT_TO_SEARCH));
//        TopDocs fileNameTopDocs = indexSearcher.search(fileNameQuery, MAX_QUERY_NUM);
//
//        Query schoolNameQuery = new TermQuery(new Term("schoolName", WHAT_TO_SEARCH));
//        TopDocs schoolNameTopDocs = indexSearcher.search(schoolNameQuery, MAX_QUERY_NUM);
//
//        Query fieldNameQuery = new TermQuery(new Term("fieldName", WHAT_TO_SEARCH));
//        TopDocs fieldNameTopDocs = indexSearcher.search(fieldNameQuery, MAX_QUERY_NUM);
//
//        Query courseNameQuery = new TermQuery(new Term("courseName", WHAT_TO_SEARCH));
//        TopDocs courseNameTopDocs = indexSearcher.search(courseNameQuery, MAX_QUERY_NUM);
//
//        Query introductionQuery = new TermQuery(new Term("introduction", WHAT_TO_SEARCH));
//        TopDocs introductionTopDocs = indexSearcher.search(introductionQuery, MAX_QUERY_NUM);

        /** Use TermQuery. */
        Query allQuery = new TermQuery(new Term("all", WHAT_TO_SEARCH));
        TopDocs allTopDocs = indexSearcher.search(allQuery, MAX_QUERY_NUM);

        long stopTime = System.currentTimeMillis();
        System.out.println("Finish searching index ...\n");

        // The duration of searching question.
        System.out.println(String.format("Duration of searching index: %s ms.\n",
                String.valueOf((double)((stopTime - startTime) / 1000))));

        System.out.println("----------------------------------------");
        System.out.println(String.format("Search: %s ...", WHAT_TO_SEARCH));
        System.out.println("----------------------------------------\n");

        System.out.println(String.format("Total number of results: %s.", allTopDocs.totalHits));
        System.out.println(String.format("(Show at most %s results.)\n", MAX_QUERY_NUM));

        /** Show the found results. */
        ScoreDoc[] allScoreDocs = allTopDocs.scoreDocs;
        for (ScoreDoc scoreDoc: allScoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);

            System.out.println(doc.get("fileName"));
            System.out.println(doc.get("all"));

            System.out.println();
        }

        indexReader.close();
    }
}
