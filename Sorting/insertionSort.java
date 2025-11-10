class InsertionSort {

    // Method to perform insertion sort
    public static void insertionSort(int[] arr) {
        int n = arr.length;

        // Traverse through all array elements
        for (int i = 0; i < n - 1; i++) {
            // Find the minimum element in unsorted portion
	    int[] min = A[i];
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;		// Update index of the minimum element
                }
            }

            // Swap the found minimum element with the first element
            int temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }
    // Method to print an array
    public static void printArray(int[] arr) {
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
    // Main method to test the sort
    public static void main(String[] args) {
        int[] arr = {42, 53, 71, 9, 15, 55, 4, 82};
		System.out.println("Original array: ");

		printArray(arr);
		selectionSort(arr);rtr5

		System.out.println("Sorted array: ");
		printArray(arr);
    }
}

