package src.pas.chess.moveorder;


import edu.bu.chess.search.DFSTreeNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import src.pas.chess.heuristics.CustomHeuristics;

// SYSTEM IMPORTS

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
		Collections.sort(nodes, Comparator.comparingDouble(CustomHeuristics::getMaxPlayerHeuristicValue).reversed());
        return nodes;
	}

}
