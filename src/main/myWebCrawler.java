package main;

import java.io.*;

import java.util.Scanner;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Zi Jun, Xu
 * @since 12/12/2018
 */
public class myWebCrawler {
    /** The folder that stores the data files. */
    private final static String SAVE_PATH = "src\\data";

    /**
     * Check if the folder exist, if not, create the folder;
     * if there is any file under the folder, delete it.
     * @param needCheckPath
     * @throws IOException
     */
    public static void CheckFolder(String needCheckPath) throws IOException {
        System.out.println(String.format("Start checking folder %s ...", needCheckPath));

        File checkSavePath = new File(needCheckPath);
        if(!checkSavePath.exists()) {
            System.err.println(String.format("Warning: Folder %s is NOT found.", needCheckPath));
//            System.exit(1);
            System.out.println(String.format("Start creating folder %s ...", needCheckPath));
            File parentFile = new File(checkSavePath.getParent());
            parentFile.mkdirs(); // Create the folder.
            System.out.println(String.format("Finish creating folder %s ...", needCheckPath));
        } else {
            File[] childList = checkSavePath.listFiles();
            for (File fileChild: childList) { // Traverse the files.
                if (!fileChild.delete()) { // If any file is NOT deleted correctly, exit the program.
                    System.err.println(String.format("Error: %s is NOT deleted successfully.",
                            fileChild.getName()));
                    System.exit(1);
                }
            }
        }

        System.out.println(String.format("Finish checking folder %s ...\n", needCheckPath));
    }

    /**
     * Convert the data and save it.
     * @param schoolName
     * @param fieldName
     * @param courseName
     * @param introduction
     * @throws IOException
     */
    private static void DataWriter(Element schoolName, Elements fieldName, Elements courseName,
                                   Elements introduction) throws IOException {
        CheckFolder(SAVE_PATH);

        /** Convert fieldName to ArrayList type, since ArrayList type is easier to position. */
        ArrayList<String> fieldNameList = new ArrayList<>();
        for(Element f: fieldName) {
            fieldNameList.add(f.text());
        }

        ArrayList<String> courseNameList = new ArrayList<>();
        for(Element c: courseName) {
            courseNameList.add(c.text());
        }

        ArrayList<String> introductionList = new ArrayList<>();
        for(Element i: introduction) {
            introductionList.add(i.text());
        }

        /** Write the data to the files. */
        int fileCount = courseName.size();
        int fieldPointer = 0; // Many courses are in the same field,
                              // so the variable is the pointer to indicate
                              // which field the course is.
        // Save the previous course type corresponding to the first two characters of courseName.
        String preFieldType = courseNameList.get(0).substring(0, 2);

        System.out.println("Start writing files ...");

        for (int i = 0; i < fileCount; i++) {
            // The filename is iterative.
            String fileName = SAVE_PATH + "\\" + String.valueOf(i) + ".txt";
            File file = new File(fileName);
            if (!file.exists()) { // If the file doesn't exist, create it.
//                System.out.println(String.format("Start creating file %s ...", fileName));
                file.createNewFile();
//                System.out.println(String.format("Finish creating file %s ...\n", fileName));
            }

            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            /** Find the corresponding field. */
            String nowFieldType = courseNameList.get(i).substring(0, 2);
            if (!preFieldType.equals(nowFieldType)) {
                fieldPointer++;
                preFieldType = nowFieldType;
            }

            bufferedWriter.write(String.format("%s\r\n", schoolName.text()));
            bufferedWriter.write(String.format("%s\r\n", fieldNameList.get(fieldPointer)));
            bufferedWriter.write(String.format("%s\r\n", courseNameList.get(i)));
            bufferedWriter.write(String.format("%s\r\n", introductionList.get(i)));

            bufferedWriter.close();
        }

        System.out.println("Finish writing files ...\n");
    }

    /**
     * User interface.
     * @return The input string.
     * @throws IOException
     */
    private static String UserInterface() throws IOException {
        System.out.println("******************************");
        System.out.println("*     @author: Zi Jun, Xu    *");
        System.out.println("******************************\n");
        System.out.println("Welcome to CMU course website.\n");
        System.out.println("Please input what you want to search: ");
        System.out.println("Notice: If you want to quit, please don't input anything.");
        System.out.print  ("> ");

        /** Input from user (terminal). */
        Scanner input = new Scanner(System.in);
        String inputString = input.nextLine();
        input.close();

        if (inputString.length() == 0) {
            System.exit(0);
        }

        return inputString;
    }

    /**
     * Main function.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        /** Connect to CMU course website. */
        String url = "http://coursecatalog.web.cmu.edu/schoolofcomputerscience/courses/";
        Document doc = Jsoup.connect(url).get();

        /** Collect information. */
        Element  schoolName = doc.select("title").first(); // School name.
        Elements fieldName = doc.select("h3"); // Course field.
        Elements courseName = doc.select("dt.keepwithnext"); // Course name.
        Elements introduction = doc.select("dd"); // Introduction.

        /** Convert collected information and save to files. */
        DataWriter(schoolName, fieldName, courseName, introduction);

        /** User interface. */
        String whatToSearch = UserInterface();

        /** Create index and search the question. */
        Index index = new Index(whatToSearch);
        index.SearchIndex();
    }
}
