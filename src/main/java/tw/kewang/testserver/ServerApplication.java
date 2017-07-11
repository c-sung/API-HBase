package tw.kewang.testserver;

import tw.kewang.testserver.api.DataApi;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ServerApplication extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> restServiceSet = new HashSet<Class<?>>();

		restServiceSet.add(DataApi.class);

		return restServiceSet;
	}
}