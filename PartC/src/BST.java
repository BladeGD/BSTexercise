
public class BST<Key extends Comparable<Key>, value> {

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	public Node root;

	class Node {
		Key key;
		value val;
		Node left, right;
		int n; // Number of nodes in subtree
		boolean color;

		public Node(Key key, value val, int n, boolean color) {
			this.key = key;
			this.val = val;
			this.n = n;
			this.color = color;
		}

		public String toString() {
			return key.toString() + "," + val.toString();
		}

	}

	private int size(Node x) {
		if (x == null)
			return 0;
		else
			return x.n;
	}

	private boolean isRed(Node x) {
		if (x == null)
			return false;
		return x.color == RED;
	}

	private Node rotateLeft(Node h) {
		Node x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = h.color;
		h.color = RED;
		x.n = h.n;
		h.n = 1 + size(h.left) + size(h.right);
		return x;
	}

	private Node rotateRight(Node h) {
		Node x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = h.color;
		h.color = RED;
		x.n = h.n;
		h.n = 1 + size(h.left) + size(h.right);
		return x;
	}

	void flipColors(Node h) {
		h.color = RED;
		h.left.color = BLACK;
		h.right.color = BLACK;
	}

	public void put(Key key, value val) {
		root = put(root, key, val);
		root.color = BLACK;
	}

	private Node put(Node h, Key key, value val) {
		if (h == null)
			return new Node(key, val, 1, RED);

		int cmp = key.compareTo(h.key);
		if (cmp < 0)
			h.left = put(h.left, key, val);
		else if (cmp > 0)
			h.right = put(h.right, key, val);
		else
			h.val = val;

		if (isRed(h.right) && !isRed(h.left))
			h = rotateLeft(h);

		if (isRed(h.left) && isRed(h.left.left))
			h = rotateRight(h);

		if (isRed(h.left) && isRed(h.right))
			flipColors(h);

		h.n = 1 + size(h.left) + size(h.right);
		return h;
	}
	
	private Node search(Node node, Key key) {
		if(node == null)
			return null;
		
		int cmp = key.compareTo(node.key);
		if(cmp == 0)
			return node;
		else if(cmp > 0)
			return search(node.right, key);
		else
			return search(node.left, key);
	}
	
	public Node get(Key key) {
		return search(root, key);
	}
	
	private void print(Node root) {
		if (root.left != null)
			print(root.left);
		
		System.out.println(root);

		if (root.right != null)
			print(root.right);
	}

	public void print() {
		print(root);
	}
	
	private void inorder(Node node, BSTCallback callback) {
		if(node == null)
			return;
		
		inorder(node.left, callback);
		callback.run(node);
		inorder(node.right, callback);
	}
	
	public void inorder(BSTCallback callback) {
		inorder(root, callback);
	}
	
}	
