import java.util.Arrays;
import java.util.Scanner;

class BinarySearch {
    // Method to perform binary serach
    public static int binarySearch(int[] arr, int target) {
	int left = 0;
	int right = arr.length - 1;

	while (left <= right){
		int mid = left + (right - left) / 2;  // Prevent Overflow
	
		// Check if the traget is present at middle
		if (arr[mid] == target) {
                	return mid;  // Return the index if found
            	}

            	// If target greater, ignore left half
            	else if (arr[mid] < target) {
                	left = mid + 1;
            	}

            	// If target is smaller, ignore right half
            	else {
                	right = mid - 1;
            	}
	}
        return -1; // Return -1 if the target is not found
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
		
        // Input the array size
        System.out.print("Enter the number of elements in the array: ");
        int size = scanner.nextInt();

        int[] arr = new int[size];

        System.out.println("Enter the elements of the sorted array:");
        for (int i = 0; i < size; i++) {
            arr[i] = scanner.nextInt();
        }

        // Input target to search
        System.out.print("Enter target element to search: ");
        int target = scanner.nextInt();

        // Perform binary search
        int result = binarySearch(arr, target);

        // Output the result
        if (result != -1) {
            System.out.println("Element found at index: " + result);
        } else {
            System.out.println("Element not found in the array.");
        }
	// Close the scanner
        scanner.close();
    }

}
