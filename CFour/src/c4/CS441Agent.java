package c4;
/**
 * CS441 Kapsaminda yazdigim AI algoritmasi
 * 
 * Framework'un sagladigi ozellikleri kullanarak yazildi
 * 
 * Derinlik sinirlamasiyla Alpha Beta Pruning Yontemiyle Minimax algoritmasi implement edildi.
 * 
 * @author shy
 *
 */
public class CS441Agent extends ConnectFour implements Agent {

	@Override
	public AgentState getAgentState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBestMove(int[][] table) {
		double bestVal = -9999;
		int bestMove = 0;
		
		int player = (countPieces() % 2 == 0 ? PLAYER1 : PLAYER2);
		int fak = (player == PLAYER1 ? -1 : 1);
		
		String str = "";
		double[] values = getNextVTable(table, false);
		for(int i=0; i< COLCOUNT; i++)
		{
			str += i + ": " + values[i] * fak + " - ";
			if(isLegalMove(i) && values[i] * fak > bestVal)
				{ bestMove = i; bestVal = values[i] * fak; }
			else if(isLegalMove(i) && values[i] *fak == bestVal && getEvalValue(i) > getEvalValue(bestMove))
			{
				bestMove = i; bestVal = values[i] * fak;
			}
				
		}
		System.out.println("Rates:" + str);
		return bestMove;
	}


	@Override
	public double getScore(int[][] table, boolean putInRange) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected double stateValue(int player)
	{
		double value = 0;
		int otherPlayer = (player == PLAYER1 ? PLAYER2 : PLAYER1);
		
		for (int j = ROWCOUNT - 1; j >= 0; j--) {
			for (int i = 0; i < COLCOUNT; i++) {
				if ((getField(player) & fieldMask[i][j]) != 0L)
					{
						if(i<4) value += ((i+1)*(i+1));
						else value += ((i-7)*(i-7));
					}
				else if((getField(otherPlayer) & fieldMask[i][j]) != 0L){
					{
						if(i<4) value -= ((i+1)*(i+1));
						else value -= ((i-7)*(i-7));
					}
				}
			}
		}
		return value;
	}
	protected double getEvalValue() {
		double val = 0;
		for(int move = 0; move < COLCOUNT; move++)
		if(isLegalMove(move))
		{
			if((move == 0 || move == 6) && val < 1) val = 1;
			if((move == 1 || move == 5) && val < 4) val = 4;
			if((move == 2 || move == 4) && val < 9) val = 9;
			if(move == 3 && val < 16) val = 16;
		}
		return val;

		
	}	
	protected double getEvalValue(int move) {
		double val = 0;
		if(isLegalMove(move))
		{
			if((move == 0 || move == 6)) val = 1;
			if((move == 1 || move == 5)) val = 4;
			if((move == 2 || move == 4)) val = 9;
			if(move == 3) val = 16;
		}
		return val;

		
	}
	protected double alphaBetaPrune(int depth, double alpha, double beta, boolean isMaxing, int curPlayer, int move) {
		double val = 0;
		System.out.println("Depth:" + depth + " a: "+ alpha + " b: " + beta + " isMax: " + isMaxing + " player: " + curPlayer + " prevmove: " + move);
		if(hasWin(curPlayer)) return (curPlayer == PLAYER1 ? -1*(1001 - depth) : (1001 - depth));
		// En uca indiysek geri donelim
		if(depth == 4) return (curPlayer == PLAYER1 ? getEvalValue(move) : getEvalValue(move)*-1);
		
		if(isMaxing) {
			double bestVal = -9999;
			for(int x = 0; x < COLCOUNT; x++) 
				if (colHeight[x] < ROWCOUNT){
				putPiece(curPlayer, x);
				double result = alphaBetaPrune(depth+1, alpha, beta, false, curPlayer == PLAYER1 ? PLAYER2 : PLAYER1, x);
				bestVal = (bestVal > result ? bestVal : result);
				alpha = (alpha > bestVal ? alpha : bestVal);
				removePiece(curPlayer, x);
				if(beta <= alpha)
					return alpha;
			}
			return bestVal;
		}
		if(!isMaxing) {
			double bestVal = 9999;
			for(int x = 0; x < COLCOUNT; x++) {
				if (colHeight[x] < ROWCOUNT) {		
					putPiece(curPlayer, x);
					double result = alphaBetaPrune(depth+1, alpha, beta, true, curPlayer == PLAYER1 ? PLAYER2 : PLAYER1, x);
					bestVal = (bestVal > result ? result : bestVal);
					beta = (beta > bestVal ? bestVal : beta);
					removePiece(curPlayer, x);
					if(beta <= alpha)
						return beta;
				}
			}
			return bestVal;
		}
		return val;
	}
	@Override
	public double[] getNextVTable(int[][] table, boolean putInRange) {
		double[] values = new double[COLCOUNT];
		
		int player = (countPieces() % 2 == 0 ? PLAYER1 : PLAYER2);
		int otherPlayer = (player == PLAYER1 ? PLAYER2 : PLAYER1);
		System.out.println("Player " + player);
		setBoard(table);
		int colIndex;
		for(colIndex = 0; colIndex < COLCOUNT; colIndex++) {
			if (colHeight[colIndex] < ROWCOUNT) {
				// Tablonun mevcut durumunda bu kolona koyarsak kazanabiliyor muyuz?
				if(canWin(player, colIndex, colHeight[colIndex])) {
					values[colIndex] = (player == PLAYER1 ? -1001 : 1001);
					System.out.println("Can Win At: " + colIndex);
					continue;
				}
				else{
					// Kazanamiyorsak hesaplamaya devam ediyoruz
					putPiece(player, colIndex);
					double score = 0;
					if(hasWin(otherPlayer))
						score = (otherPlayer == PLAYER1 ? -1001 : 1001);
					else
						score = alphaBetaPrune(0, -9999, 9999, false, otherPlayer, colIndex);
					removePiece(player, colIndex);
					values[colIndex] = score;
				}
			}

		}
		return values;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CS441 Algo";
	}

	@Override
	public void semOpDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void semOpUp() {
		// TODO Auto-generated method stub

	}

}
