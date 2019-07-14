import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class ComparativeAdvantage {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Note: Does not currently support time based quantity tables");

        // Asks user for the table they want analyzed then opens that table
        Scanner prompt = new Scanner(System.in);
        System.out.print("What file is the table saved under?");
        String response = prompt.next();
        File table = new File(response);

        // Extracts necessary data from table
        scrape(table);
    }

    public static void scrape(File f) throws FileNotFoundException {

        // Categorizes analyzed data
        String[] products = new String[2];
        String[] countryX = new String[3];
        String[] countryY = new String[3];

        // int total = 8;
        int i = 0;

        // Analyzes each aspect of the table row by row
        Scanner lineReader = new Scanner(f);

        // Specifies the characters for lines that shouldn't be analyzed
        String ignoredChars = "+-";

        while(lineReader.hasNextLine()) {
            String line = lineReader.nextLine();

            // Tells computer to skip over the analysis of line if it has no data
            Boolean shouldIgnore = line.indexOf(ignoredChars) >= 0;
            if (shouldIgnore) {
                continue;
            }

            // Iterates through data in a line one by one
            Scanner dataReader = new Scanner(line);

            while(dataReader.hasNext()) {
                String data = dataReader.next();

                // Ignore table design when analyzing data
                if (data.equals("|")) {
                    continue;
                }

                // Put data in its respective array
                if (i < 2) {
                    products[i] = data;
                } else if (i < 5) {
                    int det = i - 2;
                    countryX[det] = data;
                } else if (i < 8) {
                    int det = i - 5;
                    countryY[det] = data;
                }
                i++;
            }
        }

        analyze(products, countryX, countryY, f);
    }

    public static void analyze(String[] products, String[] countryX, String[] countryY, File f)
            throws FileNotFoundException {

        // Numerical data collected through String array must convert to int
        int xGoodA = Integer.parseInt(countryX[1]);
        int xGoodB = Integer.parseInt(countryX[2]);
        int yGoodA = Integer.parseInt(countryY[1]);
        int yGoodB = Integer.parseInt(countryY[2]);

        // Algebraic Calculations
        double tL = Math.round(((double)xGoodB  / xGoodA) * 100.0) / 100.0;
        double tR = Math.round(((double)xGoodA  / xGoodB) * 100.0) / 100.0;
        double bL = Math.round(((double)yGoodB  / yGoodA) * 100.0) / 100.0;
        double bR = Math.round(((double)yGoodA  / yGoodB) * 100.0) / 100.0;

        // Need to find maximum length out of the group of product labels and quantities
        // Also need to show work on how comparative advantage was found
        String topLeft = countryX[1] + "(1 " + products[0]  + " = " +
                tL + products[1] + ")";
        String topRight = products[1] + "(1 " + products[1]  + " = " +
                tR + products[1] + ")";
        String bottomLeft = countryY[1] + "(1 " + products[0]  + " = " +
                bL + products[1] + ")";
        String bottomRight = countryY[2] + "(1 " + products[1]  + " = " +
                bR + products[1] + ")";

        // Gruesome-looking math to figure out maximum of each designated dataset
        int maxNum = Math.max(topLeft.length(), Math.max(topRight.length(),
                Math.max(bottomLeft.length(), bottomRight.length())));
        int productMax = Math.max(maxNum, Math.max(products[0].length(), products[1].length()));
        int countryMax = Math.max(countryX[0].length(), countryY[0].length());

        // Overwrite original file with worked solution
        PrintStream solution = new PrintStream(f);

        // Creation of updated table
        makeBlank(countryMax, solution);
        shortStarPattern(productMax, solution);
        makeBlank(countryMax, solution);
        productLabelPlacement(products[0], products[1], productMax, solution);
        starDashRepeatPattern(productMax, countryMax, solution);
        countryLabelPlacement(countryX[0], countryMax, solution);
        productLabelPlacement(topLeft, topRight, productMax, solution);
        starDashRepeatPattern(productMax, countryMax, solution);
        countryLabelPlacement(countryY[0], countryMax, solution);
        productLabelPlacement(bottomLeft, bottomRight, productMax, solution);
        starDashRepeatPattern(productMax, countryMax, solution);

        // Absolute Advantage usually stated before Comparative Advantage
        Boolean comparison1 = Integer.parseInt(countryX[1]) > Integer.parseInt(countryY[1]);
        Boolean comparison2 = Integer.parseInt(countryX[2]) > Integer.parseInt(countryY[2]);
        Boolean comparison3 = Integer.parseInt(countryX[1]) < Integer.parseInt(countryY[1]);
        Boolean comparison4 = Integer.parseInt(countryX[2]) < Integer.parseInt(countryY[2]);

        // This block of code determines absolute advantage.
        if (comparison1 && comparison2) {
            solution.println(countryX[0] + " has an absolute advantage in " + products[0]
            + " and " + products[1]);
        } else if (comparison1) {
            solution.println(countryX[0] + " has an absolute advantage in " + products[0]);
        } else if (comparison2) {
            solution.println(countryX[0] + " has an absolute advantage in " + products[1]);
        } else if (comparison3 && comparison4) {
            solution.println(countryY[0] + " has an absolute advantage in " + products[0]
                    + " and " + products[1]);
        } else if (comparison3) {
            solution.println(countryY[0] + " has an absolute advantage in " + products[0]);
        } else if (comparison4) {
            solution.println(countryY[0] + " has an absolute advantage in " + products[1]);
        } else {
            solution.println("Neither country has an absolute advantage " +
                    "relative to each other");
        }

        // Use Jacob Clifford's "Quick and Dirty" Method to find optimal combo
        int optionOne = xGoodA * yGoodB;
        int optionTwo = xGoodB * yGoodA;
        Boolean firstIsOptimal = optionOne > optionTwo;
        Boolean secondIsOptimal = optionTwo > optionOne;

        if (firstIsOptimal) {
            solution.println(countryX[0] + " has a comparative advantage in " + products[0] + " while " + countryY[0]
            + " has a comparative advantage in " + products[1]);
        } else if (secondIsOptimal){
            solution.println(countryX[0] + " has a comparative advantage in " + products[1] + " while " + countryY[0]
                    + " has a comparative advantage in " + products[0]);
        } else {
            solution.println("Neither country has a comparative advantage relative to" +
                    " one another. However, each country should " +
                    " still specialize in producing a different good");
        }

        // Final Part of the program: Terms of Trade
        Boolean inTermsOfGoodB = xGoodA < xGoodB || xGoodA == xGoodB;
        // Boolean inTermsOfGoodA = xGoodA < xGoodB;

        // Needed to calculate optimal amount
        double bound1;
        double bound2;
        double potentialDeal;


        // Prints one possible trade deal that benefits both countries
        if (inTermsOfGoodB) {
            bound1 = xGoodB / xGoodA;
            bound2 = yGoodB / yGoodA;
            potentialDeal = (((double) (bound1)) + (bound2)) / 2;
            solution.println("One possible term of trade that benefits " +
                    "both countries is trading 1 " + products[0] + " for " +
            potentialDeal + " " + products[1] + "(s)");
        } else {
            bound1 = xGoodA / xGoodB;
            bound2 = yGoodA / yGoodB;
            potentialDeal = (((double) (bound1)) + (bound2)) / 2;

            solution.println("One possible term of trade that benefits " +
                    "both countries is trading 1 " + products[1] + " for " +
                    potentialDeal + " " + products[0] + "(s)");
        }

        System.out.print("Solution has been updated");
    }

    public static void makeBlank(int countryMax, PrintStream output) {
        for (int i = 1; i <= countryMax + 3; i++) {
            output.print(" ");
        }
    }

    public static void starDashRepeatPattern(int productMax, int countryMax, PrintStream output) {
        output.print("+");
        // Decides the amount of dashes to be produced
        // depending on max length of country label
        for (int i = 1; i <= countryMax + 2; i++) {
            output.print("-");
        }

        shortStarPattern(productMax, output);

        /*
        // Prints out two of "+---" structures
        for (int i = 1; i <= 2; i++) {
            output.print("+");

            // Prints out amount of dashes to be produced
            // based on max length of string out of
            // all product labels and numbers
            for (int j = 1; j <= productMax + 2; i++) {
                output.print("-");
            }
        }
        output.print("+");

        // Experimental. Delete if it screws up table design
        output.println();

        */
    }

    public static void shortStarPattern(int productMax, PrintStream output) {
        // Prints out two of "+---" structures
        for (int i = 1; i <= 2; i++) {
            output.print("+");

            // Prints out amount of dashes to be produced
            // based on max length of string out of
            // all product labels and numbers
            for (int j = 1; j <= productMax + 2; j++) {
                output.print("-");
            }
        }
        output.print("+");

        // Experimental. Delete if it screws up table design
        output.println();
    }

    public static void productLabelPlacement(String itemA, String itemB, int productMax, PrintStream output) {
        String[] item = {itemA, itemB};


        // Prints out "| <itemA> | <itemB> " pattern
        for (int i = 0; i < item.length; i++) {
            output.print("| ");
            output.print(item[i]);

            for (int j = 1; j <= (productMax - item[i].length()) + 1; j++) {
                output.print(" ");
            }
        }
        // Completes previous pattern
        output.print("|");

        // Experimental. Delete if it screws up table design
        output.println();
    }

    public static void countryLabelPlacement(String country, int countryMax, PrintStream output) {
        output.print("| ");
        output.print(country);

        for (int i = 1; i <= (countryMax - country.length()) + 1; i++) {
            output.print(" ");
        }
    }

    /*
    public static void countryStarDashPattern(int countryMax, PrintStream output) {
        output.print("+");

        for (int i = 1; i <= countryMax + 2; i++) {
            output.print("-");
        }
    }
    */
}
