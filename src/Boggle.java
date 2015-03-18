
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;

/**
 * An interface of Positions
 *
 * @author David Romero PID: 3624439
 */
interface Position
{
    int getRow();
    int getCol();
    List<Position> getNeighbors();
}

/**
 * Creates a boggle puzzle and plays a game of boggle. It achieves this by 
 * reading and searching a dictionary and then attempts to find words in on
 * the board
 * 
 * @author David Romero PID: 3624439
 */
public class Boggle
{
    private int numOfRows = 0;      //The number of rows in the boggle puzzle
    private int numOfCols = 0;      //The number of columns 
    char[][] puzzle;        //The board
    String[] dictArr;       //The array that holds the dictionary 

    private class BogPosition implements Position
    {
        private int row = 0;        //Rows
        private int col = 0;        //Columns

        /**
         * Creates a position column that has a row and a column
         * 
         * @param r The row
         * @param c The column
         */
        public BogPosition(int r, int c)
        {
            row = r;
            col = c;
        }
        /**
         * Based off position, it checks for positions next to current one a 
         * puts its "neighbors" in an array list
         * 
         * @return Array containing adjacent positions
         */
        public List<Position> getNeighbors()
        {
            //Checking if you're at the edge of the puzzle board, else
            //there is a row before.
            int lowRow = (row == 0) ? 0 : (row - 1);
            //Checking if you're at the edge of the board, else there is a 
            //column before
            int lowCol = (col == 0) ? 0 : (col - 1);
            //Checks if you're at the end of the row, if so then it's the last 
            //element in the array, else it gets the next element
            int highRow = (row == numOfRows - 1) ? row : (row + 1);
            //Checks if you're at the end of the column, if so then it's the 
            //last element in the array of arrays, else it gets the next element
            int highCol = (col == numOfCols - 1) ? col : (col + 1);

            List<Position> result = new ArrayList<>();

            for (int r = lowRow; r <= highRow; ++r)
            {
                for (int c = lowCol; c <= highCol; ++c)
                {
                    if (r != row || c != col)
                    {
                        result.add(newPosition(r, c));
                    }
                }
            }

            return result;
        }
        /**
         * Returns the row of the position
         * 
         * @return The row
         */
        public int getRow()
        {
            return row;
        }
        /**
         * Returns the column of the position
         * 
         * @return The column
         */
        public int getCol()
        {
            return col;
        }
        /**
         * Sets a new position
         * 
         * @param newR The new row
         * @param newC The new column
         * @return The new position
         */
        public Position newPosition(int newR, int newC)
        {
            return new BogPosition(newR, newC);
        }
        /**
         * Checks the equality of the position
         * 
         * @param other The position compared against
         * @return Whether the positions are equal
         */
        @Override
        public boolean equals(Object other)
        {
            //Checking if the object passed is in fact a position
            if (!(other instanceof BogPosition))
            {
                return false;
            }

            BogPosition temp = (BogPosition) other;

            return row == temp.getRow() && col == temp.getCol();
        }

        /**
         * Prints the position
         * 
         * @return String of the position 
         */
        @Override
        public String toString()
        {
            return "(" + row + "," + col + ")";
        }
    }

