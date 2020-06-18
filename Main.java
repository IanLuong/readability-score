import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays; 
import java.util.Scanner;
 
public class Main {
    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
    
    public static double processARIScore(double words, double sentences, double chars) {
        return 4.71 * (chars / words) + 0.5 * (words / sentences) - 21.43;
    }
    
    public static double processFKScore(double words, double sentences, double syllables) {
        return 0.39 * (words/sentences) + 11.8 * (syllables/words) - 15.59;
    }
    
    public static double processSMOGScore(double sentences, double polysyllables) {
        return 1.043 * Math.sqrt(polysyllables * 30.0 / sentences + 3.1291);
    }
    
    public static double processCLScore(double words, double sentences, double chars) {
        double l = chars / words * 100.0;
        double s = sentences / words * 100.0;
        return (0.0588 * l) - (0.296 * s) - 15.8;
        
    }
    
    public static double countSyllables(String input) {
        String[] words = input.split("\\s+");
        double totalSyllables = 0;
        double syllablesInWord = 0;
        
        for (String word: words) {
            word = word.replaceAll("\\s", "");
            
            if (word.endsWith("e")) {
                word = word.replaceFirst("e", "");
            }
            word = word.replaceAll("[aeiouyAEIOUY]{2}", "a");
            
            for (int i = 0; i < word.length(); i++) {
                if (Character.toString(word.charAt(i)).matches("[aeiouyAEIOUY]")) {
                    syllablesInWord++;
                }
            }
            
            if (syllablesInWord == 0) {
                totalSyllables += 1;
            } else {
                totalSyllables += syllablesInWord;
            }
            
            syllablesInWord = 0;
        }
        
        return totalSyllables;
    }
    
    public static double countPolysyllables(String input) {
        String[] words = input.split("\\s+");
        double totalPolysyllables = 0;
        double syllablesInWord = 0;
        
        for (String word: words) {
            word = word.replaceAll("\\s", "");
            word = word.replaceAll("e[\\s!?.,]", "");
            word = word.replaceAll("[aeiouyAEIOUY]{2}", "a");
            
            for (int i = 0; i < word.length(); i++) {
                if (Character.toString(word.charAt(i)).matches("[aeiouyAEIOUY]")) {
                    syllablesInWord++;
                }
            }
            
            if (syllablesInWord > 2) {
                totalPolysyllables++;
            }
            syllablesInWord = 0;
        }
        return totalPolysyllables;        
    }
    
    public static void main(String[] args) {
        try {
            String inputFile = readFileAsString(args[0]);
            
            double noOfSentences = inputFile.split("[!?.]\\s*").length;
            
            String[] charArr = inputFile.split("\\s+");
            double noOfChars = 0;
            for (int i = 0; i < charArr.length; i++) {
                noOfChars += charArr[i].length();
            }
            
            double noOfWords = inputFile.split("\\s+").length;
            double noOfSyllables = countSyllables(inputFile);
            double noOfPolysyllables = countPolysyllables(inputFile);
                    
            System.out.println("Words: " + noOfWords);
            System.out.println("Sentences: " + noOfSentences);
            System.out.println("Characters: " + noOfChars);
            System.out.println("Syllables: " + noOfSyllables);
            System.out.println("Polysyllables: " + noOfPolysyllables);
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
            String selectedScore = scanner.next(); 
            
            double scoreARI = processARIScore(noOfWords, noOfSentences, noOfChars);
            double scoreFK = processFKScore(noOfWords, noOfSentences, noOfSyllables);
            double scoreSMOG = processSMOGScore(noOfSentences, noOfPolysyllables);     
            double scoreCL = processCLScore(noOfWords, noOfSentences, noOfChars);  
            
            switch(selectedScore) {
                case "ARI":
                    System.out.printf("Automated Readability Index: %f (about %d year olds).\n", scoreARI, ReadingLevel.findByScore(scoreARI));
                    System.out.printf("\nThis text should be understood in average by %d year olds.", ReadingLevel.findByScore(scoreARI));
                    break;
                case "FK":
                    System.out.printf("Flesch–Kincaid readability tests: %f (about %d year olds).\n", scoreFK, ReadingLevel.findByScore(scoreFK));
                    System.out.printf("\nThis text should be understood in average by %d year olds.", ReadingLevel.findByScore(scoreFK));
                    break;
                case "SMOG":
                    System.out.printf("Simple Measure of Gobbledygook: %f (about %d year olds).\n", scoreSMOG, ReadingLevel.findByScore(scoreSMOG));
                    System.out.printf("\nThis text should be understood in average by %d year olds.", ReadingLevel.findByScore(scoreSMOG));
                    break;
                case "CL":
                    System.out.printf("Coleman–Liau index: %f (about %d year olds).\n", scoreCL, ReadingLevel.findByScore(scoreCL));
                    System.out.printf("\nThis text should be understood in average by %d year olds.", ReadingLevel.findByScore(scoreCL));
                    break;
                case "all":
                    System.out.printf("Automated Readability Index: %f (about %d year olds).\n", scoreARI, ReadingLevel.findByScore(scoreARI));
                    System.out.printf("Flesch–Kincaid readability tests: %f (about %d year olds).\n", scoreFK, ReadingLevel.findByScore(scoreFK));
                    System.out.printf("Simple Measure of Gobbledygook: %f (about %d year olds).\n", scoreSMOG, ReadingLevel.findByScore(scoreSMOG));
                    System.out.printf("Coleman–Liau index: %f (about %d year olds).\n", scoreCL, ReadingLevel.findByScore(scoreCL));
                    System.out.printf("\nThis text should be understood in average by %f year olds.", 
                    (ReadingLevel.findByScore(scoreARI) + ReadingLevel.findByScore(scoreFK) +
                    ReadingLevel.findByScore(scoreSMOG) + ReadingLevel.findByScore(scoreCL)) / 4.0);
                    break;
            }
        }
        catch (IOException e) {
            System.out.println("No file found...");
        }
    }
}

enum ReadingLevel {
    PROFESSOR(13, 25), COLLEGE(12, 24), TWELTH(11,  18),
    ELEVENTH(10, 17), TENTH(9, 16), NINTH(8, 15), 
    EIGHTH(7, 14), SEVENTH(6, 13), SIXTH(5, 12), 
    FIFTH(4, 11), FOURTH(3, 10), THIRD(2, 9),
    SECOND(1, 7), KINDERGARTEN(0, 6);
    
    int score;
    int age;
    
    ReadingLevel(int score, int age) {
        this.score = score;
        this.age = age;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getAge() {
        return age;
    }
    
    public static int findByScore(double inputScore) {
        for (ReadingLevel value: values()) {
            if (inputScore > value.score) {
                return value.age;
            }
        }
        return 0;
    }
}