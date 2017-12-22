package sjmhrp.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Factory<T> implements Serializable {

	private static final long serialVersionUID = -5511895182262660950L;

	static ArrayList<Factory<?>> factories = new ArrayList<Factory<?>>();
	
	public static List<Factory<?>> getFactories() {
		return factories;
	}
	
	public static void rebuild(List<Factory<?>> factories) {
		Factory.factories.clear();
		Factory.factories.addAll(factories);
		Factory.factories.forEach(Factory::build);
	}
	
	{if(store())factories.add(this);}
	
	public abstract T build();
	
	protected boolean store() {return true;}
}