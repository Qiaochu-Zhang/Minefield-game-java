// Name: Qiaochu Zhang
// USC NetID: qzhang40
// CS 455 PA3
// Fall 2023



/**
  VisibleField class
  This is the data that's being displayed at any one point in the game (i.e., visible field, because
  it's what the user can see about the minefield). Client can call getStatus(row, col) for any 
  square.  It actually has data about the whole current state of the game, including the underlying
  minefield (getMineField()).  Other accessors related to game status: numMinesLeft(),
  isGameOver().  It also has mutators related to actions the player could do (resetGameDisplay(),
  cycleGuess(), uncover()), and changes the game state accordingly.
  
  It, along with the MineField (accessible in mineField instance variable), forms the Model for the
  game application, whereas GameBoardPanel is the View and Controller in the MVC design pattern.  It
  contains the MineField that it's partially displaying.  That MineField can be accessed
  (or modified) from outside this class via the getMineField accessor.  
 */
public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus values [0,8] mentioned in comments below) are the
   // possible states of one location (a "square") in the visible field (all are values that can be
   // returned by public method getStatus(row, col)).
   
   // The following are the covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // The following are the uncovered states (all non-negative values):
   
   // values in the range [0,8] corresponds to number of mines adjacent to this opened square
   
   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already
                                          // (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of
                                                  // losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused
                                                 // you to lose)
   // ----------------------------------------------------------   
  
   private MineField mineFieldVisible; // record the minefield data
   public int[][] visibleStatus;       // record visible field data (-3 to 11) for each square
                                       // the meaning of each integer is described above
   private final int[][] largeVisibleStatus; // visibleStatus plus surrounded one row or column
                                       // of COVERED Value from each of four margin
   private final int rows;                   // number of rows of minefield
   private final int cols;                   // number of columns of minefield
   private final int mines;                  // number of mines of minefield

   /**
      Create a visible field that has the given underlying mineField.
      The initial state will have all the locations covered, no mines guessed, and the game not
      over.
      @param mineField  the minefield to use for this VisibleField
    */
   public VisibleField(MineField mineField) {
      mineFieldVisible = mineField;
      rows = mineFieldVisible.numRows();
      cols = mineFieldVisible.numCols();
      mines = mineFieldVisible.numMines();
      visibleStatus = new int[rows][cols];
      for(int i = 0; i< rows; i++){
         for(int j = 0; j < cols; j++){
            visibleStatus[i][j] = COVERED;
         }
      }
      largeVisibleStatus = new int[rows+2][cols+2];
      for(int row1=1; row1 <= rows; row1++){
         System.arraycopy(visibleStatus[row1 - 1], 0, largeVisibleStatus[row1], 1, cols);
      }
   }
   
   
   /**
      Reset the object to its initial state (see constructor comments), using the same underlying
      MineField. 
   */     
   public void resetGameDisplay() {
      mineFieldVisible = new MineField(rows, cols, mines);
      for(int i = 0; i< rows; i++){
         for(int j = 0; j < cols; j++){
            visibleStatus[i][j] = COVERED;
            largeVisibleStatus[i+1][j+1] = COVERED;
         }
      }
   }
  
   
   /**
      Returns a reference to the mineField that this VisibleField "covers"
      @return the minefield
    */
   public MineField getMineField() {
      return mineFieldVisible;
   }
   
   
   /**
      Returns the visible status of the square indicated.
      @param row  row of the square
      @param col  col of the square
      @return the status of the square at location (row, col).  See the public constants at the
            beginning of the class for the possible values that may be returned, and their meanings.
      PRE: getMineField().inRange(row, col)
    */
   public int getStatus(int row, int col) {
      return visibleStatus[row][col];
   }

   
   /**
      Returns the number of mines left to guess.  This has nothing to do with whether the mines
      guessed are correct or not.  Just gives the user an indication of how many more mines the user
      might want to guess.  This value will be negative if they have guessed more than the number of
      mines in the minefield.     
      @return the number of mines left to guess.
    */
   public int numMinesLeft() {
      int numMinesGuess = 0;
      for(int i = 0; i< rows; i++){
         for(int j = 0; j < cols; j++){
            if(visibleStatus[i][j] == MINE_GUESS){
               numMinesGuess ++;
            }
         }
      }
      return mines - numMinesGuess;
   }


   /**
      Cycles through covered states for a square, updating number of guesses as necessary.  Call on
      a COVERED square changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to
      QUESTION;  call on a QUESTION square changes it to COVERED again; call on an uncovered square
      has no effect.
      @param row  row of the square
      @param col  col of the square
      PRE: getMineField().inRange(row, col)
    */
   public void cycleGuess(int row, int col) {
      int currentStatus = visibleStatus[row][col];
      if(currentStatus == COVERED){
         currentStatus = MINE_GUESS;
      }
      else if(currentStatus == MINE_GUESS){
         currentStatus = QUESTION;
      }
      else if(currentStatus == QUESTION) {
         currentStatus = COVERED;
      }
      visibleStatus[row][col] = currentStatus;
      largeVisibleStatus[row+1][col+1] = currentStatus;
   }

   
   /**
      Uncovers this square and returns false iff you uncover a mine here.
      If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in the
      neighboring area that are also not next to any mines, possibly uncovering a large region.
      Any mine-adjacent squares you reach will also be uncovered, and form (possibly along with
      parts of the edge of the whole field) the boundary of this region.
      Does not uncover, or keep searching through, squares that have the status MINE_GUESS. 
      Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
      or a loss (opened a mine).
      @param row  of the square
      @param col  of the square
      @return false   iff you uncover a mine at (row, col)
      PRE: getMineField().inRange(row, col)
    */
   public boolean uncover(int row, int col) {
      boolean[][] mineData = mineFieldVisible.getMineFieldData();
      if(mineData[row][col] && visibleStatus[row][col] != MINE_GUESS){
         visibleStatus[row][col] = EXPLODED_MINE;
         largeVisibleStatus[row+1][col+1] = EXPLODED_MINE;
         uncoverMines();
         return false;
      }
      else {
         uncoverSafe(row, col);
         for(int row1=1; row1 <= rows; row1++){
            if (cols >= 0) System.arraycopy(largeVisibleStatus[row1], 1, visibleStatus[row1 - 1], 0, cols);
         }
         if(isGameOver()){
            uncoverMines();
         }
         return true;
      }

   }
 
   
   /**
      Returns whether the game is over.
      (Note: This is not a mutator.)
      @return whether game has ended
    */
   public boolean isGameOver() {
      int countUncoveredNum =0;
      for(int i = 0; i< rows; i++){
         for(int j = 0; j < cols; j++){
            if(visibleStatus[i][j] == EXPLODED_MINE){
               return true;
            }
            if(visibleStatus[i][j] <= 8 && visibleStatus[i][j] >= 0){
               countUncoveredNum++;
            }
         }
      }
      return countUncoveredNum == (rows * cols - mines);
   }
 
   
   /**
      Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states, 
      vs. any one of the covered states).
      @param row of the square
      @param col of the square
      @return whether the square is uncovered
      PRE: getMineField().inRange(row, col)
    */
   public boolean isUncovered(int row, int col) {
      return (visibleStatus[row][col]>=0);
   }

   /**
    Use recursive calls to uncover all the squares in the neighboring area that are also not next to any mines,
    possibly uncovering a large region.
    Any mine-adjacent squares you reach will also be uncovered, and form (possibly along with
    parts of the edge of the whole field) the boundary of this region.
    @param row of the square
    @param col of the square
    PRE: getMineField().inRange(row, col)
    */
  private void uncoverSafe(int row, int col){
      int curPositStatus = largeVisibleStatus[row+1][col+1];
      if(curPositStatus == MINE_GUESS || (curPositStatus >= 0 && curPositStatus <=8)){
         return;
      }
      else {
         int adjMines = mineFieldVisible.numAdjacentMines(row, col);
         largeVisibleStatus[row+1][col+1] = adjMines;
         if(adjMines == 0) {
            uncoverSafe(row - 1, col-1);
            uncoverSafe(row - 1, col);
            uncoverSafe(row - 1, col+1);
            uncoverSafe(row , col+1);
            uncoverSafe(row + 1, col+1);
            uncoverSafe(row+1, col);
            uncoverSafe(row + 1, col-1);
            uncoverSafe(row, col - 1);
         }
         else {
            return;
         }
      }
  }

   /**
    Update all the mines' status (for guessed or covered) at the end of the game.
    */
  private void uncoverMines(){
     for(int i = 0; i< rows; i++){
        for(int j = 0; j < cols; j++){
           boolean[][] mineData = mineFieldVisible.getMineFieldData();
           if(visibleStatus[i][j] == COVERED && mineData[i][j]){
              visibleStatus[i][j] = MINE;
           }
           if(visibleStatus[i][j] == MINE_GUESS && !mineData[i][j]){
              visibleStatus[i][j] = INCORRECT_GUESS;
           }
        }
     }
  }
}
