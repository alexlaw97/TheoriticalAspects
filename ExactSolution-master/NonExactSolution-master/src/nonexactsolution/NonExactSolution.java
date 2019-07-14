package nonexactsolution;

/**
 * @author Ramitaa Loganathan
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NonExactSolution {

    private static Graph graph;
    private static Graph temporaryPotentialCliqueGraph = new Graph();
    private static ArrayList<Node> newTempList = new ArrayList<>();
    private static ArrayList edgesToCheck = new ArrayList<>();
    private static ArrayList newEdges = new ArrayList<>();
    
    /**
     * Main function
     * @param args 
     */
    public static void main(String[] args) {
            
        //Using user's input 
        int array[] = getUserInput();
        graph = new Graph(array[0], array[1]);
        
        generateProblemInstance();

        long start = System.nanoTime();
        generateSolution();
        long finish = System.nanoTime();


        System.out.println("Time elapsed: " + (finish-start)/1000000.00 + " seconds");       
                
    }
    
    //------------------------------------------------------------------------------------------------------
    // Main Functions
    //------------------------------------------------------------------------------------------------------
    
    /**
     *  Generate and print graph details based on user input
     */
    private static void generateProblemInstance() 
    {
        System.out.print("\n");
        printSpecialLine(50, "-");
        System.out.print("\n                   GRAPH DETAILS\n");
        printSpecialLine(50, "-");
                
        // Details of the graph is printed out
        System.out.println(graph.toString());
        
        printSpecialLine(50, "-");
        System.out.print("\n                    NODE DETAILS\n");
        printSpecialLine(50, "-");
        System.out.print("\n");
        
        graph.printNodeDetails();
    }

    /**
     * Carry out Step 1 - 4 as shown below to determine if a clique exists
     * @return true if clique exists and vice versa
     */
    private static boolean generateSolution() 
    {
        System.out.print("\n");
        printSpecialLine(50, "-");
        System.out.print("\n                SOLUTION DETAILS\n");
        printSpecialLine(50, "-");
             
         //Step 1: Check if graph has minimum edges to form k-clique
        if (!minimumEdgesExists())
            return false;
        
        // Step 2: Check if graph has minimum number of nodes with minimum number of edges
        if(!minimumNodesWithMinimumEdgesExists())
            return false;
        
        // Step 3: Check if the top 10 edges can form a clique by randomizing the node 100 times
        return cliqueExists();
               
    }
    
    /**
     * Check whether the minimum number of edges for a k-clique to form exists or not
     * @return true if minimum number of edges exist in the graph
     */
    private static boolean minimumEdgesExists()
    {
        int min = (graph.getCliqueSize() * (graph.getCliqueSize() - 1)) / 2;
        
        if (graph.getEdgesTable().length >= min)
        {
            System.out.format("\nStage 1: Pass. %d edge(s) are suffient to form a %d-clique.\n", 
                    graph.getEdgesTable().length, graph.getCliqueSize());
            return true;
        }
        
        else
        {
            System.out.format("\nStage 1: Fail. %d edge(s) are not suffient to form a %d-clique. At least %d edges are required.\n", 
                    graph.getEdgesTable().length, graph.getCliqueSize(), min);
            return false;
        }
    }
    
    /**
     * Check whether there are enough nodes with minimum edges to form clique exists.
     * @return true if there a minimum number of nodes with required edges and vice versa
     */
    private static boolean minimumNodesWithMinimumEdgesExists()
    {
        int count = 0;
        ArrayList<Node> nodeList = graph.getNodeList();
        
        count = nodeList.stream().filter((n) -> (n.getEdgeCount() >= (graph.getCliqueSize() - 1))).map((_item) -> 1).reduce(count, Integer::sum);
        
        if (count >= graph.getCliqueSize())
        {
            System.out.format("Stage 2: Pass. %d node(s) with at least %d edges are sufficient to form %d-clique.\n", 
                    count, graph.getCliqueSize() - 1, graph.getCliqueSize());
            return true;
        }
        
        else
        {
            System.out.format("Stage 2: Fail. %d node(s) with %d edges are not sufficient to form %d-clique. At least %d nodes are required.\n", 
                    count, graph.getCliqueSize() - 1, graph.getCliqueSize(), graph.getCliqueSize());
            return false;
        }
    }
    
    /**
     * Check if clique exists based on formed combinations
     * @return true if clique exists and vice versa
     */
    public static boolean cliqueExists()
    {
        ArrayList<Node> oriNodeList = graph.getNodeList();
        ArrayList<Node> tempNodeList = new ArrayList<>();
        int[] array1 = new int[graph.getCliqueSize()];
        int[] array2 = new int[graph.getCliqueSize()];
        int[] array3 = new int[graph.getGraphSize() - graph.getCliqueSize()];
        
        Collections.sort(oriNodeList, new Comparator<Node>()
        {
            public int compare(Node n1, Node n2)
            {
                return Integer.valueOf(n2.getEdgeCount()).compareTo(n1.getEdgeCount());
            }
        });
                
        int count = 0;
        
        for (Node n: oriNodeList)
        {
            
            try
            {
                tempNodeList.add((Node) n.clone());
                newTempList.add((Node) n.clone());
            }

            catch (CloneNotSupportedException ex)
            {
                Logger.getLogger(NonExactSolution.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        int loop_counter = 0;
        boolean first = true;
        
        while (loop_counter < 100)
        {
            if (first)
            {
                for (int i = 0; i < graph.getCliqueSize(); i++)
                {
                    array1[i] = tempNodeList.get(i).getNodeID();
                    array2[i] = tempNodeList.get(i).getNodeID();
                }
                
                
                for(int j = 0, i = graph.getCliqueSize(); i < graph.getGraphSize(); i++, j++)
                {
                    array3[j] = tempNodeList.get(i).getNodeID();
                }
                
                first = false;
            }
            
            else
            {        
                Random rand = new Random();      
                int min = 0;
                int max1 = array1.length - 1;
                int max2 = array3.length - 1;

                int randomNum1 =  rand.nextInt((max1 - min) + 1) + min;
                int randomNum2 =  rand.nextInt((max2 - min) + 1) + min;
                
                //System.out.println("Swapping " + array1[randomNum1] + " with " + array3[randomNum2]);
                
                int temp = array1[randomNum1];
                array1[randomNum1] = array3[randomNum2];
                array3[randomNum2] = temp;
                
            }

            int counter = 0;

            for (int k = 0; k < array1.length; k++)
            {
                //System.out.println("Checking node " + array1[k]);

                for (int l = 0; l < array2.length; l++)
                {
                    if (k != l)
                    {
                        if (valueExistsInArrayList(array1[k], getEdgeListFromNode(array2[l])))
                        {
                            //System.out.println("Check if node " + array1[k] + " is in edge list of node " + array2[l]);
                            counter++;
                        }
                    }
                }

            }

            loop_counter++;

            if(counter >= (graph.getCliqueSize() * (graph.getCliqueSize() - 1)))
            {
                System.out.println("Stage 3: Clique found!");
                System.out.println(Arrays.toString(array1));
                return true; 
            }       
        }
             
        System.out.println("Stage 3: Clique not found!");
        return false;
    }
    
    /**
     * This functions help to deep copy an array
     * @param <T> ArrayList
     * @param matrix
     * @return 
     */
    <T> T[][] deepCopy(T[][] matrix) {
    return java.util.Arrays.stream(matrix).map(el -> el.clone()).toArray($ -> matrix.clone());
}
    
    /**
     * This function creates combinations based on given array
     * @param arr nodeList
     * @param data temporary nodeList to store all combinations
     * @param start start of nodeList
     * @param end end of nodeList
     * @param index current index
     * @param r size of combination
     */
    public static void combinationUtil(int arr[], int data[], int start, int end, int index, int r) 
    { 
        // Current combination is ready to be printed, print it 
        if (index == r) 
        { 
            int temp[] = new int[r];          
            System.arraycopy(data, 0, temp, 0, r);         
            edgesToCheck.add(Arrays.toString(temp));
            return; 
        } 
  
        for (int i=start; i<=end && end-i+1 >= r-index; i++) 
        { 
            data[index] = arr[i]; 
            combinationUtil(arr, data, i+1, end, index+1, r); 
        } 
    } 
    
    /**
     * This function creates a temporary array to store all combinations
     * @param arr nodeList
     * @param n length of nodeList
     * @param r size of combination
     */
    public static void createCombination(int arr[], int n, int r) 
    { 
        // A temporary array to store all combination one by one 
        int data[] = new int[r]; 
  
        edgesToCheck.clear();
        // Print all combination using temprary array 'data[]' 
        combinationUtil(arr, data, 0, n-1, 0, r); 
    }
    
    
    //------------------------------------------------------------------------------------------------------
    // Supporting Functions
    //------------------------------------------------------------------------------------------------------
    
    /**
     * This function is a method to get a valid user input from users
     * @return graphSize, cliqueSize in an array
     */
    private static int[] getUserInput() {
        
        String line;
        int graphSize, cliqueSize;
        
        Scanner sc = new Scanner(System.in);
        	
        printSpecialLine(50, "*");
        System.out.print("\n          K- CLIQUE PROBLEM SOLUTION \n");
        printSpecialLine(50, "*");

        do
        {
            System.out.print("\nEnter Graph Size: ");
            line = sc.nextLine();
            
        } while(isNumberValid(line, 10, 25) == false);
        
        graphSize = Integer.parseInt(line);
        
        do
        {
            System.out.print("\nEnter Clique Size: ");
            line = sc.nextLine();
            
        } while(isNumberValid(line, graphSize / 2, graphSize) == false);
        
        cliqueSize = Integer.parseInt(line);
        
        int array[] = {graphSize, cliqueSize};
        
        return array;
    }
    
    /**
     * This function is a method to check whether the input is valid and display error message
     * @param number input by user
     * @param min boundary check by system to ensure sizes are not too small
     * @param max boundary check by system to ensure sizes are not too large
     * @return if number is valid or not
     */
    public static boolean isNumberValid(String number, int min, int max)
    {
        try 
        {
            int intValue = Integer.parseInt(number);
            
            if (intValue > max || intValue < min)
            {
                System.out.print("Input must be a digit between " + min + " to " + max + ". Please try again!\n");
                return false;
            }
            
            else
                return true;
            
        }
        catch(NumberFormatException e) 
        {
            System.out.print("Input must an integer. Please try again!\n");
            return false;
        }   
        
    }
    
    /**
     * This function is a method to check whether a value exist in a list.
     * Further checking will be using this potential clique graph for convenience sake.
     * @param value value to be checked
     * @param list list to be checked
     * @return if value exists in array
     */
    public static boolean valueExistsInArrayList(int value, ArrayList list)
    {
        return list.contains(value);
    }
    
    /**
     * This function returns the edge list based on a given node ID
     * @param value nodeID
     * @return edgeList of node with nodeID
     */
    public static ArrayList getEdgeListFromNode(int value)
    {
        for (Node n: newTempList)
        {
            if (n.getNodeID() == value)
                return n.getEdgeList();
        }
        
        return null;
    }
    
    /**
     * This function is to print out a special line, mainly for the use of menus
     * @param no no of characters in the line
     * @param pattern the pattern of the line (whether its *, - etc.)
     */
    public static void printSpecialLine(int no, String pattern)
    {
        for (int i = 0; i < no; i++)
        {
            System.out.print(pattern);
        }
    }
      
}