package oose.sbtjavaapigen.generator

import java.lang.reflect._
import scalaz._
import Scalaz._
import Helper._

object Generator {

  import Extractors._
  import Writer._

  def apply(classes: Set[String]) = {
    val clazzes = classes.map(className => JavaClass(Class.forName(className)))

    clazzes.groupBy { _.classPackage }.write() 
  }
}

object Main extends App {

  def wrapInPackage(packageName: String)(c: String) = packageName + "." + c

  val engine = Set(
    "EngineServices",
    "FormService",
    "HistoryService",
    "IdentityService",
    "ManagementService",
    "ProcessEngine",
    "ProcessEngineInfo",
    "ProcessEngineLifecycleListener",
    "RepositoryService",
    "RuntimeService",
    "TaskService") map wrapInPackage("org.activiti.engine")

  val task = Set(
    "Attachment",
    "Comment",
    "Event",
    "IdentityLink",
    "NativeTaskQuery",
    "Task",
    "TaskQuery") map wrapInPackage("org.activiti.engine.task")
  
  val form = Set(
    "FormData",
    "FormProperty",
    "FormType",
    "StartFormData",
    "TaskFormData") map wrapInPackage("org.activiti.engine.form")

  val delegate = Set(
    "DelegateExecution",
    "DelegateTask",
    "ExecutionListener",
    "Expression",
    "JavaDelegate",
    "TaskListener",
    "VariableScope") map wrapInPackage("org.activiti.engine.delegate")

  val history = Set(
    "HistoricActivityInstance",
    "HistoricActivityInstanceQuery",
    "HistoricDetail",
    "HistoricDetailQuery",
    "HistoricFormProperty",
    "HistoricIdentityLink",
    "HistoricProcessInstance",
    "HistoricProcessInstanceQuery",
    "HistoricTaskInstance",
    "HistoricTaskInstanceQuery",
    "HistoricVariableUpdate",
    "NativeHistoricActivityInstanceQuery",
    "NativeHistoricDetailQuery",
    "NativeHistoricProcessInstanceQuery",
    "NativeHistoricTaskInstanceQuery",
    "NativeHistoricVariableInstanceQuery") map wrapInPackage("org.activiti.engine.history")

  val identity = Set(
    "Group",
    "GroupQuery",
    "NativeGroupQuery",
    "NativeUserQuery",
    "User",
    "UserQuery") map wrapInPackage("org.activiti.engine.identity")

  val repository = Set(
    "Deployment",
    "DeploymentBuilder",
    "DeploymentQuery",
    "Model",
    "ModelQuery",
    "NativeDeploymentQuery",
    "NativeProcessDefinitionQuery",
    "ProcessDefinition",
    "ProcessDefinitionQuery") map wrapInPackage("org.activiti.engine.repository")

  val runtime = Set(
    //"Clock",
    "Execution",
    "ExecutionQuery",
    "Job",
    "JobQuery",
    "NativeExecutionQuery",
    "NativeProcessInstanceQuery",
    "ProcessInstanceQuery") map wrapInPackage("org.activiti.engine.runtime")

  Generator(
    engine ++ delegate ++ history ++ identity ++ repository ++ runtime ++ task ++ form)
}