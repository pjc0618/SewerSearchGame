package student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import game.Edge;
import game.Node;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;





/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class Paths {

    /** Return a list of the nodes on the shortest path from start to 
     * end, or the empty list if a path does not exist.
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
	public static List<Node> minPath(Node v , Node end) {
        /* TODO Read Piazza note Assignment A6 for ALL details. */
        Heap<Node> F= new Heap<Node>(); // As in abstract algorithm in @700.

        // info contains an entry for each node in F or S. Thus, |info| = |F| + |S|.
        // For each such node, the value part in info contains the shortest known
        // distance to the node and the node's backpointer on that shortest path.
        HashMap<Node, SFdata> info= new HashMap<Node, SFdata>();

        F.add(v, 0);
        info.put(v, new SFdata(0, null));
        // inv: See Piazza note Assignment A6 (Fall 2018), 
        //      together with def of F and info
        while (F.size() != 0) {
            Node f= F.poll();
            if (f == end) return constructPath(end, info);
            int fDist= info.get(f).distance;
            
            for (Edge e : f.getExits()) {// for each neighbor w of f
                Node w= e.getOther(f);
                int newWdist= fDist + e.length;
                SFdata wInfo= info.get(w);
                if (wInfo == null) { //if w not in F or S
                    info.put(w, new SFdata(newWdist, f));
                    F.add(w, newWdist);
                } else if (newWdist < wInfo.distance) {
                    wInfo.distance= newWdist;
                    wInfo.backPointer= f;
                    F.changePriority(w, newWdist);
                }
            }
        }

        // no path from v to end
        return new LinkedList<Node>();
    }


    /** Return the path from the start node to node end.
     *  Precondition: nData contains all the necessary information about
     *  the path. */
    public static List<Node> constructPath(Node end, HashMap<Node, SFdata> nData) {
        LinkedList<Node> path= new LinkedList<Node>();
        Node p= end;
        // invariant: All the nodes from p's successor to the end are in
        //            path, in reverse order.
        while (p != null) {
            path.addFirst(p);
            p= nData.get(p).backPointer;
        }
        return path;
    }

    /** Return the sum of the weights of the edges on path path. */
    public static int pathDistance(List<Node> path) {
        if (path.size() == 0) return 0;
        synchronized(path) {
            Iterator<Node> iter= path.iterator();
            Node p= iter.next();  // First node on path
            int s= 0;
            // invariant: s = sum of weights of edges from start to p
            while (iter.hasNext()) {
                Node q= iter.next();
                s= s + p.getEdge(q).length;
                p= q;
            }
            return s;
        }
    }

    /** An instance contains information about a node: the previous node
     *  on a shortest path from the start node to this node and the distance
     *  of this node from the start node. */
    private static class SFdata {
        private Node backPointer; // backpointer on path from start node to this one
        private int distance; // distance from start node to this one

        /** Constructor: an instance with distance d from the start node and
         *  backpointer p.*/
        private SFdata(int d, Node p) {
            distance= d;     // Distance from start node to this one.
            backPointer= p;  // Backpointer on the path (null if start node)
        }

        /** return a representation of this instance. */
        public String toString() {
            return "dist " + distance + ", bckptr " + backPointer;
        }
    }
}
