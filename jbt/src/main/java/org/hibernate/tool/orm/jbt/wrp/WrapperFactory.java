package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;

public class WrapperFactory {

	public Object createArtifactCollectorWrapper() {
		return new DefaultArtifactCollector();
	}
	
	public Object createCfg2HbmWrapper() {
		return new Cfg2HbmTool();
	}

	public Object createNamingStrategyWrapper(String namingStrategyClassName) {
		Object result = new DefaultNamingStrategy();
		if (namingStrategyClassName != null) {
			try {
				result = getClass().getClassLoader()
						.loadClass(namingStrategyClassName)
						.getDeclaredConstructor()
						.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	public Object createReverseEngineeringSettingsWrapper(Object revengStrategy) {
		assert revengStrategy != null;
		assert revengStrategy instanceof RevengStrategy;
		return new RevengSettings((RevengStrategy)(revengStrategy));
	}

	public Object createReverseEngineeringStrategyWrapper() {
		return new DefaultStrategy();
	}

}
