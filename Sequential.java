import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/*
Built the sequential library from: https://www.geeksforgeeks.org/n-queen-problem-backtracking-3/
*/

class Sequential {

	static int N = 4;
	static int k = 1;

	static List<int[][]> correctBoards = new ArrayList<int[][]>();
	static int correctBoardsCount = 0;

	static void printSolution(int board[][]) {
		System.out.printf("%d-\n", k++);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				System.out.printf(" %d ", board[i][j]);
			System.out.printf("\n");
		}
		System.out.printf("\n");
	}

	static boolean isSafe(int board[][], int row, int col) {
		int i, j;

		for (i = 0; i < col; i++)
			if (board[row][i] == 1)
				return false;

		for (i = row, j = col; i >= 0 && j >= 0; i--, j--)
			if (board[i][j] == 1)
				return false;

		for (i = row, j = col; j >= 0 && i < N; i++, j--)
			if (board[i][j] == 1)
				return false;

		return true;
	}


	static void copyBoard(int[][] boardToCopy, int[][] newBoard) {
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

	static boolean solveNQUtil(int board[][], int col) {
		if (col == N) {
			int[][] newBoard = newEmptyBoard();
			copyBoard(board, newBoard);
			correctBoards.add(newBoard);
			correctBoardsCount++;
			return true;
		}

		boolean res = false;
		for (int i = 0; i < N; i++) {
			// System.out.println("this.range: " + N + " -- this.startCol: " + col + " numThreads: " + 0 + " -- N: " + N + " --> isSafe(this.board, i, this.startCol): "+ isSafe(board, i, col));
			if (isSafe(board, i, col)) {
				board[i][col] = 1;

				res = solveNQUtil(board, col + 1) || res;

				board[i][col] = 0; // BACKTRACK
			}
		}

		return res;
	}

	static void solveNQ() {
		int board[][] = new int[N][N];

		if (solveNQUtil(board, 0) == false) {
			System.out.printf("Solution does not exist");
			return;
		}

		return;
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			// System.out.println("args: " + Arrays.toString(args));
			N = Integer.parseInt(args[0]);
			System.out.println("Setting N to: " + N);
		}
		solveNQ();
		System.out.println("Solutions found!");
	}
}
