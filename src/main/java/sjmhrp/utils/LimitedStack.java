package sjmhrp.utils;

import java.util.Stack;

@SuppressWarnings("serial")
public class LimitedStack<E> extends Stack<E>{

	int maxSize;

	public LimitedStack(int maxSize) {
		super();
		this.maxSize = maxSize;
	}

	@Override
	public E push(E object) {
		while(this.size()>=maxSize)remove(0);
		return super.push(object);
	}
}