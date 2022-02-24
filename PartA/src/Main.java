import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	@SuppressWarnings({ "rawtypes", "resource" })
	public static void main(String[] args) throws IOException {
		
		Scanner user = new Scanner(System.in);
		String dictionaryName, inputFileName;
		
		//Take File Name from user
		System.out.println("Enter the name of the dictionnary file (.txt): ");
		dictionaryName = user.nextLine().trim();
		System.out.println("Enter the name of the input file (.txt): ");
		inputFileName = user.nextLine().trim();
		
		//Timer for Scaling
		long now = System.nanoTime();
		
		//Create the Binary Search Tree
		BST<String, Integer> bst = new BST<String, Integer>();

		//Read the file into the string builder
		String[] input = readFile(dictionaryName).split("\\s+");

		//Add all the words to the dictionary
		for (String word : input)
			bst.put(word.toLowerCase(), 0);

		//Read file
		String targetInput[] = readFile(inputFileName).replaceAll("[^a-zA-Z\\s\\-]", " ").split("\\s");

		//Increase the frequency counter of each word found
		for (String word : targetInput) {
			BST<String, Integer>.Node node = bst.get(word.toLowerCase());
			if (node != null)
				node.val = (int) node.val + 1;
		}
		
		//Eliminate words with 0 frequencies and sort by ascending frequencies
		BST<String, Integer>.Node[] bstArr = bstToArray(bst);
		
		//Create File outputs
		PrintWriter pw = new PrintWriter(new File("./frequencies.txt"));
		PrintWriter pw2 = new PrintWriter(new File("./repeated.txt"));
		
		//Keep for Print to frequencies.txt
		for(BST<String, Integer>.Node n: bstArr)
			pw.println(n);
		
		//Keep for Print to repeated.txt
		bst.inorder((BST.Node node) -> {
			if((int) node.val > 0) {
				for(int i = 0; i < (int) node.val; i++)
					pw2.print(node.key + " ");
			}
		});
		
		//Write to file and close streams
		pw.flush();
		pw.close();
		
		pw2.flush();
		pw2.close();
		
		System.out.println("Program Complete.");
		//Print out time
		System.out.println("Execution Time: " + (System.nanoTime()-now) + "nanoSeconds");
	}

	private static String readFile(String name) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
			StringBuilder sb = new StringBuilder();
			String line = "";

			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();

			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	//Eliminate 0 freq word, put rest into a sorted Array
	private static BST<String, Integer>.Node[] bstToArray(BST<String, Integer> bst) {

		ArrayList<BST<String, Integer>.Node> nodes = new ArrayList<BST<String, Integer>.Node>();

		//Check by Inorder and add to array is != 0 frequency
		bst.inorder((BST.Node node) -> {
			if ((int) node.val != 0)
				nodes.add(node);
		});
		
		BST<String, Integer>.Node[] nodeArr = new BST.Node[nodes.size()];
		for(int i = 0 ; i < nodes.size(); i++)
			nodeArr[i] = nodes.get(i);
		
		//Sort array in order of frequency
		mergesort(nodeArr, 0, nodeArr.length - 1);

		return nodeArr;
	}

	@SuppressWarnings("unchecked")
	//Splitting part of merge sort
	private static void merge(BST<String, Integer>.Node arr[], int l, int m, int r) {
		//Find sizes of two subarrays to be merged
		int n1 = m - l + 1;
		int n2 = r - m;

		//Create temp arrays
		BST<String, Integer>.Node L[] = new BST.Node[n1];
		BST<String, Integer>.Node R[] = new BST.Node[n2];

		//Copy data over to temp arrays
		for (int i = 0; i < n1; ++i)
			L[i] = arr[l + i];
		
		for (int j = 0; j < n2; ++j)
			R[j] = arr[m + 1 + j];

		//Initial indices of temp arrays
		int i = 0, j = 0;

		//Initial index of merged array
		int k = l;
		while (i < n1 && j < n2) {
			if (L[i].val.compareTo(R[j].val) <= 0) {
				arr[k] = L[i];
				i++;
			} else {
				arr[k] = R[j];
				j++;
			}
			k++;
		}

		//Copy rest of L
		while (i < n1) {
			arr[k] = L[i];
			i++;
			k++;
		}

		//Copy rest of R
		while (j < n2) {
			arr[k] = R[j];
			j++;
			k++;
		}
	}

	//Merging part of merge sort
	private static void mergesort(BST<String, Integer>.Node arr[], int l, int r) {
		if (l < r) {
			//Middle point
			int m = (l + r) / 2;

			//Sort first and second halves
			mergesort(arr, l, m);
			mergesort(arr, m + 1, r);

			//Merge the sorted halves
			merge(arr, l, m, r);
		}
	}

}
