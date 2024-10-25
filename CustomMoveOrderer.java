package src.pas.chess.moveorder;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import src.pas.chess.heuristics.CustomHeuristics;
// JAVA PROJECT IMPORTS
import src.pas.chess.moveorder.DefaultMoveOrderer;

public class CustomMoveOrderer
    extends Object
{

	/**
	 * TODO: implement me!
	 * This method should perform move ordering. Remember, move ordering is how alpha-beta pruning gets part of its power from.
	 * You want to see nodes which are beneficial FIRST so you can prune as much as possible during the search (i.e. be faster)
	 * @param nodes. The nodes to order (these are children of a DFSTreeNode) that we are about to consider in the search.
	 * @return The ordered nodes.
	 */
	public static List<DFSTreeNode> order(List<DFSTreeNode> nodes)
	{
		List<DFSTreeNode> captureNodes = new LinkedList<>();
		List<DFSTreeNode> promotionNodes = new LinkedList<>();
		List<DFSTreeNode> centerControlNodes = new LinkedList<>();
		List<DFSTreeNode> otherNodes = new LinkedList<>();

		for (DFSTreeNode node : nodes) {
			if (node.getMove() != null) {
				switch (node.getMove().getType()) {
					case CAPTUREMOVE:
						captureNodes.add(node);
						break;
					case PROMOTEMOVE:
						promotionNodes.add(node);
						break;
					default:
						if (isCenterControlMove(node)) {
							centerControlNodes.add(node);
						} else {
							otherNodes.add(node);
						}
						break;
				}
			} else {
				otherNodes.add(node);
			}
		}

		captureNodes.addAll(promotionNodes);
		captureNodes.addAll(centerControlNodes);
		captureNodes.addAll(otherNodes);
		return captureNodes;
	}

	/**
	 * Determines if a move controls the center of the board.
	 * @param node The DFSTreeNode containing the move.
	 * @return True if the move controls the center, otherwise false.
	 */
	private static boolean isCenterControlMove(DFSTreeNode node) {
		Coordinate targetPosition = node.getMove().getTargetPosition();
		int x = targetPosition.getXPosition();
		int y = targetPosition.getYPosition();
		return (x >= 4 && x <= 5 && y >= 4 && y <= 5);
	}

}
