package org.truenewx.test.init;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.truenewx.data.jdbc.datasource.DataSourceLookup;
import org.truenewx.data.model.Entity;

/**
 * 通过加载SQL脚本进行初始化的数据初始化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ScriptDataInitializer<T extends Entity> extends DataInitializer<T>
        implements ApplicationContextAware {
    @Autowired
    private DataSourceLookup dataSourceLookup;
    private ApplicationContext context;

    @Override
    public final void setApplicationContext(final ApplicationContext context)
            throws BeansException {
        this.context = context;
    }

    @Override
    protected boolean create() {
        if (!isInitialized()) {
            execute();
            // 缓存初始化后的所有数据
            final List<T> dataList = getDao().find((Map<String, ?>) null);
            setDataList(dataList);
            return true;
        }
        return false;
    }

    protected void execute() {
        final Resource resource = this.context.getResource(getLocation());
        final DatabasePopulator populator = new ResourceDatabasePopulator(resource);
        final DataSource dataSource = this.dataSourceLookup.getDataSource(getEntityName());
        DatabasePopulatorUtils.execute(populator, dataSource);
    }

    protected String getEntityName() {
        return getDao().getEntityClass().getName();
    }

    protected abstract String getLocation();

}
