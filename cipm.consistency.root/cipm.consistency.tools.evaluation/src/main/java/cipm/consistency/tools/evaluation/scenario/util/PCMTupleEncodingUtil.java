package cipm.consistency.tools.evaluation.scenario.util;

import java.util.Arrays;

public class PCMTupleEncodingUtil {

	public static void main(String[] args) {
		String d = "orders.VALUE";
		System.out.println("(1 - (" + String.valueOf(Math.E) + "^(-" + d + ")))");
		System.out.println("(" + String.valueOf(Math.E) + "^(" + d + "))");

		long orders = 10;
		long orderItems = 1000;

		double encoded = encodeTuple(orders, orderItems, 16, 24);

		System.out.println(encoded);

		double[] decoded = decodeTuple(16777231000.0d, 16, 24);
		System.out.println(Arrays.toString(decoded));

		System.out.println(generateTupleDecodeStringPCM("orderTuple.VALUE", 0, 24)); // orderItems
		System.out.println(generateTupleDecodeStringPCM("orderTuple.VALUE", 24, 40)); // orders
	}

	public static String generateTupleDecodeStringPCM(String tupleVariable, int bitsFrom, int bitsTo) {
		StringBuilder ret = new StringBuilder();

		ret.append("Round");
		for (int i = 1; i < bitsTo - bitsFrom; i++) {
			ret.append("(");
		}
		for (int k = bitsFrom; k < bitsTo; k++) {
			if (k != bitsFrom) {
				ret.append(" + ");
			}
			ret.append("(");
			ret.append("(2.0^" + (k - bitsFrom) + ".0) * ");
			ret.append("(((" + tupleVariable + " - (" + tupleVariable + " % (2.0^" + k + ".0))) / (2.0^" + k
					+ ".0)) % 2.0)");
			ret.append(")");
			if (k != bitsFrom) {
				ret.append(")");
			}
		}

		return ret.toString();
	}

	public static double encodeTuple(long orders, long orderItems, int bitsOrder, int bitsOrderItems) {
		return orderItems + (orders << bitsOrderItems);
	}

	public static double[] decodeTuple(double tuple, int bitsOrder, int bitsOrderItems) {
		double orderItems = 0;
		for (int k = 0; k < bitsOrderItems; k++) {
			orderItems += Math.pow(2, k) * (((tuple - (tuple % Math.pow(2, k))) / Math.pow(2, k)) % 2);
		}

		double orders = 0;
		for (int k = bitsOrderItems; k < bitsOrderItems + bitsOrder; k++) {
			orders += Math.pow(2, k - bitsOrderItems) * (((tuple - (tuple % Math.pow(2, k))) / Math.pow(2, k)) % 2);
		}

		return new double[] { orders, orderItems };
	}

}
