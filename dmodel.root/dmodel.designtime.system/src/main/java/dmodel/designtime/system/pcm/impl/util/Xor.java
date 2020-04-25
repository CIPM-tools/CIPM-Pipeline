package dmodel.designtime.system.pcm.impl.util;

import java.util.Optional;

public class Xor<A, B> {
	private Optional<A> left;
	private Optional<B> right;

	public static <A, B> Xor<A, B> left(A a) {
		Xor<A, B> ret = new Xor<A, B>();
		ret.setLeft(a);
		return ret;
	}

	public static <A, B> Xor<A, B> right(B b) {
		Xor<A, B> ret = new Xor<A, B>();
		ret.setRight(b);
		return ret;
	}

	public Xor() {
		left = Optional.empty();
		right = Optional.empty();
	}

	public boolean leftPresent() {
		return left.isPresent();
	}

	public boolean rightPresent() {
		return right.isPresent();
	}

	public boolean anyPresent() {
		return leftPresent() || rightPresent();
	}

	public void setLeft(A a) {
		left = Optional.of(a);
		right = Optional.empty();
	}

	public void setRight(B b) {
		left = Optional.empty();
		right = Optional.of(b);
	}

	public A getLeft() {
		return left.orElse(null);
	}

	public B getRight() {
		return right.orElse(null);
	}
}
