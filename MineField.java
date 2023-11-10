// Name: Qiaochu Zhang
// USC NetID: qzhang40
// CS 455 PA3
// Fall 2023


import java.util.Random;

/**
   MineField
      Class with locations of mines for a minesweeper game.
      This class is mutable, because we sometimes need to change it once it's created.
      Mutators: populateMineField, resetEmpty
      Includes convenience method to tell the number of mines adjacent to a location.
 */
public class MineField {

   private int numberOfMines;                   // number of mines of minefield
   private final int numberOfRows;                    // number of rows of minefield
   private final int numberOfColumns;                 // number of columns of minefield
   private boolean[][] mineFieldData;            // boolean data of minefield to
                                                // store the positions of mines
   
   
   /**
      Create a minefield with same dimensions as the given array, and populate it with the mines in
      the array such that if mineData[row][col] is true, then hasMine(row,col) will be true and vice
      versa.  numMines() for this minefield will correspond to the number of 'true' values in
      mineData.
      @param mineData  the data for the mines; must have at least one row and one col,
                       and must be rectangular (i.e., every row is the same length)
    */
   public MineField(boolean[][] mineData) {
      numberOfRows = mineData.length;
      numberOfColumns = mineData[0].length;
      mineFieldData = new boolean[numberOfRows][numberOfColumns];
      numberOfMines = 0;
      for(int row1 =0; row1 < numberOfRows; row1 ++){
         for(int col1 = 0; col1 < numberOfColumns; col1 ++){
            mineFieldData[row1][col1] = mineData[row1][col1];
            if(mineData[row1][col1]){
               numberOfMines ++;
            }
         }
      }
   }
   
   
   /**
      Create an empty minefield (i.e. no mines anywhere), that may later have numMines mines (once 
      populateMineField is called on this object).  Until populateMineField is called on such a 
      MineField, numMines() will not correspond to the number of mines currently in the MineField.
      @param numRows  number of rows this minefield will have, must be positive
      @param numCols  number of columns this minefield will have, must be positive
      @param numMines   number of mines this minefield will have,  once we populate it.
      PRE: numRows > 0 and numCols > 0 and 0 <= numMines < (1/3 of total number of field locations). 
    */
   public MineField(int numRows, int numCols, int numMines) {
      numberOfRows = numRows;
      numberOfColumns = numCols;
      numberOfMines = numMines;
      mineFieldData = new boolean[numRows][numCols];
   }
   

   /**
      Removes any current mines on the minefield, and puts numMines() mines in random locations on
      the minefield, ensuring that no mine is placed at (row, col).
      @param row the row of the location to avoid placing a mine
      @param col the column of the location to avoid placing a mine
      PRE: inRange(row, col) and numMines() < (1/3 * numRows() * numCols())
    */
   public void populateMineField(int row, int col) {
      assert inRange(row,col);
      numberOfMines = numMines();
      Random random = new Random();
      mineFieldData = new boolean[numberOfRows][numberOfColumns];
      for(int count =0; count < numberOfMines;){
         int row1 = random.nextInt(numberOfRows);
         int col1 = random.nextInt(numberOfColumns);
         if(!mineFieldData[row1][col1] && (row1 != row) && (col1 != col)){
            mineFieldData[row1][col1] = true;
            count ++;
         }
      }
   }
   
   
   /**
      Reset the minefield to all empty squares.  This does not affect numMines(), numRows() or
      numCols().  Thus, after this call, the actual number of mines in the minefield does not match
      numMines().  
      Note: This is the state a minefield created with the three-arg constructor is in at the 
      beginning of a game.
    */
   public void resetEmpty() {
      for(int row1 =0; row1 < numberOfRows; row1 ++){
         for(int col1 = 0; col1 < numberOfColumns; col1 ++){
            mineFieldData[row1][col1] = false;
         }
      }
   }

   
  /**
     Returns the number of mines adjacent to the specified location (not counting a possible 
     mine at (row, col) itself).
     Diagonals are also considered adjacent, so the return value will be in the range [0,8]
     @param row  row of the location to check
     @param col  column of the location to check
     @return  the number of mines adjacent to the square at (row, col)
     PRE: inRange(row, col)
   */
   public int numAdjacentMines(int row, int col) {
      assert inRange(row,col);
      int adjMinesNum = 0;
      boolean[] adjMine = new boolean[8];
      boolean[][] largeMineField = new boolean[numberOfRows+2][numberOfColumns+2];
      for(int row1=1; row1 <= numberOfRows; row1++){
         if (numberOfColumns >= 0)
            System.arraycopy(mineFieldData[row1 - 1], 0, largeMineField[row1], 1, numberOfColumns);
      }
      adjMine[0] = largeMineField[row][col];
      adjMine[1] = largeMineField[row][col+1];
      adjMine[2] = largeMineField[row][col+2];
      adjMine[3] = largeMineField[row+1][col+2];
      adjMine[4] = largeMineField[row+2][col+2];
      adjMine[5] = largeMineField[row+2][col+1];
      adjMine[6] = largeMineField[row+2][col];
      adjMine[7] = largeMineField[row+1][col];
      for (boolean boo: adjMine){
         if(boo){
            adjMinesNum ++;
         }
      }
      return adjMinesNum;
   }
   
   
   /**
      Returns true iff (row,col) is a valid field location.  Row numbers and column numbers
      start from 0.
      @param row  row of the location to consider
      @param col  column of the location to consider
      @return whether (row, col) is a valid field location
   */
   public boolean inRange(int row, int col) {
      return row < numberOfRows && row >= 0 && col < numberOfColumns && col >= 0;
   }
   
   
   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */  
   public int numRows() {
      return numberOfRows;       // DUMMY CODE so skeleton compiles
   }
   
   
   /**
      Returns the number of columns in the field.
      @return number of columns in the field
   */    
   public int numCols() {
      return numberOfColumns;       // DUMMY CODE so skeleton compiles
   }
   
   
   /**
      Returns whether there is a mine in this square
      @param row  row of the location to check
      @param col  column of the location to check
      @return whether there is a mine in this square
      PRE: inRange(row, col)   
   */    
   public boolean hasMine(int row, int col) {
      if (!inRange(row, col)) throw new AssertionError();
      return mineFieldData[row][col];
   }
   
   
   /**
      Returns the number of mines you can have in this minefield.  For mines created with the 3-arg
      constructor, some of the time this value does not match the actual number of mines currently
      on the field.  See doc for that constructor, resetEmpty, and populateMineField for more
      details.
      @return number of mines
    */
   public int numMines() {
      return numberOfMines;
   }

   public boolean[][] getMineFieldData(){
      return this.mineFieldData;
   }

}