    /**
     * Starts the game of boggle
     * 
     * @param input The puzzle file that is to be used for the game
     */
    public Boggle(File input)
    {
        buildPuzzle(input);
        buildDictionary();
    }
    /**
     * Builds the puzzle board based off the specified file
     * 
     * @param input File with puzzle
     */
    private void buildPuzzle(File input)
    {
        try
        {
            String previous = "";       //Keeps tracks of the previous 

            ArrayList<String> bogLis = new ArrayList<>();

            Scanner scan = new Scanner(input);

            while (scan.hasNext())
            {
                String next = scan.nextLine();
                //Checking if the puzzle is a rectangle
                if (!bogLis.isEmpty() && (previous.length() != next.length()))
                {
                    System.err.println("Please make sure the "
                            + "puzzle is rectangular");
                    System.exit(0);
                }
                bogLis.add(next);
                previous = next;
            }

            numOfRows = bogLis.size();          //Keeps track of rows in puzzle
            numOfCols = bogLis.get(0).length(); //Keeps track of columns in puz.

            puzzle = new char[numOfRows][numOfCols];

            //Populating the board array
            for (int r = 0; r < numOfRows; ++r)
            {
                String temp = bogLis.get(r);
                puzzle[r] = temp.toCharArray();
            }
        }
        catch (FileNotFoundException e)
        {
            System.err.println("The requested puzzle file cannot be found."
                    + " Please make sure the file is in the root and "
                    + "try again.");
            System.exit(0);
        }
    }

