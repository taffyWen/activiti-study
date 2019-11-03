package top.mable.i_startProcess;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class StartTest {
	/**
	 * 工作流引擎 
		ProcessEngine对象，这是Activiti工作的核心。负责生成流程运行时的各种实例及数据、监控和管理流程的运行。 
	 */
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/**
	 * 部署流程定义
	 */
	@Test
	public void deploymentDefinition() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署相关的service
				.createDeployment()// 创建一个部署对象
				.name("start").addClasspathResource("diagrams/start.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/start.png").deploy();// 完成部署对象
		System.out.println(deployment.getId());
		System.out.println(deployment.getName());
	}

	public void findProcessDefinition() {
		processEngine.getRepositoryService()// 与流程定义和部署相关的service
				.createProcessDefinitionQuery()// 创建一个流程定义的查询
				.list();
	}

	/**
	 * 启动流程实例，判断流程是否结束，查询历史
	 */
	@Test
	public void startProcessInstance() {
		// 流程定义的key
		String processDefinitionKey = "start";
		ProcessInstance processInstanceByKey = processEngine.getRuntimeService()// 与正在执行的流程实例和流程对象相关的service
				.startProcessInstanceByKey(processDefinitionKey);// 使用流程定义的key启动流程实例，key对应diagrams/approveProcess.bpmn中的id的属性值<process
																	// id="approveProcess"
		System.out.println("流程实例id:" + processInstanceByKey.getId());
		System.out.println("流程实例定义id:" + processInstanceByKey.getProcessDefinitionId());
		ProcessInstance singleResult = processEngine.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(processInstanceByKey.getId()).singleResult();
		if (singleResult == null) {
			HistoricProcessInstance hpi = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceByKey.getId()).singleResult();
			System.out.println(hpi.getId() + "    " + hpi.getStartTime() + hpi.getEndTime());
		}
	}

	/**
	 * 查询当前人的个人任务
	 */
	@Test
	public void findMyPersonalTask() {
		String assignee = "张三";
		List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的service
				.createTaskQuery()// 创建任务查询对象
				.taskAssignee(assignee)// 指定个人任务查询，指定办理人
				.list();
		for (Task task : list) {
			System.out.println("任务ID:" + task.getId());
			System.out.println("任务名称:" + task.getName());
			System.out.println("任务创建时间:" + task.getCreateTime());
			System.out.println("任务的办理人：" + task.getAssignee());
			System.out.println("流程实例ID：" + task.getProcessInstanceId());
			System.out.println("执行对象ID：" + task.getExecutionId());
			System.out.println("流程定义ID：" + task.getProcessDefinitionId());
			System.out.println("#################################");
		}
	}

	/**
	 * 完成我的任务
	 */
	@Test
	public void completeMyProcess() {
		String taskId = "27502";
		processEngine.getTaskService().complete(taskId);
		System.out.println("完成任务：任务ID" + taskId);
	}
}
