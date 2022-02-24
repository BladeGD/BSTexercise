import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MainV1 {
	
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
		
		//Create the Binary Symbol Table
		BST<String, Integer> bst = new BST<String, Integer>();

		//Read the file into the string builder
		String[] input = readFile(dictionaryName).split("\\s+");

		//Add all the words to the dictionary
		for (String word : input)
			bst.put(word, 0);
		
		//Read file
		String targetInput[] = readFile(inputFileName).replaceAll("[^a-zA-Z\\s\\-]", " ").split("\\s");

		//Corrected words File
		PrintWriter pw3 = new PrintWriter(new File("./corrected_words_detected.txt"));
		
		//Increase the frequency of each word found
		for (String word : targetInput) {
			BST<String, Integer>.Node node = bst.get(word.toLowerCase());
			if (node != null)
				node.val = (int) node.val + 1;
			else {
				
				//Boolean variable
				boolean rightMatch = false;
				
				//Right pass first, excluding 1 letter words
				for(int i = 0; i < word.length() - 1; i++) {
					node = bst.get(word.substring(i).toLowerCase());
					
					//If it's a match & is "I" or more than 1 char
					if(node != null && 
							(word.charAt(0) == 'I' || word.substring(0, i).length() > 2 ||
									(word.substring(0, i).length() <= 2 && 
									bst.get(word.substring(0, i)) != null))) {
						
						//Print full word
						pw3.print(word + ", ");
						
						//Increase frequency counter
						node.val = (int) node.val + 1;
						
						//If remain is a match
						node = bst.get(word.substring(0, i));
						if(node != null) {
							
							//Print left and right word
							pw3.print(word.substring(0, i) + " ");
							pw3.println(word.substring(i));
							
							//Increase frequency
							node.val = (int) node.val + 1;
						
						} 
						//If remain is not a match
						else {
							//Print right word only
							pw3.println(word.substring(i));
						}
						
						rightMatch = true;
						break;
					}
				}
				
				//No need to do a left pass if we've already done a right pass
				if(rightMatch)
					continue;
				
				//Left pass, skip 1 and 2 letter words
				for(int i = word.length(); i > 2; i--) {
					node = bst.get(word.substring(0, i).toLowerCase());
					
					//If it's a match
					if(node != null && 
							(word.substring(i).length() > 2 || 
									(word.substring(i).length() <= 2 && 
									bst.get(word.substring(i)) != null) )) {
						
						//Print full string and left word
						pw3.println(word + ", " + word.substring(0, i));
						
						//Increase frequency
						node.val = (int) node.val + 1;
						
						break;
					}
				}
			}
		}
		
		//Eliminate words with 0 frequencies and sort by ascending frequencies
		BST<String, Integer>.Node[] bstArr = bstToArray(bst);
		
		//File outputs
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

		pw3.flush();
		pw3.close();
		
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
	private static BST<String, Integer>.Node[] bstToArray(BST<String, Integer> bst) {

		ArrayList<BST<String, Integer>.Node> nodes = new ArrayList<BST<String, Integer>.Node>();

		bst.inorder((BST.Node node) -> {
			if ((int) node.val != 0)
				nodes.add(node);
		});
		
		BST<String, Integer>.Node[] nodeArr = new BST.Node[nodes.size()];
		for(int i = 0 ; i < nodes.size(); i++)
			nodeArr[i] = nodes.get(i);
		
		mergesort(nodeArr, 0, nodeArr.length - 1);

		return nodeArr;
	}

	@SuppressWarnings("unchecked")
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
