package com.wavl;

import java.util.TreeMap;

/**
 * WAVLTree
 *
 * An implementation of a WAVL Tree with distinct integer keys and info
 * 
 */
public class Tree {

	private Node sentinel = new Node(0, "sentinel", true, false);
	private Node root = new Node();
	private int treeSize = 0;
	private int rebalanceCounter = 0;
	private Node min = new Node();
	private Node max = new Node();

	public Tree() {
		/**
		 * building an empty tree connects the sentinel to a virtual node
		 **/

		Node virtualRoot = new Node();
		sentinel.right = virtualRoot;
		virtualRoot.parent = sentinel;
		root = virtualRoot;
		treeSize = 0;
	}

	public Tree(Node r) {
		/** building a new tree with a specific root **/

		this.root = r;
		root.parent = sentinel;
		sentinel.right = r;
		root.right = new Node();
		root.right.parent = r;
		root.left = new Node();
		root.left.parent = r;
		if (r.getKey() == -1) {
			treeSize = 0;
		} else {
			treeSize = 1;
		}
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */

	public boolean empty() {
		/**
		 * 1. checks if the size of the tree = 0 2. return the answer
		 **/

		if (treeSize == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */

	public String search(int k) {
		/**
		 * 1. checks if the tree is empty-->returns null 2. else, checks if the
		 * key belongs to the root-->returns the info of the root 3. else if the
		 * root is the only node in the tree--->returns null 4. else if k is
		 * bigger then root.key-->calculates in another function with the root's
		 * right sub tree 5. else, k is smaller then root.ket-->calculates in
		 * another function with the root's left sun tree
		 */

		if (empty()) { // 1
			return null;
		}
		if (root.key == k) { // 2
			return root.info;
		} else if (isLeaf(root)) { // 3
			return null;
		}
		if (k > root.getKey()) { // 4
			return recSearch(k, root.right).getValue();
		}
		return recSearch(k, root.left).getValue(); // 5
	}

	public Node recSearch(int k, Node curRoot) {
		/**
		 * 1. checks if current root is a virtual leaf-->returns the current
		 * root 2. checks if current root is the node we are looking for (by
		 * comparing the keys)-->returns current root 3. else, if k is bigger
		 * then current root-->searching in current root's right sub tree 4.
		 * else, k is smaller then current root-->serching in current root's
		 * lwft sub tree
		 */

		if (curRoot.isVirtualLeaf) { // 1
			return curRoot;
		}
		if (curRoot.key == k) { // 2
			return curRoot;
		} else if (k > curRoot.getKey()) { // 3
			return recSearch(k, curRoot.right); // 3
		}
		return recSearch(k, curRoot.left); // 4
	}

	private void rebalanceInsert(Node node) {
		/**
		 * 1. checks if node's rank difference is (0,1) (1,0) 2. promotes node
		 * rank by 1 3. add 1 to rebalance counter 3. if node's parent is not
		 * the sentinel-->enter the function again with node's parent 4. else,
		 * if node's rank difference is (0,2) 4.1. and node's left son has (1,2)
		 * rank difference-->single rotation right for node 4.1.1. add 1 to
		 * rebalance counter 4.2 and node's left son has (2,1) rank
		 * difference-->double rotation right for node 4.2.1 add 2 to rebalance
		 * counter 5. else, if node's rank difference is (2,0) 5.1 and node's
		 * right son has (2,1) rank difference-->single rotation left for node
		 * 5.1.1 add 1 to rebalance counter 5.2 and node's right son has (1,2)
		 * rank difference-->double rotation left for node 5.2.1 add 2 to
		 * rabalance counter
		 **/

		if (node.is01Node() || node.is10Node()) { // 1
			node.rank++; // 2
			this.rebalanceCounter++; // 3
			if (node.parent.isSentinel == false) { // 3
				rebalanceInsert(node.parent); // 3
			}
		} else if (node.is02Node()) { // 4
			if (node.left.is12Node()) { // 4.1
				singleRotationRightI(node); // 4.1
				this.rebalanceCounter++; // 4.1.1
			} else if (node.left.is21Node()) { // 4.2
				doubleRotationRightI(node); // 4.2
				this.rebalanceCounter = this.rebalanceCounter + 2; // 4.2.1
			}
		} else if (node.is20Node()) { // 5
			if (node.right.is21Node()) { // 5.1
				singleRotationLeftI(node); // 5.1
				this.rebalanceCounter++; // 5.1.1
			} else if (node.right.is12Node()) { // 5.2
				doubleRotationLeftI(node); // 5.2
				this.rebalanceCounter = this.rebalanceCounter + 2; // 5.2.1
			}
		}
	}

	private void rebalanceDelete(Node node) {
		/**
		 * 1. checks if node's rank difference is (2,2) and node is a leaf or
		 * rank dofference is (2,3) or (3,2) 2. demote 3. add 1 to rebalance
		 * counter 4. if node is a leaf-->changes his rank to 0 and count one
		 * more 5. if node's parent is not sentinel-->rebalance for node's
		 * parent 6. if node's rank difference is (3,1) 6.1 and node's right son
		 * has rank difference of (2,2)-->double demote node 6.1.1 add 2 to
		 * rebalance counter 7. if node's parent is not sentinel-->rebalance for
		 * node's parent 6.2 and node's right son has rank difference of (2,1)
		 * or (1,1)-->single rotation left for node 6.2.1 add 1 to rebalance
		 * counter 6.3 and node's right son has rank difference of
		 * (1,2)-->double rotation left for node 6.3.1 add 2 to rebalance
		 * counter 8. if node's rank difference is (1,3) 8.1 and node's left son
		 * has (2,2) rank difference-->double demote node 8.1.1 add 2 to
		 * rwebalance counter 9. if node's parent is not sentinel-->rebalance
		 * for node's parent 8.2 and node's left son has (1,1) or (1,2) rank
		 * difference-->single rotation right for node 8.2.1 add 1 to rebalance
		 * counter 8.3 and node's left son has (2,1) rank difference-->double
		 * rotation right for node 8.3.1 add 2 to rebalance counter 10. if
		 * node's parent is not sentinel-->rebalance for node's parent
		 */

		if ((isLeaf(node) && node.is22Node()) || node.is23Node() || node.is32Node()) { // 1
			demote(node); // 2
			this.rebalanceCounter++; // 3
			if (isLeaf(node)) { // 4
				node.rank = 0; // 4
				this.rebalanceCounter++; // 4
			}
			if (!node.parent.isSentinel) { // 5
				rebalanceDelete(node.parent); // 5
			}
		}
		if (node.is31Node()) { // 6
			if (node.right.is22Node()) { // 6.1
				demote(node.right); // 6.1
				demote(node); // 6.1
				this.rebalanceCounter = this.rebalanceCounter + 2; // 6.1.1
				if (!node.parent.isSentinel) { // 7
					rebalanceDelete(node.parent); // 7
				}
			} else if (node.right.is11Node() || node.right.is21Node()) { // 6.2
				singleRotationLeftD(node); // 6.2
				this.rebalanceCounter++; // 6.2.1
			} else if (node.right.is12Node()) { // 6.3
				doubleRotationLeftD(node); // 6.3
				this.rebalanceCounter = this.rebalanceCounter + 2; // 6.3.1
			}
		}
		if (node.is13Node()) { // 8
			if (node.left.is22Node()) { // 8.1
				demote(node); // 8.1
				demote(node.left); // 8.1
				this.rebalanceCounter = this.rebalanceCounter + 2; // 8.1.1
				if (!node.parent.isSentinel) { // 9
					rebalanceDelete(node.parent); // 9
				}
			} else if (node.left.is11Node() || node.left.is12Node()) { // 8.2
				singleRotationRightD(node); // 8.2
				this.rebalanceCounter++; // 8.2.1
			} else if (node.left.is21Node()) { // 8.3
				doubleRotationRightD(node); // 8.3
				this.rebalanceCounter = this.rebalanceCounter + 2; // 8.3.1
			}
		}
		if (!node.parent.isSentinel) // 10
			rebalanceDelete(node.parent); // 10
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the WAVL tree. the tree must
	 * remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were necessary. returns -1
	 * if an item with key k already exists in the tree.
	 */

	public int insert(int k, String i) {
		/**
		 * 1. checks if tree is empty 2. inserts as root 3.connects sentinel
		 * with root 4. update min and max to be the root 5. if not empty search
		 * for place to insert the new node 6.if key exists returns -1 7.if key
		 * doesn't exist changes virtual leaf to leaf 8. update min and max if
		 * needed 9.rebalance 10.returns counter of rebalancing
		 **/

		if (empty()) { // 1
			root = new Node(k, i); // 2
			root.parent = sentinel; // 3
			sentinel.right = root; // 3
			root.right = new Node(); // 2
			root.right.parent = root; // 2
			root.left = new Node(); // 2
			root.left.parent = root; // 2
			root.size = 1;
			treeSize = 1;
			root.rank = 0;
			this.min = root; // 4
			this.max = root; // 4
			return this.rebalanceCounter;
		}
		Node inNode = recSearch(k, root); // 5
		if (inNode.getKey() == k) { // 6
			return -1; // 6
		} else {
			virtualLeafToLeaf(inNode, k, i); // 7
			treeSize++;
			if (inNode.key < this.min.key) { // 8
				this.min = inNode;
			}
			if (inNode.key > this.max.key) { // 8
				this.max = inNode;
			}
		}
		rebalanceInsert(inNode.parent); // 9
		int temp = this.rebalanceCounter; // 10
		this.rebalanceCounter = 0; // 10
		return temp; // 10
	}

	private void PlusSize(Node node) {
		/** increases size by 1, and enter the function with node's parent **/

		if (!node.isSentinel) {
			node.size++;
			PlusSize(node.parent);
		}
	}

	private void MinusSize(Node x) {
		/** decreases size by 1, and enter the function with node's parent */

		if (x.isSentinel == false) {
			x.size--;
			MinusSize(x.parent);
		}
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of
	 * rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */

	public int delete(int k) {
		/**
		 * 1. checks if the tree is empty-->return -1 2. else, search for the
		 * node we want to delete 3. if delNode is a virtual leaf or it doents
		 * exist-->return -1 4. update min and max if needed 5. if delNode is a
		 * leaf-->connects delNodeParent to a new virtual leaf (son) 5.1 update
		 * size 6. else, if delNode is an unary node-->connects delNodeParent to
		 * delNode's existing son 6.1 update size 7. if delNode is a binary
		 * node-->switch delNode with it's predecessor 8. if delNodeParent is
		 * not the sentinel-->rebalance delNodeParent 9. if we deleted the root,
		 * update root 10. tree size -1 11. return and initial rebalance counter
		 **/

		if (this.empty()) { // 1
			return -1;
		}
		Node delNode = recSearch(k, root); // 2
		if (delNode == null || delNode.key == -1) { // 3
			return -1;
		}
		if (delNode.key == this.min.key) { // 4
			this.min = Successor(delNode);
			if (this.min == null) {
				this.min = new Node();
			}
		}
		if (delNode.key == this.max.key) { // 4
			this.max = Predecessor(delNode);
			if (this.max == null) {
				this.max = new Node();
			}
		}
		boolean bool = isRightSon(delNode);
		Node delNodeParent = delNode.parent;
		if (isLeaf(delNode)) { // 5
			Node son = new Node();
			son.parent = delNodeParent;
			if (isRightSon(delNode)) {
				delNodeParent.right = son;
			} else {
				delNodeParent.left = son;
			}
			MinusSize(delNodeParent); // 5.1
		} else if (delNode.right.isVirtualLeaf) { // 6
			delNode.left.parent = delNodeParent;
			if (isRightSon(delNode)) {
				delNodeParent.right = delNode.left;
			} else {
				delNodeParent.left = delNode.left;
			}
			MinusSize(delNodeParent); // 6.1
		} else if (delNode.left.isVirtualLeaf) { // 6
			delNode.right.parent = delNodeParent;
			if (isRightSon(delNode)) {
				delNodeParent.right = delNode.right;
			} else {
				delNodeParent.left = delNode.right;
			}
			MinusSize(delNodeParent); // 6.1
		} else { // 7
			switchToPredecessor(delNode);
			if (bool) {
				delNodeParent = delNodeParent.right;
			} else {
				delNodeParent = delNodeParent.left;
			}
		}
		if (!delNodeParent.isSentinel) { // 8
			rebalanceDelete(delNodeParent);
		}
		if (root.getKey() == k) { // 9
			root = sentinel.right;
		}
		treeSize--; // 10
		int temp = this.rebalanceCounter; // 11
		rebalanceCounter = 0;
		return temp;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null
	 * if the tree is empty
	 */

	public String min() {
		/** returns the value of the minimum node **/

		if (empty()) {
			return null;
		}
		return this.min.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if
	 * the tree is empty
	 */

	public String max() {
		/** returns the value of the maximum node **/

		if (empty()) {
			return null;
		}
		return this.max.getValue();
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty
	 * array if the tree is empty.
	 */

	public int[] keysToArray() {
		/**
		 * 1. checks if the tree is empty-->returns an empty array 2. else,
		 * initial a new array in the size of the tree 3. insert min in index 0
		 * 4. insert the successor in idex i
		 **/

		if (empty()) { // 1
			int[] arr = new int[0];
			return arr;
		}
		Node nextMin = this.min;
		int[] arr = new int[treeSize]; // 2
		arr[0] = nextMin.getKey(); // 3
		for (int i = 1; i < treeSize; i++) { // 4
			nextMin = Successor(nextMin);
			arr[i] = nextMin.getKey();
		}
		return arr;
	}

	/** LocalMin */

	private Node localMin(Node node) {
		/**
		 * returns the min in node sub tree 1. not a leaf=>not the min-->keep
		 * going left
		 **/

		Node curRoot = node;
		while (!curRoot.left.isVirtualLeaf) { // 1
			curRoot = curRoot.left;
		}
		return curRoot;
	}

	/** LocalMax */

	private Node localMax(Node node) {
		/**
		 * returns the max in node sub tree 1. if not a leaf=>not max-->keep
		 * going right
		 **/

		Node curMax = node;
		while (!curMax.right.isVirtualLeaf) { // 1
			curMax = curMax.right;
		}
		return curMax;
	}

	/** finds node's Successor **/

	public Node Successor(Node node) {
		/**
		 * returns the successor of node 1. checks if right son exists 2.
		 * returns the minimum in the right subtree (the successor) 3. goes up
		 * the tree as long as nodeSucc is not sentinel and node is a right son
		 * 4. if doesn't have successor returns null 5. has a
		 * successor-->returns it
		 **/

		if (!node.right.isVirtualLeaf) { // 1
			return localMin(node.right); // 2
		}
		Node nodeSucc = node.parent;
		while (nodeSucc != sentinel && node == nodeSucc.right) {// 3
			node = nodeSucc;
			nodeSucc = node.parent;
		}
		if (nodeSucc == sentinel) { // 4
			return null;
		} else {
			return nodeSucc; // 5
		}
	}

	public Node Predecessor(Node node) {
		/**
		 * returns the node's predecessor 1.checks if left son exists 2. returns
		 * the maximum in the left subtree (the predecessor) 3. goes up the tree
		 * as long as nodePred is not sentinel and node is a left son 4. if
		 * doesn't have predecessor returns null 5. has a predecessor-->returns
		 * it
		 **/

		if (!node.left.isVirtualLeaf) { // 1
			return localMax(node.left);// 2
		}
		Node nodePred = node.parent;
		while (nodePred != sentinel && node == nodePred.left) { // 3
			node = nodePred;
			nodePred = node.parent;
		}
		if (nodePred == sentinel) { // 4
			return null;
		} else { // 5
			return nodePred;
		}
	}

	private void switchToPredecessor(Node node) {
		/***
		 * enter this function only if node is a right son and is not a root
		 * puts the predecessor instead of the node we want to delete and delete
		 * the node as a leaf
		 * 
		 * 1. find node's predecessor 2. if the predecessor is not node's left
		 * son-->replace and delete 3. else-->replace differently and delete 4.
		 * update sub tree rank if needed (before rebalance) 5. decreases the
		 * sizes if needed 6. if predParent is not a sentinel-->rebalance
		 **/

		Node pred = Predecessor(node); // 1
		Node predParent = pred.parent;
		Node nodeParent = node.parent;
		Node predLeftSon = pred.left;
		if (node.left.key != pred.key) { // 2
			pred.left.parent = pred.parent;
			pred.parent.right = pred.left;
			pred.left = node.left;
			pred.left.parent = pred;
		} else {
			predParent = pred;
		}
		pred.parent = nodeParent;
		if (isRightSon(node)) { // 3
			nodeParent.right = pred;
		} else {
			nodeParent.left = pred;
		}
		pred.right = node.right;
		pred.right.parent = pred;
		pred.rank = node.rank;
		pred.size = node.size;
		subTreeRank(pred); // 4
		MinusSize(predLeftSon.parent); // 5
		if (!predParent.isSentinel) { // 6
			rebalanceDelete(predParent);
		}
	}

	private int subTreeRank(Node node) {
		/**
		 * 1. if node is not a virtual leaf 1.1 and if node has rank difference
		 * of (2,2)-->return node's rank 2. save node's rank before changing it
		 * 3. updating node's rank 4. if the rank does changed-->increase
		 * counter by 1 (promote/demote) 5. if node is a virtual leaf-->rank=-1
		 **/

		if (node.isRealNode()) { // 1
			if (node.is22Node() && !isLeaf(node)) { // 1.1
				return node.rank;
			}
			int oldRank = node.rank; // 2
			node.rank = Math.max(node.right.rank, subTreeRank(node.left)) + 1; // 3
			if (node.rank != oldRank) { // 4
				rebalanceCounter++;
			}
		} else { // 5
			return -1;
		}
		return node.rank;
	}

	/**
	 * public String[] infoToArray() Returns an array which contains all info in
	 * the tree, sorted by their respective keys, or an empty array if the tree
	 * is empty.
	 */

	public String[] infoToArray() {
		/**
		 * 1. if the tree is empty-->returns an empty array 2. else, create an
		 * array of the tree size 3. insert the min node to index 0 in the array
		 * 4. insert the successor of the current node
		 **/

		if (empty()) { // 1
			String[] arr = new String[0];
			return arr;
		}
		Node curNode = this.min;
		String[] arr = new String[treeSize]; // 2
		arr[0] = curNode.getValue(); // 3
		for (int i = 1; i < treeSize; i++) { // 4
			curNode = Successor(curNode);
			arr[i] = curNode.getValue();
		}
		return arr;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */

	public int size() {
		return treeSize;
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root WAVL node, or null if the tree is empty
	 *
	 * precondition: none postcondition: none
	 */

	public IWAVLNode getRoot() {

		if (empty()) {
			return null;
		}
		return root;
	}

	/**
	 * public int select(int i)
	 *
	 * Returns the value of the i'th smallest key (return -1 if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key
	 * Example 2: select(size()) returns the value of the node with maximal key
	 * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the
	 * value of the node minimal node's successor
	 *
	 * precondition: size() >= i > 0 postcondition: none
	 */

	public String select(int i) {
		/**
		 * 1. if we dont have a node with i nodes smaller (by keys)-->returns
		 * "-1" 2. if i isnt legal-->returns "-1" 3. else, this node
		 * exists-->find is in recSelect
		 **/

		if (this.empty() || i >= treeSize) { // 1
			return "-1";
		}
		if (i < 0) { // 2
			return "-1";
		}
		return recSelect(root, i); // 3
	}

	private String recSelect(Node curRoot, int i) {
		/**
		 * 1. saving sub tree size of the left son of the current root 2. if it
		 * equals to i-->returns the value of current root 3. else, if if bigger
		 * than i-->enter the function again with left sub tree 4. else, if it
		 * smaller than i-->enter the function again with right sub tree
		 **/

		int leftSize = curRoot.left.getSubtreeSize(); // 1
		if (i == leftSize) { // 2
			return curRoot.getValue();
		} else if (i < leftSize) { // 3
			return recSelect(curRoot.left, i);
		} else
			return recSelect(curRoot.right, i - leftSize - 1); // 4
	}

	/**
	 * public interface IWAVLNode ! Do not delete or modify this - otherwise all
	 * tests will fail !
	 */

	public interface IWAVLNode {

		public int getKey(); // returns node's key (for virtuval node return -1)

		public String getValue(); // returns node's value [info] (for virtuval
									// node return null)

		public IWAVLNode getLeft(); // returns left child (if there is no left
									// child return null)

		public IWAVLNode getRight(); // returns right child (if there is no
										// right child return null)

		public boolean isRealNode(); // Returns True if this is a non-virtual
										// WAVL node (i.e not a virtual leaf or
										// a sentinal)

		public int getSubtreeSize(); // Returns the number of real nodes in this
										// node's subtree (Should be implemented
										// in O(1))
	}

	/**
	 * public class WAVLNode If you wish to implement classes other than
	 * WAVLTree (for example WAVLNode), do it in this file, not in another file.
	 * This class can and must be modified. (It must implement IWAVLNode)
	 */

	public class Node implements IWAVLNode {

		private String info = null;
		private int key = -1;
		private Node left = null;
		private Node right = null;
		private Node parent = null;
		private int rank = -1;
		private boolean isSentinel = false;
		private boolean isVirtualLeaf = false;
		private int size = 0;

		public Node() {
			/** virtual leaf builder **/

			rank = -1;
			isVirtualLeaf = true;
			this.size = 0;
		}

		public Node(int key, String info) {
			/**
			 * a node builder 1. if we build a virtual leaf
			 **/

			this.key = key;
			if (key == -1) { // 1
				isVirtualLeaf = true;
			}
			this.info = info;
		}

		public Node(int key, String info, boolean isSentinel, boolean isVirtualLeaf) {
			/** a node builder that we used to build the sentinel **/

			this.key = key;
			this.info = info;
			this.isSentinel = isSentinel;
			this.isVirtualLeaf = isVirtualLeaf;
			if (isVirtualLeaf == true) {
				rank = -1;
			}
			if (this.isSentinel) {
				this.left = this;
				this.parent = this;
				this.rank = 8888888;
			}
		}

		public int getRank() {
			/** rank getter **/

			return this.rank;
		}

		public int getKey() {
			/** key getter **/

			if (this.isVirtualLeaf == true) {
				return -1;
			}
			return this.key;
		}

		public String getValue() {
			/** info getter **/

			if (this.isVirtualLeaf == true) {
				return null;
			}
			return this.info;
		}

		public IWAVLNode getLeft() {
			/** left son getter **/

			if (this.isVirtualLeaf == true) {
				return null;
			}
			return this.left;
		}

		public IWAVLNode getParent() {
			/** parent getter **/

			return this.parent;
		}

		public IWAVLNode getRight() {
			/** right son getter **/

			if (this.isVirtualLeaf == true) {
				return null;
			}
			return this.right;
		}

		public boolean isRealNode() {
			/** returns true if the node is not a virtual leaf **/

			if (this.isVirtualLeaf == true) {
				return false;
			}
			return true;
		}

		public int getSubtreeSize() {
			/** sub tree size getter **/

			if (this.isRealNode()) {
				return size;
			} else
				return 0;
		}

		private boolean is10Node() {
			/** returns true if the rank difference is (1,0) **/

			if ((this.rank - this.right.rank) == 0 && (this.rank - this.left.rank) == 1) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is01Node() {
			/** returns true if the rank difference is (0,1) **/

			if ((this.rank - this.right.rank) == 1 && (this.rank - this.left.rank) == 0) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is11Node() {
			/** returns true if the rank difference is (1,1) **/

			if ((this.rank - this.right.rank) == 1 && (this.rank - this.left.rank) == 1) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is12Node() {
			/** returns true if the rank difference is (1,2) **/

			if ((this.rank - this.right.rank) == 2 && (this.rank - this.left.rank) == 1) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is21Node() {
			/** returns true if the rank difference is (2,1) **/

			if ((this.rank - this.right.rank) == 1 && (this.rank - this.left.rank) == 2) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is02Node() {
			/** returns true if the rank difference is (2,2) **/

			if ((this.rank - this.right.rank) == 2 && (this.rank - this.left.rank) == 0) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is20Node() {
			/** returns true if the rank difference is (2,0) **/

			if ((this.rank - this.right.rank) == 0 && (this.rank - this.left.rank) == 2) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is13Node() {
			/** returns true if the rank difference is (1,3) **/

			if ((this.rank - this.right.rank) == 3 && (this.rank - this.left.rank) == 1) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is31Node() {
			/** returns true if the rank difference is (3,1) **/

			if ((this.rank - this.right.rank) == 1 && (this.rank - this.left.rank) == 3) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is32Node() {
			/** returns true if the rank difference is (3,2) **/

			if ((this.rank - this.right.rank) == 2 && (this.rank - this.left.rank) == 3) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is23Node() {
			/** returns true if the rank difference is (2,3) **/

			if ((this.rank - this.right.rank) == 3 && (this.rank - this.left.rank) == 2) {
				return true;
			} else {
				return false;
			}
		}

		private boolean is22Node() {
			/** returns true if the rank difference is (2,2) **/

			if ((this.rank - this.right.rank) == 2 && (this.rank - this.left.rank) == 2) {
				return true;
			} else {
				return false;
			}
		}
	}

	private void virtualLeafToLeaf(Node node, int k, String info) {
		/**
		 * 1. change the field that it will no longer be a virtual leaf 2.
		 * creates 2 virtual leaves as the node's sons 3. update the size of
		 * nodes in the tree if needed 4. send the parent to rebalance if needed
		 **/

		node.key = k;
		node.info = info;
		node.isVirtualLeaf = false; // 1
		node.rank = 0;
		node.right = new Node(); // 2
		node.right.parent = node;
		node.left = new Node();
		node.left.parent = node;
		PlusSize(node); // 3
		rebalanceInsert(node.parent); // 4
	}

	private void singleRotationRight(Node curRoot) {
		/**
		 * 1. right son of the left son of the current root moves to be the left
		 * son of current root 2. insert current root as his right son of his
		 * left son 3. create a new current root 4. we connected current root's
		 * left son to the current root's ancestor 5. checks if the the root's
		 * former father is the sentinel and then changes x1 to be the root
		 **/

		boolean bool = isRightSon(curRoot);
		Node curLeft = curRoot.left;
		Node ancestor = curRoot.parent;
		curLeft.right.parent = curRoot; // 1
		curRoot.left = curLeft.right; // 1
		curLeft.right = curRoot; // 2
		curRoot.parent = curLeft; // 2
		curLeft.parent = ancestor; // 3 + 4
		if (ancestor.isSentinel) { // 5
			root = curLeft;
			ancestor.right = curLeft;
		} else if (bool) { // 4
			ancestor.right = curLeft;
		} else { // 4
			ancestor.left = curLeft;
		}
	}

	private void singleRotationRightD(Node curRoot) {
		/**
		 * 1. saves the sizes of the old positions that are needed 2. send
		 * curRoot to single rotation 3. updates the sizes and ranks 4. if we
		 * created a leaf with rank difference of(2,2)-->rebalance it
		 **/

		Node leftSon = curRoot.left;
		int curRootOldSize = curRoot.size; // 1
		int leftLeftSize = leftSon.left.size; // 1
		singleRotationRight(curRoot); // 2
		curRoot.rank--; // 3
		curRoot.parent.rank++; // 3
		leftSon.size = curRootOldSize; // 3
		curRoot.size = curRoot.size - 1 - leftLeftSize; // 3
		if (curRoot.is22Node() && isLeaf(curRoot)) { // 4
			rebalanceDelete(curRoot);
		}
	}

	private void singleRotationRightI(Node curRoot) {
		/**
		 * 1. save the needed nodes 2. send current root to single rotation 3.
		 * updating sizes and ranks if needed
		 **/

		Node leftSon = curRoot.left; // 1
		Node leftLeftSon = leftSon.left; // 1
		Node rightSon = curRoot.right; // 1
		singleRotationRight(curRoot); // 2
		curRoot.rank--; // 3
		curRoot.size = curRoot.size - 1 - leftLeftSon.size; // 3
		leftSon.size = leftSon.size + rightSon.size + 1; // 3
	}

	private void doubleRotationRight(Node curRoot) {
		/**
		 * 1. rotate left the current root's left son 2. rotate right the
		 * current root
		 **/

		Node leftSon = curRoot.left;
		singleRotationLeft(leftSon);
		singleRotationRight(curRoot);
	}

	private void doubleRotationRightI(Node curRoot) {
		/**
		 * 1. save the sizes of old positions that are needed 2. send current
		 * root to double rotation 3. update ranks and sizes
		 **/

		Node leftSon = curRoot.left;
		Node leftRightSon = leftSon.right;
		int leftSonOldSize = leftSon.size; // 1
		int curRootOldSize = curRoot.size; // 1
		int leftRightRightSize = leftRightSon.right.size; // 1
		doubleRotationRight(curRoot); // 2
		leftSon.parent.rank++; // 3
		leftSon.rank--; // 3
		curRoot.rank--; // 3
		leftRightSon.size = curRootOldSize; // 3
		leftSon.size = leftSonOldSize - leftRightRightSize - 1; // 3
		curRoot.size = curRootOldSize - leftSonOldSize + leftRightRightSize; // 3
	}

	private void doubleRotationRightD(Node curRoot) {
		/**
		 * 1. saves sizes of old positions that are needed 2. send current root
		 * to double rotation right 3. updating ranks and sizes
		 **/

		Node leftSon = curRoot.left;
		Node leftRightSon = leftSon.right;
		Node lrrSon = leftRightSon.right;
		int curRootOldSize = curRoot.size; // 1
		int lrrSonOldSize = lrrSon.size; // 1
		int leftSonOldSize = leftSon.size; // 1
		doubleRotationRight(curRoot); // 2
		leftSon.rank--; // 3
		curRoot.rank = curRoot.rank - 2; // 3
		curRoot.parent.rank = curRoot.parent.rank + 2; // 3
		leftRightSon.size = curRootOldSize; // 3
		curRoot.size = curRootOldSize - leftSonOldSize + lrrSonOldSize; // 3
		leftSon.size = leftSon.size - 1 - lrrSonOldSize; // 3
	}

	private void singleRotationLeft(Node curRoot) {
		/**
		 * 1. left son of the left son of the current root moves to be the right
		 * son of current root 2. insert current root as his left son of his
		 * right son 3. create a new current root 4. we connected current root's
		 * left son to the current root's ancestor 5. checks if the the root's
		 * former father is the sentinel and then changes left son to be the
		 * root
		 **/

		boolean bool = isRightSon(curRoot);
		Node rightSon = curRoot.right;
		Node ancestor = curRoot.parent;
		rightSon.left.parent = curRoot; // 1
		curRoot.right = rightSon.left; // 1
		rightSon.left = curRoot; // 2
		curRoot.parent = rightSon; // 2
		rightSon.parent = ancestor; // 3
		if (ancestor.isSentinel) { // 4
			root = rightSon;
			ancestor.right = rightSon;
		} else if (bool) { // 3
			ancestor.right = rightSon;
		} else { // 3
			ancestor.left = rightSon;
		}
	}

	private void singleRotationLeftI(Node curRoot) {
		/**
		 * 1. saves the sizes of older positions that are needed 2. send current
		 * root to single rotation left 3. updating ranks and sizes
		 **/

		Node rightSon = curRoot.right;
		Node rightRightSon = rightSon.right;
		int curRootOldSize = curRoot.size; // 1
		int rightRightOldSize = rightRightSon.size; // 1
		singleRotationLeft(curRoot); // 2
		curRoot.rank--; // 3
		curRoot.size = curRootOldSize - 1 - rightRightOldSize; // 3
		rightSon.size = curRootOldSize; // 3
	}

	private void singleRotationLeftD(Node curRoot) {
		/**
		 * 1. saves size of old positions that are needed 2. send current root
		 * to single rotation left 3. updating the ranks and sizes 4. if we
		 * created a leaf with rank difference of (2,2)-->rebalance it
		 **/

		Node rightSon = curRoot.right;
		int curRootOldSize = curRoot.size; // 1
		int rightRightOldSize = rightSon.right.size; // 1
		singleRotationLeft(curRoot); // 2
		curRoot.rank--; // 3
		curRoot.parent.rank++; // 3
		rightSon.size = curRootOldSize; // 3
		curRoot.size = curRootOldSize - 1 - rightRightOldSize; // 3
		if (curRoot.is22Node() && isLeaf(curRoot)) { // 4
			rebalanceDelete(curRoot);
		}
	}

	private boolean isRightSon(Node node) {
		/** returns true id node is the right son of its parent **/

		if (node.parent.right.getKey() == node.getKey()) {
			return true;
		}
		return false;
	}

	private void doubleRotationLeft(Node curRoot) {
		/**
		 * 1. saves old positions sizes that are needed 2. send current root's
		 * right son to single rotation right 3. send current root to single
		 * rotation left 3. updating sizes
		 **/

		Node rightSon = curRoot.right;
		Node rightLeftSon = rightSon.left;
		int rightOldSize = rightSon.size; // 1
		int curRootOldSize = curRoot.size; // 1
		int rllOldSize = rightLeftSon.left.size; // 1
		singleRotationRight(rightSon); // 2
		singleRotationLeft(curRoot); // 3
		rightLeftSon.size = curRootOldSize; // 4
		rightSon.size = rightOldSize - rllOldSize - 1; // 4
		curRoot.size = curRootOldSize - rightOldSize + rllOldSize; // 4
	}

	private void doubleRotationLeftI(Node curRoot) {
		/**
		 * 1. send current root to double rotation left 2. updating ranks
		 **/

		Node rightSon = curRoot.right;
		doubleRotationLeft(curRoot); // 1
		rightSon.parent.rank++; // 2
		rightSon.rank--; // 2
		curRoot.rank--; // 2
	}

	private void doubleRotationLeftD(Node curRoot) {
		/**
		 * 1. saves old positions' sizes that are needed 1.1 checks if current
		 * root's right left son is a real node 2. send current root to double
		 * rotation left 3. updating ranks and sizes
		 **/

		Node rightSon = curRoot.right;
		Node rightLeftSon = rightSon.left;
		int curRootOldSize = curRoot.size; // 1
		int rightOldSize = rightSon.size; // 1
		int lllOldSize = 0; // 1
		if (rightLeftSon.isRealNode()) { // 1.1
			lllOldSize = rightLeftSon.left.size;
		}
		doubleRotationLeft(curRoot); // 2
		curRoot.rank = curRoot.rank - 2; // 3
		curRoot.parent.rank = curRoot.parent.rank + 2; // 3
		curRoot.parent.right.rank--; // 3
		rightLeftSon.size = curRootOldSize; // 3
		curRoot.size = curRootOldSize - rightOldSize + lllOldSize; // 3
		rightSon.size = rightOldSize - 1 - lllOldSize; // 3
	}

	private void demote(Node node) {
		/** decreases the rank by 1 **/

		node.rank--;
	}

	private boolean isLeaf(Node node) {
		/** returns true if node is a leaf **/

		if (node.right.isVirtualLeaf && node.left.isVirtualLeaf) {
			return true;
		} else {
			return false;
		}
	}

}
