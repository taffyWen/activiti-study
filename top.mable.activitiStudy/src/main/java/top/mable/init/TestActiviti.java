package top.mable.init;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

//如果只是其中一张表被删除，执行以下程序后报错。
//目前方案，先删除所有表，在重新执行一边
/**
 * 
 * @author wen
 * @date 2019年10月22日 下午10:26:00
 */
public class TestActiviti {

	//获取流程引擎
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/**
	 * 获取流程引擎
	 * 使用代码创建23张表
	 */
	@Test
	public void createTable() {
		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
				.createStandaloneProcessEngineConfiguration();

		processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
		processEngineConfiguration
				.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useUnicode=true&characterEncoding=utf8");
		processEngineConfiguration.setJdbcUsername("root");
		processEngineConfiguration.setJdbcPassword("521037");

		// public static final String DB_SCHEMA_UPDATE_FALSE = "false";不能自动创建
		// public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";先删除后创建
		// public static final String DB_SCHEMA_UPDATE_TRUE = "true"; 不存在就创建
		processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		ProcessEngine buildProcessEngine = processEngineConfiguration.buildProcessEngine();
		System.out.println(buildProcessEngine);
	}

	/**
	 * 读取配置文件获取流程引擎
	 * 使用配置文件创建23张表
	 */
	@Test
	public void helloWorld() {
		ProcessEngine buildProcessEngine = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
				.buildProcessEngine();
		System.out.println(buildProcessEngine);
	}
}
