package top.mable.g_receiveTask;

import java.io.InputStream;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class ReceiveTaskTest {
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
		InputStream inputStreamBpm = this.getClass().getResourceAsStream("/diagrams/receiveTask.bpmn");
		InputStream inputStreamPng = this.getClass().getResourceAsStream("/diagrams/receiveTask.png");
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署相关的service
				.createDeployment()// 创建一个部署对象
				.name("接收活动")
				.addInputStream("receiveTask.bpmn", inputStreamBpm)// 从classpath的资源中加载，一次只能加载一个文件
				.addInputStream("receiveTask.png", inputStreamPng)
				.deploy();// 完成部署对象
		System.out.println("部署ID：" + deployment.getId());
		System.out.println("部署名称：" + deployment.getName());
	}

	public void findProcessDefinition() {
		List<ProcessDefinition> list = processEngine.getRepositoryService()// 与流程定义和部署相关的service
				.createProcessDefinitionQuery()// 创建一个流程定义的查询
				.list();
	}

	/**
	 * 启动流程实例 + 设置流程变量 + 获取流程变量 + 向后执行一步
	 */
	@Test
	public void startProcessInstance() {
		// 流程定义的key
		String processDefinitionKey = "receiveTask";
		// 根据流程定义key获取流程实例
		ProcessInstance processInstanceByKey = processEngine.getRuntimeService()// 与正在执行的流程实例和流程对象相关的service
				.startProcessInstanceByKey(processDefinitionKey);// 使用流程定义的key启动流程实例，key对应diagrams/approveProcess.bpmn中的id的属性值<process
																	// id="approveProcess"
		System.out.println("流程实例id:" + processInstanceByKey.getId());
		System.out.println("流程实例定义id:" + processInstanceByKey.getProcessDefinitionId());
		
		/**
		 * 查询执行对象ID
		 */
		Execution execution = processEngine.getRuntimeService()
												.createExecutionQuery() // 创建执行对象查询
												.processInstanceId(processInstanceByKey.getId())// 使用流程实例ID查询
												.activityId("receivetask1") // 当前活动ID，对应receiveTask.bpmn文件中的活动节点的属性值
												.singleResult();
		/**
		 * 使用流程变量设置当前销售额，用来传递业务数据
		 */
		processEngine.getRuntimeService()
						.setVariable(execution.getId(), "汇总当日销售额", 6660);
		
		/**
		 * 向后执行一步，如果流程处于等待状态，使得流程继续进行
		 */
		processEngine.getRuntimeService()
						.signal(execution.getId());
		/**
		 * 查询执行对象ID
		 */
		Execution result = processEngine.getRuntimeService()
						.createExecutionQuery()
						.processInstanceId(execution.getId())
						.activityId("receivetask2") // 当前活动ID，对应receiveTask.bpmn文件中的活动节点的属性值
						.singleResult();
		processEngine.getRuntimeService()
						.getVariable(result.getId(), "汇总当日销售额");
		/**
		 * 向后执行一步，如果流程处于等待状态，使得流程继续进行
		 */
		processEngine.getRuntimeService()
						.signal(execution.getId());
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
