package dmodel.designtime.system.pcm.impl.util;

import java.util.Optional;

/**
 * Simple class that contains an object of a given type A OR (xor) an object of
 * a given type B.
 * 
 * @author David Monschein
 *
 * @param <A> type A
 * @param <B> type B
 */
public class Xor<A, B> {
	private Optional<A> left;
	private Optional<B> right;

	/**
	 * Creates a new instance where the left type (A) is present.
	 * 
	 * @param <A> type A
	 * @param <B> type B
	 * @param a   object instance of type A
	 * @return the created instance
	 */
	public static <A, B> Xor<A, B> left(A a) {
		Xor<A, B> ret = new Xor<A, B>();
		ret.setLeft(a);
		return ret;
	}

	/**
	 * Creates a new instance where the right type (B) is present.
	 * 
	 * @param <A> type A
	 * @param <B> type B
	 * @param b   object instanceo f type B
	 * @return the created instance
	 */
	public static <A, B> Xor<A, B> right(B b) {
		Xor<A, B> ret = new Xor<A, B>();
		ret.setRight(b);
		return ret;
	}

	/**
	 * Creates a new instance where both the left and the right type are not
	 * present.
	 */
	public Xor() {
		left = Optional.empty();
		right = Optional.empty();
	}

	/**
	 * Determines whether the left type is present.
	 * 
	 * @return true if the left type is present, false otherwise
	 */
	public boolean leftPresent() {
		return left.isPresent();
	}

	/**
	 * Determines whether the right type is present.
	 * 
	 * @return true if the right type is present, false otherwise
	 */
	public boolean rightPresent() {
		return right.isPresent();
	}

	/**
	 * Determines whether the right type or the left type is present.
	 * 
	 * @return true if the right type or the left type is present, only returns
	 *         false if none of both is present
	 */
	public boolean anyPresent() {
		return leftPresent() || rightPresent();
	}

	/**
	 * Sets the left type and empties the right one.
	 * 
	 * @param a the object to set
	 */
	public void setLeft(A a) {
		left = Optional.of(a);
		right = Optional.empty();
	}

	/**
	 * Sets the right type and empties the left one.
	 * 
	 * @param a the object to set
	 */
	public void setRight(B b) {
		left = Optional.empty();
		right = Optional.of(b);
	}

	/**
	 * Gets the left value or null if it is not present.
	 * 
	 * @return left value or null if it is not present
	 */
	public A getLeft() {
		return left.orElse(null);
	}

	/**
	 * Gets the right value or null if it is not present.
	 * 
	 * @return right value or null if it is not present
	 */
	public B getRight() {
		return right.orElse(null);
	}
}
