package src.pas.chess.heuristics;

// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.player.Player;
import edu.bu.chess.game.player.PlayerType;
import edu.bu.chess.game.move.Move;
import edu.bu.chess.game.move.MoveType;
import edu.bu.chess.game.Game;
import edu.bu.chess.game.Board;
import edu.bu.chess.utils.Coordinate;
import edu.cwru.sepia.util.Direction;
import edu.bu.chess.game.move.PromotePawnMove;

// JAVA PROJECT IMPORTS
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;


public class CustomHeuristics
    extends Object
{

	/**
	 * Get the max player from a node
	 * 
	 * @param node
	 * @return
	 */
	public static Player getMaxPlayer(DFSTreeNode node) {
		return node.getMaxPlayer();
	}

	/**
	 * Get the min player from a node
	 * 
	 * @param node
	 * @return
	 */
	public static Player getMinPlayer(DFSTreeNode node) {
		return CustomHeuristics.getMaxPlayer(node).equals(node.getGame().getCurrentPlayer())
				? node.getGame().getOtherPlayer()
				: node.getGame().getCurrentPlayer();
	}

	// calculate the difference in material between the two players
	public static int getMaterialAdvantage(DFSTreeNode node) {
		int maxPlayerMaterial = 0;
		int minPlayerMaterial = 0;

		for (PieceType pieceType : PieceType.values()) {
			int pieceValue = Piece.getPointValue(pieceType);

			int numMaxPieces = node.getGame().getNumberOfAlivePieces(getMaxPlayer(node), pieceType);
			int numMinPieces = node.getGame().getNumberOfAlivePieces(getMinPlayer(node), pieceType);

			maxPlayerMaterial += numMaxPieces * pieceValue;
			minPlayerMaterial += numMinPieces * pieceValue;
		}

		return maxPlayerMaterial - minPlayerMaterial;
	}

	// calculate the difference in mobility between the two players
	public static int getMobilityAdvantage(DFSTreeNode node) {
		int maxPlayerMoves = node.getGame().getAllMoves(getMaxPlayer(node)).size();
		int minPlayerMoves = node.getGame().getAllMoves(getMinPlayer(node)).size();

		return maxPlayerMoves - minPlayerMoves;
	}

	// calculate the difference in center control between the two players
	public static double getCenterControlAdvantage(DFSTreeNode node) {
		double maxPlayerCenterPieces = 0;
		double minPlayerCenterPieces = 0;

		// max
		for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
			Coordinate position = node.getGame().getCurrentPosition(piece);
			if (isCenterSquare(position, "max")) {
				if (piece.getType() == PieceType.PAWN) {
					maxPlayerCenterPieces += 2;
				} else {
					maxPlayerCenterPieces += 0.5;
				}
			} else if (isCenterSquare(position, "min")) {
				maxPlayerCenterPieces += 0.2;
			}
		}

		// min
		for (Piece piece : node.getGame().getBoard().getPieces(getMinPlayer(node))) {
			Coordinate position = node.getGame().getCurrentPosition(piece);
			if (isCenterSquare(position, "min")) {
				if (piece.getType() == PieceType.PAWN) {
					maxPlayerCenterPieces += 2;
				} else {
					minPlayerCenterPieces += 0.5;
				}
			} else if (isCenterSquare(position, "max")) {
				minPlayerCenterPieces += 0.2;
			}
		}

		return maxPlayerCenterPieces - minPlayerCenterPieces;
	}

	// helper function to determine if a coordinate is in the center of the board
	public static boolean isCenterSquare(Coordinate coord, String player) {
		int x = coord.getXPosition();
		int y = coord.getYPosition();

		if (player == "max") {
			return y == 4 && x >= 4 && x <= 5;
		} else if (player == "min") {
			return y == 4 && x >= 4 && x <= 5;
		}
		return false;
	}

	// calculate the difference in development between the two players
	public static double getDevelopmentAdvantage(DFSTreeNode node) {
		double maxPlayerDevelopment = countDevelopedPieces(node, getMaxPlayer(node));
		double minPlayerDevelopment = countDevelopedPieces(node, getMinPlayer(node));
		return maxPlayerDevelopment - minPlayerDevelopment;
	}

	// helper function to count the number of developed pieces for a player
	private static double countDevelopedPieces(DFSTreeNode node, Player player) {
		double developedPieces = 0;
		Board board = node.getGame().getBoard();
		Map<PieceType, List<Coordinate>> initialPositions = getInitialPositions(player);

		for (Piece piece : board.getPieces(player)) {
			if (piece.getType() == PieceType.KING || piece.getType() == PieceType.PAWN) {
				continue;
			}
			Coordinate currentPosition = node.getGame().getCurrentPosition(piece);
			if (!initialPositions.get(piece.getType()).contains(currentPosition)) {
				if (piece.getType() == PieceType.KNIGHT || piece.getType() == PieceType.BISHOP) {
					developedPieces += 2;
				} else {
					developedPieces += 0.2;
				}
			}
		}
		return developedPieces;
	}

	// helper function to get the initial positions of the pieces for a player
	private static Map<PieceType, List<Coordinate>> getInitialPositions(Player player) {
		Map<PieceType, List<Coordinate>> initialPositions = new HashMap<>();

		int backRankY = player.getPlayerType() == PlayerType.WHITE ? 8 : 1;

		List<Coordinate> rookPositions = Arrays.asList(new Coordinate(1, backRankY), new Coordinate(8, backRankY));
		initialPositions.put(PieceType.ROOK, rookPositions);

		List<Coordinate> knightPositions = Arrays.asList(new Coordinate(2, backRankY), new Coordinate(7, backRankY));
		initialPositions.put(PieceType.KNIGHT, knightPositions);

		List<Coordinate> bishopPositions = Arrays.asList(new Coordinate(3, backRankY), new Coordinate(6, backRankY));
		initialPositions.put(PieceType.BISHOP, bishopPositions);

		List<Coordinate> queenPositions = Arrays.asList(new Coordinate(4, backRankY));
		initialPositions.put(PieceType.QUEEN, queenPositions);

		return initialPositions;
	}

	// calculate the difference in promotion between the two players
	public static double getPromotionAdvantage(DFSTreeNode node) {
		double maxPlayerPromotion = 0;
		double minPlayerPromotion = 0;
		Game game = node.getGame();

		// max
		for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
			if (piece.getType() == PieceType.PAWN) {
				List<Move> moves = piece.getAllMoves(game);
				for (Move move : moves) {
					if (move.getType() == MoveType.PROMOTEPAWNMOVE) {
						maxPlayerPromotion += 1;
					}
				}
			}
		}

		// min
		for (Piece piece : node.getGame().getBoard().getPieces(getMinPlayer(node))) {
			if (piece.getType() == PieceType.PAWN) {
				List<Move> moves = piece.getAllMoves(game);
				for (Move move : moves) {
					if (move.getType() == MoveType.PROMOTEPAWNMOVE) {
						minPlayerPromotion += 1;
					}
				}
			}
		}

		return maxPlayerPromotion - minPlayerPromotion;
	}

	public static double getMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		double materialHeuristicValue = CustomHeuristics.getMaterialAdvantage(node);
		double mobilityHeuristicValue = CustomHeuristics.getMobilityAdvantage(node);
		double centerHeuristicValue = CustomHeuristics.getCenterControlAdvantage(node);
		double developmentHeuristicValue = CustomHeuristics.getDevelopmentAdvantage(node);
		double promotionHeuristicValue = CustomHeuristics.getPromotionAdvantage(node);

		return (3.0 * materialHeuristicValue) + (0.05 * mobilityHeuristicValue) + (0.3 * centerHeuristicValue)
				+ (0.8 * developmentHeuristicValue) + (3.0 * promotionHeuristicValue);
	}

}
