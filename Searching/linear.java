import java.util.Scanner;

class LinearSearch {
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i; // return the index where target is found
            }
        }
        return -1; // target not found
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
	System.out.println("25MCA53 - Sayed Mohammed Kaif Talib");
	System.out.println("01 - 10 - 2025");

        System.out.print("Enter the number of elements: ");
        int size = scanner.nextInt();

        int[] array = new int[size];

        System.out.println("Enter the elements of the array: ");
        for (int i = 0; i < size; i++) {
            array[i] = scanner.nextInt();
        }

        System.out.print("Enter the target value: ");
        int target = scanner.nextInt();

        int result = linearSearch(array, target);

        if (result != -1) {
            System.out.println("Element found at index: " + result);
        } else {
            System.out.println("Element not found.");
        }

        scanner.close();
    }
}