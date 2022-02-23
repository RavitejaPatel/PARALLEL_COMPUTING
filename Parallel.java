import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/*
	Used as reference and some of the code: https://www.geeksforgeeks.org/n-queen-problem-backtracking-3/
*/
public class Parallel {
	static int N = 4;
	static List<int[][]> correctBoards = new ArrayList<int[][]>();
	public static Semaphore semaphore = new Semaphore(1, true);
	static int MAX_THREADS = 12;
	static int correctBoardCount = 0;

	class InsideSolve implements Runnable {
		int range, startCol, startRow, threadID, level;
		int[][] board;
		boolean[] threadResult;
		int[] interior;

		public InsideSolve(int[][] board, int n, int startRow, int startCol, int threadID) {
			this(board, n, startRow, startCol, threadID, null, 0);
		}

		public InsideSolve(int[][] board, int n, int startRow, int startCol, int threadID, boolean[] threadResult) {
			this(board, n, startRow, startCol, threadID, threadResult, 0);
		}

		public InsideSolve(int[][] board, int n, int startRow, int startCol, int threadID, boolean[] threadResult,
				int level) {
			this.range = n;
			this.startCol = startCol;
			this.startRow = startRow;
			// printMessage("$# at level: " + this.level +"-- start row thread: " +
			// startRow);
			this.board = board;
			this.threadID = threadID;
			this.threadResult = threadResult;
			this.level = level;
		}

		@Override
		public void run() {
			if (this.startCol >= (this.range)) {
				addCorrectBoard();
				this.threadResult[this.threadID] = true;
				return;
			}

			int numThreads = numThreads(level);
			List<Thread> threadList = new ArrayList<Thread>();
			int[][][] boardList = new int[N][N][N];
			boolean[] localThreadResult = new boolean[N];

			for (int i = 0; i < this.range; i++) {
				if (isSafe(this.board, i, this.startCol)) {
					boardList[i] = newEmptyBoard();
					copyBoard(board, boardList[i]);
					boardList[i][i][this.startCol] = 1;

					if (i < numThreads && createThread()) {
						Thread t = new Thread(new InsideSolve(boardList[i], N, i, this.startCol + 1, i,
								localThreadResult, this.level + 1));
						t.start();
						threadList.add(t);
					} else {
						localThreadResult[i] = solveNQUtil(boardList[i], this.startCol + 1, this.level + 1);
					}
				}
			}
			for (int i = 0; i < threadList.size(); i++) {
				try {
					threadList.get(i).join();
					releaseThread();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			this.threadResult[this.threadID] = arraySucceed(localThreadResult);
			for (int i = 0; i < localThreadResult.length; i++) {
				if (!localThreadResult[i])
					boardList[i] = null;
			}
			return;
		}

		boolean isSafe(int[][] board, int row, int col) {
			int i, j;

			for (i = 0; i < col; i++)
				if (board[row][i] == 1)
					return false;

			for (i = row, j = col; i >= 0 && j >= 0; i--, j--)
				if (board[i][j] == 1)
					return false;

			for (i = row, j = col; j >= 0 && i < this.range; i++, j--)
				if (board[i][j] == 1)
					return false;

			return true;
		}

		public boolean solveNQUtil(int[][] board, int col, int level) {
			if (col >= (this.range)) {
				addCorrectBoard();
				return true;
			}

			for (int i = 0; i < this.range; i++) {
				if (isSafe(board, i, col)) {

					board[i][col] = 1;
					if (solveNQUtil(board, col + 1, level + 1))
						return true;

					board[i][col] = 0;;
				}
			}

			return false;
		}

		public boolean solveNQUtil(int board[][], int col) {
			if (col >= N) {
				addCorrectBoard();
				return true;
			}

			for (int i = col; i < (startCol + N); i++) {
				if (isSafe(board, i, col)) {
					board[i][col] = 1;

					if (solveNQUtil(board, col + 1))
						return true;

					board[i][col] = 0; // BACKTRACK
				}
			}

			return false;
		}

		public boolean solveNQThreaded() {
			boolean[] threadResult = new boolean[N];
			int numThreads = numThreads(this.level);
			List<Thread> threadList = new ArrayList<Thread>();
			int[][][] boardList = new int[N][N][N];
			for (int i = 0; i < N; i++) {
				boardList[i] = newEmptyBoard();
				boardList[i][i][0] = 1;
				if (i < numThreads && createThread()) {
					Thread t = new Thread(
							new InsideSolve(boardList[i], N, i, this.startCol + 1, i, threadResult, this.level + 1));
					t.start();
					threadList.add(t);

				} else {
					threadResult[i] = this.solveNQUtil(boardList[i], this.startCol + 1, this.level + 1);
				}

			}
			for (int i = 0; i < threadList.size(); i++) {
				try {
					threadList.get(i).join();
					releaseThread();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < boardList.length; i++) {
				boardList[i] = null;
			}
			
			return arraySucceed(threadResult);
		}

		// calcualte the number of threads that can be returned per node
		public int numThreads(int level) {
			return N - level - 1;
		}
	}

	/* A utility function to print solution */
	static void printSolution(int[][] board) {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				System.out.print(" " + board[i][j] + " ");
			System.out.println();
		}
	}

	static void printSolution(int[][] board, String pad) {
		try {
			semaphore.acquire(1);
			for (int i = 0; i < N; i++) {
				System.out.print(pad);
				for (int j = 0; j < N; j++)
					System.out.print(" " + board[i][j] + " ");
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			semaphore.release(1);
		}
	}

	static void printMessage(String msg) {
		try {
			semaphore.acquire(1);
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			semaphore.release(1);
		}
	}

	static void addCorrectBoard(int[][] board) {
		try {
			semaphore.acquire(1);
			correctBoards.add(board);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			semaphore.release(1);
		}
	}

	static void addCorrectBoard() {
		try {
			semaphore.acquire(1);
			correctBoardCount++;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			semaphore.release(1);
		}
	}

	void copyBoard(int[][] boardToCopy, int[][] newBoard) {
		for (int i = 0; i < boardToCopy.length; i++) {
			for (int j = 0; j < boardToCopy.length; j++) {
				newBoard[i][j] = boardToCopy[i][j];
			}
		}
	}

	static int[][] newEmptyBoard() {
		int[][] newBoard = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				newBoard[i][j] = 0;
			}
		}
		return newBoard;
	}

	static boolean arraySucceed(boolean[] threadResult) {
		for (int i = 0; i < threadResult.length; i++)
			if (threadResult[i]) {
				return true;
			}
		return false;
	}

	static boolean createThread() {
		boolean create = false;
		try {
			semaphore.acquire(1);
			if (MAX_THREADS > 0) {
				MAX_THREADS -= 1;
				create = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			semaphore.release(1);
		}
		return create;
	}

	static void releaseThread() {
		try {
			semaphore.acquire(1);
			MAX_THREADS += 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			semaphore.release(1);
		}
	}

	void runProgram() {
		int[][] board = newEmptyBoard();
		InsideSolve Queen = new InsideSolve(board, N, 0, 0, 0);
		if (Queen.solveNQThreaded()) {
			System.out.println("Solutions found!");
		} else {
			System.out.print("Solution does not exist");
		}
	}

	public static void main(String args[]) {
		if (args.length > 0) {
			N = Integer.parseInt(args[0]);
			if (args.length > 1) {
				MAX_THREADS = Integer.parseInt(args[1]);
			}
			System.out.println("Setting N to: " + N + " and Threads to: " + MAX_THREADS);
		}
		Parallel x = new Parallel();
		x.runProgram();
	}
}
