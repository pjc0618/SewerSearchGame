package student;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.GetOutState;
import game.Tile;
import game.FindState;
import game.SewerDiver;
import game.Node;
import game.NodeStatus;
import game.Edge;

public class DiverMax extends SewerDiver {


    /** Get to the ring in as few steps as possible. Once you get there, 
     * you must return from this function in order to pick
     * it up. If you continue to move after finding the ring rather 
     * than returning, it will not count.
     * If you return from this function while not standing on top of the ring, 
     * it will count as a failure.
     * 
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the ring in fewer steps.
     * 
     * At every step, you know only your current tile's ID and the ID of all 
     * open neighbor tiles, as well as the distance to the ring at each of these tiles
     * (ignoring walls and obstacles). 
     * 
     * In order to get information about the current state, use functions
     * currentLocation(), neighbors(), and distanceToRing() in FindState.
     * You know you are standing on the ring when distanceToRing() is 0.
     * 
     * Use function moveTo(long id) in FindState to move to a neighboring 
     * tile by its ID. Doing this will change state to reflect your new position.
     * 
     * A suggested first implementation that will always find the ring, but likely won't
     * receive a large bonus multiplier, is a depth-first walk. Some
     * modification is necessary to make the search better, in general.*/

	@Override public void findRing(FindState state) {
        //TODO : Find the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method elsewhere, with a good specification,
        // and call it from this one.
		if (state.distanceToRing()==0) return;
		HashMap<Long, Boolean> visited= new HashMap<Long, Boolean>();
		dfsWalk(state, visited);
		return;
    }
    /** Max is standing on a Node u given by FindState s, while HashMap v contains
     * the ID's of visited nodes and the boolean value of false if the node given by 
     * the ID does not contain the ring, true otherwise.  The function stops when 
     * u is the node containing the ring
    
    Precondition: u is unvisited. */
   public static void dfsWalk(FindState s, HashMap<Long, Boolean> v) {
	   long u= s.currentLocation();
	   if (s.distanceToRing()==0) {
		   v.put(u, true);
		   return;}
	   v.put(u, false);//u has been visited; does not have ring
	   Heap<NodeStatus> nh= new Heap<NodeStatus>();
	   for (NodeStatus ns:s.neighbors()) {
		   nh.add(ns, ns.getDistanceToTarget());
	   }
	   while (nh.size()>0) {
		   NodeStatus n=nh.poll();
		   if (!v.containsKey(n.getId())) {
			   s.moveTo(n.getId());
			   v.put(n.getId(), false);
			   dfsWalk(s,v);
			   if(v.get(s.currentLocation())) return;
			   s.moveTo(u);
		   }
	   } 
   }


    /** Get out of the sewer system before the steps are all used, trying to collect
     * as many coins as possible along the way. Your solution must ALWAYS get out
     * before the steps are all used, and this should be prioritized above
     * collecting coins.
     * 
     * You now have access to the entire underlying graph, which can be accessed
     * through GetOutState. currentNode() and getExit() will return Node objects
     * of interest, and getNodes() will return a collection of all nodes on the graph. 
     * 
     * You have to get out of the sewer system in the number of steps given by
     * getStepsRemaining(); for each move along an edge, this number is decremented
     * by the weight of the edge taken.
     * 
     * Use moveTo(n) to move to a node n that is adjacent to the current node.
     * When n is moved-to, coins on node n are automatically picked up.
     * 
     * You must return from this function while standing at the exit. Failing to
     * do so before steps run out or returning from the wrong node will be
     * considered a failed run.
     * 
     * Initially, there are enough steps to get from the starting point to the
     * exit using the shortest path, although this will not collect many coins.
     * For this reason, a good starting solution is to use the shortest path to
     * the exit. */
    @Override public void getOut(GetOutState state) {
        //TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        //with a good specification, and call it from this one.
        Node n= state.currentNode();
        int coins= n.getTile().coins();
        if (coins > 0) state.grabCoins();
        getBetterPath(state);
        }
        

    /**Calculates the path that gives an optimal number of coins without runing out of steps
     * 
     */
	private void getBetterPath(GetOutState state) {
		Node n= state.currentNode();
		Node e=state.getExit();
		List<Node>path=Paths.minPath(n, e);
        path.remove(0);
        HashMap<Node, Integer> coins=new HashMap<Node, Integer>();
        for(Node c:state.allNodes()) {
        	if(c.getTile().coins()>0) coins.put(c, c.getTile().coins());
        }
        Heap<Node> w=getWeight(n,coins);
        if(w.size()>0) e=w.poll();
        path=getPath(n,e);
        while(pathSize(path)*2+pathSize(getPath(n,state.getExit()))<state.stepsLeft()) {
        	path.remove(0);
        	for(Node p: path) {
        		state.moveTo(p);
        	}
        	n=state.currentNode();coins.remove(e);w=getWeight(n, coins);
        	e=w.poll();path=getPath(n,e);
        }
        path=getPath(state.currentNode(), state.getExit());
        path.remove(0);
        for(Node p: path) {
        	state.moveTo(p);
        }
	}

	
	/**Returns the shortest path between two nodes as a list.  This is accomplished via 
	 * Dijkstra's Shortest Path Algorithm as implemented in A6
	 * Parameter start: the starting node
	 * Parameter n: the ending node
	 */
	private List<Node> getPath(Node start, Node end) {
		// TODO Auto-generated method stub
		return Paths.minPath(start, end);
	}	
	
	/**Returns the size of the path between two nodes as an int, calculated by 
	 * summing the edge weights. This is accomplished by calling a method from A6
	 * Parameter path: the path to be summed
	 * 
	 */
	private int pathSize(List<Node> path) {
		return Paths.pathDistance(path);
	}
	
	/**Assigns each Node a weight based on the value of the coins it contains and distance from 
	 * the starting node; since nodes without coins will always have weight zero, they 
	 * are never looked at.  This weight is returned in the form of a min heap, with the lowest-
	 * weighted node as the root.  Low weights are better.
	 * Parameter n: the starting node
	 * Parameter coins: the HashMap containing the list of Nodes with coins and the values of the
	 * coins
	 */
	private Heap<Node> getWeight(Node n, HashMap<Node, Integer> coins) {
		Set<Node> s = coins.keySet();
		Heap<Node> result=new Heap<Node>();
		for(Node w: s) {
			result.add(w, (double)Paths.minPath(n,w).size()/coins.get(w));
		}
		return result;
	}
}