    /**
     * Reads the dictionary file and puts it in the array 
     * 
     */
    private void buildDictionary()
    {
        ArrayList<String> dictList = new ArrayList<>();

        try
        {
            Scanner scan = new Scanner(new File("dict.txt"));

            while (scan.hasNext())
            {
                String entry = scan.nextLine();
                //Skipping any words of length 2
                if (entry.length() > 2)
                {
                    dictList.add(entry);
                }
            }

            dictArr = dictList.toArray(new String[dictList.size()]);

            Arrays.sort(dictArr);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Cannot find dict.txt. Please make sure "
                    + "dict.txt is at the root and try again.");
            System.exit(0);
        }
    }
    /**
     * Prints the boggle puzzle
     * 
     * @return The boggle puzzle board
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < numOfRows; ++i)
        {
            for (int j = 0; j < numOfCols; ++j)
            {
                sb.append(puzzle[i][j]);
            }
            sb.append("\n");
        }

        return new String(sb);
    }

    /**
     * Prints the results of the boggle game
     * 
     * @param m A map containing the found words and its positions 
     */
    public void printResult(Map<String, List<Position>> m)
    {
        //Array containing the points, length of words up to 9 correspond with
        //index positions
        int[] points = new int[] {0, 0, 0, 1, 2, 3, 4, 6, 10, 15};

        int total       = 0;        //Total score
        int countThree  = 0;        //Number of three letter words
        int countFour   = 0;        //Number of four letter words
        int countFive   = 0;        //Number of five letter words
        int countSix    = 0;        //Number of six letter words
        int countSeven  = 0;        //Number of seven letter words
        int upperPoints = 0;        //Determined by whether it is eight, 
                                    //or nine or more

        for (Map.Entry<String, List<Position>> e : m.entrySet())
        {
            String word = e.getKey();                   //Word in the results
            List<Position> positions = e.getValue();    //Word's position

            total += (word.length() > 8) ? (points[9]) : (points[word.length()]);
            
            if (m.size() > 199)
            {
                //Accumulating corresponding count based off word length
                countSeven += (word.length() == 7) ? (1) : 0;
                countSix   += (word.length() == 6) ? (1) : 0;
                countFive  += (word.length() == 5) ? (1) : 0;
                countFour  += (word.length() == 4) ? (1) : 0;
                countThree += (word.length() == 3) ? (1) : 0;

                //Only printing words of length 8 and greater when there are 
                //200 or more words found
                if (word.length() > 7)
                {
                    //Determining value of upperPoints
                    upperPoints = (word.length() == 8) ? points[8] : 15;
                    System.out.println(word + "\t\tPoints:" + upperPoints 
                                       + "\t\t" + positions);
                }
            }
            else    //Do this if less than 200 words found
            {
                //If a words length exceeds the length of the array, assign 
                //upperPoints accordingly
                if (word.length() > 7)
                {
                    upperPoints = (word.length() == 8) ? points[8] : 15;
                    System.out.println(word + "\t\tPoints:" + upperPoints +
                            "\t\t" + positions);
                }
                else
                {
                    System.out.println(word + "\t\tPoints:" + 
                            points[word.length()] + "\t\t" + positions);
                }
            }
        }
        //If greater than or equal to 200 words found, only printing 
        //characters greater than 7 and printing how much the other
        //acceptable values add to the total
        if (m.size() > 199)
        {
            //The points that add towards the total
            int threeTotal = points[3] * countThree;
            int fourTotal  = points[4] * countFour;
            int fiveTotal  = points[5] * countFive;
            int sixTotal   = points[6] * countSix;
            int sevenTotal = points[7] * countSeven;

            System.out.println("\nThere are " + countThree + " words of length "
                    + "3. Account for " + threeTotal + " of total score");
            System.out.println("There are " + countFour + " words of length 4."
                    + " Account for " + fourTotal + " of total score");
            System.out.println("There are " + countFive + " words of length 5."
                    + " Account for " + fiveTotal + " of total score");
            System.out.println("There are " + countSix + " words of length 6."
                    + " Account for " + sixTotal + " of total score");
            System.out.println("There are " + countSeven + " words of length 7."
                    + " Account for " + sevenTotal + " of total score");
        }

        System.out.println("\nWords: " + m.size() + "\t\tTotal " + total);
    }

    /**
     * Driver routine to solve the Boggle game.
     *
     * @return a Map containing the strings as keys, and the positions used to
     * form the string (as a List) as values
     */
    public Map<String, List<Position>> solve()
    {
        Map<String, List<Position>> results = new TreeMap<>();
        List<Position> path = new ArrayList<>();

        for (int r = 0; r < numOfRows; r++)
        {
            for (int c = 0; c < numOfCols; c++)
            {
                solve(new BogPosition(r, c), "", path, results);
            }
        }

        return results;
    }

    /**
     * Hidden recursive routine.
     *
     * @param thisPos the current position
     * @param charSequence the characters in the potential matching string
     * thus far
     * @param path the List of positions used to form the potential matching
     * string thus far
     * @param results the Map that contains the strings that have been found as
     * keys and the positions used to form the string (as a List) as values.
     */
    private void solve(Position thisPos, String charSequence,
            List<Position> path, Map<String, List<Position>> results)
    {
        //Base case. If the position has already been visited, end
        if (path.contains(thisPos))
        {
            return;
        }
        
        //Else, add the character at this position to the word we are building
        charSequence += puzzle[thisPos.getRow()][thisPos.getCol()];

        //Find if the current word we are building is in the dictionary
        int index = dictCheck(charSequence);

        //If the dictionary check finds that the words index is the length of 
        //the array, exit. That is to say that the binarySearch determines that
        //the word should be located past the last entry of the dictionary
        //(e.g. A-Z and binarySearch finds 'ZZ')
        if (index == dictArr.length)
        {
            return;
        }
        
        path.add(thisPos);
        
        //Else, if the word found has the current word we're building as a
        //prefix
        if (dictArr[index].startsWith(charSequence))
        {
            //If the prefix itself is a word, add it
            if (dictArr[index].equals(charSequence))
            {
                results.put(charSequence, new ArrayList<>(path));
            }
            //Else search this positions neighbors and check if the make words
            for (Position p : thisPos.getNeighbors())
            {
                solve(p, charSequence, path, results);
            }
        }
        
        path.remove(thisPos);
    }
    /**
     * Searches the dictionary for the inquired word
     * 
     * @param inq The inquired word
     * @return Index of the word if it's found or if not, where it should be
     * in the array.
     */
    public int dictCheck(String inq)
    {
        int searchResult = Arrays.binarySearch(dictArr, inq);

        if (searchResult > -1)
        {
            return searchResult;
        }
        //Get the index of the approximation
        return (-searchResult - 1);
    }

    public static void main(String[] args)
    {
        try
        {
            Boggle bog = new Boggle(new File(args[0]));
            System.out.println(bog);
            bog.printResult(bog.solve());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.println("No file passed as an argument.");
        }
    }
}
